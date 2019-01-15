package com.example.perfect.juzzuber;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perfect.juzzuber.Model.CircleName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Perfect on 6/29/2018.
 */

public class CircleNamesAdapter extends RecyclerView.Adapter<CircleNamesAdapter.ViewHolder> {
    private ArrayList<CircleName> namelist;
    Context c;
    FirebaseAuth auth;
    FirebaseUser user;

    CircleNamesAdapter(ArrayList<CircleName> namelist,Context c){
        this.namelist = namelist;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.list_item,null,false);

        return new ViewHolder(v,c,namelist);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CircleName circleName = namelist.get(position);
        holder.classnameTV.setText(circleName.getCname());


    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView classnameTV;
        ArrayList<CircleName> names = new ArrayList<CircleName>();
        Context c;
        public ViewHolder(View itemView, final Context c, final ArrayList<CircleName> names) {
            super(itemView);
            this.c = c;
            this.names = names;
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            classnameTV = itemView.findViewById(R.id.circleTv);

            itemView.setOnClickListener(this);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    CircleName circleNameOb = names.get(pos);
                //    Toast.makeText(c," "+circleNameOb.getCname(),Toast.LENGTH_LONG).show();
                    showDeleteDialog(circleNameOb.getCname(),c);
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            CircleName circleName = this.names.get(position);
            Intent intent = new Intent(c,CircleDetailsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("circlename",circleName.getCname());
            this.c.startActivity(intent);
        }
    }

    private void showDeleteDialog(final String cname, final Context c) {
     //   Toast.makeText(c,"inside showDialog Method",Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        LayoutInflater inflater = LayoutInflater.from(c);
        final View dialogView = inflater.inflate(R.layout.delete_this,null);
        dialog.setView(dialogView);

        final Button delete = dialogView.findViewById(R.id.delete);

        dialog.setMessage("Are you sure you want to delete "+cname+"?");

        final AlertDialog b = dialog.create();
        b.show();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Circlename");

                dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dss:dataSnapshot.getChildren()){

                            if (dss.child("cname").getValue().equals(cname)){

                                DatabaseReference ref = dataRef.child(dss.getKey());
                                ref.removeValue();
                          //      Toast.makeText(c," "+dss.getValue()+"    "+dss.getKey(),Toast.LENGTH_LONG).show();
                                break;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                b.dismiss();
            }
        });

    }


}
