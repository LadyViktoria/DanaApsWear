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
import com.eveningoutpost.dexdrip.ImportedLibraries.dexcom.SyncingService;
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


        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        //start collectionservice
        //Context context = getApplicationContext();
        //CollectionServiceStarter collectionServiceStarter = new CollectionServiceStarter(context);
        //collectionServiceStarter.start(getApplicationContext());


    }

    // listen for touch activity
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Log.d("debug", "dispatchTouchEvent");
        showBG();
        return  super.dispatchTouchEvent(ev);
    }

    public void showBG(){
        BgReading lastBgreading = BgReading.last();
        Log.d("BGREADING", "BGReading:" + lastBgreading);

    }
    public void addcalibration(){
        String string_value =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("wear_calibrationvalue", "");

        double calValue = Double.parseDouble(string_value);
        //Calibration.initialCalibration(calValue_1, calValue_2, getApplicationContext());
        Calibration calibration = Calibration.create(calValue, getApplicationContext());
        Log.d("NEW CALIBRATION", "Calibration value: " + calValue);
        callibrationCheckin();
    }

    public void sensorActivity(){


        if (Sensor.isActive()) {
            Log.d("Sensor", "sensor is active:" + Sensor.isActive());
            Log.d("Sensor", "Current Sensor started at " + Sensor.currentSensor());
            String sensorstartsting =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("mobile_sensorstartsting", "");
            Log.d("NEW SENSOR", "New Sensor started at " + sensorstartsting);



        }
        else {
            Log.d("Sensor", "sensor is not active:" + Sensor.isActive());
            Log.d("Sensor", "Current Sensor started at " + Sensor.currentSensor());
            Calendar kalender = Calendar.getInstance();

            String sensorstartsting =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("mobile_sensorstartsting", "");
            Log.d("NEW SENSOR", "New Sensor started at " + sensorstartsting);


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

private void callibrationCheckin(){
    if (Sensor.isActive()) {
        SyncingService.startActionCalibrationCheckin(getApplicationContext());
        Log.d("CALIBRATION", "Checked in all calibrations");
        finish();
    } else {
        Log.d("CALIBRATION", "ERROR, sensor not active");
    }
}
    private void initBTDevice() {
        String getName =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("wear_getName", "");
        String getAddress =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("wear_getAddress", "00:00:00:00:00:00");
        //b4:99:4c:67:5e:67
        Log.d("BLUETOOTH Name", "getName: " + getName);
        Log.d("BLUETOOTH ADRESS", "getAddress: " + getAddress);


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


        Context context = getApplicationContext();
        CollectionServiceStarter restartCollectionService = new CollectionServiceStarter(context);
        restartCollectionService.restartCollectionService(getApplicationContext());

    }
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getBundleExtra("datamap");
            // Display rceiveed data in UI
            String display = "Received from the data Layer\n" +
                    "Bluetooth MAC" + data.getString("mobile_getAddress") + "\n" +
                    "collectionMethod: " + data.getString("mobile_collectionMethod") + "\n" +
                    "Transmitter ID: "+ data.getString("mobile_txid") + "\n" +
                    "BG Calibration Value: "+ data.getString("mobile_calibrationvalue") + "\n" +
                    "Sensor started at: "+ data.getString("mobile_sensorstartsting") + "\n" +
                    "BT name: " +  data.getString("mobile_getName");

            mTextView.setText(display);
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            prefs.edit().putString("wear_getAddress", data.getString("mobile_getAddress")).apply();
            prefs.edit().putString("wear_collection_method", data.getString("mobile_collectionMethod")).apply();
            prefs.edit().putString("wear_txid", data.getString("mobile_txid")).apply();
            prefs.edit().putString("wear_getName", data.getString("mobile_getName")).apply();
            prefs.edit().putString("wear_calibrationvalue", data.getString("mobile_calibrationvalue")).apply();
            prefs.edit().putString("dex_collection_method", data.getString("mobile_collectionMethod")).apply();
            prefs.edit().putString("wear_sensorstartsting", data.getString("mobile_sensorstartsting")).apply();



            //set bluetooth device settings

            addcalibration();
            sensorActivity();
            initBTDevice();
        }
    }
}
