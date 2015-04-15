package com.nyasai.droidtextediter;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.ContentUris;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.Contacts.ContactMethods;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileActivity extends ListActivity {

    private enum DISPLAYMODE{ ABSOLUTE,RELATIVE; }
    private final DISPLAYMODE displayMode = DISPLAYMODE.ABSOLUTE;
    private ArrayList<String> directoryEntries = new ArrayList<String>();
    private File currentDirectory = new File("/");
    private final String FILEFORMAT = ".*\\.txt|.*\\.cpp|.*\\.c" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        browseToRoot();
    }

    private void browseToRoot() {
        browseTo(new File("/"));
    }

    private void upOneLevel(){
        if(this.currentDirectory.getParent() != null) {
            this.browseTo(this.currentDirectory.getParentFile());
        }
    }

    private void browseTo(final File aDirectory) {
        Pattern myPattern = Pattern.compile(FILEFORMAT,Pattern.MULTILINE);
        Matcher myMatcher = myPattern.matcher(aDirectory.getName());
        String fileName = aDirectory.getName();
        Boolean fileFlag = myMatcher.find();

        if(aDirectory.isDirectory()){
            this.currentDirectory = aDirectory;
            fill(aDirectory.listFiles());
        }
        //指定したファイルがサポートしているとき
        else if(fileFlag){
            Intent intent = new Intent();
            Bundle dataBundle = new Bundle();
            dataBundle.putString("put.StrData", aDirectory.getPath());
            intent.putExtras(dataBundle);
            Log.d("log2", aDirectory.getPath());
            setResult(RESULT_OK, intent);
            finish();
        }
        else{
            OnClickListener okButtonListener = new OnClickListener(){
                // @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW,
                                      Uri.parse("file://" + aDirectory.getAbsolutePath()));
                    startActivity(myIntent);
                }
            };
            OnClickListener cancelButtonListener = new OnClickListener(){
                // @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // Do nothing
                }
            };
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("This file is not support\n"+ aDirectory.getName())
                    /*.setPositiveButton("OK", okButtonListener)*/
                    .setNegativeButton("Cancel", cancelButtonListener)
                    .show();
        }
    }



    private void fill(File[] files) {
        Pattern myPattern = Pattern.compile(FILEFORMAT,Pattern.MULTILINE);

        this.directoryEntries.clear();
        // Add the "." and the ".." == 'Up one level'
        try {
            Thread.sleep(10);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        this.directoryEntries.add(getString(R.string.current_dir));

        if(this.currentDirectory.getParent() != null)
            this.directoryEntries.add(getString(R.string.up_one_level));

        switch(this.displayMode){
            case ABSOLUTE:
                if(files == null){break;}
                for (File file : files){
                    this.directoryEntries.add(file.getPath());
                }
                break;
            case RELATIVE: // On relative Mode, we have to add the current-path to the beginning
                if(files == null){break;}
                int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
                for (File file : files){
                    this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght));
                }
                break;
        }

        /*for(int i=0; i<directoryEntries.size(); i++){
            if((!myPattern.matcher(directoryEntries.get(i)).find())){ directoryEntries.remove(i); }
        }*/

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                R.layout.my_text_view, this.directoryEntries);

        this.setListAdapter(directoryList);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d("debug2", String.valueOf(getListView().getAdapter().getItem(position)));
        //int selectionRowID = (int) this.getSelectedItemId();
        //String selectedFileString = this.directoryEntries.get(selectionRowID);
        String selectedFileString = String.valueOf(getListView().getAdapter().getItem(position));
        if (selectedFileString.equals(".")) {
            // Refresh
            this.browseTo(this.currentDirectory);
        } else if(selectedFileString.equals("..")){
            this.upOneLevel();
        } else {
            File clickedFile = null;
            switch(this.displayMode){
                case RELATIVE:
                    clickedFile = new File(this.currentDirectory.getAbsolutePath() + selectedFileString);
                            /*+ this.directoryEntries.get(selectionRowID));*/
                    break;
                case ABSOLUTE:
                    //clickedFile = new File(this.directoryEntries.get(selectionRowID));
                    clickedFile = new File(selectedFileString);
                    break;
            }
            if(clickedFile != null) {this.browseTo(clickedFile);}
        }
    }

}
