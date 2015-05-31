package com.nyasai.droidtextediter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MyFileOpen extends Activity {
    //デフォルトエンコードの指定
    private static final String DEFAULT_ENCORDING = "UTF-8";
    private static int textLines;


    /*public String fileLoad(String fileName){
        StringBuilder files = new StringBuilder();
        String tempFiles;
        textLines = 0;

        try{
            FileInputStream inputStream = new FileInputStream(new File(fileName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,DEFAULT_ENCORDING));
            while ((tempFiles = reader.readLine()) != null ){
                files.append(tempFiles);
                files.append("\n");
                textLines++;
            }
            reader.close();
        }catch (Exception e){
            files.append("This File null");
        }
        return files.toString();
    }*/

    public ArrayList<String> fileLoad(String fileName) {
        ArrayList<String> files = new ArrayList<String>();
        String tempFiles;
        textLines = 0;

        try {
            FileInputStream inputStream = new FileInputStream(new File(fileName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_ENCORDING));
            while ((tempFiles = reader.readLine()) != null) {
                files.add(tempFiles + "\n");
                textLines++;
            }
            reader.close();
        } catch (Exception e) {
            files.add("This File null");
        }
        return files;
    }

    public static int getLines() {
        return textLines;
    }


}