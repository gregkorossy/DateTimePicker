package com.takisoft.datetimepicker.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
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
