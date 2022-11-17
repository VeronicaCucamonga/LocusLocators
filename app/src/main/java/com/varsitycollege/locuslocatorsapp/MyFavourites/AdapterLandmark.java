package com.varsitycollege.locuslocatorsapp.MyFavourites;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.varsitycollege.locuslocatorsapp.databinding.RowLandmarkBinding;

import java.util.ArrayList;

/*
* Code Attribution
* Name: Atif Pervaiz
* Published: 23 May 2021
* URL: https://youtu.be/j6GrP2MdFos
* nicole started
*/
public class AdapterLandmark extends RecyclerView.Adapter<AdapterLandmark.HolderLandmark> {

    //context and arraylist
    public ArrayList<ModelLandmark> modelLandmarkArrayList;
    private Context context;

    //view binding
    private RowLandmarkBinding binding;

    //constructor
    public AdapterLandmark(Context context, ArrayList<ModelLandmark> modelLandmarkArrayList) {
        this.context = context;
        this.modelLandmarkArrayList = modelLandmarkArrayList;
    }

    @NonNull
    @Override
    public HolderLandmark onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //binding row landmarks layout
        binding = RowLandmarkBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderLandmark(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLandmark holder, int position) {
        //get data
        ModelLandmark model = modelLandmarkArrayList.get(position);
        String location = model.getLocation_name();
        Double latitude = model.getLatitude();
        Double longitude = model.getLongitude();

        //set data in holders
        holder.locationName.setText(location);
        holder.latitude.setText(Double.toString(latitude));
        holder.longitude.setText(Double.toString(longitude));

        //handle on click of delete btn
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirm delete landmark
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this landmark?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //begin deletion
                                Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
                                deleteCategory(model, holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewFavourites.class);
                intent.putExtra("locationName", location);
                intent.putExtra("locationLat", Double.toString(latitude));
                intent.putExtra("locationLon", Double.toString(longitude));
                context.startActivity(intent);
            }
        });
    }

    //method that deletes a landmark from firebase database
    private void deleteCategory(ModelLandmark model, HolderLandmark holder) {
        //get id of category
        String id = model.getTimestamp();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourite Landmarks");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //delete successfully
                        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //delete failed
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelLandmarkArrayList.size();
    }

    //view holder class that holds ui views for row_collections
    class HolderLandmark extends RecyclerView.ViewHolder{

        //ui views for row collections layout
        TextView locationName, latitude, longitude;
        ImageButton deleteBtn;

        public HolderLandmark(@NonNull View itemView) {
            super(itemView);

            //init views
            locationName = binding.locationTv;
            latitude = binding.tvLatitude;
            longitude = binding.tvLongitude;
            deleteBtn = binding.deleteBtn;
        }
    }
}
/*
* nicole ended
* Code Attribution Ended*/

