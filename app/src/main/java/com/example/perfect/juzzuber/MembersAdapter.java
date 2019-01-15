package com.example.perfect.juzzuber;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.perfect.juzzuber.Model.CircleName;
import com.example.perfect.juzzuber.Model.Name;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Perfect on 7/4/2018.
 */

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private ArrayList<Name> namelist;
    Context c;
    String circlename;
    FirebaseAuth auth;
    FirebaseUser user;

   public MembersAdapter(ArrayList<Name> namelist, Context c,String circlename){
        this.namelist = namelist;
        this.c = c;
        this.circlename = circlename;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.list_members,null,false);

        return new ViewHolder(v,c,namelist);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Name name = namelist.get(position);
    //    holder.phoneTV.setText(name.getP());
        holder.nameTV.setText(name.getN());
    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTV;
        ArrayList<Name> names = new ArrayList<>();
        Context c;
        public ViewHolder(View itemView, final Context c, final ArrayList<Name> names) {
            super(itemView);
            this.c = c;
            this.names = names;
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
  //          itemView.setOnClickListener(this);

      //      phoneTV = itemView.findViewById(R.id.phoneTv);
            nameTV = itemView.findViewById(R.id.nameTv);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    Name nameOb = names.get(pos);
                    //    Toast.makeText(c," "+circleNameOb.getCname(),Toast.LENGTH_LONG).show();
                    showDeleteDialog(nameOb.getN(),c);
                    return true;
                }
            });
        }



  //      @Override
    /*    public void onClick(View v) {
            int position = getAdapterPosition();
            Uid uid = this.names.get(position);
            Intent intent = new Intent(c,AddMembersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("phone",uid.getPhone());
            intent.putExtra("name",uid.getUid());
            this.c.startActivity(intent);
        } */
    }

    private void showDeleteDialog(final String n, Context c) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        LayoutInflater inflater = LayoutInflater.from(c);
        final View dialogView = inflater.inflate(R.layout.delete_this,null);
        dialog.setView(dialogView);

        final Button delete = dialogView.findViewById(R.id.delete);

        dialog.setMessage("Are you sure you want to delete "+n+"?");

        final AlertDialog b = dialog.create();
        b.show();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child(circlename);

                dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dss:dataSnapshot.getChildren()){

                            if (dss.child("name").getValue().equals(n)){

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
