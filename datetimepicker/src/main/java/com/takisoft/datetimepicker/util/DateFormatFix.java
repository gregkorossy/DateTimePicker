package com.takisoft.datetimepicker.util;

import android.content.Context;
import android.os.Build;
import androidx.annotation.VisibleForTesting;
import android.text.format.DateFormat;

import com.takisoft.datetimepicker.R;

import java.util.Locale;
// import libcore.icu.ICU;

public class DateFormatFix {
    public static final String SKELETON_Hm = "Hm";
    public static final String SKELETON_hm = "hm";
    public static final String SKELETON_EMMMd = "EMMMd";
    public static final String SKELETON_EMMMMdy = "EMMMMdy";
    public static final String SKELETON_MMMMy = "MMMMy";

    /**
     * Creates the best date-time pattern for the specified locale using the given skeleton. This
     * method works by calling {@link DateFormat#getBestDateTimePattern(Locale, String)} on API 18
     * and up, while returning a static pattern on lower API levels.
     *
     * @param context  the context that will be used on pre API 18 devices to guess the format
     * @param locale   the locale to be used as the formatting base (it might be ignored on pre API 18)
     * @param skeleton the skeleton for the pattern guesser / native method
     * @return A pattern that should be usable by formatters.
     * @see DateFormat
     * @see java.text.SimpleDateFormat
     */
    public static String getBestDateTimePattern(Context context, Locale locale, String skeleton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return DateFormat.getBestDateTimePattern(locale, skeleton);
        } else {
            switch (skeleton) {
                case SKELETON_Hm:
                    return context.getString(R.string.datetime_Hm);
                case SKELETON_hm:
                    return context.getString(R.string.datetime_hm);
                case SKELETON_EMMMd:
                    return context.getString(R.string.datetime_EMMMd);
                case SKELETON_EMMMMdy:
                    return context.getString(R.string.datetime_EMMMMdy);
                case SKELETON_MMMMy:
                    return context.getString(R.string.datetime_MMMMy);

                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    @VisibleForTesting
    public static String getBestDateTimePatternForced(Context context, Locale locale, String skeleton) {
        switch (skeleton) {
            case SKELETON_Hm:
                return context.getString(R.string.datetime_Hm);
            case SKELETON_hm:
                return context.getString(R.string.datetime_hm);
            case SKELETON_EMMMd:
                return context.getString(R.string.datetime_EMMMd);
            case SKELETON_EMMMMdy:
                return context.getString(R.string.datetime_EMMMMdy);
            case SKELETON_MMMMy:
                return context.getString(R.string.datetime_MMMMy);

            default:
                throw new UnsupportedOperationException();
        }
    }

    public static char[] getDateFormatOrder(Context context, Locale locale, String skeleton) {
        //char[] order = ICU.getDateFormatOrder(pattern);
       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return DateFormat.getDateFormatOrder(context);
            //return android.icu.text.DateFormat.getInstanceForSkeleton(skeleton, locale);
        }*/
        // FIXME String pattern = getBestDateTimePattern(context, locale, skeleton);
        // FIXME check this ICU.getDateFormatOrder(pattern);
        return DateFormat.getDateFormatOrder(context);
    }
}
