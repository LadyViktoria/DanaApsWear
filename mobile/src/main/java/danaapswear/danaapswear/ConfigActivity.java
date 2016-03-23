package danaapswear.danaapswear;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class ConfigActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText txid, getAddress;
    RadioGroup rbg;
    TextView sensorstart;
    String sensorstartsting, SelectedRadioButton;
    int year, month, day, hour, minute;
    CheckBox stopsensor;
    boolean stopsensorvalue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        rbg = (RadioGroup) findViewById(R.id.selectcollectionmethod);
        txid = (EditText) findViewById(R.id.txid);
        txid.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        getAddress = (EditText) findViewById(R.id.getAddress);
        getAddress.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        sensorstart = (TextView)findViewById(R.id.sensorstart);
        sensorstart.setText(R.string.sensorstart);
        stopsensor = (CheckBox) findViewById(R.id.stopsensor);
        getSelectedRadioButton();
        getcheckbox();
        loadPrefs();


        findViewById(R.id.savebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrefs("txid", txid.getText().toString());
                savePrefs("selectedradiobutton", rbg.getCheckedRadioButtonId());
                savePrefs("selectcollectionmethod", SelectedRadioButton);
                savePrefs("getAddress", getAddress.getText().toString());
                savePrefs("sensorstart", sensorstart.getText().toString());
                savePrefs("intyear", year);
                savePrefs("intmonth", month);
                savePrefs("intday", day);
                savePrefs("inthour", hour);
                savePrefs("intminute", minute);
                savePrefs("stopsensor", stopsensorvalue);
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

    }

    public void getSelectedRadioButton(){
        rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for (int i = 0; i < rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if (btn.getId() == checkedId) {
                        SelectedRadioButton = btn.getText().toString();
                        return;
                    }
                }
            }
        });
    }
    public void getcheckbox(){
        stopsensor.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {stopsensorvalue = true;}
                else {stopsensorvalue = false;}
            }
        });
    }

    public void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String transmitterid = sp.getString("txid", "Your Transmitter ID:");
        String bluetoothmac = sp.getString("getAddress", "Your Bluetooth MAC:");
        int selectedid = sp.getInt("selectedradiobutton", 0);
        String sensorstartsting = sp.getString("sensorstart", "Your Sensor Started at:");
        boolean booleanstopsensor = sp.getBoolean("stopsensor", false);

        year = sp.getInt("intyear", 0);
        month = sp.getInt("intmonth", 0);
        day = sp.getInt("intday", 0);
        hour = sp.getInt("inthour", 0);
        minute = sp.getInt("intminute", 0);
        sensorstart.setText(sensorstartsting);
        txid.setText(transmitterid);
        getAddress.setText(bluetoothmac);
        rbg.check(selectedid);
        stopsensor.setChecked(booleanstopsensor);
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
    public void onDateSet(DatePickerDialog datePickerDialog, int intyear, int intmonth, int intday) {
        year=intyear;
        month=intmonth+1;
        day=intday;
        sensorstartsting = + day + "." + month + "." + year + " ";
        sensorstart.setText(sensorstartsting);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int inthour, int intminute) {
        hour = inthour;
        minute = inthour;
        String modifysensorstartsting = sensorstartsting + hour + ":" + minute;
        sensorstart.setText(modifysensorstartsting);
    }
}