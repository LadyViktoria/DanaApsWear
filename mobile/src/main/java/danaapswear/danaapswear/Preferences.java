package danaapswear.danaapswear;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class Preferences extends PreferenceActivity implements View.OnClickListener {

        String CollectionMethod, txid, getAddress;
        Button sendpreferencesbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.preferences);

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
                        new DataLayerActivity().SendPreferences();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
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
