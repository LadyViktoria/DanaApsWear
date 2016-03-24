package danaapswear.danaapswear;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


public class Preferences extends PreferenceActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

        String CollectionMethod, txid, getAddress;
        Button sendpreferencesbutton;
        GoogleApiClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.preferences);

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        sendpreferencesbutton = (Button) findViewById(R.id.sendpreferencesbutton);
        sendpreferencesbutton.setOnClickListener(this);
    }

    private void sendconfigButtonclick() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        CollectionMethod = SP.getString("CollectionMethod", "NA");
        txid = SP.getString("txid", "NA");
        getAddress = SP.getString("getAdress", "NA");

        new AlertDialog.Builder(this)
                .setTitle("saved config:")
                .setMessage(
                        "txid: " + txid + "\n"
                                + "CollectionMethod: " + CollectionMethod + "\n"
                                + "getAddress: " + getAddress + "\n"

                )
                .setPositiveButton("Send to Wear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        googleClient.connect();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
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
        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putString("getAddress", getAddress);
        dataMap.putString("txid", txid);
        dataMap.putString("getName", "xbridge");
        dataMap.putString("collectionmethod", CollectionMethod);
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
        new AlertDialog.Builder(this)
                .setTitle("Connection Suspended!")
                .setMessage("Connection to Wear Suspended")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        new AlertDialog.Builder(this)
                .setTitle("Connection Failed!")
                .setMessage("Connection to Wear Failed")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
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
                Looper.prepare();
                new AlertDialog.Builder(Preferences.this)
                        .setTitle("Connection Success!")
                        .setMessage("Connection to Wear Success" + "\n"
                        + dataMap)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                Looper.loop();
            } else {
                Looper.prepare();
                new AlertDialog.Builder(Preferences.this)
                        .setTitle("Connection Failed!")
                        .setMessage("ERROR: failed to send DataMap to data layer")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                Looper.loop();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.sendpreferencesbutton:
                sendconfigButtonclick();
                break;
        }
    }
}
