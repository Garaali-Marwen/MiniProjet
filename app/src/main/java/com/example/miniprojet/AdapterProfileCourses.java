package com.example.miniprojet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterProfileCourses extends ArrayAdapter<Formation> {
    private Context nContext;
    private int nRessource;
    private ArrayList<Formation> formations;

    public AdapterProfileCourses(@NonNull Context context, int resource, @NonNull ArrayList<Formation> objects) {
        super(context, resource, objects);
        this.nRessource=resource;
        this.nContext=context;
        this.formations=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view= LayoutInflater.from(nContext).inflate(nRessource,parent,false);
        ImageView imageButton = view.findViewById(R.id.deleteButton);
        TextView title= view.findViewById(R.id.courseName);
        TextView price = view.findViewById(R.id.coursePrice);
        title.setText(getItem(position).getTitle());
        price.setText(String.valueOf(getItem(position).getPrix())+" TND");

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Formation formation = getItem(position);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Courses").child(formation.getId());
                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Delete success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Formation formation = getItem(position);
                Intent intent = new Intent(parent.getContext(), CourseDetailsActivity.class);
                intent.putExtra("course", formation);
                nContext.startActivity(intent);
            }
        });

        return view;
    }
}
