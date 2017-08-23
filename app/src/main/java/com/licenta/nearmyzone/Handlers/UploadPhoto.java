package com.licenta.nearmyzone.Handlers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.licenta.nearmyzone.AppDelegate;
import com.licenta.nearmyzone.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by Morgenstern on 08/23/2017.
 */

public class UploadPhoto {

    public final static int SELECT_CAMERA = 0;
    public final static int SELECT_GALLERY = 1;

    private static Context ctx;
    private static String photoPath, photoPathEdited;


    public void setContext(Context _ctx) {
        ctx = _ctx;
        photoPath = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "photo.jpg";
        photoPathEdited = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "photoEdited.jpg";
    }

    /**
     * Delete the Files from the Photos folder
     */
    public void clearPhotoDir() {
        new File(photoPath).delete();
        new File(photoPathEdited).delete();
    }

    public static String getPicturePath() {
        return photoPath;
    }

    public static String getPicturePathEdited() {
        return photoPathEdited;
    }

    public static boolean isPictureEditedCreated() {
        return new File(photoPathEdited).exists();
    }


    public UploadPhoto() {
    }
    // endregion

    public void selectCamera() {
        File tempPictureFile = new File(photoPath);
        Uri tempPictureURI = Uri.fromFile(tempPictureFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPictureURI);
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            ((Activity) ctx).startActivityForResult(intent, SELECT_CAMERA);
        }
    }

    @TargetApi(24)
    public void setSelectCameraApi24() {
        File tempPictureFile = new File(photoPath);
        Uri tempPictureURI = FileProvider.getUriForFile(AppDelegate.getMyContext(), AppDelegate.getMyContext().getApplicationContext().getPackageName() + ".provider", tempPictureFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPictureURI);
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            ((Activity) ctx).startActivityForResult(intent, SELECT_CAMERA);
        }
    }

    public void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        ((Activity) ctx).startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_GALLERY);
    }

    /**
     * Default function for selecting picture input mode
     */
    public void defaultSelectMethodDialog() {
        final CharSequence[] items = {"Take Photo",
                "Choose from Library",
                "Cancel"};
        // "View Photo"

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    setSelectCameraApi24();
                }
                if (items[item].equals("Choose from Library")) {
                    selectGallery();
                }
                if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                    ((Activity) ctx).finish();
                }
                if (items[item].equals("View photo")) {

                }
            }
        });
        builder.show();
    }


    public void selectGalleryResult(Intent data) {
        if (data != null) {
            Uri selectedImageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};
            final Cursor cursor = ctx.getContentResolver().query(selectedImageUri, projection, null, null,
                    null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);

            /** gets the file path from our selected gallery image and copy it to a set destination*/
            try {
                copy(new File(selectedImagePath), new File(photoPath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            cursor.close();
        }
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    /**
     * handles the default result and sets the picture to a specified path
     */
    public boolean handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return false;
        }

        if (requestCode == UploadPhoto.SELECT_CAMERA) {
            return true;
        }

        if (requestCode == UploadPhoto.SELECT_GALLERY) {
            selectGalleryResult(data);
            return true;
        }

        return true;
    }

}