package com.development.security.ciphernote.security;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.development.security.ciphernote.ListActivity;
import com.development.security.ciphernote.MainActivity;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.SecurityQuestion;
import com.development.security.ciphernote.model.UserConfiguration;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ConfigureSecurityQuestionsActivity extends AppCompatActivity {
    Context applicationContext = null;
    WebView browser = null;
    boolean changeMadeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_security_questions);

        applicationContext = this;

        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new ConfigureSecurityQuestionsActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/securityQuestionConfigure.html");

        ImageView deleteButton = (ImageView) findViewById(R.id.cancelButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeMadeFlag) {
                    new android.app.AlertDialog.Builder(applicationContext)
                            .setTitle("Cancel changes?")
                            .setMessage("Are you sure you want to throw away any changes?")
                            .setIcon(android.R.drawable.ic_delete)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }else{
                    finish();
                }
            }
        });

    }

    private void setSecurityQuestion(String password, String securityQuestionResponse) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidParameterSpecException, JSONException, AuthenticatorException {
        DatabaseManager databaseManager = new DatabaseManager(applicationContext);
        UserConfiguration userConfiguration = databaseManager.getUserConfiguration();

        SecurityManager securityManager = new SecurityManager();
        userConfiguration = securityManager.setSecurityQuestion(userConfiguration, applicationContext, password, question, securityQuestionResponse);
        databaseManager.updateUserConfiguration(userConfiguration);
    }

    protected String userPassword = "";
    protected String response = "";
    protected String question = "";

    private class AsyncSetSecurityQuestion extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                setSecurityQuestion(userPassword, response);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            if (status) {
                Log.d("status", "good");
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:wrapUp()");
                    }
                });
                CharSequence successToast = "Security questions setup successfully!";

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();
            } else {
                Log.d("status", "bad");
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:clearInputs()");
                    }
                });
                CharSequence failedToast = "Incorrect password!";

                Toast toast = Toast.makeText(applicationContext, failedToast, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }


    @Override
    public void onBackPressed() {
        if (changeMadeFlag) {
            new android.app.AlertDialog.Builder(applicationContext)
                    .setTitle("Cancel changes?")
                    .setMessage("Are you sure you want to throw away any changes?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }else{
            finish();
        }
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }


        @JavascriptInterface
        public void setSecurityQuestion(String password, String securityQuestion, String securityQuestionResponse) {
            userPassword = password;
            response = securityQuestionResponse;
            question = securityQuestion;
            new AsyncSetSecurityQuestion().execute();
        }

        @JavascriptInterface
        public void goHome() {
            Intent listActivityIntent = new Intent(applicationContext, ListActivity.class);
            startActivity(listActivityIntent);

            finish();
        }

        @JavascriptInterface
        public void keyPressOccured() {
            changeMadeFlag = true;
        }

        @JavascriptInterface
        public boolean checkQuestions() {
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);
            List<SecurityQuestion> securityQuestions = databaseManager.getAllSecurityQuestions();
            if (securityQuestions.size() > 0) {
                return true;
            }
            return false;
        }
    }
}