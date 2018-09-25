package com.takisoft.datetimepicker;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.takisoft.datetimepicker.util.DateFormatFix;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.takisoft.datetimepicker.test", appContext.getPackageName());
    }

    /**
     * This method is used for retrieving the patterns for pre-processing, which then can be
     * supplied to the date / time pickers.
     */
    @Test
    public void getPatterns2() {
        String[] skeletons = new String[]{DateFormatFix.SKELETON_MMMMy, DateFormatFix.SKELETON_EMMMMdy, DateFormatFix.SKELETON_EMMMd, DateFormatFix.SKELETON_Hm, DateFormatFix.SKELETON_hm};
        Map<String, String> localeMap = new TreeMap<>();
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale locale : locales) {
            Locale.setDefault(locale);
            String lang = locale.getLanguage();
            String country = locale.getCountry();
            //String script = locale.getScript();
            //String variant = locale.getVariant();

            //Log.v("DateFormatFix", "locale: " + locale.toString() + ", lang: " + lang + ", country: " + country + ", script: " + script + ", variant: " + variant);

            if (lang.length() != 2) {
                continue;
            }

            StringBuilder qualifierSb = new StringBuilder(lang);
            if (!TextUtils.isEmpty(country) && country.length() == 2) {
                qualifierSb.append("-r").append(country);
            }

            /* alternative method; it will attach the scripts too, but it's probably not needed as
               API 17 and earlier don't understand the "b+zh+CN+Hans" format
            if (TextUtils.isEmpty(script)) {
                if (!TextUtils.isEmpty(country) && country.length() == 2) {
                    qualifierSb.append("-r").append(country);
                }
            } else {
                if (!TextUtils.isEmpty(country) && country.length() == 2) {
                    qualifierSb.append("+").append(country);
                }
                qualifierSb.append("+").append(script);
            }
             */

            String qualifier = qualifierSb.toString();

            if (localeMap.containsKey(qualifier)) {
                //qualifier = locale.toString() + "####";
                continue;
            }

            StringBuilder patternSb = new StringBuilder();

            for (String skeleton : skeletons) {
                patternSb.append(',');
                add(patternSb, skeleton);
                patternSb.append(',');
                add(patternSb, DateFormat.getBestDateTimePattern(locale, skeleton));
            }

            if (localeMap.containsKey(lang) && localeMap.get(lang).equals(patternSb.toString())) {
                Log.i("DateFormatFix", "patterns equal for '" + lang + "' and '" + qualifier + "', skipping");
                continue;
            }

            if (!localeMap.containsKey(qualifier)) {
                localeMap.put(qualifier, patternSb.toString());
            } else {
                Log.w("DateFormatFix", "qualifier exists: " + qualifier);
            }
        }

        Log.i("DateFormatFix", "locale map count: " + localeMap.size());

        for (Map.Entry<String, String> localeEntry : localeMap.entrySet()) {
            StringBuilder sb = new StringBuilder();

            add(sb, localeEntry.getKey());
            sb.append(localeEntry.getValue());
            Log.d("DateFormatFix", sb.toString());
        }
    }

    private void add(StringBuilder sb, String s) {
        sb.append('"').append(s).append('"');
    }

    /*@Test
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void useFakeDateTimeFormat_MMMMy() throws Exception {
        fakeDateTimeTester(DateFormatFix.SKELETON_MMMMy);
    }

    @Test
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void useFakeDateTimeFormat_EMMMMdy() throws Exception {
        fakeDateTimeTester(DateFormatFix.SKELETON_EMMMMdy);
    }

    @Test
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void useFakeDateTimeFormat_EMMMd() throws Exception {
        fakeDateTimeTester(DateFormatFix.SKELETON_EMMMd);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void fakeDateTimeTester(String skeleton) {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Date date = new Date();

        Locale[] locales = Locale.getAvailableLocales();
        Log.i("DateFormatFix", "locale count: " + locales.length);

        for (Locale locale : locales) {
            String staticPattern = DateFormatFix.getBestDateTimePatternForced(appContext, locale, skeleton);
            String bestPattern = DateFormat.getBestDateTimePattern(locale, skeleton);

            String fake = new SimpleDateFormat(staticPattern, locale).format(date);
            String best = new SimpleDateFormat(bestPattern, locale).format(date);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !TextUtils.isEmpty(locale.getScript())) {
                if (!TextUtils.equals(fake, best)) {
                    Log.w("DateFormatFix", "No match [locale='" + locale + "', best='" + best + "', static='" + fake + "']");
                    continue;
                }
            }

            assertEquals("locale: " + locale + ", best: '" + best + "'" + ", fake: '" + fake + "'", best, fake);
        }
    }*/
}
