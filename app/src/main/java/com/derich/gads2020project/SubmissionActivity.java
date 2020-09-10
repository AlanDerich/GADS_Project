package com.derich.gads2020project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.derich.gads2020project.helper.DialogFragmentsInterface;
import com.derich.gads2020project.helper.SubmissionDialog;
import com.derich.gads2020project.helper.SubmitProjectRunnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubmissionActivity extends AppCompatActivity implements
        View.OnClickListener,Handler.Callback, DialogFragmentsInterface, View.OnFocusChangeListener {

    private final String FIRST_NAME = "com.derich.gads2020project.FIRST_NAME";
    private final String LAST_NAME = "com.derich.gads2020project.LAST_NAME";
    private final String EMAIL_ADDRESS = "com.derich.gads2020project.EMAIL_ADDRESS";
    private final String GITHUB_LINK = "com.derich.gads2020project.GITHUB_LINK";
    public static final String SUBMIT_ACTIVITY_THREAD = "SubmitActivityThread";
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmailAddress;
    private EditText etGitHubLink;
    private TextView tvErrorText;
    private ConstraintLayout mTextViewsConstraintLayout;
    private String mFirstName;
    private String mLastName;
    private String mEmailAddress;
    private String mGithubLink;
    private HandlerThread mHandlerThread;
    private SubmissionDialog mSubmissionDialog;
    private ProgressBar mProgressBar;
    private boolean isSaveInstanceBundleNull;
    private boolean canSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmailAddress = findViewById(R.id.et_email_address);
        etGitHubLink = findViewById(R.id.et_github_link);
        tvErrorText = findViewById(R.id.et_error_text_view);
        mTextViewsConstraintLayout = findViewById(R.id.submission_fields_constraint_layout);

        etFirstName.setOnFocusChangeListener(this);
        etLastName.setOnFocusChangeListener(this);
        etEmailAddress.setOnFocusChangeListener(this);
        etGitHubLink.setOnFocusChangeListener(this);

        Button submitButton = findViewById(R.id.submit_button);
        ImageView backButtton = findViewById(R.id.image_back_button);
        submitButton.setOnClickListener(this);
        backButtton.setOnClickListener(this);

        if (savedInstanceState != null){
            isSaveInstanceBundleNull = false;
            getSubmitValuesFromBundle(savedInstanceState);
        }
        else isSaveInstanceBundleNull = true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSubmitValuesToBundle(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getCurrentEditTextValues();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandlerThread = new HandlerThread(SUBMIT_ACTIVITY_THREAD);
        mHandlerThread.start();
        if (!isSaveInstanceBundleNull)
            initializeEditText();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandlerThread.quit();
    }

    public void saveSubmitValuesToBundle(Bundle bundle){
        bundle.putString(FIRST_NAME,mFirstName);
        bundle.putString(LAST_NAME,mLastName);
        bundle.putString(EMAIL_ADDRESS,mEmailAddress);
        bundle.putString(GITHUB_LINK,mGithubLink);
    }

    public void getSubmitValuesFromBundle(Bundle bundle){
        mFirstName = bundle.getString(FIRST_NAME);
        mLastName = bundle.getString(LAST_NAME);
        mEmailAddress = bundle.getString(EMAIL_ADDRESS);
        mGithubLink = bundle.getString(GITHUB_LINK);
    }


    private void initializeEditText() {
        if (mFirstName != null)
            etFirstName.setText(mFirstName);
        if (mLastName != null)
            etLastName.setText(mLastName);
        if (mEmailAddress  != null)
            etEmailAddress.setText(mEmailAddress);
        if (mGithubLink != null)
            etGitHubLink.setText(mGithubLink);
    }

    private void getCurrentEditTextValues(){
        mFirstName = etFirstName.getText().toString();
        mLastName = etLastName.getText().toString();
        mEmailAddress = etEmailAddress.getText().toString();
        mGithubLink = etGitHubLink.getText().toString();
    }

    private void clearEditTexts(){
        etFirstName.setText("");
        etLastName.setText("");
        etEmailAddress.setText("");
        etGitHubLink.setText("");
    }


    private void confirmSubmission(){
        beginFragmentTransaction(R.layout.submission_dialog);
    }
    private void beginFragmentTransaction(int layoutResource){
        mSubmissionDialog = new SubmissionDialog(layoutResource);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.submission_dialog_frame_layout,mSubmissionDialog);
        transaction.commit();
    }

    private void runRegex(String regex, String text, String errorMessage){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()){
            tvErrorText.setText("");
            canSubmit = true;
        }else{
            tvErrorText.setText(errorMessage);
            canSubmit = false;
        }
    }

    private void submitProject(){
        getCurrentEditTextValues();
        Handler myThreadHandler = new Handler(mHandlerThread.getLooper());
        Handler mainThreadHandler = new Handler(this);
        myThreadHandler.post(new SubmitProjectRunnable(
                mainThreadHandler,
                mFirstName,
                mLastName,
                mEmailAddress,
                mGithubLink));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back_button:
                onBackPressed();
                break;
            case R.id.submit_button:
                if (canSubmit) {
                    mTextViewsConstraintLayout.setVisibility(View.INVISIBLE);
                    confirmSubmission();
                }
                else{
                    Toast.makeText(this,"Please check all fields",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        final int PROJECT_SUBMISSION_SUCCESSFUL = 200;
        final int PROJECT_SUBMISSION_FAILED = 404;
        switch (msg.what){
            case PROJECT_SUBMISSION_SUCCESSFUL:
                mProgressBar.setVisibility(View.INVISIBLE);
                clearEditTexts();
                mSubmissionDialog.dismiss();
                beginFragmentTransaction(R.layout.successful_submission_dialog);
                break;
            case PROJECT_SUBMISSION_FAILED:
                mSubmissionDialog.dismiss();
                beginFragmentTransaction(R.layout.failed_submission_dialog);
                Toast.makeText(this,R.string.unable_to_submit_project,Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }


    @Override
    public void onNegativeButtonClicked(int clickedButton) {
        mTextViewsConstraintLayout.setVisibility(View.VISIBLE);
        mSubmissionDialog.dismiss();
    }

    @Override
    public void onPositiveButtonClicked(int clickedButton, ProgressBar progressBar) {
        submitProject();
        mProgressBar = progressBar;
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDialogDismissed(boolean wasButtonClicked) {
        if (!wasButtonClicked){
            mTextViewsConstraintLayout.setVisibility(View.VISIBLE);
            mSubmissionDialog.dismiss();
        }
    }

    private void hideNavigationBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
            return;
        switch (v.getId()){
            case R.id.et_email_address:
                final String EMAIL_REGEX = "^[a-zA-Z]([a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+)?@[a-zA-Z][a-zA-Z0-9.-]+$";
                runRegex(EMAIL_REGEX,etEmailAddress.getText().toString(),getString(R.string.edit_text_email_error_message));
                break;
            case R.id.et_github_link:
                //regex based on google form link validation
                final String LINK_REGEX = "^(https?:/{2}[w{3}.])(.+)[.](.+)$";
                runRegex(LINK_REGEX,etGitHubLink.getText().toString(),getString(R.string.edit_text_github_link_error_message));
                break;
            case R.id.et_first_name:
                String firstname = etFirstName.getText().toString().trim();
                if (!(firstname.isEmpty())) {
                    canSubmit = true;
                    return;
                }
                tvErrorText.setText(R.string.edit_text_first_name_error_message);
                canSubmit = false;
                break;
            case R.id.et_last_name:
                String lastname = etFirstName.getText().toString().trim();
                if (!(lastname.isEmpty())) {
                    canSubmit = true;
                    return;
                }
                tvErrorText.setText(R.string.edit_text_last_name_error_message);
                canSubmit = false;
                break;
        }
    }
}