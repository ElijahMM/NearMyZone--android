package com.licenta.nearmyzone.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.licenta.nearmyzone.CustomView.LoadingDialog;
import com.licenta.nearmyzone.Handlers.UploadPhoto;
import com.licenta.nearmyzone.Models.User;
import com.licenta.nearmyzone.R;
import com.licenta.nearmyzone.Utils.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_activity_imageUser)
    ImageView userImage;
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

    private FirebaseAuth auth;
    private UploadPhoto uploadPhoto;
    private Boolean isGallery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        resetErrorFields();
        auth = FirebaseAuth.getInstance();
        uploadPhoto = new UploadPhoto();
        uploadPhoto.setContext(RegisterActivity.this);
        uploadPhoto.clearPhotoDir();


    }

    private void registerUser(String email, String password) {
        final LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this,"Creating user");
        loadingDialog.showLoadingDialog();
        auth.createUserWithEmailAndPassword(email, Util.sha1Hash(password))
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final FirebaseUser firebaseUser = task.getResult().getUser();
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            final DatabaseReference dref = FirebaseDatabase.getInstance().getReference("users");
                            File file = new File(UploadPhoto.getPicturePathEdited());
                            StorageReference sRef = FirebaseStorage.getInstance().getReference("profilePicture/" + firebaseUser.getUid());
                            try {
                                InputStream stream = new FileInputStream(file);
                                UploadTask uploadTask = sRef.putStream(stream);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                       Log.w("UploadPhoto","Failure");
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        User.getInstance().getUserModel().setUserPhotoUrl(downloadUrl.toString());
                                        dref.child(firebaseUser.getUid()).setValue(User.getInstance().getUserModel());
                                        loadingDialog.dismissLoadingDialog();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                        Log.w("UploadPhoto","Success");
                                    }
                                });
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
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
        if (!Util.isValidEmail(mailEditText.getText().toString())) {
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
        if (!passwordEditText.getText().toString().equals(confirmpasswordEditText.getText().toString())) {
            confirmpasswordLayout.setError("Passwords did not match");
            return;
        }
        User.getInstance().getUserModel().setEmail(mailEditText.getText().toString());
        User.getInstance().getUserModel().setUsername(userNameEditText.getText().toString());
        User.getInstance().getUserModel().setPassword(passwordEditText.getText().toString());
        User.getInstance().getUserModel().setDistance(20);
        registerUser(mailEditText.getText().toString(), passwordEditText.getText().toString());


    }

    private void handleNewPhoto() {
        /** save image file to bitmap*/
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(UploadPhoto.getPicturePath(), options);

        /** if bitmap to big we scale it down to have max 1024 width or height*/
        final int REQUIRED_SIZE = 1024;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap = BitmapFactory.decodeFile(UploadPhoto.getPicturePath(), options);
        int width = scaledBitmap.getWidth();
        int height = scaledBitmap.getHeight();
        if (height > width) {
            width = ((width * 1000) / height);
            height = 1000;
        } else {
            height = ((height * 1000) / width);
            width = 1000;
        }
        Bitmap thumbnail = Bitmap.createScaledBitmap(scaledBitmap, width, height, true);
        saveEditedBitmap(thumbnail);
    }


    private Bitmap rotatePicture(Bitmap scaledBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }

    private void saveEditedBitmap(Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(UploadPhoto.getPicturePathEdited());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            userImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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


    @OnClick({R.id.register_activity_login_button, R.id.register_activity_imageUser})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_activity_login_button:
                validateAndNext();
                break;
            case R.id.register_activity_imageUser:
                uploadPhoto.defaultSelectMethodDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            isGallery = false;
        else {
            isGallery = true;
        }
        if (uploadPhoto.handleOnActivityResult(requestCode, resultCode, data)) {
            handleNewPhoto();
        }
    }
}
