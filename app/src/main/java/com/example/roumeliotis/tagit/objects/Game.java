package com.example.roumeliotis.tagit.objects;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Game implements Parcelable {
    private long id = -1;            //Game id, -1 is default unassigned value
    private long remote_id = -1;     //ID in server database
    private String username = null; //Name of game owner
    private String name = null;     //Name of the game
    private int team_id = -1;       //Placeholder for if Game object is used during gameplay, TODO remove if only used for db handling
    private Date time_end= null;    //Should be input and output as milliseconds

    public Game(long id, long remote_id, String username, String name, long time_end) {
        this.id = id;
        this.remote_id = remote_id;
        this.username = username;
        this.name = name;
        this.time_end = new Date(time_end);
    }

    protected Game(Parcel in) {
        id = in.readLong();
        remote_id = in.readLong();
        username = in.readString();
        name = in.readString();
        team_id = in.readInt();
        time_end = new Date(in.readLong());
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(remote_id);
        parcel.writeString(username);
        parcel.writeString(name);
        parcel.writeInt(team_id);
        parcel.writeLong(time_end.getTime());
    }


}