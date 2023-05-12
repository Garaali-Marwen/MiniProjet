package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseDetailsActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;

    TextView title;
    TextView beginDate;
    TextView endDate;
    TextView description;
    TextView managerName;
    TextView managerTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        title = findViewById(R.id.formationTitle);
        beginDate = findViewById(R.id.formationBegin);
        endDate = findViewById(R.id.formationEnd);
        description = findViewById(R.id.formationDecription);
        managerName = findViewById(R.id.name);
        managerTel = findViewById(R.id.managerPhone);

        Intent intent = getIntent();
        Formation formation = (Formation) intent.getSerializableExtra("course");

        title.setText(formation.getTitle());
        beginDate.setText(beginDate.getText()+formation.getDateDebut());
        endDate.setText(endDate.getText()+formation.getDateFin());
        description.setText(description.getText()+"\n\n"+formation.getDescription());

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
                Toast.makeText(CourseDetailsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(formation.getCenterId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                managerName.setText(managerName.getText()+user.getFirstName()+" "+user.getLastName());
                managerTel.setText(managerTel.getText()+user.getTel());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseDetailsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent;
                switch (itemId) {
                    case R.id.home:
                        intent = new Intent(CourseDetailsActivity.this, CoursesActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.profile:
                        intent = new Intent(CourseDetailsActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.add:
                        intent = new Intent(CourseDetailsActivity.this, addFormationActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(CourseDetailsActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        managerTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri dialUri = Uri.parse("tel:" + managerTel.getText().toString());
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, dialUri);
                startActivity(dialIntent);
            }
        });
    }
}