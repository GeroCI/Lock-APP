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
import com.system.LockManage.bean.Lock;
import com.system.LockManage.bean.User;

import java.util.LinkedList;

public class lockFragmentAdapter extends BaseAdapter {
    //声明一个链表和Context对象
    private LinkedList<Lock> mList;
    private Context mContext;
    public lockFragmentAdapter(LinkedList<Lock> mList, Context mContext){
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
        convertView=LayoutInflater.from(mContext).inflate(R.layout.item_lock,parent,false);
        //实例化元件
        TextView lock_id=(TextView) convertView.findViewById(R.id.lock_id);
        TextView lock_address=(TextView) convertView.findViewById(R.id.lock_address);
        //元件获取数据
        lock_id.setText("锁具名称: "+mList.get(position).getLock_name());
        lock_address.setText("锁具地址："+(mList.get(position).getInstallation_site()+"，"+mList.get(position).getDetail_addr()).replace('，','/'));
        return convertView;
    }
}
