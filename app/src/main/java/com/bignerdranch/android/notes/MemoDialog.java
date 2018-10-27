package com.bignerdranch.android.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class MemoDialog extends DialogFragment {

    public static final String MEMO_TEXT = "text";
    public static final String MEMO_ID = "id";
    public static final String MEMO_UPDATE_MODE = "update";

    private EditText editTextMemo;
    private MemoDialogListener listener;
    private long id;
    private String text;
    private boolean updateMode;

    public static MemoDialog newInstance(String text, long id, boolean updateMode) {
        Bundle args = new Bundle();
        args.putSerializable(MEMO_ID, id);
        args.putSerializable(MEMO_TEXT, text);
        args.putSerializable(MEMO_UPDATE_MODE, updateMode);
        MemoDialog fragment = new MemoDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        id = (long) getArguments().getSerializable(MEMO_ID);
        text = (String) getArguments().getSerializable(MEMO_TEXT);
        updateMode = (boolean) getArguments().getSerializable(MEMO_UPDATE_MODE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        editTextMemo = view.findViewById(R.id.edit_memo);
        editTextMemo.setText(text);

        builder.setView(view)
                .setTitle("Memo")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String memo = editTextMemo.getText().toString();
                        if(updateMode){
                            listener.updateText(memo, id);
                        } else{
                            listener.applyText(memo);
                        }
                    }
                });



        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (MemoDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MemoDialogListener");
        }
    }

    public interface MemoDialogListener{
        void applyText(String s);
        void updateText(String s, long id);
    }

}
