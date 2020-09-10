package com.derich.gads2020project.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.derich.gads2020project.R;

public class SubmissionDialog extends DialogFragment implements View.OnClickListener {

    private int layoutResource;

    public ProgressBar mProgressBar;
    public ImageView mNegativeButton;
    public Button mPositiveButton;
    private View mView;

    private DialogFragmentsInterface mDialogInterface;
    private boolean wasButtonClicked;

    public SubmissionDialog(int layoutResource) {
        this.layoutResource = layoutResource;
    }
    public SubmissionDialog (){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        /*WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.CENTER;*/

        View view = inflater.inflate(layoutResource, container,false);
        view.findViewById(R.id.dialog_background).setOnClickListener(this);
        //WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        //windowManager.addView(dialogBG,params);

        if (layoutResource == R.layout.submission_dialog) {
            mPositiveButton = view.findViewById(R.id.dialog_positive_button);
            mPositiveButton.setOnClickListener(this);
            mNegativeButton = view.findViewById(R.id.dialog_negative_button);
            mNegativeButton.setOnClickListener(this);
            mProgressBar = view.findViewById(R.id.submission_dialog_progress_bar2);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    private void hideNavigationBar(){
        View decorView = requireActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /*mView = LayoutInflater.from(getContext()).inflate(layoutResource,null);
        mView.findViewById(R.id.dialog_positive_button).setOnClickListener(this);
        mView.findViewById(R.id.dialog_negative_button).setOnClickListener(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setView(mView);*/
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mDialogInterface = (DialogFragmentsInterface) context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.dialog_positive_button:
                wasButtonClicked = true;
                mPositiveButton.setVisibility(View.INVISIBLE);
                mNegativeButton.setVisibility(View.INVISIBLE);
                mDialogInterface.onPositiveButtonClicked(R.id.dialog_positive_button, mProgressBar);
                break;
            case R.id.dialog_negative_button:
                wasButtonClicked = true;
                mDialogInterface.onNegativeButtonClicked(R.id.dialog_negative_button);
                break;
            case R.id.dialog_background:
                mDialogInterface.onDialogDismissed(wasButtonClicked);;
        }
    }
}
