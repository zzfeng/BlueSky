package com.sundyn.bluesky.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sundyn.bluesky.R;

import static com.baidu.location.b.g.R;

public class RebackDialogFra extends DialogFragment {

    private String tag;
    private EditText et_mesaage;

    public interface RebackInputListener {
        void onComplete(String content, String tag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        tag = getTag();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_back, null);
        et_mesaage = (EditText) view.findViewById(R.id.et_message);
        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RebackInputListener listener = (RebackInputListener) getActivity();
                        String content = et_mesaage.getText().toString();
                        if (TextUtils.isEmpty(content)) {
                            return;
                        }
                        listener.onComplete(content, tag);
                    }
                }).setNegativeButton("取消", null);
        return builder.create();
    }
}
