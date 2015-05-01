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
                "public|protected|private|template|typname|asm";

        private final String CHECKSTR2= "true|false|typedef|operator|this|if|else|for|while|do|switch|case|default|" +
                "break|continue|goto|return|try|catch|new|delete|dynamic_cast|static_cast|const_cast|reinterpret_cast|" +
                "sizeof|typeid|throw|namespace|using|#include|#define|and|and_eq|bitand|bitor|compl|not|not_eq|or|or_eq|xor|xor_eq";
        private final String CHECKSTR3="[0-9]";
        private final String CHECKSTR4="~|!|%|&|=|:|;|\"|,|/||<|>\\^|\\*|\\(|\\)|\\-|\\+|\\{|\\}|\\[|\\]|\\.|\\?";
        private final String CHECKCOMMENT = "//";

        //文字のチェック
        private int checkReservedword(String tempStr){
                Pattern myPattern1 = Pattern.compile(CHECKSTR1,Pattern.CASE_INSENSITIVE);
                Pattern myPattern2 = Pattern.compile(CHECKSTR2,Pattern.CASE_INSENSITIVE);
                Pattern myPattern3 = Pattern.compile(CHECKSTR3,Pattern.CASE_INSENSITIVE);
                Pattern myPattern4 = Pattern.compile(CHECKSTR4,Pattern.CASE_INSENSITIVE);
                Pattern commentPattern = Pattern.compile(CHECKCOMMENT,Pattern.CASE_INSENSITIVE);
                Matcher myMatcher1 = myPattern1.matcher(tempStr);
                Matcher myMatcher2 = myPattern2.matcher(tempStr);
                Matcher myMatcher3 = myPattern3.matcher(tempStr);
                Matcher myMatcher4 = myPattern4.matcher(tempStr);
                Matcher commentMather = commentPattern.matcher(tempStr);

                if(myMatcher1.matches()){
                        return 1;
                }
                else if(myMatcher2.matches()){
                        return 2;
                }
                else if(myMatcher3.matches()){
                        return 3;
                }
                else if(myMatcher4.matches()){
                        return 4;
                }
                return 0;
        }

        private void textSetBranch(int typeFlag, String setText, TextView myTextViewMain){
                switch (typeFlag){
                        case 0:
                                myTextViewMain.append(setText +" ");
                                break;
                        case 1:
                                myTextViewMain.append(Html.fromHtml("<font color=#008000>"+setText+"</font>"));
                                myTextViewMain.append(" ");
                                break;
                        case 2:
                                myTextViewMain.append(Html.fromHtml("<font color=#DAA520>"+setText+"</font>"));
                                myTextViewMain.append(" ");
                                break;
                        case 3:
                                myTextViewMain.append(Html.fromHtml("<font color=#4169E1>"+setText+"</font>"));
                                myTextViewMain.append(" ");
                                break;
                        case 4:
                                myTextViewMain.append(Html.fromHtml("<font color=#F4A460>"+setText+"</font>"));
                                myTextViewMain.append(" ");
                                break;

                }
        }

        public void textSets(ArrayList<String> fileStr, MainActivity mainActivity){
                myTextViewMain = (TextView)mainActivity.findViewById(R.id.myTextViewMain);
                String[] fileStrOneWords;
                int typeFlag = 0;
                Pattern splitPattern = Pattern.compile(" |-|\\(|\\[|\\{|>|\"|'|,|:|;");

                for (int i=0; i<fileStr.size(); i++){
                        fileStrOneWords = fileStr.get(i).split(" ");
                        for(int j=0; j<fileStrOneWords.length; j++){
                                typeFlag = this.checkReservedword(fileStrOneWords[j]);
                                this.textSetBranch(typeFlag, fileStrOneWords[j], myTextViewMain);
                        }
                        //myTextViewMain.append(fileStr.get(i));
                        fileStrOneWords = null;
                }
        }

}
