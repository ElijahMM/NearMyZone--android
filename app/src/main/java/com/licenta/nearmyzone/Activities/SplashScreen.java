package com.licenta.nearmyzone.Activities;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
import com.licenta.nearmyzone.Models.FireBaseUserModel;
import com.licenta.nearmyzone.Models.User;
import com.licenta.nearmyzone.R;
import com.licenta.nearmyzone.Utils.Util;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (OfflineHandler.getInstance().isEmailStored() && OfflineHandler.getInstance().isPasswordStored()) {
                    firebaseLogin(
                            OfflineHandler.getInstance().restoreEmail(),
                            OfflineHandler.getInstance().restorePassword()
                    );
                }else {
                    Util.openActivityClosingStack(SplashScreen.this, LoginActivity.class);
                }
            }
        }, 2000);
    }
    private void firebaseLogin(String email, String password) {
        final LoadingDialog loadingDialog = new LoadingDialog(SplashScreen.this, "Logging in..");
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
                                            loadingDialog.dismissLoadingDialog();
                                            Util.openActivityClosingParent(SplashScreen.this, MainActivity.class);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } catch (Exception ex) {
                            Util.showShortToast(SplashScreen.this, "Invalid email or password");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
