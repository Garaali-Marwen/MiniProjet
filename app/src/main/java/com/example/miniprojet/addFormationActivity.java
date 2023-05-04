package com.example.miniprojet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addFormationActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    EditText edittitle;
    EditText editdescription;
    EditText editdateDebut;
    EditText editdateFin;
    TextView textViewimg;
    Button bimg;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_formation);



        edittitle=findViewById(R.id.formationtitle);
        editdescription=findViewById(R.id.description);
        editdateDebut=findViewById(R.id.editTextDate);
        editdateFin=findViewById(R.id.editTextDate2);
        textViewimg=findViewById(R.id.imgname);
        save=findViewById(R.id.save);
        bimg=findViewById(R.id.img);

        bimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("courses");

                String title=edittitle.getText().toString();
                String description=editdescription.getText().toString();
                String dateDebut=editdateDebut.getText().toString();
                String dateFin=editdateFin.getText().toString();
                String img=textViewimg.getText().toString();
                if (!title.isEmpty() && !description.isEmpty() && !dateDebut.isEmpty() && !dateFin.isEmpty() && !img.isEmpty()){
                    Formation formation=new Formation(title,description,dateDebut,dateFin,img);
                    myRef.child(title).setValue(formation);
                }

            }
        });





    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            textViewimg.setText((CharSequence) selectedImageUri);
        }
    }
}