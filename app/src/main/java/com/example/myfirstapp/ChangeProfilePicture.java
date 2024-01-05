package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ChangeProfilePicture extends AppCompatActivity {
    private ImageButton returnButton;
    private ImageView picture;
    private Button presetAvatar;
    private Button fromCamera;
    private Button refresh;
    private Uri imageUri;
    public static final int TAKE_PHOTO=1;
    private static final int PHOTO_REQUEST_GALLERY = 2;

    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);
        picture = (ImageView) findViewById(R.id.userAvatar);

        storageRef = FirebaseStorage.getInstance().getReference();

        downloadDefaultImage();

        //Return button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //button: setting picture from preset avatar
        presetAvatar = findViewById(R.id.buttonFromPreset);
        presetAvatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(ChangeProfilePicture.this, PictureFromPreset.class);
                startActivity(it);
            }
        });

        //button: take picture from camera
        fromCamera = findViewById(R.id.buttonFromCamera);
        fromCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(Build.VERSION.SDK_INT>=24)
                {
                    imageUri= FileProvider.getUriForFile(ChangeProfilePicture.this,
                            "com.mydomain.fileprovider",outputImage);
                }
                else {
                    imageUri=Uri.fromFile(outputImage);
                }

                Intent it=new Intent("android.media.action.IMAGE_CAPTURE");

                it.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(it,TAKE_PHOTO);
            }
        });

        //button, choose picture from album
        Button chooseFromAlbum = (Button) findViewById(R.id.buttonForAlbum);
        chooseFromAlbum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent(Intent.ACTION_PICK);
                it.setType("image/*");
                startActivityForResult(it, PHOTO_REQUEST_GALLERY);
            }
        });




        //Refresh button
        refresh = findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO) {
            try {
                File path = new File(getExternalCacheDir(), "output_image.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                bitmap = rotatingImageView(readPictureDegree(String.valueOf(path)), bitmap);
                picture.setImageBitmap(bitmap);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
                uploadImage(picture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == PHOTO_REQUEST_GALLERY){
            if(data != null){
                Uri uri = data.getData();
                picture.setImageURI(uri);
                uploadImage(picture);
            }
        }
    }

    public void downloadDefaultImage(){
        StorageReference imageRef;

        imageRef = storageRef.child("UserAvatar/avatar_1.png");
        long MAXBYTES = 1024*1024;

        imageRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                picture.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void downloadImage(){
        StorageReference imageRef;
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        imageRef = storageRef.child("UserAvatar/" + userUid);
        long MAXBYTES = 10240*1024;

        imageRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                picture.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void uploadImage(ImageView pic){
        pic.setDrawingCacheEnabled(true);
        pic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) pic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference userAvatarRef = FirebaseStorage.getInstance().getReference().child("UserAvatar/" + userUid);

        UploadTask uploadTask = userAvatarRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public Bitmap rotatingImageView(int angle , Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}