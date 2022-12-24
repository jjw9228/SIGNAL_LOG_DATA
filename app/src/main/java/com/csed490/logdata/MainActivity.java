package com.csed490.logdata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private File emotionFile;
    private File callFile;

    private boolean isGoodTimeToCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();

        String emotionFileName = getFilesDir() + "/emotion.csv";
        String callFileName = getFilesDir() + "/call.csv";

        emotionFile = new File(emotionFileName);
        callFile = new File(callFileName);

        try {
            BufferedReader br = new BufferedReader(new FileReader(callFile));
            String line = "";
            String text = "";
            while ((line=br.readLine()) != null){
                text = line;
            }
            String callState = text.split(",")[0];
            if (Objects.equals(callState, "good")){
                isGoodTimeToCall = true;
            }
            else {
                isGoodTimeToCall = false;
            }

        } catch (FileNotFoundException e) {
            isGoodTimeToCall = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        String msg = isGoodTimeToCall ? "good" : "bad";
        ((TextView)findViewById(R.id.currentState)).setText("current state : " + msg);



    }

    private void setListeners(){
        findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGoodTimeToCall = !isGoodTimeToCall;
                String msg = isGoodTimeToCall ? "good" : "bad";
                Date nowDate = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                String date = simpleDateFormat.format(nowDate);

                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(callFile, true));
                    bw.write(msg + "," + date);
                    bw.newLine();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bw != null){
                        try {
                            bw.flush();
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ((TextView)findViewById(R.id.currentState)).setText("current state : " + msg);
            }
        });
        findViewById(R.id.locationStartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationService();

            }
        });
        findViewById(R.id.locationStopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocationService();
            }
        });
        findViewById(R.id.emotionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BufferedWriter bw = null;
                Date nowDate = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                try {
                    bw = new BufferedWriter(new FileWriter(emotionFile, true));
                    bw.write(simpleDateFormat.format(nowDate));
                    bw.newLine();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bw != null){
                        try {
                            bw.flush();
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService(){
        if (!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
}