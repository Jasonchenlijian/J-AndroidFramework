package com.clj.jaf.utils;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class JViewUtil {

    /**
     * 设置view组件gone或者visible
     */
    public static <V extends View> V setGone(final V view, final boolean gone) {
        if (view != null)
            if (gone) {
                if (GONE != view.getVisibility())
                    view.setVisibility(GONE);
            } else {
                if (VISIBLE != view.getVisibility())
                    view.setVisibility(VISIBLE);
            }
        return view;
    }

    /**
     * 设置view组件invisible或者visible
     */
    public static <V extends View> V setInvisible(final V view, final boolean invisible) {
        if (view != null)
            if (invisible) {
                if (INVISIBLE != view.getVisibility())
                    view.setVisibility(INVISIBLE);
            } else {
                if (VISIBLE != view.getVisibility())
                    view.setVisibility(VISIBLE);
            }
        return view;
    }

    /**
     * 增加view的点击区域，当view是一张小图或者不方便点击时可采用此方法
     *
     * @param amount 上下左右的区域
     * @param delegate 所需要增加区域的对象view
     */
    public static void increaseHitRectBy(final int amount, final View delegate) {
        increaseHitRectBy(amount, amount, amount, amount, delegate);
    }

    /**
     * @param top
     * @param left
     * @param bottom
     * @param right
     * @param delegate
     */
    public static void increaseHitRectBy(final int top, final int left,
                                         final int bottom, final int right, final View delegate) {
        final View parent = (View) delegate.getParent();
        if (parent != null && delegate.getContext() != null) {
            parent.post(new Runnable() {
                // Post in the parent's message queue to make sure the parent
                // lays out its children before we call getHitRect()
                public void run() {
                    final float densityDpi = delegate.getContext()
                            .getResources().getDisplayMetrics().densityDpi;
                    final Rect r = new Rect();
                    delegate.getHitRect(r);
                    r.top -= transformToDensityPixel(top, densityDpi);
                    r.left -= transformToDensityPixel(left, densityDpi);
                    r.bottom += transformToDensityPixel(bottom, densityDpi);
                    r.right += transformToDensityPixel(right, densityDpi);
                    parent.setTouchDelegate(new TouchDelegate(r, delegate));
                }
            });
        }
    }

    public static int transformToDensityPixel(int regularPixel,
                                              DisplayMetrics displayMetrics) {
        return transformToDensityPixel(regularPixel, displayMetrics.densityDpi);
    }

    public static int transformToDensityPixel(int regularPixel, float densityDpi) {
        return (int) (regularPixel * densityDpi);
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) ((pxValue * 160) / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean checkBtnEnable(EditText... editTexts) {
        boolean enable = true;
        for (EditText each : editTexts) {
            if (JStringUtil.isBlank(each.getText())) {
                enable = false;
                break;
            }
        }
        return enable;
    }

    public static void checkNotBlank(final Button button,
                                     final EditText... editTexts) {
        TextWatcher notBlank = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i,
                                          int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2,
                                      int i3) {
                if (checkBtnEnable(editTexts)) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        for (EditText each : editTexts) {
            each.addTextChangedListener(notBlank);
        }
    }

}
