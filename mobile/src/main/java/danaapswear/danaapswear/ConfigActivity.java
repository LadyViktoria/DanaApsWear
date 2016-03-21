package danaapswear.danaapswear;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ConfigActivity extends Activity implements OnClickListener {

    CheckBox cb;
    EditText et;
    Button b;
    RadioGroup rbg;
    RadioButton radioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        rbg = (RadioGroup) findViewById(R.id.selectcollectionmethod);
        int selectedId = rbg.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioButton = (RadioButton) findViewById(selectedId);

        switch (selectedId) {
            default:
            case R.id.xdrip:
                //prefs.edit().putString("dex_collection_method", "xdrip").apply();
                break;
            case R.id.xbridge:
                //prefs.edit().putString("dex_collection_method", "DexbridgeWixel").apply();
                break;
        }


        cb = (CheckBox) findViewById(R.id.checkBox1);
        et = (EditText) findViewById(R.id.editText1);
        b = (Button) findViewById(R.id.savebutton);
        b.setOnClickListener(this);
        loadPrefs();
    }

    private void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean cbValue = sp.getBoolean("CHECKBOX", false);
        String name = sp.getString("NAME", "YourName");
        int selectedid = sp.getInt("selectcollectionmethod",0);
        if(cbValue){
            cb.setChecked(true);
        }else{
            cb.setChecked(false);
        }
        et.setText(name);
        rbg.check(selectedid);
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
        // TODO Auto-generated method stub
        savePrefs("CHECKBOX", cb.isChecked());
        savePrefs("NAME", et.getText().toString());
        savePrefs("selectcollectionmethod", rbg.getCheckedRadioButtonId());
        //savePrefs("selectcollectionmethodtxt", rbg.getText());
        //Log.v("myTag", "selectcollectionmethodtxt: " + radioButton.getText());
        Log.v("myTag", "selectcollectionmethod: " + rbg.getCheckedRadioButtonId());

        finish();
    }

}