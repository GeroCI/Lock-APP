package com.system.LockManage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.system.LockManage.R;
import com.system.LockManage.activity.LoginActivity;
import com.system.LockManage.adapter.alarmFragmentAdapter;
import com.system.LockManage.adapter.devicesFragmentAdapter;
import com.system.LockManage.bean.User;
import com.system.LockManage.bean.Warning;
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

public class AlarmFragment extends Fragment {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public JSONObject json;
    public ListView listView;
    public com.system.LockManage.adapter.lockFragmentAdapter lockFragmentAdapter = null;
    public LinkedList<Warning> wList = null;
    public Context mContext ;
    public Warning warning = null;
    public static JSONArray resultAlarm = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, null);
        initView(view);
        return view;
    }


    private void initView(View view) {
        wList = new LinkedList<Warning>();
        listView = (ListView) view.findViewById(R.id.listView_alarm);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    json = new JSONObject();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    json.put("username",LoginActivity.usrname.getText().toString());
                    RequestBody body =RequestBody.create(JSON,json.toString());
                    String url = "http://47.111.79.11:8080/getWarningList";
                    Request request = new Request.Builder().url(url).get().build();
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
                                Log.i("value",value);
                                Log.i("value_a",value_a);
                                JSONObject jsonObject = new JSONObject(value_a.substring(value.indexOf("{"), value_a.lastIndexOf("}") + 1));
                                int code = jsonObject.getInt("code");
                                Log.i("code", String.valueOf(code));
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
                resultAlarm = jsonObject.getJSONArray("data");
                if(resultAlarm.length()>0){
                for(int i =0;i<jsonObject.length();i++) {
                    warning = new Warning();
                    warning.setId(resultAlarm.getJSONObject(i).getInt("id"));
                    warning.setLock_name(resultAlarm.getJSONObject(i).getString("name"));
                    warning.setWarning_message(resultAlarm.getJSONObject(i).getString("warning_message"));
                    warning.setWarning_time(Timestamp.valueOf(DateUtil.getDateToString(resultAlarm.getJSONObject(i).getLong("warning_time"),"yyyy-MM-dd HH:mm:ss")));
                    wList.add(warning);
                }
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
                    alarmFragmentAdapter alarmFragmentAdapter = new alarmFragmentAdapter(wList,mContext);
                    listView.setAdapter(alarmFragmentAdapter);
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

                        }
                    });
                    break;
            }
        }
    };


}
