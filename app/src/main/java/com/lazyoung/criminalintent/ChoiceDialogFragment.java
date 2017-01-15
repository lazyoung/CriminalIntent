package com.lazyoung.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Date;

public class ChoiceDialogFragment extends DialogFragment{
    public static final String EXTRA_CHOICE = "com.lazyoung.criminalintent.choice";
    private int mChoice = 0;
    public static final int CHOICE_DATE = 1;
    public static final int CHOICE_TIME = 2;

    public static ChoiceDialogFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CHOICE, date);
        ChoiceDialogFragment fragment = new ChoiceDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_setRequest);
        builder.setPositiveButton(R.string.dialog_setDate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChoice = CHOICE_DATE;
                        sendResult(Activity.RESULT_OK);
                    }
                });
        builder.setNegativeButton(R.string.dialog_setTime, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mChoice = CHOICE_TIME;
                sendResult(Activity.RESULT_OK);
            }
        });
        builder.setNeutralButton(android.R.string.cancel, null);
        return builder.create();
    }

    private void sendResult(int resultCode) {
        if(getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_CHOICE, mChoice);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
