package com.takisoft.datetimepicker.sample;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
                            DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, (datePicker, year, month, dayOfMonth) -> {
                                Toast.makeText(MainActivity.this, String.format("%d", year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth), Toast.LENGTH_SHORT).show();
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

                            Calendar minDate = Calendar.getInstance();
                            minDate.set(mMinDateYear, mMinDateMonth - 1, mMinDateDay);
                            long minDateTimeInMillis = minDate.getTimeInMillis();

                            Calendar maxDate = Calendar.getInstance();
                            maxDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                            long maxDateTimeInMillis = maxDate.getTimeInMillis();

                            ColorStateList daySelectorColor = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                            ColorStateList dayHighlightColor = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.overlay_dark_10));
                            ColorDrawable headerBackground = new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                            ColorStateList yearHighlightColor = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.grey_900));
                            ColorStateList yearSelectorColor = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
//                            ColorStateList calendarTextColor = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorRedDark));

                            int headerNormalTextColor = ContextCompat.getColor(getApplicationContext(), R.color.material_text_color_white_secondary_text);
                            int headerSelectedTextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorWhite);
                            final int[][] headerStateSet = new int[][]{{android.R.attr.state_activated}, {}};
                            final int[] headerColors = new int[]{headerSelectedTextColor, headerNormalTextColor};
                            ColorStateList headerTextColor = new ColorStateList(headerStateSet, headerColors);

                            int calendarNormalTextColor = ContextCompat.getColor(getApplicationContext(), R.color.grey_900);
                            int calendarSelectedTextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorWhite);
                            int calendarUnEnabledTextColor = ContextCompat.getColor(getApplicationContext(), R.color.dark_gray);
                            final int[][] calendarStateSet = new int[][]{{android.R.attr.state_activated}, {android.R.attr.state_enabled}, {}};
                            final int[] calendarColors = new int[]{calendarSelectedTextColor, calendarNormalTextColor, calendarUnEnabledTextColor};
                            ColorStateList calendarTextColor = new ColorStateList(calendarStateSet, calendarColors);

                            dpd.setMinDate(minDateTimeInMillis);
                            dpd.setMaxDate(maxDateTimeInMillis);
                            dpd.setDaySelectorColor(daySelectorColor);
                            dpd.setDayHighlightColor(dayHighlightColor);
                            dpd.setHeaderBackground(headerBackground);
                            dpd.setHeaderTextColor(headerTextColor);
                            dpd.setYearHighlightColor(yearHighlightColor);
                            dpd.setYearSelectorColor(yearSelectorColor);
                            dpd.setCalendarTextColor(calendarTextColor);
                            dpd.setOnShowListener(dialog -> {
                                dpd.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                                dpd.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                            });

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
}
