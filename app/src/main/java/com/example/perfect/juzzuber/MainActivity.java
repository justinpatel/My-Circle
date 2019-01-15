package com.example.perfect.juzzuber;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.perfect.juzzuber.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn,btnRegister;


    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase db;
    DatabaseReference users,databaseReference;

    String imgSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    /*    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgSet = dataSnapshot.child("imageUrl").getValue(String.class);
             //   Toast.makeText(SetProfileActivity.this," "+imgSet,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });    */

        if (user == null){
            setContentView(R.layout.activity_main);
        }
        else {
                Intent intent = new Intent(MainActivity.this,MyCircleActivity.class);
                startActivity(intent);
                finish();
            }

        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSigninDialog();
            }
        });
    }

    private void showSigninDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Sign In ");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);

        final MaterialEditText editEmail = login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText editPassword = login_layout.findViewById(R.id.edtPassword);
        // Button signInCancelBTN = login_layout.findViewById(R.id.signInCancelBtn);
        Button loginBTN = login_layout.findViewById(R.id.loginBtn);
        dialog.setView(login_layout);

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (editPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password too short ! !", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();

                auth.signInWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();

                        user = auth.getCurrentUser();
                        if (user.isEmailVerified()){
                            Intent intent = new Intent(MainActivity.this,MyCircleActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Email is not verified yet!Please check email.",Toast.LENGTH_LONG).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_LONG).show();

                    }
                });
            }
        });
    /*    dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                        if (TextUtils.isEmpty(editEmail.getText().toString())) {
                            Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(editPassword.getText().toString())) {
                            Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (editPassword.getText().toString().length() < 6) {
                            Snackbar.make(rootLayout, "Password too short ! !", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                        waitingDialog.show();

                        auth.signInWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();

                                user = auth.getCurrentUser();
                                if (user.isEmailVerified()){
                                    Intent intent = new Intent(MainActivity.this,NavigationDrawerActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(MainActivity.this,"Email is not verified yet!Please check email.",Toast.LENGTH_LONG).show();
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_LONG).show();

                            }
                        });

                    }
                });   */

        dialog.show();

    }

    private void showRegisterDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register ");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register,null);

        final MaterialEditText editEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText editPassword = register_layout.findViewById(R.id.edtPassword);

        final MaterialEditText editUsrName = register_layout.findViewById(R.id.edtUsrName);
        final MaterialEditText editPhone = register_layout.findViewById(R.id.edtPhone);

        Button registerBTN = register_layout.findViewById(R.id.registerBtn);

        dialog.setView(register_layout);

        dialog.show();

       /* circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivity(i,12);
            }
        }); */


       registerBTN.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (TextUtils.isEmpty(editEmail.getText().toString())) {
                   Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                   return;
               }
               if (TextUtils.isEmpty(editPhone.getText().toString())) {
                   Snackbar.make(rootLayout, "Please enter phone number", Snackbar.LENGTH_SHORT).show();
                   return;
               }
               if (TextUtils.isEmpty(editPassword.getText().toString())) {
                   Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                   return;
               }
               if (editPassword.getText().toString().length() < 6) {
                   Snackbar.make(rootLayout, "Password too short ! !", Snackbar.LENGTH_SHORT).show();
                   return;
               }
               if (TextUtils.isEmpty(editUsrName.getText().toString())) {
                   Snackbar.make(rootLayout, "Please enter name", Snackbar.LENGTH_SHORT).show();
                   return;
               }
               final android.app.AlertDialog dialog1 = new SpotsDialog(MainActivity.this);
               dialog1.show();
               auth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                       .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                           @Override
                           public void onSuccess(AuthResult authResult) {

                               User userU = new User();
                               userU.setEmail(editEmail.getText().toString());
                               userU.setName(editUsrName.getText().toString());
                               userU.setPassword(editPassword.getText().toString());
                               userU.setPhone(editPhone.getText().toString());
                               user = auth.getCurrentUser();
                               userU.setUserId(user.getUid());

                               String code = generateCode();
                               userU.setCode(code);

                               users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userU).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       sendEmailVerification();
                                       dialog1.dismiss();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       dialog1.dismiss();
                                       Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                   }
                               });
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               dialog1.dismiss();
                               Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                           }
                       });
           }
       });

    /*    dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                if(TextUtils.isEmpty(editEmail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(editPhone.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter phone number",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(editPassword.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(editPassword.getText().toString().length()<6){
                    Snackbar.make(rootLayout,"Password too short ! !",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(editName.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter name",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final android.app.AlertDialog dialog1 = new SpotsDialog(MainActivity.this);
                dialog1.show();

                auth.createUserWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User userU = new User();
                        userU.setEmail(editEmail.getText().toString());
                        userU.setName(editName.getText().toString());
                        userU.setPassword(editPassword.getText().toString());
                        userU.setPhone(editPhone.getText().toString());
                        user = auth.getCurrentUser();
                        userU.setUserId(user.getUid());

                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userU).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                sendEmailVerification();
                                dialog1.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog1.dismiss();
                                Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog1.dismiss();
                        Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });    */

    }

    private void sendEmailVerification() {
        user = auth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Verification Email sent.Please check email.",Toast.LENGTH_LONG).show();

                    auth.signOut();
                }
                else {
                    Toast.makeText(MainActivity.this,"Could not sent an email",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public String generateCode(){
        Date myDate = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        String date = format1.format(myDate);
    //    Toast.makeText(MainActivity.this,"Date: "+date,Toast.LENGTH_LONG).show();
        Random r = new Random();
        int n = 1000 + r.nextInt(9000);
        String code = String.valueOf(n);
        return code;
    }
}
