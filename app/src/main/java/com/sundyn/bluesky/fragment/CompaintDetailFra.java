package com.sundyn.bluesky.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseFragment;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangjl
 * @date 2016-8-10����10:15:41
 * @�汾��1.0
 * @������Ͷ�߻�������ҳ��
 */
public class CompaintDetailFra extends BaseFragment {

    private View view;
    @ViewInject(R.id.id_expandView)
    private ExpandableListView mExpandableListView;
    private MyAdapter mAdapter;

    private List<String> parentList = null;
    Map<String, List<String>> map = null;

    private int sign = -1;// �����б��չ��

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_compaintdetail, null);
        x.view().inject(this, view); // ע��view���¼�

        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setChildDivider(getResources().getDrawable(
                R.mipmap.news_list_line));
        initEvent();
        return view;
    }

    private void initEvent() {
        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if (sign == -1) {
                    // չ����ѡ��group
                    mExpandableListView.expandGroup(groupPosition);
                    // ���ñ�ѡ�е�group���ڶ���
                    mExpandableListView.setSelectedGroup(groupPosition);
                    sign = groupPosition;
                } else if (sign == groupPosition) {
                    mExpandableListView.collapseGroup(sign);
                    sign = -1;
                } else {
                    mExpandableListView.collapseGroup(sign);
                    // չ����ѡ��group
                    mExpandableListView.expandGroup(groupPosition);
                    // ���ñ�ѡ�е�group���ڶ���
                    mExpandableListView.setSelectedGroup(groupPosition);
                    sign = groupPosition;
                }
                return true;
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        parentList = new ArrayList<String>();
        parentList.add("��ʵ��");
        parentList.add("������");
        parentList.add("�Ѱ��");
        map = new HashMap<String, List<String>>();
        List<String> list1 = new ArrayList<String>();
        list1.add("child1-1");
        list1.add("child1-2");
        list1.add("child1-3");
        map.put("��ʵ��", list1);

        List<String> list2 = new ArrayList<String>();
        list2.add("child2-1");
        list2.add("child2-2");
        list2.add("child2-3");
        map.put("������", list2);

        List<String> list3 = new ArrayList<String>();
        list3.add("child3-1");
        list3.add("child3-2");
        list3.add("child3-3");
        map.put("�Ѱ��", list3);


        mAdapter = new MyAdapter();
        mExpandableListView.setAdapter(mAdapter);
        // getAllArea();
    }

    class MyAdapter extends BaseExpandableListAdapter {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String key = parentList.get(groupPosition);
            return (map.get(key).get(childPosition));
        }

        // �õ���item��ID
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // ������item�����
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            String key = parentList.get(groupPosition);
            String info = map.get(key).get(childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(
                        R.layout.expandview_compaint_child, null);
            }
            return convertView;
        }

        // ��ȡ��ǰ��item�µ���item�ĸ���
        @Override
        public int getChildrenCount(int groupPosition) {
            String key = parentList.get(groupPosition);
            int size = map.get(key).size();
            return size;
        }

        // ��ȡ��ǰ��item������
        @Override
        public Object getGroup(int groupPosition) {
            return parentList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return parentList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        // ���ø�item���
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(
                        R.layout.expandview_compaint_group, null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView
                        .findViewById(R.id.parent_textview);
                holder.arrow = (ImageView) convertView
                        .findViewById(R.id.id_arrow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(parentList.get(groupPosition));

            if (isExpanded) {
                holder.arrow.setImageResource(R.mipmap.arrow_up);
            } else {
                holder.arrow.setImageResource(R.mipmap.arrow_down);
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    static class ViewHolder {
        public TextView tv;
        public ImageView arrow;
    }

}
