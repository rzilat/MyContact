package com.example.mycontact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddContactActivity extends AppCompatActivity {

    private EditText nomEditText;
    private EditText prenomEditText;
    private EditText telephoneEditText;
    private EditText adresseEditText;
    private EditText emailEditText;
    private Button ajouterButton;
    private ContactDbAdapter myDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Intent intent = getIntent();
        nomEditText = findViewById(R.id.editTextNom);
        prenomEditText = findViewById(R.id.editTextPrenom);
        telephoneEditText = findViewById(R.id.editTextTelephone);
        adresseEditText = findViewById(R.id.editTextAdresse);
        emailEditText = findViewById(R.id.editTextEmail);
        ajouterButton = findViewById(R.id.buttonAjouter);
        myDbHelper = new ContactDbAdapter(this);
        myDbHelper.open();


           ajouterButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(!nomEditText.getText().toString().isEmpty() && !telephoneEditText.getText().toString().isEmpty()){
                   myDbHelper.createContact(nomEditText.getText().toString(), prenomEditText.getText().toString(), telephoneEditText.getText().toString(), adresseEditText.getText().toString(), emailEditText.getText().toString());

                   nomEditText.setText("");
                   prenomEditText.setText("");
                   telephoneEditText.setText("");
                   adresseEditText.setText("");
                   emailEditText.setText("");

                   }
               }
           });















    }


}