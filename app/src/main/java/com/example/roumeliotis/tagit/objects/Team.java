package com.example.roumeliotis.tagit.objects;

import java.io.Serializable;

public class Team implements Serializable{
    private long id = -1;
    private long game_id = -1;
    private long remote_id = -1;
    private String name = null;
    private String colour = null;

    public Team(long id, long game_id, long remote_id, String name, String colour) {
        this.id = id;
        this.game_id = game_id;
        this.remote_id = remote_id;
        this.name = name;
        this.colour = colour;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGame_id() {
        return game_id;
    }

    public void setGame_id(long game_id) {
        this.game_id = game_id;
    }

    public long getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(long remote_id) {
        this.remote_id = remote_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
