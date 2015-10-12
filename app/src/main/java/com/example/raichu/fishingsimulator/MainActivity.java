package com.example.raichu.fishingsimulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TriggerEventListener mTriggerEventListener;
    private boolean isShaken;
    private Timer timer;
    private TimerTask timerTask;
    private static Context context;
    private Random random;
    private float random_frequency = 0.35f;
    private int default_pic = R.drawable.qmark;
    private int pic_ids[] = {R.drawable.fish1, R.drawable.fish2, R.drawable.fish3, R.drawable.fish4, R.drawable.boots, R.drawable.shark, R.drawable.trash};
    private Map invalid;
    private int previous_id;
    private int total_points = 0;
    float max_x, min_x, max_y, min_y, max_z, min_z;

    float grav_x, grav_y, grav_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grav_x = grav_y = grav_z = 0f;
        max_x = max_y = max_z = -100f;
        min_x = min_y = min_z = 100f;

        isShaken = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        random = new Random();
        context = this;
        invalid = new HashMap();
        invalid.put(R.drawable.boots, "You reeled a pair of boots. Ew stinky!");
        invalid.put(R.drawable.trash, "You found some trash. What a dirty ocean...");
        invalid.put(R.drawable.shark, "How did you fish out a shark?!!!");

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imagev = (ImageView) findViewById(R.id.picture);
                        TextView view = (TextView) findViewById(R.id.main_text);
                        int image_index;
                        String text;

                        if (random.nextFloat() < random_frequency) {
                            image_index = pic_ids[random.nextInt(pic_ids.length)];
                            text = "REEEL MEE!";
                        } else {
                            image_index = default_pic;
                            text = "Reel only the fishes...";
                        }

                        if (isShaken) {
                            TextView descriptionv = (TextView) findViewById(R.id.description);
                            if (invalid.containsKey(previous_id)) {
                                String description_text = (String) invalid.get(previous_id);
                                descriptionv.setText(description_text);

                                total_points -= 150;
                            } else if (previous_id != R.drawable.qmark) {
                                descriptionv.setText("You got a fish. HOORAY!");

                                total_points += 100;
                            }
                            TextView scorev = (TextView) findViewById(R.id.score);
                            scorev.setText("Total Points: " + total_points);
                        }

//                        TextView debugv = (TextView) findViewById(R.id.debug);
//                        String debugt = "";
////                        debugt += "max x: " + max_x + "\t\tmin x: " + min_x + "\n";
////                        debugt += "max y: " + max_y + "\t\tmin y: " + min_y + "\n";
//                        debugt += "max z: " + max_z + "\t\tmin z: " + min_z;
//                        debugv.setText(debugt);
////
                        max_x = max_y = max_z = -100f;
                        min_x = min_y = min_z = 100f;

                        previous_id = image_index;
                        imagev.setImageResource(image_index);
                        view.setText(text);
                        isShaken = false;
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1500);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private String get_Shaken() {
        return (isShaken) ? "true" : "false";
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        grav_x = event.values[0];
        grav_y = event.values[1];
        grav_z = event.values[2];

        max_x = Math.max(max_x, grav_x);
        min_x = Math.min(min_x, grav_x);
        max_y = Math.max(max_y, grav_y);
        min_y = Math.min(min_y, grav_y);
        max_z = Math.max(max_z, grav_z);
        min_z = Math.min(min_z, grav_z);

        if (max_z - min_z > 5f) isShaken = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    public void testDialog(View v) {
//        dialog = new AlertDialog.Builder(this).setTitle("test dialog").setMessage("tesing notification").setPositiveButton("yes", new
//        DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) { dialog = null; }
//        }).show();
//    }

}
