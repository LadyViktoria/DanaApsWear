package danaapswear.danaapswear;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.eveningoutpost.dexdrip.Models.ActiveBluetoothDevice;
import com.eveningoutpost.dexdrip.Models.BgReading;
import com.eveningoutpost.dexdrip.Models.Calibration;
import com.eveningoutpost.dexdrip.Models.Sensor;
import com.eveningoutpost.dexdrip.UtilityModels.CollectionServiceStarter;

import java.util.Calendar;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        setAmbientEnabled();

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        //start collectionservice
        Context context = getApplicationContext();
        CollectionServiceStarter collectionServiceStarter = new CollectionServiceStarter(context);
        collectionServiceStarter.start(getApplicationContext());
    }

    // listen for touch activity
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("debug", "dispatchTouchEvent");
        showBG();
        return  super.dispatchTouchEvent(ev);
    }

    public void showBG(){
        BgReading lastBgreading = BgReading.last();
        Log.d("BGREADING", "BGReading:" + lastBgreading);

    }
    public void addcalibration(){
        String string_value =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("calibrationvalue", "");

        double calValue = Double.parseDouble(string_value);
        //Calibration.initialCalibration(calValue_1, calValue_2, getApplicationContext());
        Calibration calibration = Calibration.create(calValue, getApplicationContext());
    }

    public void sensorActivity(){


        if (Sensor.isActive()) {
            Log.d("Sensor", "sensor is active:" + Sensor.isActive());
            Log.d("Sensor", "Current Sensor started at " + Sensor.currentSensor());


        }
        else {
            Log.d("Sensor", "sensor is not active:" + Sensor.isActive());
            Log.d("Sensor", "Current Sensor started at " + Sensor.currentSensor());
            Calendar kalender = Calendar.getInstance();

            //kalender.set(Calendar.DATE, 22);
            //kalender.set(Calendar.MONTH, 2 - 1);
            //kalender.set(Calendar.YEAR, 2016);
            //kalender.set(Calendar.HOUR_OF_DAY, 13);
            //kalender.set(Calendar.MINUTE, 55);
            //kalender.set(Calendar.SECOND, 0);
            kalender.set(22, 2-1, 2016, 13, 55, 0);
            long startTime = kalender.getTime().getTime();
            Sensor.create(startTime);
            Log.d("NEW SENSOR", "New Sensor started at " + startTime);

        }
    }


    private void initBTDevice() {
        String getName =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("getName", "");
        String getAddress =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("last_connected_device_address", "00:00:00:00:00:00");
        //b4:99:4c:67:5e:67

        ActiveBluetoothDevice btDevice = new Select().from(ActiveBluetoothDevice.class)
                .orderBy("_ID desc")
                .executeSingle();

        if (btDevice == null) {
            ActiveBluetoothDevice newBtDevice = new ActiveBluetoothDevice();
            newBtDevice.name = getName;
            newBtDevice.address = getAddress;
            newBtDevice.save();
        } else {
            btDevice.name = getName;
            btDevice.address = getAddress;
            btDevice.save();
        }
    }
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getBundleExtra("datamap");
            // Display rceiveed data in UI
            String display = "Received from the data Layer\n" +
                    "Bluetooth MAC" + data.getString("getAddress") + "\n" +
                    "collectionMethod: " + data.getString("collectionMethod") + "\n" +
                    "Transmitter ID: "+ data.getString("txid") + "\n" +
                    "BG Calibration Value: "+ data.getString("calibrationvalue") + "\n" +
                    "BT name: " +  data.getString("getName");

            mTextView.setText(display);
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            prefs.edit().putString("last_connected_device_address", data.getString("getAddress")).apply();
            prefs.edit().putString("dex_collection_method", data.getString("collectionMethod")).apply();
            prefs.edit().putString("dex_txid", data.getString("txid")).apply();
            prefs.edit().putString("bt_name", data.getString("getName")).apply();
            prefs.edit().putString("calibrationvalue", data.getString("calibrationvalue")).apply();

            //set bluetooth device settings
            initBTDevice();
            addcalibration();
            sensorActivity();
        }
    }
}
