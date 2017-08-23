package com.licenta.nearmyzone.Activities;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.licenta.nearmyzone.R;
import com.licenta.nearmyzone.Utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.register_activity_username_text)
    EditText userNameEditText;
    @BindView(R.id.register_activity_mail_text)
    EditText mailEditText;
    @BindView(R.id.register_activity_password_text)
    EditText passwordEditText;
    @BindView(R.id.register_activity_confirmpassword_text)
    EditText confirmpasswordEditText;

    @BindView(R.id.register_activity_tinputUsername)
    TextInputLayout usernameLayout;
    @BindView(R.id.register_activity_tinputMail)
    TextInputLayout mailLayout;
    @BindView(R.id.register_activity_tinputPassword)
    TextInputLayout passwordLayout;
    @BindView(R.id.register_activity_tinputConfirmPassword)
    TextInputLayout confirmpasswordLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        resetErrorFields();
    }



    public void validateAndNext() {
        if (userNameEditText.getText().toString().isEmpty()) {
            usernameLayout.setError("Username must not be empty");
            return;
        }
        if (mailEditText.getText().toString().isEmpty()) {
            mailLayout.setError("E-Mail must not be empty");
            return;

        }
        if (!Util.isValidEmail(mailEditText.getText().toString())){
            mailLayout.setError("Invalid E-Mail");
            return;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLayout.setError("Password must not be empty");
            return;
        }

        if (confirmpasswordEditText.getText().toString().isEmpty()) {
            confirmpasswordLayout.setError("Password must not be empty");
            return;
        }
        if (!passwordEditText.getText().toString().equals(confirmpasswordEditText.getText().toString()))
        {
            confirmpasswordLayout.setError("Passwords did not match");
            return;
        }


    }

    public void resetErrorFields() {
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmpasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmpasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.register_activity_login_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_activity_login_button:
                validateAndNext();
                break;
        }
    }

}




