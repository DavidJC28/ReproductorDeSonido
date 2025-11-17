package com.example.reproductordesonido;

import androidx.appcompat.app.AppCompatActivity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView txtCurrent, txtTotal, txtSongName;
    private Handler handler = new Handler();
    private Runnable runnable;

    private SoundPool soundPool;
    private int sound1, sound2, sound3, sound4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.cancion);

        // Views
        seekBar = findViewById(R.id.seekBar);
        txtCurrent = findViewById(R.id.txtCurrent);
        txtTotal = findViewById(R.id.txtTotal);
        txtSongName = findViewById(R.id.txtSongName);
        txtSongName.setText("Solid State Scouter");

        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPause = findViewById(R.id.btnPause);

        // Sonidos cortos (SoundPool)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(4, android.media.AudioManager.STREAM_MUSIC, 0);
        }

        sound1 = soundPool.load(this, R.raw.sonido1, 1);
        sound2 = soundPool.load(this, R.raw.sonido2, 1);
        sound3 = soundPool.load(this, R.raw.sonido3, 1);
        sound4 = soundPool.load(this, R.raw.sonido4, 1);

        // Botones SoundPool
        findViewById(R.id.btnSound1).setOnClickListener(v -> soundPool.play(sound1, 1, 1, 0, 0, 1));
        findViewById(R.id.btnSound2).setOnClickListener(v -> soundPool.play(sound2, 1, 1, 0, 0, 1));
        findViewById(R.id.btnSound3).setOnClickListener(v -> soundPool.play(sound3, 1, 1, 0, 0, 1));
        findViewById(R.id.btnSound4).setOnClickListener(v -> soundPool.play(sound4, 1, 1, 0, 0, 1));

        // Play/Pause MediaPlayer
        btnPlay.setOnClickListener(v -> {
            mediaPlayer.start();
            updateSeekBar();
        });

        btnPause.setOnClickListener(v -> mediaPlayer.pause());

        // SeekBar
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                txtCurrent.setText(formatTime(mediaPlayer.getCurrentPosition()));
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        txtTotal.setText(formatTime(mediaPlayer.getDuration()));
    }

    private void updateSeekBar() {
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                txtCurrent.setText(formatTime(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private String formatTime(int ms) {
        int minutes = (ms / 1000) / 60;
        int seconds = (ms / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacksAndMessages(null);
        soundPool.release();
    }
}
