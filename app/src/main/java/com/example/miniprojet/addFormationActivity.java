package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class addFormationActivity extends AppCompatActivity {

    EditText title;
    EditText description;
    EditText beginDate;
    EditText endDate;
    EditText price;
    Formation formation;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_formation);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.add);

        bottomNavigationView.setSelectedItemId(R.id.add);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();


        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getRole() == Role.CENTER) {
                    item.setVisible(true);
                }else {
                    item.setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(addFormationActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent;
                switch (itemId) {
                    case R.id.home:
                        intent = new Intent(addFormationActivity.this, CoursesActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.profile:
                        intent = new Intent(addFormationActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.add:
                        intent = new Intent(addFormationActivity.this, addFormationActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(addFormationActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        title = findViewById(R.id.titre);
        description = findViewById(R.id.description);
        beginDate = findViewById(R.id.beginDate);
        endDate = findViewById(R.id.endDate);
        price = findViewById(R.id.price);

        reference = FirebaseDatabase.getInstance().getReference().child("Courses");
        firebaseAuth = FirebaseAuth.getInstance();

        formation = new Formation();
    }

    public void addCourse(View view) {
        String title = this.title.getText().toString();
        String description = this.description.getText().toString();
        String beginDate = this.beginDate.getText().toString();
        String endDate = this.endDate.getText().toString();
        String price = this.price.getText().toString();

        if (title.isEmpty()) {
            this.title.setError("Title cannot be empty!");
            return;
        }
        if (description.isEmpty()) {
            this.description.setError("Description cannot be empty!");
            return;
        }
        if (beginDate.isEmpty()) {
            this.beginDate.setError("Begin date cannot be empty!");
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            simpleDateFormat.parse(beginDate);
        } catch (ParseException e) {
            this.beginDate.setError("Invalid date format!");
            return;
        }
        if (endDate.isEmpty()) {
            this.endDate.setError("End date cannot be empty!");
            return;
        }
        try {
            simpleDateFormat.parse(endDate);
        } catch (ParseException e) {
            this.endDate.setError("Invalid date format!");
            return;
        }
        if (price.isEmpty()) {
            this.price.setError("Price cannot be empty!");
            return;
        }

        formation.setTitle(title);
        formation.setDescription(description);
        formation.setDateDebut(beginDate);
        formation.setDateFin(endDate);
        formation.setPrix(Float.parseFloat(price));

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();

        formation.setCenterId(uid);

        reference.push().setValue(formation);
        Toast.makeText(getApplicationContext(), "Training successfully added", Toast.LENGTH_LONG).show();
        finish();
    }
}