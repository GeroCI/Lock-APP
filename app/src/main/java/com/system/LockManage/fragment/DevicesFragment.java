package com.system.LockManage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.system.LockManage.R;
import com.system.LockManage.activity.LoginActivity;
import com.system.LockManage.adapter.devicesFragmentAdapter;
import com.system.LockManage.bean.Key;
import com.system.LockManage.bean.User;
import com.system.LockManage.util.DateUtil;
import com.system.LockManage.util.DialogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DevicesFragment extends Fragment {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public JSONObject json;
    public ListView listView;
    public devicesFragmentAdapter devicesFragmentAdapter = null;
    public LinkedList<Key> kList = null;
    public Context mContext ;
    public Key key = null;
    public static JSONArray resultDevices = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        kList = new LinkedList<Key>();
        listView = (ListView) view.findViewById(R.id.listView_devices);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    json = new JSONObject();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    json.put("username",LoginActivity.usrname.getText().toString());
                    RequestBody body =RequestBody.create(JSON,json.toString());
                    String url = "http://47.111.79.11:8080/getKeyList";
                    Request request = new Request.Builder().url(url).post(body).build();
                    Call mcall = client.newCall(request);
                    mcall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            DialogUtil.title = "连接服务器失败";
                            DialogUtil.message="请等待管理员完善服务器";
                            DialogUtil.leftButton="关闭";
                            DialogUtil.rightButton="确定";
                            DialogUtil.dialogShow(getContext());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String value =  response.body().string();
                                String value_a = value.replace("\\","");
//                                Log.i("value",value);
//                                Log.i("value_a",value_a);
                                JSONObject jsonObject = new JSONObject(value_a.substring(value.indexOf("{"), value_a.lastIndexOf("}") + 1));
                                int code = jsonObject.getInt("code");
//                                Log.i("code", String.valueOf(code));
                                if(code == 1) {
                                    jsonJX(jsonObject); ;
                                }else{
                                    DialogUtil.title = "服务器返回数据有误";
                                    DialogUtil.message="请等待管理员检查数据";
                                    DialogUtil.leftButton="关闭";
                                    DialogUtil.rightButton="确定";
                                    DialogUtil.dialogShow(getContext());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ;
    }
    private void jsonJX(JSONObject jsonObject) {
        //判断数据是空
        if (jsonObject != null) {
            try {
                 resultDevices = jsonObject.getJSONArray("data");
                for(int i=0;i<resultDevices.length();i++) {
                    key = new Key();
                    key.setId(resultDevices.getJSONObject(i).getInt("id"));
                    key.setBelonger(resultDevices.getJSONObject(i).getString("belonger"));
                    key.setKey_name(resultDevices.getJSONObject(i).getString("key_name"));
                    key.setMac_addr(resultDevices.getJSONObject(i).getString("mac_addr"));
                    key.setNotes(resultDevices.getJSONObject(i).getString("notes"));
                    key.setStatus((byte) resultDevices.getJSONObject(i).getInt("status"));
                    key.setStatus_((byte)resultDevices.getJSONObject(i).getInt("status_"));
//                    user.setName(result.getString("name"));
//                    user.setAdmin_areas(result.getString("admin_areas"));
//                    user.setAuthority((byte) result.getInt("authority"));
//                    user.setDepartment(result.getString("department"));
//                    user.setDuty(result.getString("duty"));
//                    String pattern = "yyyy-MM-dd HH:mm:ss";
//                    String end_time = DateUtil.getDateToString(result.getLong("end_time"), pattern);
//                    String start_time = DateUtil.getDateToString(result.getLong("start_time"), pattern);
//                    user.setEnd_time(Timestamp.valueOf(end_time));
//                    user.setPassword(result.getString("password"));
//                    user.setPhone(result.getString("phone"));
//                    user.setStart_time(Timestamp.valueOf(start_time));
//                    user.setStatus_(Byte.parseByte(result.getString("status_")));
//                    user.setUsername(result.getString("username"));
                    kList.add(key);
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //Handler运行在主线程中(UI线程中)，  它与子线程可以通过Message对象来传递数据
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mContext = getContext();
                    devicesFragmentAdapter devicesFragmentAdapter = new devicesFragmentAdapter(kList,mContext);
                    listView.setAdapter(devicesFragmentAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            final AlertDialog.Builder builder =
                                    new AlertDialog.Builder(getContext());
                            // 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                             View dialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_device, null);
                             builder.setView(dialog);
                             final AlertDialog temp = builder.show();
                             TextView content = (TextView) dialog.findViewById(R.id.content);
                             TextView mac_address = (TextView) dialog.findViewById(R.id.mac_address);
                             TextView auth_elec = (TextView) dialog.findViewById(R.id.auth_elec);
                             TextView auth_lock = (TextView) dialog.findViewById(R.id.auth_lock);
                             TextView auth_begintime = (TextView) dialog.findViewById(R.id.auth_begintime);
                             TextView auth_endtime = (TextView) dialog.findViewById(R.id.auth_endtime);
                             Button key_close = (Button)dialog.findViewById(R.id.key_close);
                             key_close.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     temp.dismiss();
                                 }
                             });
//                            normalDialog.setIcon(R.mipmap.error);
//                            normalDialog.setTitle("钥匙详情:");
//                            normalDialog.setMessage("钥匙名称:");
//                            normalDialog.setMessage("MAC地址:");
//                            normalDialog.setMessage("钥匙电量:");
//                            normalDialog.setMessage("授权锁具:");
//                            normalDialog.setMessage("授权开始时间:");
//                            normalDialog.setMessage("授权结束时间:");
//                            normalDialog.setPositiveButton("关闭",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            //...To-do
//                                        }
//                                    });

                        }
                    });
                    break;
            }
        }
    };
}

