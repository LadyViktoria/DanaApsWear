package danaapswear.danaapswear;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ConfigActivity extends Activity implements OnClickListener {

    EditText txid;
    EditText btmac;
    EditText calibration;
    Button b;
    RadioGroup rbg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        rbg = (RadioGroup) findViewById(R.id.selectcollectionmethod);
        txid = (EditText) findViewById(R.id.txid);
        btmac = (EditText) findViewById(R.id.btmac);
        calibration = (EditText) findViewById(R.id.calibration);
        b = (Button) findViewById(R.id.savebutton);
        b.setOnClickListener(this);
        loadPrefs();
    }

    public void getSelectedRadioButton(){
        rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for(int i=0; i<rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if(btn.getId() == checkedId) {
                        String text = btn.getText().toString();
                        savePrefs("selectcollectionmethod", text);
                        return;
                    }
                }
            }
        });
    }

    private void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String transmitterid = sp.getString("txid", "Your Transmitter ID");
        String bluetoothmac = sp.getString("btmac", "Your Bluetooth MAC");
        String bgcalibration = sp.getString("calibration", "enter Calibration");
        int selectedid = sp.getInt("selectedradiobutton", 0);
        txid.setText(transmitterid);
        btmac.setText(bluetoothmac);
        calibration.setText(bgcalibration);
        rbg.check(selectedid);
        getSelectedRadioButton();

    }

    private void savePrefs(String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    private void savePrefs(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private void savePrefs(String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.commit();
    }
    @Override


    public void onClick(View v) {
        savePrefs("txid", txid.getText().toString());
        savePrefs("selectedradiobutton", rbg.getCheckedRadioButtonId());
        savePrefs("calibration", calibration.getText().toString());
        savePrefs("btmac", btmac.getText().toString());
        getSelectedRadioButton();
        finish();
    }

}