/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.mycontact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class ContactDbAdapter {

    public static final String KEY_NOM = "nom";
    public static final String KEY_PRENOM = "prenom";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TELEPHONE = "telephone";
    public static final String KEY_ADRESSE = "adresse";
    public static final String KEY_EMAIL = "email";


    private static final String TAG = "ContactDbAdapter";
    private DatabaseHelper myDbHelper;
    private SQLiteDatabase myDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table contact (_id integer primary key autoincrement, "
        + "nom text not null, prenom text , telephone text not null, adresse text , email text);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "contact";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public ContactDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ContactDbAdapter open() throws SQLException {
        myDbHelper = new DatabaseHelper(mCtx);
        myDb = myDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDbHelper.close();
    }



    public long createContact(String nom, String prenom, String telephone, String adresse, String email) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NOM, nom);
        initialValues.put(KEY_PRENOM, prenom);
        initialValues.put(KEY_TELEPHONE, telephone);
        initialValues.put(KEY_ADRESSE, adresse);
        initialValues.put(KEY_EMAIL, email);


        return myDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteContact(long rowId) {

        return myDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    public void deleteAll(){
        myDb.delete(DATABASE_TABLE,null,null);
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllContacts() {

        return myDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NOM,
                KEY_PRENOM, KEY_TELEPHONE, KEY_ADRESSE, KEY_EMAIL}, null, null, null, null, KEY_NOM);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchContact(long rowId) throws SQLException {

        Cursor mCursor =

            myDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NOM, KEY_PRENOM, KEY_TELEPHONE, KEY_ADRESSE, KEY_EMAIL}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateContact(long rowId, String nom, String prenom,String telephone,String adresse,String email) {
        ContentValues args = new ContentValues();
        args.put(KEY_NOM, nom);
        args.put(KEY_PRENOM, prenom);
        args.put(KEY_TELEPHONE, telephone);
        args.put(KEY_ADRESSE, adresse);
        args.put(KEY_EMAIL, email);

        return myDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
