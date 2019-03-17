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
import com.system.LockManage.bean.OperationLog;
import com.system.LockManage.bean.User;

import java.util.LinkedList;

public class logFragmentAdapter extends BaseAdapter {
    //声明一个链表和Context对象
    private LinkedList<OperationLog> mList;
    private Context mContext;
    public logFragmentAdapter(LinkedList<OperationLog> mList, Context mContext){
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
        convertView=LayoutInflater.from(mContext).inflate(R.layout.item_log,parent,false);
        //实例化元件
        TextView log_type=(TextView) convertView.findViewById(R.id.log_type);
        TextView log_device=(TextView) convertView.findViewById(R.id.log_device);
        TextView log_time=(TextView) convertView.findViewById(R.id.log_time);
        //元件获取数据
        log_type.setText("日志ID: "+mList.get(position).getId());
        log_device.setText("钥匙名称:"+mList.get(position).getKey_name());
        log_time.setText("锁的名称:"+mList.get(position).getLock_name());

        return convertView;
    }
}
