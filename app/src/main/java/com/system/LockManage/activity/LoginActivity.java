package com.system.LockManage.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.system.LockManage.R;
import com.system.LockManage.bean.User;
import com.system.LockManage.util.DialogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity {
    public static EditText usrname = null;
    private EditText pasword = null;
    private CheckBox checkBox = null;
    private Button btn = null;
    private Intent intent = null;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    public LinkedList<User> mList = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usrname = (EditText) findViewById(R.id.et_user_name);
        pasword = (EditText) findViewById(R.id.et_psw);
        checkBox = (CheckBox) findViewById(R.id.remember_psd);
        btn = (Button) findViewById(R.id.btn_login);
        intent = new Intent(this,MainActivity.class);
        sp= this.getSharedPreferences("userinfo",MODE_PRIVATE);
        //判断记住密码chekbox的状态
        if(sp.getBoolean("ISCHECK",false)){
            /* 设置默认是记住密码状态*/
            checkBox.setChecked(true);
            usrname.setText(sp.getString("name",""));
            pasword.setText(sp.getString("password",""));
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox.isChecked()){
                    sp.edit().putBoolean("ISCHECK",true).commit();
                }
                else {
                    sp.edit().putBoolean("ISCHECK",false).commit();
                }
            }
        });
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                    try {
                                        Looper.prepare();
                                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                        JSONObject json = new JSONObject();
                                        json.put("username",usrname.getText().toString());
                                        json.put("password",pasword.getText().toString());
//                                        Log.i("username",usrname.getText().toString());
//                                        Log.i("password",pasword.getText().toString());
                                        String url = "http://47.111.79.11:8080/loginApp";
                                        OkHttpClient client = new OkHttpClient();
                                        RequestBody body = RequestBody.create(JSON,json.toString());
                                        Request request = new Request.Builder().url(url)
                                                .post(body).build();
                                        Call mcall = client.newCall(request);
                                        mcall.enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                DialogUtil.title = "服务器连接失败";
                                                DialogUtil.message="请等待管理员检查服务器";
                                                DialogUtil.leftButton="关闭";
                                                DialogUtil.rightButton="确定";
                                                DialogUtil.dialogShow(getApplicationContext());
                                            }
                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                try {
//                                                    JSONArray jsonArray = new JSONArray(response.body().string());
//                                                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                                                    String value =  response.body().string();
                                                    String value_review = value.replace("\\","");
//                                                    Log.i("value",value);
//                                                    Log.i("value_review",value_review);
                                                    JSONObject jsonObject = new JSONObject(value_review.substring(value.indexOf("{"), value_review.lastIndexOf("}") + 1));
                                                    int code = jsonObject.getInt("code");
                                                    if(code == 1) {
                                                        if(checkBox.isChecked()){
                                                            SharedPreferences.Editor editor = sp.edit();
                                                            editor.putString("name",usrname.getText().toString());
                                                            editor.putString("password",pasword.getText().toString());
                                                            editor.commit();
                                                        }
                                                        startActivity(intent);
                                                    }else{
                                                        DialogUtil.title = "服务器返回数据错误";
                                                        DialogUtil.message="请等待管理员检查数据";
                                                        DialogUtil.leftButton="关闭";
                                                        DialogUtil.rightButton="确定";
                                                        DialogUtil.dialogShow(getApplicationContext());
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        Looper.loop();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                            }
                        }).start();
                    }
                }
        );
        }


}
