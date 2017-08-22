package com.takisoft.datetimepicker.util;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
// import libcore.icu.ICU;

public class DateFormatFix {
    public static String getBestDateTimePattern(Context context, Locale locale, String skeleton) {
        //Log.d("DateFormatFix", "getBestDateTimePattern() - skeleton: " + skeleton);
        String format;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            format = DateFormat.getBestDateTimePattern(locale, skeleton);
        } else {
            // Try to create the pattern manually
            // - remove letters from format not in skeleton
            // - remove leading non-skeleton chars
            // - use the count of letters in skeleton to inflate the pattern
            // example: skeleton="yyyyMMMdd" + format="EEEE, MMMM d, y" -> "MMM dd, yyyy
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL, locale);
            format = simpleDateFormat.toLocalizedPattern();

            //Log.d("DateFormatFix", "localized pattern: " + format);

            Set<Character> excludedChars = new HashSet<>();
            HashMap<Character, Integer> includedChars = new HashMap<>();

            String normalizedFormat = format;

            for (int i = 0; i < format.length(); i++) {
                char ch = format.charAt(i);
                boolean isLetter = (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');

                if (isLetter && !excludedChars.contains(ch)) {
                    if (!skeleton.contains(ch + "")) {
                        excludedChars.add(ch);
                    }

                    if (!includedChars.containsKey(ch) && !excludedChars.contains(ch)) {
                        includedChars.put(ch, 0);
                        normalizedFormat = normalizedFormat.replaceAll(ch + "+", "" + ch);
                    }
                }
            }

            if (!includedChars.isEmpty()) {
                for (int i = 0; i < skeleton.length(); i++) {
                    char ch = skeleton.charAt(i);

                    if (includedChars.containsKey(ch)) {
                        Integer counter = includedChars.get(ch);
                        counter++;
                        includedChars.put(ch, counter);
                    }
                }


                for (Character ch : includedChars.keySet()) {
                    final StringBuilder sb = new StringBuilder();
                    final int n = includedChars.get(ch);

                    for (int i = 0; i < n; i++) {
                        sb.append(ch);
                    }

                    normalizedFormat = normalizedFormat.replaceAll("" + ch, "" + sb.toString());
                }
            }

            format = normalizedFormat;

            if (!excludedChars.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                Iterator<Character> it = excludedChars.iterator();

                while (it.hasNext()) {
                    sb.append(it.next());

                    if (it.hasNext()) {
                        sb.append('|');
                    }
                }


                format = format.replaceAll("[" + sb.toString() + "][^\\w]*|[^\\w]*[" + sb.toString() + "][^\\w ]*", "");
            }
            // [d][^\w]*\s*
            // [d][^\w]?\s*
            // better: [d][^\w]*|[^\w]*[d][^\w ]*
        }

        //Log.d("DateFormatFix", "final pattern: " + format);

        return format;
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
