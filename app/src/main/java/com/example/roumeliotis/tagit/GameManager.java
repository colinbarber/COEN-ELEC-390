package com.example.roumeliotis.tagit;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.roumeliotis.tagit.GameManagerConfigs;


public class GameManager extends SQLiteOpenHelper{
    private static final String TAG = "GameManager";
    private Context context = null;

    public GameManager(Context context) {
        super(context, GameManagerConfigs.DATABASE_NAME, null, GameManagerConfigs.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"GameManager onCreate");

        String CREATE_GAME_TABLE = "CREATE TABLE " + GameManagerConfigs.TABLE_GAME + "(" +
                GameManagerConfigs.GAME_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameManagerConfigs.GAME_NAME_COLUMN + " TEXT NOT NULL, " +
                GameManagerConfigs.ENDTIME_COLUMN + " TEXT NOT NULL)";

        Log.d(TAG, CREATE_GAME_TABLE);
        db.execSQL(CREATE_GAME_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GameManagerConfigs.TABLE_GAME);
        onCreate(db);
    }

}
