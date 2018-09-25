/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.takisoft.datetimepicker.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.widget.FrameLayout;

import com.takisoft.datetimepicker.R;
import com.takisoft.datetimepicker.util.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

// import android.util.MathUtils;

// FIXME import android.icu.util.Calendar;
//import libcore.icu.LocaleData;

/**
 * A widget for selecting the time of day, in either 24-hour or AM/PM mode.
 * <p>
 * For a dialog using this view, see {@link android.app.TimePickerDialog}. See
 * the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide for more information.
 *
 * @attr ref android.R.styleable#TimePicker_timePickerMode
 */
public class TimePicker extends FrameLayout {
    private static final String LOG_TAG = TimePicker.class.getSimpleName();

    /**
     * Presentation mode for the Holo-style time picker that uses a set of
     * {@link android.widget.NumberPicker}s.
     *
     * @hide Visible for testing only.
     * @see #getMode()
     */
    public static final int MODE_SPINNER = 1;

    /**
     * Presentation mode for the Material-style time picker that uses a clock
     * face.
     *
     * @hide Visible for testing only.
     * @see #getMode()
     */
    public static final int MODE_CLOCK = 2;

    /**
     * @hide
     */
    @IntDef({MODE_SPINNER, MODE_CLOCK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimePickerMode {
    }

    private TimePickerDelegate mDelegate;

    @TimePickerMode
    private int mMode;

    /**
     * The callback interface used to indicate the time has been adjusted.
     */
    public interface OnTimeChangedListener {

        /**
         * @param view      The view associated with this listener.
         * @param hourOfDay The current hour.
         * @param minute    The current minute.
         */
        void onTimeChanged(TimePicker view, int hourOfDay, int minute);
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.timePickerStyle);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attrHandler(context, attrs, defStyleAttr, Utils.isLightTheme(context) ? R.style.Widget_Material_Light_TimePicker : R.style.Widget_Material_TimePicker); // TODO should supply  R.style.Widget_Material_TimePicker or R.style.Widget_Material_Light_TimePicker;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimePicker(final Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        attrHandler(context, attrs, defStyleAttr, defStyleRes);
    }

    private void attrHandler(final Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // DatePicker is important by default, unless app developer overrode attribute.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            if (getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
                setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_YES);
            }
        }

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.TimePicker, defStyleAttr, defStyleRes);
        final boolean isDialogMode = a.getBoolean(R.styleable.TimePicker_dtp_dialogMode, false);
        final int requestedMode = a.getInt(R.styleable.TimePicker_timePickerMode, MODE_SPINNER);
        a.recycle();

        if (requestedMode == MODE_CLOCK && isDialogMode) {
            // You want MODE_CLOCK? YOU CAN'T HANDLE MODE_CLOCK! Well, maybe
            // you can depending on your screen size. Let's check...
            mMode = context.getResources().getInteger(R.integer.time_picker_mode);
        } else {
            mMode = requestedMode;
        }

        switch (mMode) {
            case MODE_CLOCK:
                mDelegate = new TimePickerClockDelegate(
                        this, context, attrs, defStyleAttr, defStyleRes);
                break;
            case MODE_SPINNER:
            default:
                mDelegate = new TimePickerSpinnerDelegate(
                        this, context, attrs, defStyleAttr, defStyleRes);
                break;
        }


        mDelegate.setAutoFillChangeListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker v, int h, int m) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    final AutofillManager afm = context.getSystemService(AutofillManager.class);
                    if (afm != null) {
                        afm.notifyValueChanged(TimePicker.this);
                    }
                }
            }
        });
    }

    /**
     * @return the picker's presentation mode, one of {@link #MODE_CLOCK} or
     * {@link #MODE_SPINNER}
     * @attr ref android.R.styleable#TimePicker_timePickerMode
     * @hide Visible for testing only.
     */
    @TimePickerMode
    public int getMode() {
        return mMode;
    }

    /**
     * Sets the currently selected hour using 24-hour time.
     *
     * @param hour the hour to set, in the range (0-23)
     * @see #getHour()
     */
    public void setHour(@IntRange(from = 0, to = 23) int hour) {
        mDelegate.setHour(MathUtils.clamp(hour, 0, 23));
    }

    /**
     * Returns the currently selected hour using 24-hour time.
     *
     * @return the currently selected hour, in the range (0-23)
     * @see #setHour(int)
     */
    public int getHour() {
        return mDelegate.getHour();
    }

    /**
     * Sets the currently selected minute.
     *
     * @param minute the minute to set, in the range (0-59)
     * @see #getMinute()
     */
    public void setMinute(@IntRange(from = 0, to = 59) int minute) {
        mDelegate.setMinute(MathUtils.clamp(minute, 0, 59));
    }

    /**
     * Returns the currently selected minute.
     *
     * @return the currently selected minute, in the range (0-59)
     * @see #setMinute(int)
     */
    public int getMinute() {
        return mDelegate.getMinute();
    }

    /**
     * Sets the currently selected hour using 24-hour time.
     *
     * @param currentHour the hour to set, in the range (0-23)
     * @deprecated Use {@link #setHour(int)}
     */
    @Deprecated
    public void setCurrentHour(@NonNull Integer currentHour) {
        setHour(currentHour);
    }

    /**
     * @return the currently selected hour, in the range (0-23)
     * @deprecated Use {@link #getHour()}
     */
    @NonNull
    @Deprecated
    public Integer getCurrentHour() {
        return getHour();
    }

    /**
     * Sets the currently selected minute.
     *
     * @param currentMinute the minute to set, in the range (0-59)
     * @deprecated Use {@link #setMinute(int)}
     */
    @Deprecated
    public void setCurrentMinute(@NonNull Integer currentMinute) {
        setMinute(currentMinute);
    }

    /**
     * @return the currently selected minute, in the range (0-59)
     * @deprecated Use {@link #getMinute()}
     */
    @NonNull
    @Deprecated
    public Integer getCurrentMinute() {
        return getMinute();
    }

    /**
     * Sets whether this widget displays time in 24-hour mode or 12-hour mode
     * with an AM/PM picker.
     *
     * @param is24HourView {@code true} to display in 24-hour mode,
     *                     {@code false} for 12-hour mode with AM/PM
     * @see #is24HourView()
     */
    public void setIs24HourView(@NonNull Boolean is24HourView) {
        if (is24HourView == null) {
            return;
        }

        mDelegate.setIs24Hour(is24HourView);
    }

    /**
     * @return {@code true} if this widget displays time in 24-hour mode,
     * {@code false} otherwise}
     * @see #setIs24HourView(Boolean)
     */
    public boolean is24HourView() {
        return mDelegate.is24Hour();
    }

    /**
     * Set the callback that indicates the time has been adjusted by the user.
     *
     * @param onTimeChangedListener the callback, should not be null.
     */
    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        mDelegate.setOnTimeChangedListener(onTimeChangedListener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mDelegate.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return mDelegate.isEnabled();
    }

    @Override
    public int getBaseline() {
        return mDelegate.getBaseline();
    }

    /**
     * Validates whether current input by the user is a valid time based on the locale. TimePicker
     * will show an error message to the user if the time is not valid.
     *
     * @return {@code true} if the input is valid, {@code false} otherwise
     */
    public boolean validateInput() {
        return mDelegate.validateInput();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return mDelegate.onSaveInstanceState(superState);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BaseSavedState ss = (BaseSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mDelegate.onRestoreInstanceState(ss);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return TimePicker.class.getName();
    }

    /* FIXME accessibility
    /** @hide *
    @Override
    public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        return mDelegate.dispatchPopulateAccessibilityEvent(event);
    }*/

    /**
     * @hide
     */
    private View getHourView() {
        return mDelegate.getHourView();
    }

    /**
     * @hide
     */
    private View getMinuteView() {
        return mDelegate.getMinuteView();
    }

    /**
     * @hide
     */
    private View getAmView() {
        return mDelegate.getAmView();
    }

    /**
     * @hide
     */
    private View getPmView() {
        return mDelegate.getPmView();
    }

    /**
     * A delegate interface that defined the public API of the TimePicker. Allows different
     * TimePicker implementations. This would need to be implemented by the TimePicker delegates
     * for the real behavior.
     */
    interface TimePickerDelegate {
        void setHour(@IntRange(from = 0, to = 23) int hour);

        int getHour();

        void setMinute(@IntRange(from = 0, to = 59) int minute);

        int getMinute();

        void setDate(long date);

        long getDate();

        void setIs24Hour(boolean is24Hour);

        boolean is24Hour();

        boolean validateInput();

        void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener);

        void setAutoFillChangeListener(OnTimeChangedListener autoFillChangeListener);

        void setEnabled(boolean enabled);

        boolean isEnabled();

        int getBaseline();

        Parcelable onSaveInstanceState(Parcelable superState);

        void onRestoreInstanceState(Parcelable state);

        boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event);

        void onPopulateAccessibilityEvent(AccessibilityEvent event);

        /**
         * @hide
         */
        View getHourView();

        /**
         * @hide
         */
        View getMinuteView();

        /**
         * @hide
         */
        View getAmView();

        /**
         * @hide
         */
        View getPmView();
    }

    static String[] getAmPmStrings(Context context) {
        final Locale locale = context.getResources().getConfiguration().locale;
        /*final LocaleData d = LocaleData.get(locale);

        final String[] result = new String[2];
        result[0] = d.amPm[0].length() > 4 ? d.narrowAm : d.amPm[0];
        result[1] = d.amPm[1].length() > 4 ? d.narrowPm : d.amPm[1];
        return result;*/

        final String[] result = new String[2];
        final String[] amPm;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            amPm = android.icu.text.DateFormatSymbols.getInstance(locale).getAmPmStrings();
        } else {
            amPm = DateFormatSymbols.getInstance(locale).getAmPmStrings();
        }

        result[0] = amPm[0].length() > 4 ? amPm[0].substring(0, 1) : amPm[0];
        result[1] = amPm[1].length() > 4 ? amPm[1].substring(0, 1) : amPm[1];

        return result;
    }

    /**
     * An abstract class which can be used as a start for TimePicker implementations
     */
    abstract static class AbstractTimePickerDelegate implements TimePickerDelegate {
        protected final TimePicker mDelegator;
        protected final Context mContext;
        protected final Locale mLocale;

        protected OnTimeChangedListener mOnTimeChangedListener;
        protected OnTimeChangedListener mAutoFillChangeListener;

        public AbstractTimePickerDelegate(@NonNull TimePicker delegator, @NonNull Context context) {
            mDelegator = delegator;
            mContext = context;
            mLocale = context.getResources().getConfiguration().locale;
        }

        @Override
        public void setOnTimeChangedListener(OnTimeChangedListener callback) {
            mOnTimeChangedListener = callback;
        }

        @Override
        public void setAutoFillChangeListener(OnTimeChangedListener callback) {
            mAutoFillChangeListener = callback;
        }

        @Override
        public void setDate(long date) {
            Calendar cal = Calendar.getInstance(mLocale);
            cal.setTimeInMillis(date);
            setHour(cal.get(Calendar.HOUR_OF_DAY));
            setMinute(cal.get(Calendar.MINUTE));
        }

        @Override
        public long getDate() {
            Calendar cal = Calendar.getInstance(mLocale);
            cal.set(Calendar.HOUR_OF_DAY, getHour());
            cal.set(Calendar.MINUTE, getMinute());
            return cal.getTimeInMillis();
        }

        protected static class SavedState extends View.BaseSavedState {
            private final int mHour;
            private final int mMinute;
            private final boolean mIs24HourMode;
            private final int mCurrentItemShowing;

            public SavedState(Parcelable superState, int hour, int minute, boolean is24HourMode) {
                this(superState, hour, minute, is24HourMode, 0);
            }

            public SavedState(Parcelable superState, int hour, int minute, boolean is24HourMode,
                              int currentItemShowing) {
                super(superState);
                mHour = hour;
                mMinute = minute;
                mIs24HourMode = is24HourMode;
                mCurrentItemShowing = currentItemShowing;
            }

            private SavedState(Parcel in) {
                super(in);
                mHour = in.readInt();
                mMinute = in.readInt();
                mIs24HourMode = (in.readInt() == 1);
                mCurrentItemShowing = in.readInt();
            }

            public int getHour() {
                return mHour;
            }

            public int getMinute() {
                return mMinute;
            }

            public boolean is24HourMode() {
                return mIs24HourMode;
            }

            public int getCurrentItemShowing() {
                return mCurrentItemShowing;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(mHour);
                dest.writeInt(mMinute);
                dest.writeInt(mIs24HourMode ? 1 : 0);
                dest.writeInt(mCurrentItemShowing);
            }

            @SuppressWarnings({"unused", "hiding"})
            public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
                public SavedState createFromParcel(Parcel in) {
                    return new SavedState(in);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            };
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void dispatchProvideAutofillStructure(ViewStructure structure, int flags) {
        // This view is self-sufficient for autofill, so it needs to call
        // onProvideAutoFillStructure() to fill itself, but it does not need to call
        // dispatchProvideAutoFillStructure() to fill its children.
        structure.setAutofillId(getAutofillId());
        onProvideAutofillStructure(structure, flags);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void autofill(AutofillValue value) {
        if (!isEnabled()) return;

        if (!value.isDate()) {
            Log.w(LOG_TAG, value + " could not be autofilled into " + this);
            return;
        }

        mDelegate.setDate(value.getDateValue());
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public int getAutofillType() {
        return isEnabled() ? AUTOFILL_TYPE_DATE : AUTOFILL_TYPE_NONE;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public AutofillValue getAutofillValue() {
        return isEnabled() ? AutofillValue.forDate(mDelegate.getDate()) : null;
    }
}
