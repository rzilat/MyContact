package com.example.mycontact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.List;



public class MainActivity extends AppCompatActivity {

    private ListView myContactList;
    private ContactDbAdapter myDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
                Snackbar.make(view, "Ajouter un contact", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });


        myContactList = findViewById(R.id.contact_list);
        registerForContextMenu( myContactList);
        myDbHelper = new ContactDbAdapter(this);
        myDbHelper.open();
        fillData();

        myContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor SelectedTaskCursor = (Cursor) myContactList.getItemAtPosition(position);
                final String nom = SelectedTaskCursor.getString(SelectedTaskCursor.getColumnIndex("nom"));
                final String prenom = SelectedTaskCursor.getString(SelectedTaskCursor.getColumnIndex("prenom"));
                final String telephone = SelectedTaskCursor.getString(SelectedTaskCursor.getColumnIndex("telephone"));
                final String adresse = SelectedTaskCursor.getString(SelectedTaskCursor.getColumnIndex("adresse"));
                final String email = SelectedTaskCursor.getString(SelectedTaskCursor.getColumnIndex("email"));

                showContact(id,nom,prenom,telephone,adresse,email);

            }
        });
    }

    public void addContact(){
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);

    }



    public void showContact(long id,String nom,String prenom,String telephone,String adresse,String email){
        Intent intentShow = new Intent(this, ShowContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("id" ,id);
        bundle.putString("nom" ,nom);
        bundle.putString("prenom" ,prenom);
        bundle.putString("telephone" ,telephone);
        bundle.putString("adresse" ,adresse);
        bundle.putString("email" ,email);

        intentShow.putExtras(bundle);
        startActivity(intentShow);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Cursor SelectedTaskCursor = (Cursor) myContactList.getItemAtPosition(info.position);
        final long id = SelectedTaskCursor.getLong(SelectedTaskCursor.getColumnIndex("_id"));
        final String telephone = SelectedTaskCursor.getString(SelectedTaskCursor.getColumnIndex("telephone"));
        final String email = SelectedTaskCursor.getString(SelectedTaskCursor.getColumnIndex("email"));
        final String adresse = SelectedTaskCursor.getString(SelectedTaskCursor.getColumnIndex("adresse"));
        PackageManager packageManager = getPackageManager();
        Intent myIntent;
        boolean isIntentSafe;
        List<ResolveInfo> activities;
        Toast toast;

        switch (item.getItemId()) {
            case R.id.supprimer_contact:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.alert_supprimer_message)
                        .setTitle(R.string.alert_supprimer_title)
                        .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDbHelper.deleteContact(id);
                                fillData();
                            }
                        })
                        .setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;

            case R.id.appeler_contact:
                Uri number = Uri.parse("tel:"+telephone);
                myIntent = new Intent(Intent.ACTION_DIAL, number);
                activities = packageManager.queryIntentActivities(myIntent, 0);
                isIntentSafe = activities.size() > 0;
                toast = Toast.makeText(this,"j'appele  :  "+isIntentSafe+" "+telephone,Toast.LENGTH_SHORT);
                toast.show();
                if (isIntentSafe) {
                    startActivity(myIntent);
                }
                return true;

            case R.id.envoyer_sms:
                Uri sms = Uri.parse("smsto:"+telephone);
                myIntent = new Intent(Intent.ACTION_SENDTO, sms);
                myIntent.putExtra("sms_body", "");
                activities = packageManager.queryIntentActivities(myIntent, 0);
                isIntentSafe = activities.size() > 0;
                toast = Toast.makeText(this,"j'envoi un sms à  :  "+isIntentSafe+" "+telephone,Toast.LENGTH_SHORT);
                toast.show();
                if (isIntentSafe) {
                    startActivity(myIntent);
                }
                return true;

            case R.id.envoyer_email:
                myIntent  = new Intent(Intent.ACTION_SEND);
                myIntent.setType("message/rfc822");
                myIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
                myIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
                myIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
                activities = packageManager.queryIntentActivities(myIntent, 0);
                isIntentSafe = activities.size() > 0;
                toast = Toast.makeText(this,"J'envoi un email :  "+isIntentSafe+" "+email,Toast.LENGTH_SHORT);
                toast.show();
                if (isIntentSafe) {
                    startActivity(myIntent);
                }
                return true;

            case R.id.loc_google:
                Uri location = Uri.parse("geo:0,0?q="+adresse);
                myIntent  = new Intent(Intent.ACTION_VIEW,location);
                activities = packageManager.queryIntentActivities(myIntent, 0);
                isIntentSafe = activities.size() > 0;
                toast = Toast.makeText(this,"Je fais la recherche de :  "+isIntentSafe+" "+adresse,Toast.LENGTH_SHORT);
                toast.show();
                if (isIntentSafe) {
                    startActivity(myIntent);
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = myDbHelper.fetchAllContacts();
        startManagingCursor(c);

        String[] from = new String[] { ContactDbAdapter.KEY_NOM, ContactDbAdapter.KEY_PRENOM };
        int[] to = new int[] { R.id.text1, R.id.text2};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter contacts =
                new SimpleCursorAdapter(this, R.layout.contacts_row, c, from, to,0);
        myContactList.setAdapter(contacts);

    }
}
