package com.takisoft.datetimepicker.util;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import com.takisoft.datetimepicker.widget.TimePicker;

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Locale;
// import libcore.icu.ICU;

public class DateFormatFix {

    /**
     * Creates the best date-time pattern for the specified locale using the given skeleton. This
     * method works by calling {@link DateFormat#getBestDateTimePattern(Locale, String)} on API 18
     * and up, but on lower APIs it is very limited: only 'Hm' and 'hm' produce normal patterns that
     * the widgets can use, others just return the skeleton itself. This means that for normally the
     * {@link FakeDateTimeFormat} should be used for formatting dates and times, but this is still
     * needed mostly for the {@link TimePicker} to help formulating the look-and-feel of it.
     *
     * @param context  the context that will be used on pre API 18 devices to guess the format
     * @param locale   the locale to be used as the formatting base (it might be ignored on pre API 18)
     * @param skeleton the skeleton for the pattern guesser / native method
     * @return A pattern that should be usable by formatters on API 18+, and a pseudo-pattern that
     * is a viable alternative for the time picker to formulate the look-and-feel.
     * @see FakeDateTimeFormat
     * @see DateFormat
     */
    public static String getBestDateTimePattern(Context context, Locale locale, String skeleton) {
        //Log.d("DateFormatFix", "getBestDateTimePattern() - skeleton: " + skeleton);
        String format;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            format = DateFormat.getBestDateTimePattern(locale, skeleton);
        } else {
            Formatter formatter;
            char zeroDigit = DecimalFormatSymbols.getInstance(locale).getZeroDigit();
            char separator = ':';
            int sepIdx;
            StringBuilder sb = new StringBuilder();

            switch (skeleton) {
                case FakeDateTimeFormat.Hm:
                    formatter = new Formatter(new StringBuilder(50), locale);
                    String strHm = DateUtils.formatDateRange(context, formatter, 0, 0, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR, "UTC").toString();
                    // 24 hour
                    sepIdx = strHm.indexOf(separator);

                    if (sepIdx >= 1) {
                        boolean endsZero = strHm.charAt(sepIdx - 1) == zeroDigit;
                        int leadingZeroLength = (sepIdx - 2 >= 0 && strHm.charAt(sepIdx - 2) == zeroDigit) ? 1 : 0;

                        if (endsZero) {
                            // H - 0–23
                            for (int i = 0; i < 1 + leadingZeroLength; i++) {
                                sb.append('H');
                            }
                        } else {
                            // k - 1-24
                            for (int i = 0; i < 1 + leadingZeroLength; i++) {
                                sb.append('k');
                            }
                        }
                        sb.append(":mm");
                    } else {
                        // should not happen, but still...
                        sb.append(((SimpleDateFormat) SimpleDateFormat.getTimeInstance(java.text.DateFormat.SHORT, locale)).toLocalizedPattern());
                    }
                    format = sb.toString();
                    break;
                case FakeDateTimeFormat.hm:
                    formatter = new Formatter(new StringBuilder(50), locale);
                    String strhm = DateUtils.formatDateRange(context, formatter, 0, 0, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR, "UTC").toString();

                    // 12 HOUR
                    sepIdx = strhm.indexOf(separator);
                    boolean firstDigit = Character.isDigit(strhm.charAt(0));

                    if (!firstDigit) {
                        sb.append('a');
                    }

                    if (sepIdx >= 1) {
                        char preCh = strhm.charAt(sepIdx - 1);
                        if (preCh == zeroDigit) {
                            // K - 0-11
                            sb.append('K');
                        } else {
                            // h - 1-12
                            sb.append('h');
                        }
                        sb.append(":mm");

                        if (firstDigit) {
                            sb.append('a');
                        }
                    } else {
                        // should not happen, but still...
                        sb.append(((SimpleDateFormat) SimpleDateFormat.getTimeInstance(java.text.DateFormat.SHORT, locale)).toLocalizedPattern());

                    }

                    format = sb.toString();
                    break;
                default:
                    format = skeleton; // we basically ignore it, because we use FakeDateTimeFormat in those situations
            }

            // Try to create the pattern manually
            // - remove letters from format not in skeleton
            // - remove leading non-skeleton chars
            // - use the count of letters in skeleton to inflate the pattern
            // example: skeleton="yyyyMMMdd" + format="EEEE, MMMM d, y" -> "MMM dd, yyyy
            /*SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.FULL, locale);
            format = simpleDateFormat.toLocalizedPattern();

            Log.d("DateFormatFix", "localized pattern: " + format);

            Log.d("DateFormatFix","localizedPattern (FULL): " + ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.FULL, locale)).toLocalizedPattern());
            Log.d("DateFormatFix","localizedPattern (LONG): " + ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.LONG, locale)).toLocalizedPattern());
            Log.d("DateFormatFix","localizedPattern (MEDIUM): " + ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, locale)).toLocalizedPattern());
            Log.d("DateFormatFix","localizedPattern (SHORT): " + ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, locale)).toLocalizedPattern());
            Log.d("DateFormatFix","localizedPattern (FULL p): " + ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.FULL, locale)).toPattern());
            Log.d("DateFormatFix","localizedPattern (LONG p): " + ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.LONG, locale)).toPattern());
            Log.d("DateFormatFix","localizedPattern (MEDIUM p): " + ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, locale)).toPattern());
            Log.d("DateFormatFix","localizedPattern (SHORT p): " + ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, locale)).toPattern());

            Log.d("DateFormatFix","localizedPattern (WUT)" + DateUtils.formatDateTime(context, 0, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR));
            Set<Character> interchangable = new HashSet<Character>() {
                {
                    add('h');
                    add('H');
                }
            };

            Set<Character> excludedChars = new HashSet<>();
            HashMap<Character, Integer> includedChars = new HashMap<>();

            String normalizedFormat = format;
            boolean isQuoted = false;

            for (int i = 0; i < format.length(); i++) {
                char ch = format.charAt(i);
                char chLow = Character.toLowerCase(ch);
                char chUp = Character.toUpperCase(ch);
                boolean isLetter = (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');

                if (ch == '\'') {
                    isQuoted = !isQuoted;
                }

                if (isQuoted) {
                    continue;
                }

                if (ch == 'a') {
                    continue;
                }

                if (isLetter && !excludedChars.contains(ch) && !(interchangable.contains(ch) && (skeleton.contains(chLow + "") || skeleton.contains(chUp + "")))) {
                    if (!skeleton.contains(ch + "")) { // ((interchangable.contains(chLow) || interchangable.contains(chUp)) && (skeleton.contains(chLow + "") || skeleton.contains(chUp + "")))
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
            }*/
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
