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


public class FileTypeToCpp extends AsyncTask<ArrayList<String>, Integer, ArrayList<String>>{

    ActionBarActivity actionBarActivity;
    MyProgressDialog progressDialog = null;


    private static final int NOMAL = 0;
    private static final int VARIABLE = 1;
    private static final int FUNC = 2;
    private static final int VALUE = 3;
    private static final int SYMBOL = 4;
    private static final int COMENT = 5;
    private static final int STRING = 6;

    private final String CHECKSTR1 = "(int|long|short|signed|unsigned|float|double|bool|char|wchar_t|" +
            "void|auto|class|struct|union|enum|const|volatile|extern|" +
            "register|static|mutable|friend|explicit|inline|virtual|" +
            "public|protected|private|template|typname|asm|true|false|)";
    private final String CHECKSTR2 = ".*(typedef|operator|this|if|else|for|while|do|switch|case|default|" +
            "break|continue|goto|return|try|catch|new|delete|dynamic_cast|static_cast|const_cast|reinterpret_cast|" +
            "sizeof|typeid|throw|namespace|using|#include|#define|and|and_eq|bitand|bitor|compl|not|not_eq|or|or_eq|xor|xor_eq|#if|#endif)";
    private final String CHECKSTR3 = "[0-9]";
    private final String CHECKSTR4 = "~|!|%|&|=|:|;|\"|'|,|/|<|>|&|\\||\\^|\\*|\\(|\\)|\\-|\\+|\\{|\\}|\\[|\\]|\\.|\\?";
    private final String CHECKCOMMENT = "^//.*\n|^/\\*.*\n";
    private final String SPLITPATTERN = "(?<= )|(?= )|(?<=-)|(?=-)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\\*)|(?=\\*)|(?<=\")|(?=\")|(?<=\')|(?=\')|(?<=;)|(?=;)|(?<=\t)|(?=\t)" +
            "|(?<=:)|(?=:)|(?<==)|(?==)|(?<=,)|(?=,)|(?<=>)|(?=>)|(?<=\\[)|(?=\\[)|(?<=\\])|(?=\\])|(?<=\\.)|(?=\\.)|(?=\\})|(?<=\\})|(?=\\{)|(?<=\\{)" +
            "|(?<=&)|(?=&)|(?<=\\|)|(?=\\|)";


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
            textView.setText("");
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
        textView.setText("");
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
            }
            fileStrOneWords = null;
            endFiles.add("<br/>");
        }
        return endFiles;
    }

    //文字のチェック
    private int checkReservedword(String tempStr) {
        int paternFlag = NOMAL;
        Pattern myPattern1 = Pattern.compile(CHECKSTR1, Pattern.DOTALL | Pattern.UNIX_LINES);
        Pattern myPattern2 = Pattern.compile(CHECKSTR2, Pattern.DOTALL | Pattern.UNIX_LINES);
        Pattern myPattern3 = Pattern.compile(CHECKSTR3, Pattern.DOTALL);
        Pattern myPattern4 = Pattern.compile(CHECKSTR4, Pattern.DOTALL);
        Pattern commentPattern = Pattern.compile(CHECKCOMMENT, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE);
        Matcher myMatcher1 = myPattern1.matcher(tempStr);
        Matcher myMatcher2 = myPattern2.matcher(tempStr);
        Matcher myMatcher3 = myPattern3.matcher(tempStr);
        Matcher myMatcher4 = myPattern4.matcher(tempStr);
        Matcher commentMather = commentPattern.matcher(tempStr);


        if (myMatcher1.matches()) {
            paternFlag = VARIABLE;
        }
        if (myMatcher2.matches()) {
            paternFlag = FUNC;
        }
        if (myMatcher3.matches()) {
            paternFlag = VALUE;
        }
        if (myMatcher4.matches()) {
            paternFlag = SYMBOL;
        }
        //if(commentMather.find()){
        //        paternFlag = COMENT;
        //}
        return paternFlag;
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
            //Log.d("space", "***-" + setText + "-***");
        }
        if (tabMatcher.find()) {
            setText = tabMatcher.replaceAll("<pre>&nbsp;&nbsp;&nbsp;&nbsp;</pre>");
        }
        switch (typeFlag) {
            case NOMAL:
                return setText;
            case VARIABLE:
                return ("<font color=#8B4513>" + setText + "</font>");
            case FUNC:
                return ("<font color=#DAA520>" + setText + "</font>");
            case VALUE:
                return ("<font color=#4169E1>" + setText + "</font>");
            case SYMBOL:
                return ("<font color=#B8860B>" + setText + "</font>");
            case COMENT:
                return ("<font color=#008000>" + setText + "</font>");
            case STRING:
                break;
        }
        return null;
    }


}
