package com.takisoft.datetimepicker.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class FakeDateTimeFormat extends Format {

    public static final String Hm = "Hm";
    public static final String hm = "hm";
    public static final String EMMMd = "EMMMd";
    public static final String EMMMMdy = "EMMMMdy";
    public static final String MMMMy = "MMMMy";

    private Context context;
    private Locale locale;
    private int flags;

    public FakeDateTimeFormat(Context context, String skeleton, Locale locale){
        this.context = context;
        this.locale = locale;

        switch (skeleton){
            case Hm:
                flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
                break;
            case hm:
                flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_CAP_AMPM;
                break;
            case EMMMd:
                flags = DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY;
                break;
            case EMMMMdy:
                flags = DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
                break;
            case MMMMy:
                flags = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
                break;
                default:
                    throw new IllegalArgumentException("'" + skeleton + "' does not exist");
        }
    }

    @Override
    public StringBuffer format(Object o, @NonNull StringBuffer stringBuffer, @NonNull FieldPosition fieldPosition) {
        boolean found = false;
        long time = 0;

        if(o != null) {
            if (o instanceof Date) {
                found = true;
                time = ((Date) o).getTime();
            } else if (o instanceof Long || long.class.isAssignableFrom(o.getClass())) {
                found = true;
                time = (long) o;
            }

            if (found) {
                Formatter formatter = new Formatter(stringBuffer, locale);
                DateUtils.formatDateRange(context, formatter, time, time, flags);
            }
        }

        return stringBuffer;
    }

    @Override
    public Object parseObject(String s, @NonNull ParsePosition parsePosition) {
        throw new UnsupportedOperationException();
    }
}
