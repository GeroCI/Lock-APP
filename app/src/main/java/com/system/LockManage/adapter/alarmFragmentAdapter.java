package com.system.LockManage.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.system.LockManage.R;
import com.system.LockManage.bean.User;
import com.system.LockManage.bean.Warning;

import java.sql.Timestamp;
import java.util.LinkedList;

public class alarmFragmentAdapter extends BaseAdapter {
    //声明一个链表和Context对象
    private LinkedList<Warning> mList;
    private Context mContext;
    public alarmFragmentAdapter(LinkedList<Warning> mList, Context mContext){
        this.mList=mList;
        this.mContext=mContext;
    }
    @Override
    //获取数据的数量
    public int getCount(){
        return mList.size();
    }
    @Override
    //获取数据的内容
    public Object getItem(int position){
        return  null;
    }
    @Override
    //获取数据的id
    public long getItemId(int position){
        return position;
    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //加载一个适配器界面
        convertView=LayoutInflater.from(mContext).inflate(R.layout.item_alarm,parent,false);
        //实例化元件
        TextView alarm_type=(TextView) convertView.findViewById(R.id.alarm_type);
        TextView alarm_device=(TextView) convertView.findViewById(R.id.alarm_device);
        TextView alarm_time=(TextView) convertView.findViewById(R.id.alarm_time);
        TextView alarm_message=(TextView)convertView.findViewById(R.id.alarm_message);
        //元件获取数据

        alarm_type.setText(mList.get(position).getId());
        alarm_device.setText(mList.get(position).getLock_name());
        alarm_time.setText(String.valueOf(mList.get(position).getWarning_time()));
        alarm_message.setText(mList.get(position).getWarning_message());
        return convertView;
    }
}
