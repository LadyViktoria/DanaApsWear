package danaapswear.danaapswear;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class ConfigActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText txid;
    EditText btmac;
    RadioGroup rbg;
    TextView sensorstart;
    String sensorstartsting;
    String modifysensorstartsting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        rbg = (RadioGroup) findViewById(R.id.selectcollectionmethod);
        txid = (EditText) findViewById(R.id.txid);
        txid.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        btmac = (EditText) findViewById(R.id.btmac);
        btmac.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        sensorstart = (TextView)findViewById(R.id.sensorstart);
        sensorstart.setText(R.string.sensorstart);


        findViewById(R.id.savebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrefs("txid", txid.getText().toString());
                savePrefs("selectedradiobutton", rbg.getCheckedRadioButtonId());
                savePrefs("btmac", btmac.getText().toString());
                savePrefs("sensorstart", sensorstart.getText().toString());
                getSelectedRadioButton();
                finish();
            }
        });

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance((TimePickerDialog.OnTimeSetListener) this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        findViewById(R.id.sensorbutton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timePickerDialog.show(getSupportFragmentManager(), "timepicker");
                datePickerDialog.setYearRange(2016, 2028);
                datePickerDialog.show(getSupportFragmentManager(), "datepicker");
            }
        });
        loadPrefs();

    }

    public void getSelectedRadioButton(){
        rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for (int i = 0; i < rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if (btn.getId() == checkedId) {
                        String text = btn.getText().toString();
                        savePrefs("selectcollectionmethod", text);
                        return;
                    }
                }
            }
        });
    }

    public void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String transmitterid = sp.getString("txid", "Your Transmitter ID:");
        String bluetoothmac = sp.getString("btmac", "Your Bluetooth MAC:");
        int selectedid = sp.getInt("selectedradiobutton", 0);
        String sensorstartsting = sp.getString("sensorstart", "Your Sensor Started at:");
        sensorstart.setText(sensorstartsting);
        txid.setText(transmitterid);
        btmac.setText(bluetoothmac);
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
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        month=month+1;
        sensorstartsting = + day + "." + month + "." + year + " ";
        sensorstart.setText(sensorstartsting);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String modifysensorstartsting = sensorstartsting + hourOfDay + ":" + minute;
        sensorstart.setText(modifysensorstartsting);
    }
}
