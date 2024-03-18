package com.example.ramadan1;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {
    private static MediaPlayer mediaPlayer;
    private static int PLAY_SOUND_COUNT = 1; // Set the number of times to play the sound

    public static void play(Context context, int rawResourceId) {
        stop();  // Stop any existing playback
        mediaPlayer = MediaPlayer.create(context, rawResourceId);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                PLAY_SOUND_COUNT--; // Decrement the play count
                if (PLAY_SOUND_COUNT > 0) {
                    mp.start(); // Restart the sound for the next loop
                } else {
                    stop(); // Optionally stop playback when the desired count is reached
                }
            }
        });

        mediaPlayer.start(); // Start Playback
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
