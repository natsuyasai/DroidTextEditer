package com.nyasai.droidtextediter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class MyFileOpen extends Activity{
    private Intent intent;
    private MainActivity main_activity;

    public void moveActivity(){
        main_activity = new MainActivity();
        intent = new Intent(main_activity,FileActivity.class);
        startActivity(intent);
    }

}