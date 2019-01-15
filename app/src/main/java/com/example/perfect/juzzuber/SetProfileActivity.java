/*package com.example.perfect.juzzuber;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.perfect.juzzuber.Model.ImgUri;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class SetProfileActivity extends AppCompatActivity {

    CircleImageView imageSetProfile;
    Uri resultUri = null;
    private final int PICK_IMAGE_REQUEST = 12;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        imageSetProfile = (CircleImageView) findViewById(R.id.imageSetProfile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public void next(View view){
        Toast.makeText(SetProfileActivity.this," "+resultUri,Toast.LENGTH_LONG);
        if(resultUri != null){
            uploadImage(view);

            Intent intent = new Intent(SetProfileActivity.this,MyCircleActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Snackbar.make(view,"Please select an Image",Snackbar.LENGTH_SHORT).show();
        }

    }

    private void uploadImage(final View view) {
        final android.app.AlertDialog waitingDialog = new SpotsDialog(SetProfileActivity.this);
        waitingDialog.show();

        StorageReference StorageRef = storageReference.child("User_profiles").child(resultUri.getLastPathSegment());
        String profilePicUrl = resultUri.getLastPathSegment();

        StorageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                final Uri imgUrl = taskSnapshot.getDownloadUrl();
                Snackbar.make(view, "Profile Picture Uploaded", Snackbar.LENGTH_SHORT).show();
                databaseReference.child("imageUrl").setValue(imgUrl.toString());
                waitingDialog.dismiss();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(view, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });

    //    ImgUri imgUri = new ImgUri(resultUri);
    }

    public void selectImage(View v){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null){
            Uri imgUri = data.getData();
            CropImage.activity(imgUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imageSetProfile.setImageURI(resultUri);
                Toast.makeText(SetProfileActivity.this," "+resultUri,Toast.LENGTH_LONG);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
*/