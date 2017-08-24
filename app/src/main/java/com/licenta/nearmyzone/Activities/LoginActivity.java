package com.licenta.nearmyzone.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.licenta.nearmyzone.CustomView.LoadingDialog;
import com.licenta.nearmyzone.Handlers.OfflineHandler;
import com.licenta.nearmyzone.Handlers.PermissionHandler;

import com.licenta.nearmyzone.Models.FireBaseUserModel;
import com.licenta.nearmyzone.Models.User;
import com.licenta.nearmyzone.R;
import com.licenta.nearmyzone.Utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_activity_email_text)
    EditText emialEditText;
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
        if (emialEditText.getText().toString().isEmpty()) {
            usernameLayout.setError("Email must not be empty");
            return;
        }
        if (!Util.isValidEmail(emialEditText.getText().toString())) {
            usernameLayout.setError("Email is invalid");
            return;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLayout.setError("Password must not be empty");
            return;
        }
        firebaseLogin(emialEditText.getText().toString(), passwordEditText.getText().toString());
    }

    private void firebaseLogin(String email, String password) {
        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this, "Logging in..");
        loadingDialog.showLoadingDialog();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email,
                Util.sha1Hash(password))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            DatabaseReference fDB = FirebaseDatabase.getInstance().getReference().child("users");
                            Log.d("Uid", task.getResult().getUser().getUid());
                            Query q = fDB.orderByKey().equalTo(task.getResult().getUser().getUid());
                            q.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            FireBaseUserModel fireBaseUserModel = dataSnapshot1.getValue(FireBaseUserModel.class);
                                            User.getInstance().setUserModel(fireBaseUserModel);
                                            OfflineHandler.getInstance().storeEmail(emialEditText.getText().toString());
                                            OfflineHandler.getInstance().storePassword(passwordEditText.getText().toString());
                                            loadingDialog.dismissLoadingDialog();
                                            Util.openActivityClosingParent(LoginActivity.this, MainActivity.class);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } catch (Exception ex) {
                            Util.showShortToast(LoginActivity.this, "Invalid email or password");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void resetErrorFields() {
        emialEditText.addTextChangedListener(new TextWatcher() {
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

                if (!PermissionHandler.getInstance().checkPermissions(LoginActivity.this)) {
                    return;
                }
                validateAndNext();
                break;
            case R.id.login_activity_register_button:
                if (!PermissionHandler.getInstance().checkPermissions(LoginActivity.this)) {
                    return;
                }
                Util.openActivity(LoginActivity.this, RegisterActivity.class);
                break;
        }
    }

}
