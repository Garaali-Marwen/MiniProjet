package com.example.miniprojet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCourses extends ArrayAdapter<Formation> {
    private Context nContext;
    private int nRessource;
    private ArrayList<Formation> formations;
    public AdapterCourses(@NonNull Context context, int resource, @NonNull ArrayList<Formation> objects) {
        super(context, resource, objects);
        this.nRessource=resource;
        this.nContext=context;
        this.formations=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view= LayoutInflater.from(nContext).inflate(nRessource,parent,false);
        Button detailsButton = view.findViewById(R.id.details);
        TextView title= view.findViewById(R.id.courseName);
        TextView description= view.findViewById(R.id.courseDescription);
        TextView price = view.findViewById(R.id.coursePrice);
        title.setText(getItem(position).getTitle());
        description.setText(getItem(position).getDescription());
        price.setText(String.valueOf(getItem(position).getPrix()));

        detailsButton.setOnClickListener(new View.OnClickListener() {
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