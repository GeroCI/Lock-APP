package com.system.LockManage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.system.LockManage.R;
import com.system.LockManage.activity.LoginActivity;
import com.system.LockManage.activity.Map_Activity;
import com.system.LockManage.adapter.lockFragmentAdapter;
import com.system.LockManage.adapter.meFragmentAdapter;
import com.system.LockManage.bean.Lock;
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

//import com.sytem.LockManage.activity.Map_Activity;

public class LockFragment extends Fragment {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public JSONObject json;
    public ListView listView;
    public lockFragmentAdapter lockFragmentAdapter = null;
    public LinkedList<Lock> lList = null;
    public Context mContext ;
    public Lock lock = null;
    public static JSONArray resultLock = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock, null);
        TextView list_view = (TextView) view.findViewById(R.id.list_view);
        TextView map_view = (TextView) view.findViewById(R.id.map_view);
        map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Map_Activity.class);

                startActivity(intent);
            }
        });
        list_view.setWidth(getScreenWidth(view.getContext())/2);
        map_view.setWidth(getScreenWidth(view.getContext())/2);
        initView(view);
        return view;
    }
    public static int getScreenWidth(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
    public static int getScreenHeight(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }
    private void initView(View view) {
        lList = new LinkedList<Lock>();
        listView = (ListView) view.findViewById(R.id.listView_lock);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    json = new JSONObject();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    json.put("username",LoginActivity.usrname.getText().toString());
                    RequestBody body =RequestBody.create(JSON,json.toString());
                    String url = "http://47.111.79.11:8080/getLockList";
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
                resultLock = jsonObject.getJSONArray("data");
                for(int i = 0;i<resultLock.length();i++) {
                    lock = new Lock();
                    lock.setBelonger(resultLock.getJSONObject(i).getString("belonger"));
                    lock.setDetail_addr(resultLock.getJSONObject(i).getString("detail_addr"));
                    lock.setDevice_key(resultLock.getJSONObject(i).getInt("device_key"));
                    lock.setId(resultLock.getJSONObject(i).getInt("id"));
                    lock.setInstallation_site(resultLock.getJSONObject(i).getString("installation_site"));
                    lock.setLat((float) resultLock.getJSONObject(i).getDouble("lat"));
                    lock.setLng((float) resultLock.getJSONObject(i).getDouble("lng"));
                    lock.setLock_name(resultLock.getJSONObject(i).getString("lock_name"));
                    lock.setMac_addr(resultLock.getJSONObject(i).getString("mac_addr"));
                    lock.setNotes(resultLock.getJSONObject(i).getString("notes"));
                    lock.setSignal_value((byte) resultLock.getJSONObject(i).getInt("signal_value"));
                    lock.setStatus_((byte)resultLock.getJSONObject(i).getInt("status_"));
                    lList.add(lock);
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
                    lockFragmentAdapter lockFragmentAdapter = new lockFragmentAdapter(lList,mContext);
                    listView.setAdapter(lockFragmentAdapter);
                    break;
            }
        }
    };


}
