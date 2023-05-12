package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    EditText firstName;
    EditText lastName;
    EditText tel;

    Button button;

    ArrayList<Formation> formations = new ArrayList<>();
    ListView listView;
    AdapterProfileCourses adapter;
    DatabaseReference coursesReference;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        firstName = findViewById(R.id.profileFirstName);
        lastName = findViewById(R.id.profileLastName);
        tel = findViewById(R.id.profilePhone);
        button = findViewById(R.id.saveButton);
        imageView = findViewById(R.id.profileImage);

        firstName.setEnabled(false);
        lastName.setEnabled(false);
        tel.setEnabled(false);
        button.setVisibility(View.INVISIBLE);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.add);

        bottomNavigationView.setSelectedItemId(R.id.profile);


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
                } else {
                    item.setVisible(false);
                }

                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                tel.setText(user.getTel());


                if (user.getImg() != null && !user.getImg().isEmpty()) {
                    Glide.with(ProfileActivity.this)
                            .load(Uri.parse(user.getImg()))
                            .into(imageView);
                }

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setClipToOutline(true);
                imageView.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        int diameter = Math.min(view.getWidth(), view.getHeight());
                        outline.setOval(0, 0, diameter, diameter);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent;
                switch (itemId) {
                    case R.id.home:
                        intent = new Intent(ProfileActivity.this, CoursesActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.profile:
                        intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.add:
                        intent = new Intent(ProfileActivity.this, addFormationActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        listView = findViewById(R.id.centerCourses);
        formations = new ArrayList<>();
        adapter = new AdapterProfileCourses(ProfileActivity.this, R.layout.center_courses_item, formations);
        coursesReference = FirebaseDatabase.getInstance().getReference().child("Courses");
        coursesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                formations.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Formation formation = ds.getValue(Formation.class);
                    if (formation.getCenterId().equals(uid)) {
                        formation.setId(ds.getKey());
                        formations.add(formation);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setAdapter(adapter);
    }

    public void enableUpdate(View view) {
        firstName.setEnabled(true);
        lastName.setEnabled(true);
        tel.setEnabled(true);
        button.setVisibility(View.VISIBLE);
    }

    public void saveChanges(View view) {
        if (firstName.getText().toString().equals("")) {
            this.firstName.setError("First name cannot be empty!");
            return;
        }
        if (lastName.getText().toString().equals("")) {
            this.lastName.setError("Last name cannot be empty!");
            return;
        }
        if (tel.getText().toString().equals("")) {
            this.tel.setError("Tel cannot be empty!");
            return;
        } else if (tel.length() != 8) {
            this.tel.setError("Invalid phone number!");
            return;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String uid = firebaseUser.getUid();
                reference = FirebaseDatabase.getInstance().getReference().child("Users");
                Map<String, Object> update = new HashMap<>();
                User user = new User();
                user.setLastName(lastName.getText().toString());
                user.setFirstName(firstName.getText().toString());
                user.setTel(tel.getText().toString());
                user.setImg(user1.getImg());
                user.setRole(user1.getRole());
                update.put(uid, user);

                reference.updateChildren(update).addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Edit success.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Edit failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}