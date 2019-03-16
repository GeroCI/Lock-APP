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
import com.system.LockManage.bean.Authorization;
import com.system.LockManage.bean.User;
import com.system.LockManage.util.DateUtil;

import java.util.LinkedList;

public class authFragmentAdapter extends BaseAdapter {
    //声明一个链表和Context对象
    private LinkedList<Authorization> mList;
    private Context mContext;
    public authFragmentAdapter(LinkedList<Authorization> mList, Context mContext){
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
        convertView=LayoutInflater.from(mContext).inflate(R.layout.item_auth,parent,false);
        //实例化元件
        TextView auth_name=(TextView) convertView.findViewById(R.id.auth_name);
        TextView auth_key=(TextView) convertView.findViewById(R.id.auth_key);
        TextView auth_time=(TextView) convertView.findViewById(R.id.auth_time);
        //元件获取数据
        auth_name.setText(mList.get(position).getName());
        auth_key.setText(String.valueOf(mList.get(position).getStart_time()));
        auth_time.setText(String.valueOf(mList.get(position).getEnd_time()));

        return convertView;
    }
}
