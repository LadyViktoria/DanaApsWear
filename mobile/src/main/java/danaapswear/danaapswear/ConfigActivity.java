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
    public String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        rbg = (RadioGroup) findViewById(R.id.selectcollectionmethod);
        rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for(int i=0; i<rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if(btn.getId() == checkedId) {
                        String text = btn.getText().toString();
                        Log.v("myTag", "selectcollectionmethod: " + text);
                        return;
                    }
                }
            }
        });

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
        int selectedid = sp.getInt("selectcollectionmethod", 0);
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
        savePrefs("CHECKBOX", cb.isChecked());
        savePrefs("NAME", et.getText().toString());
        savePrefs("selectcollectionmethod", rbg.getCheckedRadioButtonId());
        //savePrefs("selectcollectionmethodtxt", rbg.getText());
        //Log.v("myTag", "selectcollectionmethodtxt: " + radioButtontext);
        Log.v("myTag", "selectcollectionmethod: " + rbg.getCheckedRadioButtonId());

        finish();
    }

}