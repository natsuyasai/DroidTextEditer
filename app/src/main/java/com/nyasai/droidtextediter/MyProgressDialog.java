package com.nyasai.droidtextediter;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.io.Serializable;

public class MyProgressDialog extends DialogFragment {
    private static ProgressDialog progressDialog = null;
    private CancelListener listener;

    public static MyProgressDialog newInstance(String title, String message, Boolean cancel, Serializable Cancel_Listener) {
        MyProgressDialog instance = new MyProgressDialog();

        // ダイアログにパラメータを渡す
        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        arguments.putString("message", message);
        arguments.putBoolean("cancel", cancel);
        if (cancel) // キャンセルの場合
            arguments.putSerializable("Cancel_Listener", Cancel_Listener);

        instance.setArguments(arguments);

        return instance;
    }

    // キャンセルリスナ
    public interface CancelListener extends Serializable {
        public void canceled(DialogInterface _interface);
    }

    // ProgressDialog作成
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (progressDialog != null)
            return progressDialog;

        // パラメータを取得
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        Boolean cancel = getArguments().getBoolean("cancel", false);
        // リスナーを設定
        listener = (CancelListener) getArguments().getSerializable("Cancel_Listener");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // プログレスダイアログのスタイルを円スタイルに設定
        // プログレスダイアログのキャンセルが可能かどうかを設定（バックボタンでダイアログをキャンセルできないようにする）
        setCancelable(cancel);

        if (cancel) { // キャンセルボタンありの場合はボタンを追加します。
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.canceled(dialog);
                }
            });
        }

        return progressDialog;
    }

    // progressDialog取得
    @Override
    public Dialog getDialog() {
        return progressDialog;
    }

    // ProgressDialog破棄
    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog = null;
    }

    // キャンセルのときに、リスナーに通知します。
    @Override
    public void onCancel(DialogInterface dialog) {
        if (getArguments().getBoolean("cancel", false)) {
            listener.canceled(dialog);
        }
    }
}