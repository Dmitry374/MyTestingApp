package com.example.dima.mytestingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dima.mytestingapp.Items.ItemChildReminder;
import com.example.dima.mytestingapp.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Dima on 15.08.2017.
 */

public class ExpandableListAdapterList extends BaseExpandableListAdapter {

    String[] titles = new String[]{"Выполненные", "Просроченные", "Сегодня", "Завтра", "Будущие"};

    private Context context;
    private Map<String, ArrayList<ItemChildReminder>> childCollection;
    private ArrayList<String> group;

    LinearLayout groupLayout;

    LayoutInflater layoutInflater;

    public ExpandableListAdapterList(Context context, ArrayList<String> group,
                                     Map<String, ArrayList<ItemChildReminder>> childCollection){
        this.context = context;
        this.group = group;
        this.childCollection = childCollection;

        layoutInflater = LayoutInflater.from(context);

    }


    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childCollection.get(group.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childCollection.get(group.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        String groupName = (String) getGroup(groupPosition);


        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.group_expandable_view, parent, false);
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);

        textGroup.setText(groupName);

        groupLayout = (LinearLayout) convertView.findViewById(R.id.groupLayout);

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        final ItemChildReminder child = (ItemChildReminder) getChild(groupPosition, childPosition);



        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_expandable_view, null);
        }


        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(child.getTitle());

        ImageView imgReminder = (ImageView) convertView.findViewById(R.id.imgReminder);
//        imgReminder.setImageResource(R.mipmap.ic_launcher);
        if (child.getImageReminderIcon() == 123){
//            imgReminder.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            imgReminder.setMaxWidth(0);

//            imgReminder.setImageResource(R.drawable.ic_action_close);
//
//            android.view.ViewGroup.LayoutParams layoutParams = imgReminder.getLayoutParams();
//            layoutParams.width = 0;
//            layoutParams.height = 20;
//            imgReminder.setLayoutParams(layoutParams);

        } else {
            imgReminder.setImageResource(child.getImageReminderIcon());
        }

        TextView tvReminderTime = (TextView) convertView.findViewById(R.id.tvReminderTime);
        tvReminderTime.setText(child.getTime());

        TextView tvReminderDate = (TextView) convertView.findViewById(R.id.tvReminderDate);
        tvReminderDate.setText(child.getDate());

        TextView tvReminderRepeat = (TextView) convertView.findViewById(R.id.tvReminderRepeat);
        tvReminderRepeat.setText(child.getRepeatPeriod());



//        TextView tvDateTime = (TextView) convertView.findViewById(R.id.tvDateTime);
//        tvDateTime.setText(child.getDate() + " " + child.getTime());
//
//        TextView tvRepeatInfo = (TextView) convertView.findViewById(R.id.tvRepeatInfo);
//        tvRepeatInfo.setText(child.getRepeatPeriod());
//
//        ImageView imgReminder = (ImageView) convertView.findViewById(R.id.imgReminder);
//        imgReminder.setImageResource(child.getImageReminderIcon());

        return convertView;

    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }

}
