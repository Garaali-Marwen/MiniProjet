package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUp extends AppCompatActivity {

    User user;
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    RadioButton candidat;
    RadioButton center;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        candidat = findViewById(R.id.candidat);
        center = findViewById(R.id.center);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        user = new User();

    }

    public void signUp(View v) {

        String firstName = this.firstName.getText().toString();
        String lastName = this.lastName.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        if (firstName.isEmpty()) {
            this.firstName.setError("First name cannot be empty!");
            return;
        }
        if (lastName.isEmpty()) {
            this.lastName.setError("Last name cannot be empty!");
            return;
        }
        if (email.isEmpty()) {
            this.email.setError("Email cannot be empty!");
            return;
        } else if (!validateEmailFormat(email)) {
            this.email.setError("Invalide email format");
            return;
        }
        if (password.isEmpty()) {
            this.password.setError("Password cannot be empty!");
            return;
        } else if (password.length() < 8) {
            this.firstName.setError("Password must be at least 8 characters!");
            return;
        }
        if (!candidat.isChecked() && !center.isChecked()) {
            this.center.setError("Account type must be checked");
            return;
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        if (candidat.isChecked()) {
            user.setRole(Role.CANDIDAT);
        } else {
            user.setRole(Role.CENTER);
        }

        EditText emailInput = this.email;
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String uid = firebaseUser.getUid();
                            // Add the user data to the database Users
                            reference.child(uid).setValue(user);
                            Toast.makeText(getApplicationContext(), "Sign up success !", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                emailInput.setError("This email is already in use!");
                            } else {
                                Toast.makeText(signUp.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public static boolean validateEmailFormat(String email) {
        String regex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        return email.matches(regex);
    }
}