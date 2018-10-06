package com.appz_world_hackathon.ryadi.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appz_world_hackathon.ryadi.AppUtil.mApp.AppSharedPreferences;
import com.appz_world_hackathon.ryadi.R;

import java.util.Locale;

public class AuthActivity extends AppCompatActivity {

    Button btn_login ,btn_register, btnLanguage;

    String languageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

/*        if (AppSharedPreferences.isThemeTypeGirl(this)) {
            setTheme(R.style.AppThemeFemale);
        } else {
            setTheme(R.style.AppTheme);
        }*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

/*        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setSubtitleTextColor(Color.BLACK);*/

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthActivity.this,LoginActivity.class));
            }
        });
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthActivity.this,RegistrationActivity.class));
            }
        });

        btnLanguage = findViewById(R.id.btn_language);
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });

    }

    private void showLanguageDialog() {

        languageCode = AppSharedPreferences.restoreLanguage(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.choose_lang);
        RadioGroup radioGroup = dialog.findViewById(R.id.language_group);
        RadioButton rgEnglish = dialog.findViewById(R.id.rg_english);
        RadioButton rgArabic = dialog.findViewById(R.id.rg_arabic);

        if (languageCode != null && languageCode.equals("ar")) {
            rgArabic.setChecked(true);
        } else {
            rgEnglish.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if (checkedId == R.id.rg_english) {
                    Locale.setDefault(Locale.US);
                    languageCode = Locale.US.toString();
                } else {
                    languageCode = "ar";
                }

                AppSharedPreferences.saveLanguage(languageCode, AuthActivity.this);
                setLanguageForApp(languageCode);
                dialog.dismiss();
                recreate();
            }
        });

        dialog.show();
    }

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

}
