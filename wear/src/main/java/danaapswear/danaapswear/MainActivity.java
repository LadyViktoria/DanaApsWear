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
import android.util.Log;
import android.view.MotionEvent;

import com.activeandroid.query.Select;
import com.eveningoutpost.dexdrip.ImportedLibraries.dexcom.SyncingService;
import com.eveningoutpost.dexdrip.Models.ActiveBluetoothDevice;
import com.eveningoutpost.dexdrip.Models.BgReading;
import com.eveningoutpost.dexdrip.Models.Calibration;
import com.eveningoutpost.dexdrip.Models.Sensor;
import com.eveningoutpost.dexdrip.UtilityModels.CollectionServiceStarter;

import java.util.Calendar;

public class MainActivity extends WearableActivity {

    int year, month ,day ,hour ,minute;
    String txid, selectcollectionmethod, getAddress, calibration, getName;
    Boolean stopsensor , startbt, calibrationcheckin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        startbt = true;
        startbt();
    }

    private void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //read shared prefs
        calibration = sp.getString("calibration", "");
        txid = sp.getString("dex_txid", "");
        selectcollectionmethod = sp.getString("selectcollectionmethod", "");
        getAddress = sp.getString("getAddress", "00:00:00:00:00:00");
        getName = sp.getString("getName", "");
        year = sp.getInt("year", 0);
        month = sp.getInt("month", 0);
        day = sp.getInt("day", 0);
        hour = sp.getInt("hour", 0);
        minute = sp.getInt("minute", 0);
        stopsensor = sp.getBoolean("stopsensor", false);
        calibrationcheckin = sp.getBoolean("calibrationcheckin", false);
    }

    // listen for touch activity
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        showBG();
        return  super.dispatchTouchEvent(ev);
    }

    public void showBG(){
        BgReading lastBgreading = BgReading.last();
        Log.d("BGREADING", "BGReading:" + lastBgreading);

    }
    public void addcalibration(){
        loadPrefs();
        if (calibrationcheckin == true) {
            //double calValue = Double.parseDouble(calibration);
            //Calibration calibration = Calibration.create(calValue, getApplicationContext());
            //Log.d("NEW CALIBRATION", "Calibration value: " + calValue);
            //callibrationCheckin();

            double calValue_1 = Double.parseDouble(calibration);
            double calValue_2 = Double.parseDouble(calibration);
            Calibration.initialCalibration(calValue_1, calValue_2, getApplicationContext());

            calibrationcheckin = false;
        }
    }

    public void stopsensor(){
        loadPrefs();
        if (stopsensor == true){
            Sensor.stopSensor();
            Log.d("Sensor", "sensor stopped");}
        else{
            if (Sensor.isActive()) {Log.d("Sensor", "sensor is active:" + Sensor.isActive());}
            else {
                Log.d("Sensor", "sensor is not active starting new Sensor:" + Sensor.isActive());
                //new calendar
                Calendar calendar = Calendar.getInstance();
                calendar.set(day, month, year, hour, minute, 0);
                long startTime = calendar.getTime().getTime();
                //init sensor start
                Sensor.create(startTime);
                Log.d("NEW SENSOR", "New Sensor started at " + startTime);
            }
        }
    }

    public void startbt(){
        loadPrefs();
        if (startbt == true){
            initBTDevice();
            Log.d("Bluetooth", "started");
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
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            prefs.edit().putString("getAddress", data.getString("getAddress")).apply();
            prefs.edit().putString("dex_txid", data.getString("txid")).apply();
            prefs.edit().putString("getName", data.getString("getName")).apply();
            prefs.edit().putString("calibration", data.getString("calibration")).apply();
            prefs.edit().putString("selectcollectionmethod", data.getString("selectcollectionmethod")).apply();
            prefs.edit().putInt("year", data.getInt("year")).apply();
            prefs.edit().putInt("month", data.getInt("month")).apply();
            prefs.edit().putInt("day", data.getInt("day")).apply();
            prefs.edit().putInt("hour", data.getInt("hour")).apply();
            prefs.edit().putInt("minute", data.getInt("minute")).apply();
            prefs.edit().putBoolean("stopsensor", data.getBoolean("stopsensor")).apply();
            prefs.edit().putBoolean("calibrationcheckin", data.getBoolean("calibrationcheckin")).apply();
            stopsensor();
            addcalibration();
        }
    }
}
