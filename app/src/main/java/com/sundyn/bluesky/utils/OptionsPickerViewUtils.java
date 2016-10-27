package com.sundyn.bluesky.utils;

import android.content.Context;

import com.sundyn.bluesky.view.pickerview.OptionsPickerView;

import java.util.ArrayList;

public class OptionsPickerViewUtils {
    private OptionsPickerView pvOptions;
    private OptionsCallBack callBack;
    private static OptionsPickerViewUtils mOptionsPickerViewUtils;

    private OptionsPickerViewUtils() {

    }

    public static OptionsPickerViewUtils getOptionsPickerViewUtils() {
        if (mOptionsPickerViewUtils == null) {
            mOptionsPickerViewUtils = new OptionsPickerViewUtils();
        }
        return mOptionsPickerViewUtils;
    }

    public <T> void showSelectOptions(Context mContext,
                                      final ArrayList<T> mData, String title, OptionsCallBack callback) {
        this.callBack = callback;
        // Ñ¡ÏîÑ¡ÔñÆ÷
        pvOptions = new OptionsPickerView(mContext);
        pvOptions.setTitle(title);
        pvOptions.setPicker(mData);
        pvOptions.setCyclic(false);
        pvOptions
                .setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int options1, int option2,
                                                int options3) {
                        if (callBack != null) {
                            callBack.onOptionsSelect(options1, option2,
                                    options3);
                        }

                    }
                });
        pvOptions.show();
    }

    public void disOptionsView() {
        if (pvOptions != null) {
            pvOptions.dismiss();
        }
    }

    public interface OptionsCallBack {
        void onOptionsSelect(int options1, int option2, int options3);
    }

}
