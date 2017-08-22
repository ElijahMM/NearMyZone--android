package com.licenta.nearmyzone.Activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.licenta.nearmyzone.Handlers.PermissionHandler;
import com.licenta.nearmyzone.R;
import com.licenta.nearmyzone.Utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_activity_username_text)
    EditText userNameEditText;
    @BindView(R.id.login_activity_password_text)
    EditText passwordEditText;

    @BindView(R.id.login_activity_tinputUsername)
    TextInputLayout usernameLayout;
    @BindView(R.id.login_activity_tinputPassword)
    TextInputLayout passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        resetErrorFields();
    }


    public void validateAndNext() {
        if (userNameEditText.getText().toString().isEmpty()) {
            usernameLayout.setError("Username must not be empty");
            return;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLayout.setError("Password must not be empty");
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
    }

    @OnClick({R.id.login_activity_login_button, R.id.login_activity_register_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_activity_login_button:
//                validateAndNext();
                if (!PermissionHandler.getInstance().checkPermissions(LoginActivity.this)) {
                    return;
                }
                Util.openActivity(LoginActivity.this, MainActivity.class);
                break;
            case R.id.login_activity_register_button:
                Util.openActivity(LoginActivity.this, RegisterActivity.class);
                break;
        }
    }

}
