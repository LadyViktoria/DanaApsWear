package danaapswear.danaapswear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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


        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putLong("time", new Date().getTime());
        dataMap.putString("getAddress", getAddress);
        dataMap.putString("txid", txid);
        dataMap.putString("calibrationvalue", calibration);
        dataMap.putString("getName", "xbridge");
        dataMap.putString("collectionMethod", selectcollectionmethod);


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
        startActivity(new Intent("danaapswear.danaapswear.ConfigActivity"));

    }

    private void readconfigButtonClick() {

        //read shared prefernces and put them into strings
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String txid = sp.getString("txid", "");
        String selectcollectionmethod = sp.getString("selectcollectionmethod", "");
        String getAddress = sp.getString("btmac", "");
        String calibration = sp.getString("calibration", "");
        Log.v("myTag", "txid: " + txid);
        Log.v("myTag", "selectcollectionmethod: " + selectcollectionmethod);
        Log.v("myTag", "btmac: " + getAddress);
        Log.v("myTag", "calibration: " + calibration);
    }



    private void sendconfigButtonclick() {
        googleClient.connect();
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
