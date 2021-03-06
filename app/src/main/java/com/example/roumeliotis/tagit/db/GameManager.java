package com.example.roumeliotis.tagit.db;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.roumeliotis.tagit.objects.Game;
import com.example.roumeliotis.tagit.objects.NFCTag;
import com.example.roumeliotis.tagit.objects.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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

        //Create Game Table
        String CREATE_GAME_TABLE = "CREATE TABLE " + GameManagerConfigs.TABLE_GAME + "(" +
                GameManagerConfigs.GAME_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameManagerConfigs.GAME_REMOTE_ID + " INTEGER NOT NULL UNIQUE, " +
                GameManagerConfigs.GAME_NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
                GameManagerConfigs.GAME_OWNER_NAME_COLUMN + " TEXT NOT NULL, " +
                GameManagerConfigs.GAME_ENDTIME_COLUMN + " INTEGER NOT NULL)";

        Log.d(TAG, CREATE_GAME_TABLE);
        db.execSQL(CREATE_GAME_TABLE);

        //Create Team Table
        String CREATE_TEAM_TABLE = "CREATE TABLE " + GameManagerConfigs.TABLE_TEAM + "(" +
                GameManagerConfigs.TEAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameManagerConfigs.TEAM_REMOTE_ID + " INTEGER NOT NULL UNIQUE, " +
                GameManagerConfigs.TEAM_GAME_ID + " INTEGER NOT NULL, " +
                GameManagerConfigs.TEAM_NAME + " TEXT NOT NULL, " +
                GameManagerConfigs.TEAM_COLOUR + " TEXT NOT NULL)";

        Log.d(TAG, CREATE_TEAM_TABLE);
        db.execSQL(CREATE_TEAM_TABLE);

        //Create Tag Table
        String CREATE_TAG_TABLE = "CREATE TABLE " + GameManagerConfigs.TABLE_TAG + "(" +
                GameManagerConfigs.TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameManagerConfigs.TAG_REMOTE_ID + " INTEGER NOT NULL UNIQUE, " +
                GameManagerConfigs.TAG_GAME_ID + " INTEGER NOT NULL, " +
                GameManagerConfigs.TAG_HINT + " TEXT NOT NULL, " +
                GameManagerConfigs.TAG_POINT +  " INTEGER NOT NULL)";

        Log.d(TAG,CREATE_TAG_TABLE);
        db.execSQL(CREATE_TAG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GameManagerConfigs.TABLE_GAME);
        onCreate(db);
    }

    public long insertGame(Game game){
        Log.d(TAG, "insertGame");

        long id = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GameManagerConfigs.GAME_NAME_COLUMN, game.getName());
        contentValues.put(GameManagerConfigs.GAME_REMOTE_ID, game.getRemote_id());
        contentValues.put(GameManagerConfigs.GAME_OWNER_NAME_COLUMN, game.getUsername());
        contentValues.put(GameManagerConfigs.GAME_ENDTIME_COLUMN, game.getTime_end());

        try {
            id = sqLiteDatabase.replaceOrThrow(GameManagerConfigs.TABLE_GAME, null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        Log.d(TAG, "success");
        return id;
    }

    public long insertTeam(Team team){
        Log.d(TAG, "insertTeam");

        long id = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GameManagerConfigs.TEAM_GAME_ID, team.getGame_id());
        contentValues.put(GameManagerConfigs.TEAM_REMOTE_ID, team.getRemote_id());
        contentValues.put(GameManagerConfigs.TEAM_NAME, team.getName());
        contentValues.put(GameManagerConfigs.TEAM_COLOUR, team.getColour());

        try {
            id = sqLiteDatabase.replaceOrThrow(GameManagerConfigs.TABLE_TEAM, null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        Log.d(TAG, "success");
        return id;
    }

    public long insertTag(NFCTag tag){
        Log.d(TAG, "insertTag");

        long id = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GameManagerConfigs.TAG_GAME_ID, tag.getGame_id());
        contentValues.put(GameManagerConfigs.TAG_REMOTE_ID, tag.getRemote_id());
        contentValues.put(GameManagerConfigs.TAG_HINT, tag.getHint());
        contentValues.put(GameManagerConfigs.TAG_POINT, 0);

        try {
            id = sqLiteDatabase.replaceOrThrow(GameManagerConfigs.TABLE_TAG, null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        Log.d(TAG, "success");
        return id;
    }

    public List<Game> getAllGames(){
        Log.d(TAG,"getAllGames");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(GameManagerConfigs.TABLE_GAME, null, null, null, null, null, null, null);
            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Game> courseList = new ArrayList<>();
                    do {
                        long id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.GAME_ID_COLUMN));
                        long remote_id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.GAME_REMOTE_ID));
                        String username = cursor.getString(cursor.getColumnIndex(GameManagerConfigs.GAME_OWNER_NAME_COLUMN));
                        String name = cursor.getString(cursor.getColumnIndex(GameManagerConfigs.GAME_NAME_COLUMN));
                        long time_end = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.GAME_ENDTIME_COLUMN));

                        courseList.add(new Game(id,remote_id,username, name,time_end));
                    }   while (cursor.moveToNext());


                    Log.d(TAG, "success");
                    return courseList;
                }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public Game getGameByID(long id){

        Log.d(TAG, "getGameByID");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        Game game = null;
        try {
            String SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s",GameManagerConfigs.TABLE_GAME,
                    GameManagerConfigs.GAME_ID_COLUMN, id);
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);


            if(cursor.moveToFirst()){
                long remote_id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.GAME_REMOTE_ID));
                String username = cursor.getString(cursor.getColumnIndex(GameManagerConfigs.GAME_OWNER_NAME_COLUMN));
                String name = cursor.getString((cursor.getColumnIndex(GameManagerConfigs.GAME_NAME_COLUMN)));
                long time_end = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.GAME_ENDTIME_COLUMN));

               game = new Game(id, remote_id, username, name, time_end);
            }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return game;
    }

    public Game getGameByRemoteID(long remote_id){

        Log.d(TAG, "getGameByRemoteID");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        Game game = null;
        try {
            String SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s",GameManagerConfigs.TABLE_GAME,
                    GameManagerConfigs.GAME_REMOTE_ID, remote_id);
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);


            if(cursor.moveToFirst()){
                long id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.GAME_ID_COLUMN));
                String username = cursor.getString(cursor.getColumnIndex(GameManagerConfigs.GAME_OWNER_NAME_COLUMN));
                String name = cursor.getString((cursor.getColumnIndex(GameManagerConfigs.GAME_NAME_COLUMN)));
                long time_end = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.GAME_ENDTIME_COLUMN));

                game = new Game(id, remote_id, username, name, time_end);
            }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return game;
    }

    public Team getTeamByRemoteID(long remote_id){

        Log.d(TAG, "getTeamByRemoteID");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        Team team = null;
        try {
            String SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s",GameManagerConfigs.TABLE_TEAM,
                    GameManagerConfigs.TEAM_REMOTE_ID, remote_id);
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);


            if(cursor.moveToFirst()){
                long id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.TEAM_ID));
                String team_name = cursor.getString(cursor.getColumnIndex(GameManagerConfigs.TEAM_NAME));
                String team_colour = cursor.getString((cursor.getColumnIndex(GameManagerConfigs.TEAM_COLOUR)));
                long game_id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.TEAM_GAME_ID));

                team = new Team(id, game_id, remote_id, team_name, team_colour);
            }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return team;
    }

    public NFCTag getTagByRemoteID(long remote_id){

        Log.d(TAG, "getTagByRemoteID");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        NFCTag nfcTag = null;
        try {
            String SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s",GameManagerConfigs.TABLE_TAG,
                    GameManagerConfigs.TAG_REMOTE_ID, remote_id);
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);


            if(cursor.moveToFirst()){
                long id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.TAG_ID));
                String hint = cursor.getString((cursor.getColumnIndex(GameManagerConfigs.TAG_HINT)));
                long game_id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.TAG_GAME_ID));

                nfcTag = new NFCTag(id, remote_id, game_id, hint);
            }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return nfcTag;
    }

    public List<Team> getTeamsByGameID(long gameId){

        Log.d(TAG, "getTeamsByGameID");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try {

            String SELECT_QUERY= String.format("SELECT * FROM %s WHERE %s = %s",GameManagerConfigs.TABLE_TEAM,
                    GameManagerConfigs.TEAM_GAME_ID, gameId);
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY,null);
            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Team> teamList = new ArrayList<>();
                    do {
                        long id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.TEAM_ID));
                        long remote_id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.TEAM_REMOTE_ID));
                        String name = cursor.getString(cursor.getColumnIndex(GameManagerConfigs.TEAM_NAME));
                        String colour = cursor.getString(cursor.getColumnIndex(GameManagerConfigs.TEAM_COLOUR));

                        teamList.add(new Team(id,gameId,remote_id,name,colour));
                    }   while (cursor.moveToNext());

                    return teamList;
                }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public List<NFCTag> getTagsByGameID(long gameId){

        Log.d(TAG, "getTagsByGameID");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try {

            String SELECT_QUERY= String.format("SELECT * FROM %s WHERE %s = %s",GameManagerConfigs.TABLE_TAG,
                    GameManagerConfigs.TAG_GAME_ID, gameId);
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY,null);
            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<NFCTag> tagList = new ArrayList<>();
                    do {
                        long id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.TAG_ID));
                        long remote_id = cursor.getLong(cursor.getColumnIndex(GameManagerConfigs.TAG_REMOTE_ID));
                        String hint = cursor.getString(cursor.getColumnIndex(GameManagerConfigs.TAG_HINT));
                        int point = cursor.getInt(cursor.getColumnIndex(GameManagerConfigs.TAG_POINT));

                        NFCTag tempTag = new NFCTag(id,remote_id,gameId,hint);
                        if (point == 1) tempTag.markPoint();
                        tagList.add(tempTag);
                    }   while (cursor.moveToNext());

                    return tagList;
                }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public void updateTagPoints(NFCTag nfcTag){
        int points = nfcTag.getPoints();
        long id = nfcTag.getId();

        Log.d(TAG, "updateTagPoints");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String UPDATE_QUERY = "UPDATE " + GameManagerConfigs.TABLE_TAG +
                " SET " + GameManagerConfigs.TAG_POINT +
                " = " + points +
                " WHERE " + GameManagerConfigs.TAG_ID +
                " = " + id;
        try {
            sqLiteDatabase.execSQL(UPDATE_QUERY);
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        Log.d(TAG, "success");
    }
}
