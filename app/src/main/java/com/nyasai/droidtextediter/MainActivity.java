package com.nyasai.droidtextediter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    private Intent intent;
    private TextView myTextViewMain;
    private TextView myTextViewLines;
    private MyFileOpen myFileOpen;
    private FileTypeToCpp myFileTypeToCpp;
    private static final int SUB_ACTIVITY = 1001;

    private void assignViews(){
        myTextViewMain = (TextView)findViewById(R.id.myTextViewMain);
        myTextViewLines = (TextView)findViewById(R.id.myTextViewLines);
        myFileOpen = new MyFileOpen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.assignViews();
    }

    //アクティビティの移動
    private void moveActivity(){
        intent = new Intent(this,FileActivity.class);
        startActivityForResult(intent, SUB_ACTIVITY);
    }


    //サブアクティビティからデータの受取
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle dataBundle = data.getExtras();
        if (requestCode == SUB_ACTIVITY && dataBundle.getString("put.StrData") != null) {
            if (resultCode == RESULT_OK) {
                this.myLoadText(dataBundle.getString("put.StrData"));
            }
            if(resultCode == RESULT_CANCELED){
                myTextViewLines.setText("");
                this.setTitle(R.string.app_name);
                myTextViewMain.setText("ファイルを選んでください");
            }
        }
    }


    //ファイルの読み出し,タイトルのセット,ファイルタイプの結果の受け取り
    private void myLoadText(String setText){
        ArrayList<String> fileStr;
        int textLinesLen = 0;
        int fileTypeNum = 0;
        this.clearTextView("","");
        this.setTitle(setText);
        fileTypeNum = this.checkFileType(setText);
        fileStr = myFileOpen.fileLoad(setText);
        textLinesLen = myFileOpen.getLines();
        this.myChangeText(fileStr,textLinesLen,fileTypeNum);

    }


    //文字色の置き換え
    private void myChangeText(ArrayList<String> fileStr, int textLinesLen, int fileTypeNum){
        myFileTypeToCpp = new FileTypeToCpp(this);
        switch (fileTypeNum){
            case 0://txt
                for(int i=0; i<fileStr.size(); i++){
                    myTextViewMain.append(fileStr.get(i));
                }
                for(int i=1; i<=textLinesLen; i++){
                    myTextViewLines.append(String.valueOf(i) + "\n");
                }
                break;
            case 1://c
                myFileTypeToCpp.execute(fileStr);
                for(int i=1; i<=textLinesLen; i++){
                    myTextViewLines.append(String.valueOf(i) + "\n");
                }
                break;
            case 2://cpp
                //this.myTextSets(myFileTypeToCpp.textSets(fileStr));
                myFileTypeToCpp.execute(fileStr);
                for(int i=1; i<=textLinesLen; i++){
                    myTextViewLines.append(String.valueOf(i) + "\n");
                }
                break;
        }

    }



    //テキストビューのクリア
    private void clearTextView(String newTitle,String oldTitle){
        //そのうち実装
        //今のタイトルと次に読み込もうとしたファイルのタイトルが同じかどうかで判定
        myTextViewMain.setText("");
        myTextViewLines.setText("");
    }


    //ファイルタイプのチェック
    private int checkFileType(String filePass){
        String[] FILEFORMAT = {".*\\.txt" , ".*\\.c" , ".*\\.cpp"};
        int fileTypeNum=0;
        for(int i=0; i<FILEFORMAT.length; i++) {
            Pattern myPattern = Pattern.compile(FILEFORMAT[i], Pattern.MULTILINE);
            Matcher myMatcher = myPattern.matcher(filePass);
            if(myMatcher.find()){
                fileTypeNum = i;
            }
        }
        return fileTypeNum;
    }




    //戻るボタンを押された際に強制終了しないようにする
    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        else {
            return false;
        }
        return false;
    }


    //メニュー関連
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                break;
            case R.id.settings_open:
                this.moveActivity();
                break;
            case R.id.settings_search:
                break;
            case R.id.settings_exit:
                finish();
                break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
//テキストファイルのセット
    /*private void myTextSet(String setTitle, String setText){
        String fileStr = null;
        int textLinesLen = 0;
        myTextViewMain = (TextView)findViewById(R.id.myTextViewMain);
        myTextViewLines = (TextView)findViewById(R.id.myTextViewLines);
        myFileOpen = new MyFileOpen();


        this.setTitle(setTitle);
        fileStr = myFileOpen.fileLoad(setText);
        myTextViewMain.setText(fileStr);
        textLinesLen = myFileOpen.getLines();
        for(int i=1; i<=textLinesLen; i++){
            myTextViewLines.append(String.valueOf(i) + "\n");
        }
    }*/