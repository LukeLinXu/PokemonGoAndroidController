package com.example.llin.pokemongocontrollernomap;

import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Properties;

import fi.iki.elonen.NanoHTTPD;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PORT = 8765;
    private MyHTTPD server;
    private TextView location;
    private Button northeast;
    private Button northwest;
    private Button southeast;
    private Button southwest;
    private Button north;
    private Button west;
    private Button south;
    private Button east;
    private Button walk;
    private Button drive;
    private double lng;
    private double lat;
    private double lngtime = 0;
    private double lattime = 0;
    private double step = step_walk;
    private static final double step_walk = 0.1/60/60;
    private static final double step_drive = step_walk*8;
    private CountDownTimer countDownTimer;

    private SharedPrefPersistence sharedPrefPersistence;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = (TextView) findViewById(R.id.location);
        northeast = (Button) findViewById(R.id.northeast);
        northwest = (Button) findViewById(R.id.northwest);
        southeast = (Button) findViewById(R.id.southeast);
        southwest = (Button) findViewById(R.id.southwest);
        north = (Button) findViewById(R.id.north);
        west = (Button) findViewById(R.id.west);
        east = (Button) findViewById(R.id.east);
        south = (Button) findViewById(R.id.south);
        walk = (Button) findViewById(R.id.speed_control_walk);
        drive = (Button) findViewById(R.id.speed_control_drive);

        location.setOnClickListener(this);
        north.setOnClickListener(this);
        northeast.setOnClickListener(this);
        northwest.setOnClickListener(this);
        south.setOnClickListener(this);
        southeast.setOnClickListener(this);
        southwest.setOnClickListener(this);
        east.setOnClickListener(this);
        west.setOnClickListener(this);
        drive.setOnClickListener(this);
        walk.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPrefPersistence = new SharedPrefPersistence(MainActivity.this);
        if(sharedPrefPersistence.read("lng") == null){
            lng = -79.063919;
            lat = 43.256777;
        }else {
            lng = Double.parseDouble(sharedPrefPersistence.read("lng"));
            lat = Double.parseDouble(sharedPrefPersistence.read("lat"));
        }
        TextView textIpaddr = (TextView) findViewById(R.id.ipaddr);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        textIpaddr.setText("Please access! http://" + formatedIpAddress + ":" + PORT);

        try {
            server = new MyHTTPD();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        countDownTimer = new CountDownTimer(1000*60*60*24, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                lat += step * lattime;
                lng += step * lngtime;
                Log.d("location:", "onTick: "+lat+","+lng);
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        sharedPrefPersistence.save("lng", String.valueOf(lng));
        sharedPrefPersistence.save("lat", String.valueOf(lat));
        if (server != null)
            server.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.north:
                lattime = 1;
                lngtime = 0;
                break;
            case R.id.northeast:
                lattime = 0.707;
                lngtime = 0.707;
                break;
            case R.id.northwest:
                lattime = 0.707;
                lngtime = -0.707;
                break;
            case R.id.south:
                lattime = -1;
                lngtime = 0;
                break;
            case R.id.southeast:
                lattime = -0.707;
                lngtime = 0.707;
                break;
            case R.id.southwest:
                lattime = -0.707;
                lngtime = -0.707;
                break;
            case R.id.east:
                lattime = 0;
                lngtime = 1;
                break;
            case R.id.west:
                lattime = 0;
                lngtime = -1;
                break;


            case R.id.location:
                lattime = 0;
                lngtime = 0;
                break;

            case R.id.speed_control_drive:
                step = step_drive;
                break;
            case R.id.speed_control_walk:
                step = step_walk;
                break;

        }
        location.setText(lat+","+lng);
    }

    private class MyHTTPD extends NanoHTTPD {
        public MyHTTPD() throws IOException {
            super(PORT);
        }

        @Override
        public Response serve(IHTTPSession session) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lng", m1(lng));
                jsonObject.put("lat", m1(lat));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new Response(jsonObject.toString());
        }
    }

    public String m1(double f) {
        DecimalFormat df = new DecimalFormat("#.000000000000");
        return df.format(f);
    }
}
