package com.takisoft.datetimepicker.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;

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

    @Nullable
    public static ColorStateList getColorStateList(Context context, TypedArray original, int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return original.getColorStateList(index);
        }

        int resId = original.getResourceId(index, 0);
        return AppCompatResources.getColorStateList(context, resId);
    }

    public static int multiplyAlphaComponent(int color, float alphaMod) {
        final int srcRgb = color & 0xFFFFFF;
        final int srcAlpha = (color >> 24) & 0xFF;
        final int dstAlpha = (int) (srcAlpha * alphaMod + 0.5f);
        return srcRgb | (dstAlpha << 24);
    }
}
