package com.takisoft.datetimepicker.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.takisoft.datetimepicker.DatePickerDialog;
import com.takisoft.datetimepicker.TimePickerDialog;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar cal = Calendar.getInstance();

        findViewById(R.id.btnDateFragment)
                .setOnClickListener(view -> {
                            /*DatePickerDialog
                                    .newInstance(null, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
                                    .show(getFragmentManager(), null);*/
                            DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, (view1, year, month, dayOfMonth) -> {
                                Toast.makeText(MainActivity.this, year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth), Toast.LENGTH_SHORT).show();
                            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                            dpd.show();
                        }
                );

        findViewById(R.id.btnTimeFragment)
                .setOnClickListener(view -> {
                        /*TimePickerDialog
                        .newInstance(null, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
                        .show(getFragmentManager(), null);*/
                            TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, (view1, hourOfDay, minute) -> {
                                Toast.makeText(MainActivity.this, String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute), Toast.LENGTH_SHORT).show();
                            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(MainActivity.this));
                            tpd.show();
                        }
                );
    }
}
