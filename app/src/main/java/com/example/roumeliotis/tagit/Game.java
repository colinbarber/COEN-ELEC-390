package com.example.roumeliotis.tagit;
import java.util.Date;

public class Game {
    private long id = -1;            //Game id, -1 is default unassigned value
    private long remote_id = -1;     //ID in server database
    private String username = null; //Name of game owner
    private String name = null;     //Name of the game
    private int team_id = -1;       //Placeholder for if Game object is used during gameplay, TODO remove if only used for db handling
    private Date time_end= null;    //Should be input and output as milliseconds

    public Game(long id, String username, String name, long time_end) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.time_end = new Date(time_end);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public long getTime_end() {
        return time_end.getTime();
    }

    public void setTime_end(long time_end) {
        this.time_end = new Date(time_end);
    }

    public long getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(long remote_id) {
        this.remote_id = remote_id;
    }
}
