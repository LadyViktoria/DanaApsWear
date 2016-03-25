package danaapswear.danaapswear;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    Button startsensor, stopsensor;
    EditText calibration, doublecalibration, intercept, slope;
    int year, month ,day ,hour ,minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startsensor = (Button) findViewById(R.id.startsensor);
        startsensor.setOnClickListener(this);

        stopsensor = (Button) findViewById(R.id.stopsensor);
        stopsensor.setOnClickListener(this);

        calibration = (EditText) findViewById(R.id.calibration);
        calibration.setInputType(InputType.TYPE_CLASS_NUMBER);
        calibration.setOnClickListener(this);

        doublecalibration = (EditText) findViewById(R.id.doublecalibration);
        doublecalibration.setInputType(InputType.TYPE_CLASS_NUMBER);
        doublecalibration.setOnClickListener(this);

        slope = (EditText) findViewById(R.id.slope);
        slope.setInputType(InputType.TYPE_CLASS_NUMBER);
        slope.setOnClickListener(this);

        intercept = (EditText) findViewById(R.id.intercept);
        intercept.setInputType(InputType.TYPE_CLASS_NUMBER);
        intercept.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.startsensor:
                sensorstartonClick();
                break;
            case R.id.stopsensor:
                sensorstoponClick();
                break;
            case R.id.calibration:
                calibrationonClick();
                break;
            case R.id.doublecalibration:
                doublecalibrationonClick();
                break;
            case R.id.slope:
                slopeonClick();
                break;
            case R.id.intercept:
                interceptonClick();
                break;
        }
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
            Intent i = new Intent(MainActivity.this, Preferences.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void calibrationonClick(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.calibration_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editcalibration);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Send to wear",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                calibration.setText(userInput.getText());
                                //googleClient.connect();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void doublecalibrationonClick(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.doublecalibration_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editdoublecalibration);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Send to wear",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                doublecalibration.setText(userInput.getText());
                                //googleClient.connect();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void slopeonClick(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.slope_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editslope);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Send to wear",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                slope.setText(userInput.getText());
                                //googleClient.connect();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void interceptonClick(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.intercept_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editintercept);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Send to wear",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                intercept.setText(userInput.getText());
                                //googleClient.connect();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void sensorstartonClick(){
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance((TimePickerDialog.OnTimeSetListener) this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        timePickerDialog.show(getSupportFragmentManager(), "timepicker");
        datePickerDialog.setYearRange(2016, 2028);
        datePickerDialog.show(getSupportFragmentManager(), "datepicker");
    }
    public void sensorstoponClick(){

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int intyear, int intmonth, int intday) {
        year=intyear;
        month=intmonth+1;
        day=intday;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int inthour, int intminute) {
        hour = inthour;
        minute = inthour;
        sendensordata();
    }

    public void sendensordata(){
        new AlertDialog.Builder(this)
                .setTitle("Sensor Date:")
                .setMessage("Sensor Started at: " + day + "." + month + "." + year + "  " + hour + ":" + minute)
                .setPositiveButton("Send to Wear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       /*
                        if (googleClient.isConnected()) {
                            Log.v("myTag", "send DataMap to data layer");

                        } else {
                            Log.v("myTag", "send DataMap to data layer Failed");
                        } */
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
