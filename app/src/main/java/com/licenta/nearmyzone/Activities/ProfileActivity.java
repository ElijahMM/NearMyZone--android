package com.licenta.nearmyzone.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.licenta.nearmyzone.CustomView.LoadingDialog;
import com.licenta.nearmyzone.Handlers.OfflineHandler;
import com.licenta.nearmyzone.Models.FireBaseUserModel;
import com.licenta.nearmyzone.Utils.Util;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.licenta.nearmyzone.Handlers.UploadPhoto;
import com.licenta.nearmyzone.Models.User;
import com.licenta.nearmyzone.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {


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

    @BindView(R.id.profile_activity_imageUser)
    ImageView userImage;

    private FirebaseAuth auth;
    private UploadPhoto uploadPhoto;
    private Boolean isGallery = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);
        ButterKnife.bind(this);
        resetErrorFields();
        populateView();
        auth = FirebaseAuth.getInstance();
        uploadPhoto = new UploadPhoto();
        uploadPhoto.setContext(ProfileActivity.this);
        uploadPhoto.clearPhotoDir();

    }


    public void populateView() {

        userNameEditText.setText(User.getInstance().getUserModel().getUsername());
        distanceEditText.setText(String.valueOf(User.getInstance().getUserModel().getDistance()));
        Glide.with(ProfileActivity.this).load(User.getInstance().getUserModel().getUserPhotoUrl()).into(userImage);
    }

    public void validateAndNext() {
        FireBaseUserModel fireBaseUserModel = new FireBaseUserModel();
        if (!userNameEditText.getText().toString().isEmpty() && !userNameEditText.getText().toString().equals(User.getInstance().getUserModel().getUsername())) {
            fireBaseUserModel.setUsername(userNameEditText.getText().toString());
        } else {
            fireBaseUserModel.setUsername(User.getInstance().getUserModel().getUsername());
        }
        if (oldpasswordEditText.getText().toString().isEmpty() && !newpasswordEditText.getText().toString().isEmpty()) {
            oldpasswordLayout.setError("Type your old password");
            return;
        }

        if (confirmpasswordEditText.getText().toString().isEmpty() && !newpasswordEditText.getText().toString().isEmpty()) {
            confirmnewpasswordLayout.setError(" New password must not be empty");
            return;
        }
        if (!oldpasswordEditText.getText().toString().isEmpty() && !newpasswordEditText.getText().toString().equals(confirmpasswordEditText.getText().toString())) {
            confirmnewpasswordLayout.setError("Passwords did not match");
            return;
        }

        if (distanceEditText.getText().toString().isEmpty()) {
            distanceLayout.setError("No distance set");
            return;
        }
        if (!oldpasswordEditText.getText().toString().equals(User.getInstance().getUserModel().getPassword())) {
            oldpasswordLayout.setError("Invalid password!");
        }
        FireBaseUserModel fUser = new FireBaseUserModel();
        // TODO: 08/25/2017 store new user and send it to server 
        updateFireBaseUser(Util.sha1Hash(newpasswordEditText.getText().toString()));

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


    @OnClick({R.id.profile_activity_login_button, R.id.profile_activity_imageUser})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_activity_login_button:
                validateAndNext();
                break;
            case R.id.profile_activity_imageUser:
                uploadPhoto.defaultSelectMethodDialog();
                break;
        }
    }

    public void updateFireBaseUser(final String password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(User.getInstance().getUserModel().getEmail(),
                        Util.sha1Hash(User.getInstance().getUserModel().getPassword()));
        final LoadingDialog loadingDialog = new LoadingDialog(ProfileActivity.this, "Saving...");
        loadingDialog.showLoadingDialog();
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(Util.sha1Hash(password)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Update password", "Password updated");
                                        File file = new File(UploadPhoto.getPicturePathEdited());
                                        final DatabaseReference dref = FirebaseDatabase.getInstance().getReference("users");
                                        final StorageReference sRef = FirebaseStorage.getInstance().getReference("profilePicture/" + user.getUid());
                                        try {
                                            InputStream stream = new FileInputStream(file);
                                            UploadTask uploadTask = sRef.putStream(stream);
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Log.w("UploadPhoto", "Failure");
                                                    loadingDialog.dismissLoadingDialog();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                    User.getInstance().getUserModel().setUserPhotoUrl(downloadUrl.toString());
                                                    dref.child(user.getUid()).setValue(User.getInstance().getUserModel());
                                                    loadingDialog.dismissLoadingDialog();
                                                    OfflineHandler.getInstance().deletePassword();
                                                    OfflineHandler.getInstance().storePassword(Util.sha1Hash(password));
                                                    Log.w("UploadPhoto", "Success");
                                                }
                                            });
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Log.d("Error", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d("Error", "Error auth failed");
                        }
                    }
                });

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

