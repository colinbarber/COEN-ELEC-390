package com.example.roumeliotis.tagit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ImageManager extends SQLiteOpenHelper{
    private static final String TAG = "ImageManager";
    private Context context = null;

    public ImageManager(Context context) {
        super(context, ImageManagerConfigs.DATABASE_NAME, null, ImageManagerConfigs.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"ImageManager onCreate");

        //Create Image Table
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + ImageManagerConfigs.TABLE_IMAGE + "(" +
                ImageManagerConfigs.IMAGE_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ImageManagerConfigs.HINT_ID_COLUMN + " TEXT NOT NULL, " +
                ImageManagerConfigs.IMAGE_CONTENT_COLUMN + " BLOB NOT NULL)";

        Log.d(TAG, CREATE_IMAGE_TABLE);
        db.execSQL(CREATE_IMAGE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ImageManagerConfigs.TABLE_IMAGE);
        onCreate(db);
    }

    public long insertImage(byte[] image, String hint){
        Log.d(TAG, "insertImage");

        long id = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ImageManagerConfigs.HINT_ID_COLUMN, hint);
        contentValues.put(ImageManagerConfigs.IMAGE_CONTENT_COLUMN, image);


        try {
            id = sqLiteDatabase.replaceOrThrow(ImageManagerConfigs.TABLE_IMAGE, null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        Log.d(TAG, "success");
        return id;
    }

    public byte[] getImageByHint(String hint){

        Log.d(TAG, "getGImageByHint");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        byte[] image = null;
        try {
            String SELECT_QUERY = String.format("SELECT * FROM IMAGES WHERE hint = '%s'", hint);
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);

            if(cursor.moveToLast()){
                image = cursor.getBlob(cursor.getColumnIndex(ImageManagerConfigs.IMAGE_CONTENT_COLUMN));
            }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return image;
    }

    public boolean isEmpty(String hint){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String SELECT_QUERY = String.format("SELECT * FROM IMAGES WHERE hint = '%s'", hint);
        Log.d(TAG, SELECT_QUERY + "checking if empty");
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
        Boolean rowExists;

        if(cursor.getCount() < 1)
        {
            //db is empty
            rowExists = true;
        } else
        {
            rowExists = false;
        }

        return rowExists;
    }

}
