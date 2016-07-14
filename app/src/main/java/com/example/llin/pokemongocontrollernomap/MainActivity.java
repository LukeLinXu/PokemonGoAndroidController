package com.example.llin.pokemongocontrollernomap;

import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private Button up;
    private Button down;
    private Button left;
    private Button right;
    private double lng;
    private double lat;
    private static final double step = 0.00001;
    private SharedPrefPersistence sharedPrefPersistence;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = (TextView) findViewById(R.id.location);
        up = (Button) findViewById(R.id.up);
        down = (Button) findViewById(R.id.down);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        right.setOnClickListener(this);
        left.setOnClickListener(this);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPrefPersistence.save("lng", String.valueOf(lng));
        sharedPrefPersistence.save("lat", String.valueOf(lat));
        if (server != null)
            server.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.up:
                lat += step;
                break;
            case R.id.down:
                lat -= step;
                break;
            case R.id.left:
                lng -= step;
                break;
            case R.id.right:
                lng += step;
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
