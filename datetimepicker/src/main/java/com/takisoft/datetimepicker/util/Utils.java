package com.takisoft.datetimepicker.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.content.res.AppCompatResources;

import com.takisoft.datetimepicker.R;

public class Utils {
    /*public static int getColor(Context context, Resources.Theme theme, int resId) {
        TypedArray arr = null;
        try {
            TypedValue typedValue = new TypedValue();

            theme.resolveAttribute(resId, typedValue, true);

            arr = context.obtainStyledAttributes(typedValue.data, new int[]{resId});

            if (arr.length() >= 1) {
                return arr.getColor(0, 0);
            }
        } finally {
            if (arr != null) {
                arr.recycle();
            }
        }
        return -1;
    }*/

    public static boolean colorHasState(ColorStateList color, int state) {
        Parcel parcel = Parcel.obtain();
        color.writeToParcel(parcel, 0);

        // hack from ColorStateList.hasState(...)
        final int specCount = parcel.readInt();
        for (int specIndex = 0; specIndex < specCount; specIndex++) {
            final int[] states = parcel.createIntArray();
            final int stateCount = states.length;
            for (int stateIndex = 0; stateIndex < stateCount; stateIndex++) {
                if (states[stateIndex] == state || states[stateIndex] == ~state) {
                    return true;
                }
            }
        }
        return false;

        // this is not good because it will return true only if the said 'state' is the only selector on the color
        // int[] states = new int[]{state};
        // return color.getColorForState(states, Integer.MIN_VALUE) != Integer.MIN_VALUE && color.getColorForState(states, Integer.MAX_VALUE) != Integer.MAX_VALUE;
    }

    public static boolean isLightTheme(@NonNull Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.isLightTheme});
        boolean isLightTheme = typedArray.getBoolean(0, false);
        typedArray.recycle();

        return isLightTheme;
    }

    public static Drawable tintDrawable(Context context, Drawable drawable, int tintAttr) {
        // start of FIX - tinting the drawable manually because the android:tint attribute crashes the app
        Drawable wrapped = DrawableCompat.wrap(drawable);

        TypedArray arr = context.obtainStyledAttributes(new int[]{tintAttr});
        ColorStateList tintList = Utils.getColorStateList(context, arr, 0);
        arr.recycle();

        if (tintList != null) {
            DrawableCompat.setTintList(wrapped, tintList);
        }

        return wrapped;
        // end of FIX
    }

    @Nullable
    public static ColorStateList getColorStateList(Context context, TypedArray original, int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return original.getColorStateList(index);
        }

        int resId = original.getResourceId(index, 0);
        return AppCompatResources.getColorStateList(context, resId);
    }

    @Nullable
    public static Drawable getDrawable(Context context, TypedArray original, int index, int tintResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return original.getDrawable(index);
        }

        int resId = original.getResourceId(index, 0);
        Drawable drawable = AppCompatResources.getDrawable(context, resId);

        if (drawable != null) {
            Drawable wrapped = DrawableCompat.wrap(drawable);

            DrawableCompat.applyTheme(wrapped, context.getTheme());

            TypedArray a = context.obtainStyledAttributes(new int[]{tintResId});

            ColorStateList tintList = a.getColorStateList(0);

            if (tintList != null) {
                DrawableCompat.setTintList(wrapped, tintList);
            }

            drawable = wrapped;

            a.recycle();
        }

        return drawable;
    }

    /*public static int multiplyAlphaComponent(int color, float alphaMod) {
        final int srcRgb = color & 0xFFFFFF;
        final int srcAlpha = (color >> 24) & 0xFF;
        final int dstAlpha = (int) (srcAlpha * alphaMod + 0.5f);
        return srcRgb | (dstAlpha << 24);
    }*/
}
