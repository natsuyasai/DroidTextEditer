package com.nyasai.droidtextediter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Intent intent;
    private TextView myTextView;
    private MyFileOpen myFileOpen;
    private static final int SUB_ACTIVITY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //アクティビティの移動
    private void moveActivity(){
        intent = new Intent(this,FileActivity.class);
        startActivityForResult(intent, SUB_ACTIVITY);
        //startActivity(intent);
    }


    //サブアクティビティからデータの受取
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        myTextView = (TextView)findViewById(R.id.myTextViewMain);

        Bundle dataBundle = data.getExtras();
        if (requestCode == SUB_ACTIVITY && dataBundle.getString("put.StrData") != null) {
            if (resultCode == RESULT_OK) {
                this.myTextSet(dataBundle.getString("put.StrData"),dataBundle.getString("put.StrData"));
            }
        }
    }

    private void myTextSet(String setTitle, String setText){
        myFileOpen = new MyFileOpen();
        String fileStr = null;
        int textLines;

        textLines = myFileOpen.getLines();
        this.setTitle(setTitle);
        fileStr = myFileOpen.fileLoad(setText);
        myTextView.setText(fileStr);
    }

    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        else {
            return false;
        }
        return false;
    }

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
            case R.id.settings_exit:
                finish();
                break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
