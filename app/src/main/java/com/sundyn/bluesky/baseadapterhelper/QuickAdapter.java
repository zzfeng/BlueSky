package com.sundyn.bluesky.baseadapterhelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper.get;

/**
 * Abstraction class of a BaseAdapter in which you only need to provide the
 * convert() implementation.<br/>
 * Using the provided BaseAdapterHelper, your code is minimalist.
 *
 * @param <T> The type of the items in the list.
 */
public abstract class QuickAdapter<T> extends
        BaseQuickAdapter<T, BaseAdapterHelper> {

    /**
     * Create a QuickAdapter.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     */
    public QuickAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with some
     * initialization data.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public QuickAdapter(Context context, int layoutResId, List<T> data) {
        super(context, layoutResId, data);
    }

    protected BaseAdapterHelper getAdapterHelper(int position,
                                                 View convertView, ViewGroup parent) {
        return get(context, convertView, parent, layoutResId, position);
    }

    /* 限制个数显示 yangjl */
    @Override
    public int getCount() {
        if (visiableItem > 0 && !showAllData) {
            return visiableItem;
        }
        return super.getCount();
    }

    private int visiableItem;
    private boolean showAllData = false;

    public void setShowAllData(boolean showAllData) {
        this.showAllData = showAllData;
    }

    public void setVisiableCount(int visiableItem) {
        this.visiableItem = visiableItem;
    }

}
