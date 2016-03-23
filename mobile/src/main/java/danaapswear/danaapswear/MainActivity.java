package danaapswear.danaapswear;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;



public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleClient;
    Button configbutton, sendconfigButton, sendcalibrationButton;
    EditText editcalibration, editintercept, editslope;

    int year, month ,day ,hour ,minute;
    String txid, selectcollectionmethod, getAddress, calibration;
    Boolean stopsensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configbutton = (Button) findViewById(R.id.configButton);
        configbutton.setOnClickListener(this);
        sendconfigButton = (Button) findViewById(R.id.sendconfigButton);
        sendconfigButton.setOnClickListener(this);

        // Build a new GoogleApiClient for the the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        sendcalibrationButton = (Button) findViewById(R.id.sendcalibrationButton);
        sendcalibrationButton.setOnClickListener(this);
        editcalibration = (EditText) findViewById(R.id.calibration);
        editcalibration.setInputType(InputType.TYPE_CLASS_NUMBER);
        editslope = (EditText) findViewById(R.id.slope);
        editslope.setInputType(InputType.TYPE_CLASS_NUMBER);
        editintercept = (EditText) findViewById(R.id.intercept);
        editintercept.setInputType(InputType.TYPE_CLASS_NUMBER);
        loadPrefs();
    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        String WEARABLE_DATA_PATH = "/wearable_data";
        //read shared prefernces and put them into strings
        loadPrefs();
        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putString("getAddress", getAddress);
        dataMap.putString("txid", txid);
        dataMap.putString("getName", "xbridge");
        dataMap.putString("collectionmethod", selectcollectionmethod);
        dataMap.putInt("year", year);
        dataMap.putInt("month", month);
        dataMap.putInt("day", day);
        dataMap.putInt("hour", hour);
        dataMap.putInt("minute", minute);
        dataMap.putBoolean("stopsensor", stopsensor);
        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void configButtonClick() {
        startActivity(new Intent(".ConfigActivity"));

    }

    private void savePrefs(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    private void savePrefs(String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    private void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        //read shared prefs
        calibration = sp.getString("calibration", "");
        txid = sp.getString("txid", "");
        selectcollectionmethod = sp.getString("selectcollectionmethod", "");
        getAddress = sp.getString("btmac", "");
        year = sp.getInt("intyear", 0);
        month = sp.getInt("intmonth", 0);
        day = sp.getInt("intday", 0);
        hour = sp.getInt("inthour", 0);
        minute = sp.getInt("intminute", 0);
        stopsensor = sp.getBoolean("stopsensor", false);
        //set dome things for mainactivity
        editcalibration.setText(calibration);
    }


    private void sendconfigButtonclick() {
        loadPrefs();
        new AlertDialog.Builder(this)
                .setTitle("saved config:")
                .setMessage(
                        "txid: " + txid + "\n"
                                + "selectcollectionmethod: " + selectcollectionmethod + "\n"
                                + "getAddress: " + getAddress + "\n"
                                + "intyear: " + year + "\n"
                                + "intmonth: " + month + "\n"
                                + "intday: " + day + "\n"
                                + "inthour: " + hour + "\n"
                                + "intminute: " + minute + "\n"
                                + "stopsensor: " + stopsensor
                )
                .setPositiveButton("Send to Wear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        googleClient.connect();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void sendcalibrationButtonclick(){
        savePrefs("calibration", editcalibration.getText().toString());
        String WEARABLE_DATA_PATH = "/wearable_data";
        //read shared prefernces and put them into strings
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String calibration = sp.getString("calibration", "");
        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putBoolean("calibrationcheckin", true);
        dataMap.putString("calibration", calibration);
        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
        Log.v("myTag", "send DataMap to data layer");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.configButton:
                configButtonClick();
                break;
            case R.id.sendconfigButton:
                sendconfigButtonclick();
                break;
            case R.id.sendcalibrationButton:
                sendcalibrationButtonclick();
                break;
        }
    }




    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "DataMap: " + dataMap + " sent successfully to data layer ");
            } else {
                // Log an error
                Log.v("myTag", "ERROR: failed to send DataMap to data layer");
            }
        }
    }
}
