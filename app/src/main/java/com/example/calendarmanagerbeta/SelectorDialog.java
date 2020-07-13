package com.example.calendarmanagerbeta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class SelectorDialog extends DialogFragment implements DialogInterface.OnClickListener {
    static final String TAG = "SelectorDialog";
    static int mResourceArray;
    static int mSelectedIndex;
    static OnDialogSelectorListener mDialogSelectorCallback;

    public interface OnDialogSelectorListener{
        void onSelectedOption(int dialogId);
    }

    public void setDialogSelectorListener(OnDialogSelectorListener listener){
        this.mDialogSelectorCallback = listener;
    }

    public static SelectorDialog newInstance(int res, int selected){
        final SelectorDialog dialog = new SelectorDialog();
        mResourceArray = res;
        mSelectedIndex = selected;

        return dialog;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("CANCEL", this);
        builder.setSingleChoiceItems(mResourceArray, mSelectedIndex, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which){
        switch(which){
            case Dialog.BUTTON_NEGATIVE:
                dialog.cancel();
                break;
            case Dialog.BUTTON_POSITIVE:
                dialog.dismiss();
                mDialogSelectorCallback.onSelectedOption(mSelectedIndex);
            default:
                mSelectedIndex = which;
                break;
        }
    }
}
