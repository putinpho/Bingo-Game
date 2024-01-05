package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

public class PictureFromPreset extends AppCompatActivity {
    private Button chooseAvatar1;
    private Button chooseAvatar2;
    private Button chooseAvatar3;
    private Button chooseAvatar4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_from_preset);


        final ImageView picture1 = findViewById(R.id.Avatar1);
        final ImageView picture2 = findViewById(R.id.Avatar2);
        final ImageView picture3 = findViewById(R.id.Avatar3);
        final ImageView picture4 = findViewById(R.id.Avatar4);
        //Return button
        ImageButton returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //user choose preset_Avatar_1
        chooseAvatar1 = findViewById(R.id.chooseAvatarButton1);
        chooseAvatar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(picture1);
                finish();
            }
        });

        //user choose preset_Avatar_2
        chooseAvatar2 = findViewById(R.id.chooseAvatarButton2);
        chooseAvatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(picture2);
                finish();
            }
        });

        //user choose preset_Avatar_3
        chooseAvatar3 = findViewById(R.id.chooseAvatarButton3);
        chooseAvatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(picture3);
                finish();
            }
        });

        //user choose preset_Avatar_4
        chooseAvatar4 = findViewById(R.id.chooseAvatarButton4);
        chooseAvatar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(picture4);
                finish();
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
}