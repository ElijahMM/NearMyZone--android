package com.licenta.nearmyzone.Activities;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.licenta.nearmyzone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);
        ButterKnife.bind(this);
        resetErrorFields();
    }

    @BindView(R.id.profile_activity_username_text)
    EditText userNameEditText;
    @BindView(R.id.profile_activity_oldpassword_text)
    EditText oldpasswordEditText;
    @BindView(R.id.profile_activity_new_password_text)
    EditText newpasswordEditText;
    @BindView(R.id.profile_activity_confirmnewpassword_text)
    EditText confirmpasswordEditText;
    @BindView(R.id.profile_activity_distance_text)
    EditText distanceEditText;


    @BindView(R.id.profile_activity_tinputUsername)
    TextInputLayout usernameLayout;
    @BindView(R.id.profile_activity_tinputOldPassword)
    TextInputLayout oldpasswordLayout;
    @BindView(R.id.profile_activity_tinputNewPassword)
    TextInputLayout newpasswordLayout;
    @BindView(R.id.profile_activity_tinputConfirmNewPassword)
    TextInputLayout confirmnewpasswordLayout;
    @BindView(R.id.profile_activity_tinputDistance)
    TextInputLayout distanceLayout;


    public void validateAndNext() {
        if (userNameEditText.getText().toString().isEmpty()) {
            usernameLayout.setError("Username must not be empty");
            return;
        }
        if (oldpasswordEditText.getText().toString().isEmpty()) {
            oldpasswordLayout.setError("Type your old password");
            return;
        }
        if (newpasswordEditText.getText().toString().isEmpty()) {
            newpasswordLayout.setError("New password must not be empty");
            return;
        }

        if (confirmpasswordEditText.getText().toString().isEmpty()) {
            confirmnewpasswordLayout.setError(" New password must not be empty");
            return;
        }
        if (!newpasswordEditText.getText().toString().equals(confirmpasswordEditText.getText().toString()))
        {
            confirmnewpasswordLayout.setError("Passwords did not match");
            return;
        }

        if (distanceEditText.getText().toString().isEmpty()) {
            distanceLayout.setError("No distance set");
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

        oldpasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oldpasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newpasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newpasswordLayout.setError(null);
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
                confirmnewpasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        distanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              distanceLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


        @OnClick({R.id.profile_activity_login_button})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_activity_login_button:
                    validateAndNext();
                    break;
            }
        }

    }

