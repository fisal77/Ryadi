package com.appz_world_hackathon.ryadi.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appz_world_hackathon.ryadi.AppUtil.mApp.AppSharedPreferences;
import com.appz_world_hackathon.ryadi.AppUtil.mApp.Session;
/*import com.fisal.coach.API.Project.APIUtil.APIError;
import com.fisal.coach.API.Project.APIUtil.ErrorUtils;
import com.fisal.coach.API.Project.ApiClient.APIClient;
import com.fisal.coach.API.Project.ApiInterface.APIInterface;
import com.fisal.coach.API.Response.apiUSER.REGISTER.RegisterResponse;*/

import com.appz_world_hackathon.ryadi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;*/

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A singUp screen that offers singUp via email/password.
 */
public class RegistrationActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    // Shared Preference to save user's singUp session
    AppSharedPreferences appSharedPreferences;
    Session session;

//    APIInterface apiInterface;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mNicknameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mRegisterFormView;
    private TextView mRegisteredAccountTxt;

    private void setLanguageForApp(String language) {
        String languageToLoad = language; //pass the language code as param
        Locale locale;
        if (languageToLoad == null) {
            locale = Locale.US;
        } else {
            locale = new Locale(languageToLoad);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setLanguageForApp(AppSharedPreferences.restoreLanguage(this));

/*        if (AppSharedPreferences.isThemeTypeGirl(this)) {
            setTheme(R.style.AppThemeFemale);
        } else {
            setTheme(R.style.AppTheme);
        }*/


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

/*
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setSubtitleTextColor(Color.BLACK);
*/

        // Set up the singUp form.
        mEmailView = findViewById(R.id.email);
        populateAutoComplete();

        mNicknameView = findViewById(R.id.nickname);

        mPasswordView = findViewById(R.id.password);

        mConfirmPasswordView = findViewById(R.id.confirm_password);
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

        Button mSignUpButton = findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mRegisteredAccountTxt = findViewById(R.id.registered_account_txt);
        mRegisteredAccountTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetSignInActivity();
            }
        });

        mRegisterFormView = findViewById(R.id.email_register_form);
        mProgressView = findViewById(R.id.register_progress);

        appSharedPreferences = new AppSharedPreferences(this);
        session = new Session(this);

    }

    // TODO Forget SignIn Info Activity
    private void forgetSignInActivity() {
/*        Intent intent = new Intent(RegistrationActivity.this, ForgetSignInInfoActivity.class);
        startActivity(intent);*/
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the singUp form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual singUp attempt is made.
     */
    private void attemptSignUp() {

        // check internet connection first
        if (session.isNetworkConnected(getApplication())) {

            // Reset errors.
            mEmailView.setError(null);
            mNicknameView.setError(null);
            mPasswordView.setError(null);
            mConfirmPasswordView.setError(null);

            // Store values at the time of the singUp attempt.
            String email = mEmailView.getText().toString().trim();
            String nickname = mNicknameView.getText().toString().trim();
            String password = mPasswordView.getText().toString().trim();
            String confirmPassword = mConfirmPasswordView.getText().toString().trim();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(password) /*&& !isPasswordValid(password)*/) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            } else if (!confirmPassword.equals(password)) {
                mPasswordView.setError(getString(R.string.error_not_match_password));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }

            // Check for a valid nickname.
            if (TextUtils.isEmpty(nickname)) {
                mNicknameView.setError(getString(R.string.error_field_required));
                focusView = mNicknameView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt singUp and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user singUp attempt.
                showProgress(true);
                UserSignUpTask(email, nickname, password);
            }

        } else if (!session.isNetworkConnected(getApplication())) {
            Toast.makeText(RegistrationActivity.this, getApplicationContext().getResources().getString(R.string.toastNoInternet),
                    Toast.LENGTH_LONG).show();
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the singUp form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), RegistrationActivity.ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(RegistrationActivity.ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegistrationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    /**
     * Represents an asynchronous singUp/registration task used to authenticate
     * the user.
     */
    public void UserSignUpTask(final String Email, final String Nickname, final String Password) {
        showProgress(true);
        Log.e("singUp", "singUp");

            // Reset errors.
            mEmailView.setError(null);
            mNicknameView.setError(null);
            mPasswordView.setError(null);
            mConfirmPasswordView.setError(null);

            boolean cancel = false;
            View focusView = null;

                appSharedPreferences.writeString("u_Id", Email);
                appSharedPreferences.writeString("u_Pass", Password);
                session.setLoggedin(true);
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                showProgress(false);

            if (cancel) {
                // There was an error; don't attempt singUp and focus the first
                // form field with an error.
                focusView.requestFocus();
            }

/*        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RegisterResponse> call3 = apiInterface.REGISTER_RESPONSE_CALL("" + Email, "" + Nickname, "" + Password);
        call3.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("singUp", "isSuccessful");
                    // Reset errors.
                    mEmailView.setError(null);
                    mNicknameView.setError(null);
                    mPasswordView.setError(null);
                    mConfirmPasswordView.setError(null);

                    boolean cancel = false;
                    View focusView = null;

                    if (response.body().getSuccess() == 1) {
                        appSharedPreferences.writeString("u_Id", Email);
                        appSharedPreferences.writeString("u_Pass", Password);
                        session.setLoggedin(true);
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        showProgress(false);
                        Log.e("singUp", "getSuccess()==1");
                    }  else if (response.body().getSuccess() == 0) {
                        Log.e("singUp", "getSuccess()==0");
                        Toast.makeText(RegistrationActivity.this, Email + " / " + Nickname + ": " + getString(R.string.error_profile_exists), Toast.LENGTH_LONG).show();
                        showProgress(false);
                        mEmailView.setError(getString(R.string.error_profile_exists));
                        focusView = mRegisteredAccountTxt;
                        cancel = true;
                    }  else if (response.body().getSuccess() == -1) {
                        Log.e("singUp", "getSuccess()==-1");
                        Toast.makeText(RegistrationActivity.this, Nickname + ": " + getString(R.string.error_duplicate_nickname), Toast.LENGTH_LONG).show();
                        showProgress(false);
                        mNicknameView.setError(getString(R.string.error_duplicate_nickname));
                        focusView = mNicknameView;
                        cancel = true;
                    } else {
                        showProgress(false);
                        APIError apiError = ErrorUtils.parseError(response);
                        Toast.makeText(RegistrationActivity.this, "Message : " + apiError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(RegistrationActivity.this, "Error : " + apiError.getError().toString(), Toast.LENGTH_SHORT).show();

                        Log.e("singUp", "apiError");
                    }

                    if (cancel) {
                        // There was an error; don't attempt singUp and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e("singUp", "onFailure");
                // Reset errors.
                mEmailView.setError(null);
                mNicknameView.setError(null);
                mPasswordView.setError(null);
                mConfirmPasswordView.setError(null);

                boolean cancel = false;
                View focusView = null;
                View focusView2 = null;

                    call.cancel();

                    showProgress(false);

                    Toast.makeText(RegistrationActivity.this, Nickname + ": " + getString(R.string.error_duplicate_nickname), Toast.LENGTH_LONG).show();
                    mNicknameView.setError(getString(R.string.error_duplicate_nickname));
                    focusView = mNicknameView;

                    Toast.makeText(RegistrationActivity.this, Email + " / " + Nickname + ": " + getString(R.string.error_profile_exists), Toast.LENGTH_LONG).show();
                    mEmailView.setError(getString(R.string.error_profile_exists));
                    focusView2 = mRegisteredAccountTxt;

                    cancel = true;


                if (cancel) {
                    // There was an error; don't attempt singUp and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                    focusView2.requestFocus();
                }

            }
        });*/


    }
}

