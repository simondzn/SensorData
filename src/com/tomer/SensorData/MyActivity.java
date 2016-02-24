package com.tomer.SensorData;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyActivity extends Activity implements SensorEventListener {
    /**
     * Called when the activity is first created.
     */
    SensorManager sensorManager;
    //    private ArrayList sensorData;
//    private ArrayList touchData;
//    private ArrayList rotationData;
    private JsonObject accelData;
    private JsonObject rotationData;
    private JsonObject bigObj;
    private ArrayList<JsonObject> allData;
Boolean isSample;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        isSample = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelData = new JsonObject();
        rotationData = new JsonObject();
bigObj = new JsonObject();
        allData = new ArrayList<JsonObject>();
        Button button = (Button) findViewById(R.id.button);
        Context here = this;
        Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSample) {
                    Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                    sensorManager.registerListener((SensorEventListener) here, accel, SensorManager.SENSOR_DELAY_GAME);


                    sensorManager.registerListener((SensorEventListener) here, rotation, SensorManager.SENSOR_DELAY_GAME);
                    isSample = true;
                } else {
                    sensorManager.unregisterListener((SensorEventListener) here);
                    try {
                        File sdCard = Environment.getExternalStorageDirectory();
                        File file = new File(sdCard.getAbsolutePath() + "/Tomer");
                        file.mkdirs();

                        File output = new File(file , "data.json");
//                        if(!output.exists())
//                            output.createNewFile();
                            FileWriter writer = new FileWriter(output, true);
                            writer.write(allData.toString());
                            writer.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isSample = false;
                }
            }
        });
        Button sendButton = (Button) findViewById(R.id.button2);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL url = null;
                try {
                    url = new URL("put the url bitch");
                    HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/txt");

                    DataOutputStream request = new DataOutputStream(
                            urlConnection.getOutputStream());
                    FileInputStream fStream = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Tomer", "data.json"));
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int length = -1;
                    int pro = 0;
                    while ((length = fStream.read(buffer)) != -1) {
                        request.write(buffer, 0, length);
                    }
                    fStream.close();
                    request.flush();
                    request.close();

                    InputStream responseStream = new
                            BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader responseStreamReader =
                            new BufferedReader(new InputStreamReader(responseStream));

                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((line = responseStreamReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    responseStreamReader.close();

                    String response = stringBuilder.toString();
                    responseStream.close();
                    urlConnection.disconnect();
                    Log.e(MyActivity.class.toString(), " code: " + urlConnection.getResponseCode());
                    if (urlConnection.getResponseCode() == 200) {
                    } else {
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        long futureTime = System.currentTimeMillis() + 4000;
//        while(System.currentTimeMillis()<futureTime) {
        Log.d("tag", "working");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            accelData.addProperty("x", x);
            accelData.addProperty("y", y);
            accelData.addProperty("z", z);
            accelData.addProperty("timestamp", timestamp);
            bigObj.add("Accelerometer", accelData);
            allData.add(bigObj);
        } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            rotationData.addProperty("x", x);
            rotationData.addProperty("y", y);
            rotationData.addProperty("z", z);
            rotationData.addProperty("timestamp", timestamp);
            bigObj.add("Rotation", rotationData);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}