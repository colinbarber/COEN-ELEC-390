package com.example.roumeliotis.tagit.objects;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.roumeliotis.tagit.R;

public class SoundEffects {
    private static SoundPool soundPool;
    private static int tagHit;
    private static int taggedAlready;
    private static int winGame;
    private static int loseGame;

    public SoundEffects(Context context){

        //SoundPool (int maxStreams, int streamType, int srcQuality)
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        tagHit = soundPool.load(context, R.raw.taghit_sound, 1);
        loseGame = soundPool.load(context, R.raw.gamelost_sound, 0);
        winGame = soundPool.load(context, R.raw.yaygamewon_sound, 0);
        taggedAlready = soundPool.load(context, R.raw.alreadytagged_sound, 0);

    }

    public void playTagHitSound(){
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(tagHit, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playLoseGameSound(){
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(loseGame, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playAlreadyTaggedSound(){
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(taggedAlready, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playGameWinSound(){
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(winGame, 1.0f, 1.0f, 1, 0, 1.0f);
    }

}
