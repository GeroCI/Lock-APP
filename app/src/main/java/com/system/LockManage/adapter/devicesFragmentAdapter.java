
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
import com.system.LockManage.bean.Key;
import com.system.LockManage.bean.User;

import java.util.LinkedList;

public class devicesFragmentAdapter extends BaseAdapter {
    //声明一个链表和Context对象
    private LinkedList<Key> mList;
    private Context mContext;
    public devicesFragmentAdapter(LinkedList<Key> mList, Context mContext){
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
        convertView=LayoutInflater.from(mContext).inflate(R.layout.item_devices,parent,false);
        //实例化元件
        TextView devices=(TextView) convertView.findViewById(R.id.devices);
        TextView mac=(TextView) convertView.findViewById(R.id.mac);
        TextView elec = convertView.findViewById(R.id.electorycitys);
        //元件获取数据
        devices.setText("钥匙id: "+String.valueOf(mList.get(position).getId()));
        mac.setText("Mac: "+mList.get(position).getMac_addr());
        elec.setText("电量：未知");
        return convertView;
    }
}
