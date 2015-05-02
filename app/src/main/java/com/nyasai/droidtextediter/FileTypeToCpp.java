package com.nyasai.droidtextediter;

import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import java.lang.Object;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileTypeToCpp {
        private TextView myTextViewMain;
        private final String CHECKSTR1= "int|long|short|signed|unsigned|float|double|bool|char|wchar_t|" +
                "void|auto|class|struct|union|enum|const|volatile|extern|" +
                "register|static|mutable|friend|explicit|inline|virtual|" +
                "public|protected|private|template|typname|asm|true|false|";
        private final String CHECKSTR2= "typedef|operator|this|if|else|for|while|do|switch|case|default|" +
                "break|continue|goto|return|try|catch|new|delete|dynamic_cast|static_cast|const_cast|reinterpret_cast|" +
                "sizeof|typeid|throw|namespace|using|#include|#define|and|and_eq|bitand|bitor|compl|not|not_eq|or|or_eq|xor|xor_eq|#if|#endif";
        private final String CHECKSTR3="[0-9]";
        private final String CHECKSTR4="~|!|%|&|=|:|;|\"|,|/|<|>|\\^|\\*|\\(|\\)|\\-|\\+|\\{|\\}|\\[|\\]|\\.|\\?";
        private final String CHECKCOMMENT = "//";
        private final String SPLITPATTERN = "(?<= )|(?= )|(?<=-)|(?=-)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\\*)|(?=\\*)|(?<=\")|(?=\")|(?<=;)|(?=;)" +
                                        "|(?<=:)|(?=:)|(?<==)|(?==)|(?<=,)|(?=,)|(?<=>)|(?=>)|(?<=\\[)|(?=\\[)|(?<=\\])|(?=\\])|(?<=\\.)|(?=\\.)|(?<=\\s)|(?=\\s)";

        //文字のチェック
        private int checkReservedword(String tempStr){
                int paternFlag=0;
                Pattern myPattern1 = Pattern.compile(CHECKSTR1,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL|Pattern.UNICODE_CASE);
                Pattern myPattern2 = Pattern.compile(CHECKSTR2,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL|Pattern.UNICODE_CASE);
                Pattern myPattern3 = Pattern.compile(CHECKSTR3,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL|Pattern.UNICODE_CASE);
                Pattern myPattern4 = Pattern.compile(CHECKSTR4,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL|Pattern.UNICODE_CASE);
                Pattern commentPattern = Pattern.compile(CHECKCOMMENT,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL|Pattern.UNICODE_CASE);
                Matcher myMatcher1 = myPattern1.matcher(tempStr);
                Matcher myMatcher2 = myPattern2.matcher(tempStr);
                Matcher myMatcher3 = myPattern3.matcher(tempStr);
                Matcher myMatcher4 = myPattern4.matcher(tempStr);
                Matcher commentMather = commentPattern.matcher(tempStr);

                if(myMatcher1.matches()){
                        paternFlag = 1;
                }
                if(myMatcher2.matches()){
                        paternFlag = 2;
                }
                if(myMatcher3.matches()){
                        paternFlag = 3;
                }
                if(myMatcher4.matches()){
                        paternFlag = 4;
                }
                return paternFlag;
        }

        private void textSetBranch(int typeFlag, String setText, TextView myTextViewMain){
                switch (typeFlag){
                        case 0:
                                myTextViewMain.append(setText);
                                break;
                        case 1:
                                myTextViewMain.append(Html.fromHtml("<font color=#008000>"+setText+"</font>"));
                                break;
                        case 2:
                                myTextViewMain.append(Html.fromHtml("<font color=#DAA520>"+setText+"</font>"));
                                break;
                        case 3:
                                myTextViewMain.append(Html.fromHtml("<font color=#4169E1>"+setText+"</font>"));
                                break;
                        case 4:
                                myTextViewMain.append(Html.fromHtml("<font color=#B8860B>"+setText+"</font>"));
                                break;

                }
        }

        public void textSets(ArrayList<String> fileStr, MainActivity mainActivity){
                myTextViewMain = (TextView)mainActivity.findViewById(R.id.myTextViewMain);
                String[] fileStrOneWords;
                int typeFlag = 0;
                Pattern splitPattern = Pattern.compile(SPLITPATTERN);

                for (int i=0; i<fileStr.size(); i++){
                        fileStrOneWords = splitPattern.split(fileStr.get(i));
                        for(int j=0; j<fileStrOneWords.length; j++){
                                typeFlag = this.checkReservedword(fileStrOneWords[j]);
                                this.textSetBranch(typeFlag, fileStrOneWords[j], myTextViewMain);
                        }
                        fileStrOneWords = null;
                }
        }

}
