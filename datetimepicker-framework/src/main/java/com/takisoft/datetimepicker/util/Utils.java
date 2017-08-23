package com.takisoft.datetimepicker.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;

public class Utils {
    public static int getColor(Context context, Resources.Theme theme, int resId) {
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
    }

    public static int multiplyAlphaComponent(int color, float alphaMod) {
        final int srcRgb = color & 0xFFFFFF;
        final int srcAlpha = (color >> 24) & 0xFF;
        final int dstAlpha = (int) (srcAlpha * alphaMod + 0.5f);
        return srcRgb | (dstAlpha << 24);
    }
}
