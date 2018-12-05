package com.example.roumeliotis.tagit.db;

public class GameManagerConfigs {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GAMES.db";

    public static final String TABLE_GAME = "GAMES";
    public static final String GAME_REMOTE_ID = "remote_id";
    public static final String GAME_ID_COLUMN = "id";
    public static final String GAME_OWNER_NAME_COLUMN = "username";
    public static final String GAME_NAME_COLUMN = "name";
    public static final String GAME_ENDTIME_COLUMN = "time_end";

    public static final String TABLE_TEAM = "TEAMS";
    public static final String TEAM_ID= "id";
    public static final String TEAM_REMOTE_ID = "remote_id";
    public static final String TEAM_GAME_ID = "game_id";
    public static final String TEAM_NAME = "name";
    public static final String TEAM_COLOUR = "colour";

    public static final String TABLE_TAG = "TAGS";
    public static final String TAG_ID = "id";
    public static final String TAG_REMOTE_ID = "remote_id";
    public static final String TAG_GAME_ID = "game_id";
    public static final String TAG_HINT = "hint";
    public static final String TAG_POINT = "point";
}
