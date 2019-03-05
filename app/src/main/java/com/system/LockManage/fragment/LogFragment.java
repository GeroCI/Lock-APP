package com.system.LockManage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.system.LockManage.R;
import com.system.LockManage.activity.LoginActivity;
import com.system.LockManage.adapter.logFragmentAdapter;
import com.system.LockManage.adapter.meFragmentAdapter;
import com.system.LockManage.bean.OperationLog;
import com.system.LockManage.bean.User;
import com.system.LockManage.util.DateUtil;
import com.system.LockManage.util.DialogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogFragment extends Fragment implements View.OnClickListener {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public JSONObject json;
    public ListView listView;
    public com.system.LockManage.adapter.lockFragmentAdapter lockFragmentAdapter = null;
    public LinkedList<OperationLog> oList = null;
    public Context mContext ;
    public OperationLog operationLog = null;
    public static  JSONArray resultLog = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, null);
        initView(view);
        return view;
    }
    private void initView(View view) {
        oList = new LinkedList<OperationLog>();
        listView = (ListView) view.findViewById(R.id.listView_log);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    json = new JSONObject();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    json.put("username",LoginActivity.usrname.getText().toString());
                    RequestBody body =RequestBody.create(JSON,json.toString());
                    String url = "http://47.111.79.11:8080/getOperationLogList";
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
                resultLog = jsonObject.getJSONArray("data");
                if(resultLog.length()>0) {
//                        Looper.prepare();
//                        Toast.makeText(this.getActivity(), "暂时没有日志", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                }else {
                    for(int i =0;i<resultLog.length();i++) {
                        operationLog = new OperationLog();
                        operationLog.setBind_operator(resultLog.getJSONObject(i).getString("bind_operator"));
                        operationLog.setDistrict(resultLog.getJSONObject(i).getString("district"));
                        operationLog.setId(resultLog.getJSONObject(i).getInt("id"));
                        operationLog.setKey_name(resultLog.getJSONObject(i).getString("key_name"));
                        operationLog.setLock_name(resultLog.getJSONObject(i).getString("lock_name"));
                        operationLog.setOperation_time(Timestamp.valueOf(DateUtil.getDateToString(resultLog.getJSONObject(i).getLong("operation_time"),"yyyy-MM-dd HH:mm:ss")));
                        operationLog.setOperation_type((byte)resultLog.getJSONObject(i).getInt("type"));
                        operationLog.setOperator(resultLog.getJSONObject(i).getString("operator"));
                        operationLog.setStatus_((byte)resultLog.getJSONObject(i).getInt("status_"));
                        operationLog.setUpload_time(Timestamp.valueOf(DateUtil.getDateToString(resultLog.getJSONObject(i).getLong("upload_time"),"yyyy-MM-dd HH:mm:ss")));
                        oList.add(operationLog);
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
                    logFragmentAdapter logFragmentAdapter = new logFragmentAdapter(oList,mContext);
                    listView.setAdapter(logFragmentAdapter);

                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

    }
}
