package com.nyasai.droidtextediter;

import android.widget.TextView;

import java.lang.Object;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class FileTypeToCpp {
        private TextView myTextViewMain;


        //文字のチェック
        private void checkReservedword(String tempStr){

        }


        public void setTexts(ArrayList<String> fileStr){
                for (int i=0; i<fileStr.size(); i++){
                        this.checkReservedword(fileStr.get(i));
                }

        }


}
