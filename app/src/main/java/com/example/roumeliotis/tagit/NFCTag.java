package com.example.roumeliotis.tagit;

public class NFCTag {
    private long id = -1;
    private long remote_id = -1;
    private long game_id = -1;
    private String hint = null;
    private int points = 0;

    public NFCTag(long id, long remote_id, long game_id, String hint) {
        this.id = id;
        this.remote_id = remote_id;
        this.game_id = game_id;
        this.hint = hint;
    }

    public long getId() {
        return id;
    }

    public long getRemote_id() {
        return remote_id;
    }

    public long getGame_id() {
        return game_id;
    }

    public String getHint() {
        return hint;
    }

    public int getPoints() {
        return points;
    }

    public void markPoint(){
        this.points = 1;
    }
}
