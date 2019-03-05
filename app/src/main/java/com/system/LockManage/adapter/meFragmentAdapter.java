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

import java.util.LinkedList;

public class meFragmentAdapter extends BaseAdapter {
    //声明一个链表和Context对象
    private LinkedList<User> mList;
    private Context mContext;
    public meFragmentAdapter(LinkedList<User> mList, Context mContext){
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
        convertView=LayoutInflater.from(mContext).inflate(R.layout.item_me,parent,false);
        //实例化元件
        TextView account_name=(TextView) convertView.findViewById(R.id.account_name);
        TextView account_id=(TextView) convertView.findViewById(R.id.account_id);
        //元件获取数据
        account_name.setText("用户名: "+mList.get(position).getName());
        account_id.setText("管理区域: "+mList.get(position).getAdmin_areas().replace("，","/"));
        return convertView;
    }
}
