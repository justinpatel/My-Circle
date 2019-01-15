package com.example.perfect.juzzuber;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.perfect.juzzuber.Model.CircleName;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class MyCircleActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference,ref,userRef,databaseReference1;
    FirebaseUser user;
    GeoFire geoFire;
    GoogleApiClient client;
    LocationRequest locationRequest;
    LatLng latLng;
    Location lastLocation;

    PermissionManager permissionManager;

    CircleName circleName;
    RecyclerView recyclerView;
    CircleNamesAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<CircleName> namelist;
    String cn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_circle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        ref =  FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Circlename");

        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_circlename);
        namelist = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        buildGoogleApiClient();

        databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("location");
        geoFire = new GeoFire(databaseReference1);

        loadRecyclerViewData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddingCircle();
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.circle_details,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MyCircleActivity.this,SettingActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest().create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //   locationRequest.setInterval(5000);
        //   locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);

        displayLocation();

    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest().create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //   locationRequest.setInterval(5000);
        //   locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }

    private void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_RES_REQUEST).show();
            }
            else {
                Toast.makeText(this,"This device is not supported",Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        client.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(MyCircleActivity.this,"Could not get location",Toast.LENGTH_LONG).show();
        }
        else {

            lastLocation = location;
            displayLocation();
           /* latLng = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.addMarker(new MarkerOptions().position(latLng).title("You"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f)); */
        }
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);

        if (lastLocation != null){

            latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());

            geoFire.setLocation("", new GeoLocation(latLng.latitude, latLng.longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //Add marker


              //      mMap.addMarker(new MarkerOptions().position(latLng).title("You"));

                    //     rotateMarker(mCurrent,-360,mMap);

                }
            });

        }
        else {
            Log.d("ERROR", "Cannot get your location");
        }
    }

    private void AddingCircle() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MyCircleActivity.this);
        dialog.setTitle("Name a Circle");

        LayoutInflater inflater = LayoutInflater.from(MyCircleActivity.this);
        final View add_circle_layout = inflater.inflate(R.layout.layout_add_circle,null);

        final MaterialEditText circlename = add_circle_layout.findViewById(R.id.circleNameEt);

        dialog.setView(add_circle_layout);

        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(MyCircleActivity.this);
                waitingDialog.show();

                databaseReference.child("Circlename").orderByChild("cname").equalTo(circlename.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()){

                            circleName = new CircleName();
                            circleName.setCname(circlename.getText().toString());
                            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Circlename");


                            userRef.push().setValue(circleName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    waitingDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "" + circlename.getText().toString() + " circle added", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    waitingDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else {
                            waitingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), ""+circlename.getText().toString()+" is already added", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG);
                    }
                });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        dialog.show();

    }

    private void loadRecyclerViewData() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                namelist.clear();
                if (dataSnapshot.exists()){
                    CircleName circleName1;

                    for (DataSnapshot dss: dataSnapshot.getChildren()){
                        cn = dss.child("cname").getValue(String.class);

                        circleName1 = new CircleName(cn);
                        namelist.add(circleName1);
           //            adapter.notifyDataSetChanged();
                    }
                    adapter = new CircleNamesAdapter(namelist,MyCircleActivity.this);
                    recyclerView.setAdapter(adapter);
             //       adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "failed " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}
