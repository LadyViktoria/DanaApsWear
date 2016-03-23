package danaapswear.danaapswear;

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

import java.util.Date;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    Button configbutton;
    Button readconfigButton;
    Button sendconfigButton;
    GoogleApiClient googleClient;
    Button sendcalibrationButton;

    EditText calibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configbutton = (Button) findViewById(R.id.configButton);
        configbutton.setOnClickListener(this);
        sendconfigButton = (Button) findViewById(R.id.sendconfigButton);
        sendconfigButton.setOnClickListener(this);
        readconfigButton = (Button) findViewById(R.id.readconfigButton);
        readconfigButton.setOnClickListener(this);
        // Build a new GoogleApiClient for the the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        sendcalibrationButton = (Button) findViewById(R.id.sendcalibrationButton);
        sendcalibrationButton.setOnClickListener(this);
        calibration = (EditText) findViewById(R.id.calibration);
        calibration.setInputType(InputType.TYPE_CLASS_NUMBER);
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String txid = sp.getString("txid", "");
        String selectcollectionmethod = sp.getString("selectcollectionmethod", "");
        String getAddress = sp.getString("btmac", "");
        String calibration = sp.getString("calibration", "");
        String sensorstartsting = sp.getString("sensorstart", "Your Sensor Started at:");



        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putLong("time", new Date().getTime());
        dataMap.putString("mobile_getAddress", getAddress);
        dataMap.putString("mobile_txid", txid);
        dataMap.putString("mobile_calibrationvalue", calibration);
        dataMap.putString("mobile_getName", "xbridge");
        dataMap.putString("mobile_collectionMethod", selectcollectionmethod);
        dataMap.putString("mobile_sensorstartsting", sensorstartsting);



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

    private void readconfigButtonClick() {

        //read shared prefernces and put them into strings
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String txid = sp.getString("txid", "");
        String selectcollectionmethod = sp.getString("selectcollectionmethod", "");
        String getAddress = sp.getString("btmac", "");
        String calibration = sp.getString("calibration", "");
        String sensorstartsting = sp.getString("sensorstart", "Your Sensor Started at:");
        Log.v("myTag", "sensor started at: " + sensorstartsting);
        Log.v("myTag", "txid: " + txid);
        Log.v("myTag", "selectcollectionmethod: " + selectcollectionmethod);
        Log.v("myTag", "btmac: " + getAddress);
        Log.v("myTag", "calibration: " + calibration);
    }

    private void savePrefs(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }


    private void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String bgcalibration = sp.getString("calibration", "enter Calibration");
        calibration.setText(bgcalibration);
    }


    private void sendconfigButtonclick() {
        googleClient.connect();
    }

    public void sendcalibrationButtonclick(){
        savePrefs("calibration", calibration.getText().toString());
        String WEARABLE_DATA_PATH = "/wearable_data";
        //read shared prefernces and put them into strings
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String calibration = sp.getString("calibration", "");
        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putString("mobile_calibrationvalue", calibration);
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
            case R.id.readconfigButton:
                readconfigButtonClick();
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
