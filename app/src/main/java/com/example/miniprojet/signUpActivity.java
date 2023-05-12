package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class signUpActivity extends AppCompatActivity {

    User user;
    EditText firstName;
    EditText lastName;
    EditText tel;
    EditText email;
    EditText password;
    RadioButton candidat;
    RadioButton center;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        tel = findViewById(R.id.tel);
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
        String tel = this.tel.getText().toString();
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
        if (tel.isEmpty()) {
            this.tel.setError("Tel cannot be empty!");
            return;
        } else if (tel.length() != 8) {
            this.tel.setError("Invalid phone number!");
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
        if (imageUri == null){
            Toast.makeText(getApplicationContext(), "You should choose Profile image", Toast.LENGTH_LONG).show();
            return;
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTel(tel);
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
                            reference.child(uid).setValue(user);

                           /* if (imageUri != null) {
                                uploadImage(imageUri, uid);
                            }*/

                            uploadImage(imageUri, uid);


                            Toast.makeText(getApplicationContext(), "Sign up success !", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                emailInput.setError("This email is already in use!");
                            } else {
                                Toast.makeText(signUpActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public static boolean validateEmailFormat(String email) {
        String regex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        return email.matches(regex);
    }


    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            ImageView imageView = findViewById(R.id.imageProfile);
            this.imageUri = imageUri;
            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);

            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int size = Math.min(view.getWidth(), view.getHeight());
                    outline.setOval(0, 0, size, size);
                }
            };
            imageView.setOutlineProvider(viewOutlineProvider);
            imageView.setClipToOutline(true);
        }
    }


    private void uploadImage(Uri imageUri, String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(userId);

        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveImageUrlToDatabase(imageUrl, userId);
            });
        });
    }

    private void saveImageUrlToDatabase(String imageUrl, String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.child("img").setValue(imageUrl);
    }

}