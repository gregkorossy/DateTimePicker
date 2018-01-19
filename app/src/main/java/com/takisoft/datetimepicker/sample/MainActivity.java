package com.takisoft.datetimepicker.sample;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.takisoft.datetimepicker.DatePickerDialog;
import com.takisoft.datetimepicker.TimePickerDialog;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private final int mMinDateYear = 1999;
    private final int mMinDateMonth = 8;
    private final int mMinDateDay = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();

        findViewById(R.id.btnDateFragment)
                .setOnClickListener(view -> {
                            /*DatePickerDialog
                                    .newInstance(null, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
                                    .show(getFragmentManager(), null);*/
                            DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, (view1, year, month, dayOfMonth) -> {
                                Toast.makeText(MainActivity.this, String.format("%d", year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth), Toast.LENGTH_SHORT).show();
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

                            Calendar minDate = Calendar.getInstance();
                            minDate.set(mMinDateYear, mMinDateMonth - 1, mMinDateDay);
                            long minDateTimeInMillis = minDate.getTimeInMillis();

                            Calendar maxDate = Calendar.getInstance();
                            maxDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                            long maxDateTimeInMillis = maxDate.getTimeInMillis();

                            ColorStateList daySelectorColor = colorInt2ColorStateList(R.color.colorBlue);
                            ColorStateList dayHighlightColor = colorInt2ColorStateList(R.color.overlay_dark_10);

                            dpd.setMinDate(minDateTimeInMillis);
                            dpd.setMaxDate(maxDateTimeInMillis);
                            dpd.setDaySelectorColor(daySelectorColor);
                            dpd.setDayHighlightColor(dayHighlightColor);

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
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(MainActivity.this));
                            tpd.show();
                        }
                );
    }

    /**
     * Conversion Int format to ColorStateList format.
     *
     * @param color The color id of int format.
     * @return The color ColorStateList of int format.
     */
    private ColorStateList colorInt2ColorStateList(@ColorRes int color) {
        return ColorStateList.valueOf(getResources().getColor(color));
    }
}
