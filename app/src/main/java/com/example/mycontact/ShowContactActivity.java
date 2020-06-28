package com.example.mycontact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShowContactActivity extends AppCompatActivity {

    private ContactDbAdapter myDbHelper;
    private EditText nomEditText;
    private EditText prenomEditText;
    private EditText telephoneEditText;
    private EditText adresseEditText;
    private EditText emailEditText;
    private Button buttonAppeler;
    private Button buttonSmsEnvoyer;
    private Button buttonEmailEnvoyer;
    private Button buttonLocaliser;
    private Button buttonModifier;
    private Button buttonFavoris;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        final long id = bundle.getLong("id");
        final String nom=bundle.getString("nom");
        final String prenom=bundle.getString("prenom");
        final String telephone=bundle.getString("telephone");
        final String adresse=bundle.getString("adresse");
        final String email=bundle.getString("email");
        Toast toast;

        nomEditText = findViewById(R.id.editTextNom);
        prenomEditText = findViewById(R.id.editTextPrenom);
        telephoneEditText = findViewById(R.id.editTextTelephone);
        adresseEditText = findViewById(R.id.editTextAdresse);
        emailEditText = findViewById(R.id.editTextEmail);
        buttonAppeler = findViewById(R.id.buttonAppeler);
        buttonLocaliser=findViewById(R.id.buttonLocaliser);
        buttonSmsEnvoyer=findViewById(R.id.buttonSmsEnvoyer);
        buttonEmailEnvoyer=findViewById(R.id.buttonEmailEnvoyer);
        buttonModifier=findViewById(R.id.buttonModifier);
        buttonFavoris=findViewById(R.id.buttonMesFavoris);
        myDbHelper = new ContactDbAdapter(this);
        myDbHelper.open();

        nomEditText.setText(nom);
        prenomEditText.setText(prenom);
        telephoneEditText.setText(telephone);
        adresseEditText.setText(adresse);
        emailEditText.setText(email);

        toast = Toast.makeText(this,"j'appele  :  "+nomEditText.getText().toString(),Toast.LENGTH_SHORT);
        toast.show();


        buttonLocaliser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri location = Uri.parse("geo:0,0?q="+adresse);
                Intent myIntent  = new Intent(Intent.ACTION_VIEW,location);
                    startActivity(myIntent);
            }
        });

        buttonAppeler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:"+telephone);
                Intent myIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(myIntent);
            }
        });

        buttonSmsEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri sms = Uri.parse("smsto:"+telephone);
                Intent myIntent = new Intent(Intent.ACTION_SENDTO, sms);
                myIntent.putExtra("sms_body", "");
                    startActivity(myIntent);
            }
        });

        buttonEmailEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent  = new Intent(Intent.ACTION_SEND);
                myIntent.setType("message/rfc822");
                myIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
                myIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
                myIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
                startActivity(myIntent);
            }
        });

        buttonModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDbHelper.updateContact(id, nomEditText.getText().toString(), prenomEditText.getText().toString(), telephoneEditText.getText().toString(), adresseEditText.getText().toString(), emailEditText.getText().toString());
            }
        });

        buttonFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDbHelper.updateContactFavoris(id);
            }
        });
    }
}
