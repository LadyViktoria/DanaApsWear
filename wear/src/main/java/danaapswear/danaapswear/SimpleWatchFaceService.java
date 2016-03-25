package danaapswear.danaapswear;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;

import com.activeandroid.query.Select;
import com.eveningoutpost.dexdrip.Models.ActiveBluetoothDevice;
import com.eveningoutpost.dexdrip.Models.BgReading;
import com.eveningoutpost.dexdrip.Models.Sensor;
import com.eveningoutpost.dexdrip.UtilityModels.CollectionServiceStarter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SimpleWatchFaceService extends CanvasWatchFaceService {
    int year, month, day, hour, minute;
    private static final long TICK_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1);

    @Override
    public Engine onCreateEngine() {
        return new SimpleEngine();
    }

    private class SimpleEngine extends CanvasWatchFaceService.Engine implements
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        private static final String ACTION_TIME_ZONE = "time-zone";
        private static final String TAG = "SimpleEngine";

        private SimpleWatchFace watchFace;
        private Handler timeTick;
        private GoogleApiClient googleApiClient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SimpleWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());

            timeTick = new Handler(Looper.myLooper());
            startTimerIfNecessary();

            watchFace = SimpleWatchFace.newInstance(SimpleWatchFaceService.this);
            googleApiClient = new GoogleApiClient.Builder(SimpleWatchFaceService.this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        private void startTimerIfNecessary() {
            timeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                timeTick.post(timeRunnable);
            }
        }

        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();

                if (isVisible() && !isInAmbientMode()) {
                    timeTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };

        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerTimeZoneReceiver();
                googleApiClient.connect();
            } else {
                unregisterTimeZoneReceiver();
                releaseGoogleApiClient();
            }
            startTimerIfNecessary();
        }

        private void releaseGoogleApiClient() {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                Wearable.DataApi.removeListener(googleApiClient, onDataChangedListener);
                googleApiClient.disconnect();
            }
        }

        private void unregisterTimeZoneReceiver() {
            unregisterReceiver(timeZoneChangedReceiver);
        }

        private void registerTimeZoneReceiver() {
            IntentFilter timeZoneFilter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            registerReceiver(timeZoneChangedReceiver, timeZoneFilter);
        }

        private BroadcastReceiver timeZoneChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                    watchFace.updateTimeZoneWith(intent.getStringExtra(ACTION_TIME_ZONE));
                }
            }
        };

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            watchFace.draw(canvas, bounds);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            watchFace.setAntiAlias(!inAmbientMode);
            watchFace.setShowSeconds(!isInAmbientMode());

            if (inAmbientMode) {
                watchFace.updateBackgroundColourToDefault();
                watchFace.updateDateAndTimeColourToDefault();
            } else {
                watchFace.restoreBackgroundColour();
                watchFace.restoreDateAndTimeColour();
            }
            invalidate();
            startTimerIfNecessary();
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "connected GoogleAPI");
            Wearable.DataApi.addListener(googleApiClient, onDataChangedListener);
           // Wearable.DataApi.getDataItems(googleApiClient).setResultCallback(onConnectedResultCallback);
        }

        private final DataApi.DataListener onDataChangedListener = new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents) {
                for (DataEvent event : dataEvents) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        DataItem item = event.getDataItem();
                        processConfigurationFor(item);
                    }
                    else if (event.getType() == DataEvent.TYPE_DELETED) {
                    }
                }
                dataEvents.release();
                invalidateIfNecessary();
            }
        };

        private void processConfigurationFor(DataItem item) {
            if ("/wearable_prefernces".equals(item.getUri().getPath())) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString("dex_txid", dataMap.getString("txid")).apply();
                prefs.edit().putString("getAddress", dataMap.getString("getAddress")).apply();
                prefs.edit().putString("getName", dataMap.getString("getName")).apply();
                prefs.edit().putString("CollectionMethod", dataMap.getString("CollectionMethod")).apply();
                prefs.edit().putString("dex_collection_method", dataMap.getString("CollectionMethod")).apply();
                String getAddress = prefs.getString("getAddress", "00:00:00:00:00:00");
                String getName = prefs.getString("getName", "");
                String dex_txid = prefs.getString("dex_txid", "");
                String CollectionMethod = prefs.getString("CollectionMethod", "");

                Log.d("Prefernces", "recieved: " + " getAdress: " + getAddress
                        + " getName: " + getName
                        + " dex_txid: " + dex_txid
                        + " CollectionMethod: " + CollectionMethod);
            }

            if ("/wearable_stopsensor".equals(item.getUri().getPath())) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey("StopSensor")) {
                    Sensor.stopSensor();
                    Log.d("Sensor", "sensor stopped");
                }
                else {Log.d("SENSOR", "Sensor not active please start new sensor. ");}
            }

            if ("/wearable_startsensor".equals(item.getUri().getPath())) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (Sensor.isActive()) {
                    Log.d("Sensor", "sensor is active:" + Sensor.isActive());
                } else {
                    Log.d("Sensor", "sensor is not active starting new Sensor:" + Sensor.isActive());
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    prefs.edit().putInt("year", dataMap.getInt("year")).apply();
                    prefs.edit().putInt("month", dataMap.getInt("month")).apply();
                    prefs.edit().putInt("day", dataMap.getInt("day")).apply();
                    prefs.edit().putInt("hour", dataMap.getInt("hour")).apply();
                    prefs.edit().putInt("minute", dataMap.getInt("minute")).apply();
                    year = prefs.getInt("year", 0);
                    month = prefs.getInt("month", 0);
                    day = prefs.getInt("day", 0);
                    hour = prefs.getInt("hour", 0);
                    minute = prefs.getInt("minute", 0);
                    Log.d("Sensor Date recieved: ", day + "." + month + "." + year + "  " + hour + ":" + minute);
                    //new calendar
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(day, month, year, hour, minute, 0);
                    long startTime = calendar.getTime().getTime();
                    //init sensor start
                    if (dataMap.containsKey("StartSensor")) {
                        Sensor.create(startTime);
                        Log.d("NEW SENSOR", "New Sensor started at " + startTime);
                    }
                    else {Log.d("SENSOR", "Sensor still active please stop current sensor. " + startTime);}
                }
            }

            if ("/wearable_stopcollectionservice".equals(item.getUri().getPath())) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey("StopCollectionService")) {
                    Log.d("ActiveBluetoothDevice ", "forget");
                    ActiveBluetoothDevice.forget();
                }
            }

            if ("/wearable_startcollectionservice".equals(item.getUri().getPath())) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey("StartCollectionService")) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SimpleWatchFaceService.this);
                    String getAddress = prefs.getString("getAddress", "00:00:00:00:00:00");
                    String getName = prefs.getString("getName", "");

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
                    Log.d("DexCollectionService", "DexCollectionService started " + getAddress + "  " + getName);
                }
            }

            if ("/simple_watch_face_config".equals(item.getUri().getPath())) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey("KEY_BACKGROUND_COLOUR")) {
                    String backgroundColour = dataMap.getString("KEY_BACKGROUND_COLOUR");
                    watchFace.updateBackgroundColourTo(Color.parseColor(backgroundColour));
                }

                if (dataMap.containsKey("KEY_DATE_TIME_COLOUR")) {
                    String timeColour = dataMap.getString("KEY_DATE_TIME_COLOUR");
                    watchFace.updateDateAndTimeColourTo(Color.parseColor(timeColour));
                }
            }
        }

        private final ResultCallback<DataItemBuffer> onConnectedResultCallback = new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                for (DataItem item : dataItems) {
                    processConfigurationFor(item);
                }
                dataItems.release();
                invalidateIfNecessary();
            }
        };

        @Override
        public void onConnectionSuspended(int i) {
            Log.e(TAG, "suspended GoogleAPI");
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(TAG, "connectionFailed GoogleAPI");
        }

        @Override
        public void onDestroy() {
            timeTick.removeCallbacks(timeRunnable);
            releaseGoogleApiClient();
            super.onDestroy();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            showBG();
        }

        public void showBG() {
            BgReading lastBgreading = BgReading.last();
            Log.e(TAG, "tap event " + lastBgreading);
        }
    }
}


    /*
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
    */


/*
    public void addcalibration() {
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
*/

/*
    private void callibrationCheckin() {
        if (Sensor.isActive()) {
            SyncingService.startActionCalibrationCheckin(getApplicationContext());
            Log.d("CALIBRATION", "Checked in all calibrations");
            finish();
        } else {
            Log.d("CALIBRATION", "ERROR, sensor not active");
        }
    }
    */

