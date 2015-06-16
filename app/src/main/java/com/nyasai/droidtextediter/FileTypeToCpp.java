package com.nyasai.droidtextediter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileTypeToCpp extends AsyncTask<ArrayList<String>, Integer, ArrayList<String>> {

    ActionBarActivity actionBarActivity;
    MyProgressDialog progressDialog = null;


    private static final int NOMAL = 0;
    private static final int VARIABLE = 1;
    private static final int FUNC = 2;
    private static final int VALUE = 3;
    private static final int SYMBOL = 4;
    private static final int COMMENT = 5;
    private static final int STRING = 6;
    private static final int INCLUDE = 7;

    private static boolean commentFlag = false;
    private static boolean commentsFlag = false;
    private static boolean stringFlag = false;
    private static boolean includeFlag = false;
    private static boolean cancellFlag = false;

    private final String CHECKVARIABLE = "(int|long|short|signed|unsigned|float|double|bool|char|wchar_t|" +
            "void|auto|class|struct|union|enum|const|volatile|extern|" +
            "register|static|mutable|friend|explicit|inline|virtual|" +
            "public|protected|private|template|typname|asm|true|false|)";
    private final String CHECKFUNC = "(typedef|operator|this|if|else|for|while|do|switch|case|default|" +
            "break|continue|goto|return|try|catch|new|delete|dynamic_cast|static_cast|const_cast|reinterpret_cast|" +
            "sizeof|typeid|throw|namespace|using|#include|#define|and|and_eq|bitand|bitor|compl|not|not_eq|or|or_eq|xor|xor_eq|#if|#endif)";
    private final String CHECKVALUE = "[0-9]";
    private final String CHECKSYMBOL = "~|!|%|&|=|:|;|\"|'|,|/|<|>|&|\\||\\^|\\*|\\(|\\)|\\-|\\+|\\{|\\}|\\[|\\]|\\.|\\?";
    private final String CHECKCOMMENT = "//";
    private final String CHECKCOMMENTS = "/\\*";
    private final String CHECKSTRING = "\"|'";
    private final String CHECKINCLUDE = "#include";
    private final String CHECKENDLINE = "\n|<br/>";
    private final String CHECKENDCOMMENTS = "\\*/";
    private final String SPLITPATTERN = "(?<= )|(?= )|(?<=-)|(?=-)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\")|(?=\")|(?<=\')|(?=\')|(?<=;)|(?=;)|(?<=\t)|(?=\t)" +
            "|(?<=:)|(?=:)|(?<==)|(?==)|(?<=,)|(?=,)|(?<=>)|(?=>)|(?<=\\[)|(?=\\[)|(?<=\\])|(?=\\])|(?<=\\.)|(?=\\.)|(?=\\})|(?<=\\})|(?=\\{)|(?<=\\{)" +
            "|(?<=&)|(?=&)|(?<=\\|)|(?=\\|)|(?<=//)|(?=//)|(?<=!)|(?=!)|(?<=/\\*)|(?=/\\*)|(?<=\\*/)|(?=\\*/)|(?<=(?<!/)\\*(?!/))|(?=(?<!/)\\*(?!/))";


    //コンストラクタ
    public FileTypeToCpp(ActionBarActivity actionBarActivity) {
        this.actionBarActivity = actionBarActivity;
    }


    //前処理(プログレスバーの表示準備とか)
    @Override
    protected void onPreExecute() {
        @SuppressWarnings({"serial"})
        Serializable Cancel_Listener = new MyProgressDialog.CancelListener() {
            @Override
            public void canceled(DialogInterface _interface) {
                cancel(true); // これをTrueにすることでキャンセルされ、onCancelledが呼び出される。
                cancellFlag = true;
            }
        };
        progressDialog = MyProgressDialog.newInstance("処理中", "しばらくお待ちください\n※行数が多い場合は処理終了後表示に時間がかかります", true, Cancel_Listener);
        progressDialog.show((actionBarActivity).getFragmentManager(), "progress");
    }


    //非同期処理
    @Override
    protected ArrayList<String> doInBackground(ArrayList<String>... params) {
        return this.textSets(params[0]);
    }

    //非同期処理後の結果をUIに反映
    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        if (strings != null) {
            TextView textView = (TextView) this.actionBarActivity.findViewById(R.id.myTextViewMain);
            for (int i = 0; i < strings.size(); i++) {
                textView.append(Html.fromHtml(strings.get(i)));
            }
        } else {
            TextView textView = (TextView) this.actionBarActivity.findViewById(R.id.myTextViewMain);
            TextView linetextView =(TextView) this.actionBarActivity.findViewById(R.id.myTextViewLines);
            this.actionBarActivity.setTitle(R.string.app_name);
            textView.setText("");
            linetextView.setText("");
        }
        if (progressDialog.getShowsDialog()) {
            progressDialog.dismiss();
        }
    }

    //キャンセル時の処理
    @Override
    protected void onCancelled() {
        if (progressDialog.getShowsDialog()) {
            progressDialog.dismiss();
        }
        Toast.makeText(actionBarActivity, "Canceled", Toast.LENGTH_SHORT).show();
        TextView textView = (TextView) this.actionBarActivity.findViewById(R.id.myTextViewMain);
        TextView linetextView =(TextView) this.actionBarActivity.findViewById(R.id.myTextViewLines);
        this.actionBarActivity.setTitle(R.string.app_name);
        textView.setText("");
        linetextView.setText("");

    }

    //メイン部分
    public ArrayList<String> textSets(ArrayList<String> fileStr) {
        String[] fileStrOneWords;
        ArrayList<String> endFiles = new ArrayList<String>();
        int typeFlag = 0;
        Pattern splitPattern = Pattern.compile(SPLITPATTERN);

        for (int i = 0; i < fileStr.size(); i++) {
            fileStrOneWords = splitPattern.split(fileStr.get(i), -1);
            for (int j = 0; j < fileStrOneWords.length; j++) {
                typeFlag = this.checkReservedword(fileStrOneWords[j]);
                endFiles.add(this.textSetBranch(typeFlag, fileStrOneWords[j]));
                if (cancellFlag){
                    cancellFlag = false;
                    return endFiles;
                }
            }
            fileStrOneWords = null;
            endFiles.add("<br/>");
        }
        return endFiles;
    }

    //文字のチェック
    private int checkReservedword(String tempStr) {
        int patternFlag = NOMAL;
        Pattern variablePattern = Pattern.compile(CHECKVARIABLE, Pattern.DOTALL | Pattern.UNIX_LINES);
        Pattern funcPattern = Pattern.compile(CHECKFUNC, Pattern.DOTALL | Pattern.UNIX_LINES);
        Pattern valuePattern = Pattern.compile(CHECKVALUE, Pattern.DOTALL);
        Pattern symbolPattern = Pattern.compile(CHECKSYMBOL, Pattern.DOTALL);
        Pattern stringPattern = Pattern.compile(CHECKSTRING, Pattern.DOTALL);
        Pattern commentPattern = Pattern.compile(CHECKCOMMENT, Pattern.DOTALL);
        Pattern commentsPattern = Pattern.compile(CHECKCOMMENTS, Pattern.DOTALL);
        Pattern includePattern = Pattern.compile(CHECKINCLUDE, Pattern.DOTALL);
        Pattern endlinePattern = Pattern.compile(CHECKENDLINE, Pattern.DOTALL);
        Pattern endcommentsPattern = Pattern.compile(CHECKENDCOMMENTS, Pattern.DOTALL);
        Matcher variableMatcher = variablePattern.matcher(tempStr);
        Matcher funcMatcher = funcPattern.matcher(tempStr);
        Matcher valueMatcher = valuePattern.matcher(tempStr);
        Matcher symbolMatcher = symbolPattern.matcher(tempStr);
        Matcher stringMather = stringPattern.matcher(tempStr);
        Matcher commentMather = commentPattern.matcher(tempStr);
        Matcher commentsMather = commentsPattern.matcher(tempStr);
        Matcher includeMather = includePattern.matcher(tempStr);
        Matcher endlineMather = endlinePattern.matcher(tempStr);
        Matcher endcommentsMather = endcommentsPattern.matcher(tempStr);

        if (variableMatcher.matches()) {
            patternFlag = VARIABLE;
        }
        if (funcMatcher.matches()) {
            patternFlag = FUNC;
        }
        if (valueMatcher.matches()) {
            patternFlag = VALUE;
        }
        if (symbolMatcher.find()) {
            patternFlag = SYMBOL;
        }
        //includeかどうかの判定
        //include後の”が文字列と判定されないために
        if (includeMather.matches()) {
            includeFlag = true;
        }
        if (includeFlag){
            patternFlag = INCLUDE;
        }
        if (includeFlag && endlineMather.find()) {
            includeFlag = false;
        }
        //文字列かどうかの判定
        if (!includeFlag) {
            if (!stringFlag && stringMather.find()) {
                stringFlag = true;
                patternFlag = STRING;
            }
            if (stringFlag) {
                patternFlag = STRING;
            }
            if (stringFlag && stringMather.find()) {
                patternFlag = STRING;
                stringFlag = false;
            }
        }
        //コメント行であるかどうかの判定
        if (!commentFlag && commentMather.find() && !stringFlag) {
            commentFlag = true;
            patternFlag = COMMENT;
        }
        if (commentFlag) {
            patternFlag = COMMENT;
        }
        if (commentFlag && endlineMather.find()) {
            patternFlag = COMMENT;
            commentFlag = false;
        }
        //コメント行であるかどうかの判定
        if (!commentsFlag && commentsMather.find() && !stringFlag) {
            commentsFlag = true;
            patternFlag = COMMENT;
        }
        if (commentsFlag) {
            patternFlag = COMMENT;
        }
        if (commentsFlag && endcommentsMather.find()) {
            patternFlag = COMMENT;
            commentsFlag = false;
        }
        return patternFlag;
    }

    //文字の種類によって色分け
    private String textSetBranch(int typeFlag, String setText) {
        Pattern space = Pattern.compile(" +", Pattern.DOTALL);
        Pattern tab = Pattern.compile("\t", Pattern.DOTALL);
        Matcher spaceMatcher = space.matcher(setText);
        Matcher tabMatcher = tab.matcher(setText);
        //スペースをhtmlでも認識されるように変換
        if (spaceMatcher.find()) {
            setText = spaceMatcher.replaceAll("<pre>&nbsp;</pre>");
        }
        if (tabMatcher.find()) {
            setText = tabMatcher.replaceAll("<pre>&nbsp;&nbsp;&nbsp;&nbsp;</pre>");
        }
        //Log.d("split", "***-" + setText + "-***");
        switch (typeFlag) {
            case NOMAL:
                return setText;
            case VARIABLE:
                return ("<font color=#deb887>" + setText + "</font>");
            case FUNC:
                return ("<font color=#ff8c00>" + setText + "</font>");
            case VALUE:
                return ("<font color=#4169E1>" + setText + "</font>");
            case SYMBOL:
                return ("<font color=#B8860B>" + setText + "</font>");
            case COMMENT:
                return ("<font color=#006400>" + setText + "</font>");
            case STRING:
                return ("<font color=#2e8b57>" + setText + "</font>");
            case INCLUDE:
                return ("<font color=#ff7f50>" + setText + "</font>");
        }
        return null;
    }


}
