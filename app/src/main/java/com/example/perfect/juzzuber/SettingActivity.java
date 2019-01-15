package com.example.perfect.juzzuber;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

 //   CircleImageView profile_pic;
    MaterialAutoCompleteTextView name,code,email,phone;
    Button signout;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        name = (MaterialAutoCompleteTextView) findViewById(R.id.nameSetting);
        code = (MaterialAutoCompleteTextView) findViewById(R.id.codeSetting);
        email = (MaterialAutoCompleteTextView) findViewById(R.id.emailSetting);
        phone = (MaterialAutoCompleteTextView) findViewById(R.id.phoneSetting);
        signout = (Button) findViewById(R.id.signoutSettings);
  //      profile_pic = (CircleImageView) findViewById(R.id.profilePicSettings);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue(String.class));
                code.setText(dataSnapshot.child("code").getValue(String.class));
                email.setText(dataSnapshot.child("email").getValue(String.class));
                phone.setText(dataSnapshot.child("phone").getValue(String.class));
          //      profile_pic.setImageURI(dataSnapshot.child("imageUrl").getValue(Uri.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        code.setEnabled(false);
        email.setEnabled(false);
        phone.setEnabled(false);
        name.setEnabled(false);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user;
                user = auth.getCurrentUser();
                if (user != null) {
                    auth.signOut();
                }
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
