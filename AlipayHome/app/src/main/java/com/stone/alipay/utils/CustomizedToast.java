package com.stone.alipay.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stone.alipay.R;

/**
 * @author xmuSistone
 */
public class CustomizedToast {
    private static CustomizedToast instance;
    private Toast lastToast;

    private CustomizedToast() {
        // 初始化View和Toast
    }

    public static CustomizedToast getInstance() {
        if (null == instance) {
            instance = new CustomizedToast();
        }

        return instance;
    }

    /**
     * 一种自定义的Toast
     *
     * @param toastStr 字符串
     */
    public void showToast(Context context, String toastStr) {
        if (null != lastToast) {
            lastToast.cancel();
        }

        // 初始化ToastView
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View toastView = inflater.inflate(R.layout.toast_layout, null);
        TextView toastTv = toastView.findViewById(R.id.toast_tv);
        toastTv.setText(toastStr);

        // 初始化Toast
        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);

        toast.setGravity(Gravity.BOTTOM, 0, Utils.dp2px(context.getApplicationContext(), 30));
        toast.show();
        lastToast = toast;
    }
}
