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
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.eveningoutpost.dexdrip.Models.ActiveBluetoothDevice;
import com.eveningoutpost.dexdrip.UtilityModels.CollectionServiceStarter;

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



    private void initBTDevice() {


        String getName =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("getName", "");
        String getAddress =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("last_connected_device_address", "00:00:00:00:00:00");

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
                    "BT name: " +  data.getString("getName");

            mTextView.setText(display);
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            prefs.edit().putString("last_connected_device_address", data.getString("getAddress")).apply();
            prefs.edit().putString("dex_collection_method", data.getString("collectionMethod")).apply();
            prefs.edit().putString("dex_txid", data.getString("txid")).apply();
            prefs.edit().putString("bt_name", data.getString("getName")).apply();
            //set bluetooth device settings
            initBTDevice();

        }
    }
}


