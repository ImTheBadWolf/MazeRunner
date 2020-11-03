package com.example.mazeball;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


public class GameActivity extends AppCompatActivity implements EventListener {

    SensorManager sensorManager;
    Sensor accSensor;
    game_view gameView;
    pause_view pauseView;
    Win_view winView;
    ImageButton muteButton;
    int mapIndex = 0;
    int[][][] mazeMaps = {
            {
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
            },
            {
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,3,0,2,2,2,2,2,2,0,0,0,0,0,0,0,2,0,0,1},
                    {1,2,0,0,0,0,0,0,0,0,2,2,2,0,2,2,0,0,2,1},
                    {4,0,2,2,2,2,2,2,2,2,2,0,2,0,0,2,0,2,2,1},
                    {1,0,2,2,2,0,2,0,0,0,0,0,2,2,0,0,0,0,0,1},
                    {1,0,0,0,0,0,2,0,2,0,2,2,2,2,2,2,2,2,0,1},
                    {1,0,2,2,2,2,0,0,2,0,2,2,0,0,0,0,0,0,0,1},
                    {1,0,0,2,2,0,0,2,2,0,0,2,0,2,2,2,2,2,2,1},
                    {1,2,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
            }
    };

    boolean muted; //TODO pass this variable from main menu where its loaded from global settings file

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gameView = findViewById(R.id.gameView);
        pauseView = findViewById(R.id.pauseView);
        winView = findViewById(R.id.winview);
        muteButton = findViewById(R.id.imageButton4);


        gameView.setMazeMap(cloneArray(mapIndex));
        gameView.attachActivity(this);
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(accEListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(accEListener);
    }
    public SensorEventListener accEListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0]*10;
            float y = event.values[1]*10;
            gameView.setRotationValues(x,y);
        }
    };


    public void pauseGame(View view) {
        pauseView.setVisibility(View.VISIBLE);
        findViewById(R.id.imageButton).setVisibility(View.INVISIBLE);
        gameView.setPaused(true);
    }
    public void resumeGame(View view) {
        pauseView.setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton).setVisibility(View.VISIBLE);
        gameView.setPaused(false);
    }
    public void quitGame(View view){
        NavUtils.navigateUpFromSameTask(this);
    }
    public void restartGame(View view){
        gameView.restart();
    }
    public void muteGame(View view){
        muteButton.setImageResource( muted ? R.mipmap.mute_foreground : R.mipmap.muteoff_foreground );
        muted = !muted;
    }
    public void nextLevel(View view){
        this.nextLevel();
    }
    public void nextLevel(){
        mapIndex++;
        if(mapIndex > 2)
            mapIndex = 0;
        gameView.setMazeMap(cloneArray(mapIndex));
        winView.setVisibility(View.INVISIBLE);
    }
    private int[][] cloneArray(int mapIndex){
        int[][] mazeMap = new int[mazeMaps[mapIndex].length][];
        for (int i = 0; i < mazeMaps[mapIndex].length; i++)
            mazeMap[i] = mazeMaps[mapIndex][i].clone();
        return mazeMap;
    }

    @Override
    public void levelFinished() {
        winView.setVisibility(View.VISIBLE);
    }

}