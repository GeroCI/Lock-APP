package com.system.LockManage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.system.LockManage.R;
import com.system.LockManage.bean.Key;
import com.system.LockManage.bean.Onu;
import com.system.LockManage.fragment.AlarmFragment;
import com.system.LockManage.fragment.AuthFragment;
import com.system.LockManage.fragment.DevicesFragment;
import com.system.LockManage.fragment.LockFragment;
import com.system.LockManage.fragment.LogFragment;
import com.system.LockManage.fragment.MyFragment;
import com.system.LockManage.util.BluetoothManager;
import com.system.LockManage.util.DateUtil;
import com.system.LockManage.util.DialogUtil;
import com.system.LockManage.view.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2019/2/25.
 */
public class MainActivity extends BaseActivity {

    private final String TAG = "Map_Activity";

    private String[] mTitle = {"我的", "设备", "锁具", "授权","告警","日志"};
    private ViewPager mViewPager ;
    private TabLayout mTabView ;
    private Map<Integer, Fragment> mFragmentMap ;
    private int[] mIconSelect = { R.mipmap.account_green,R.mipmap.key1_green, R.mipmap.unlock_green, R.mipmap.query_green,R.mipmap.auth_green,R.mipmap.compass_green};
    private int[] mIconNormal = { R.mipmap.account,R.mipmap.key1, R.mipmap.unlock, R.mipmap.query,R.mipmap.auth,R.mipmap.compass};
    private ImageView QRCode ;
    private ImageView Lanya ;
    private int year;
    private int month;
    private int day;
    private StringBuffer date;
    private View dialog;
    private EditText edit_text_setupTime;
    private EditText edit_text_setupDate;
    private JSONObject jsonObject,jsonResult;
    private  Button onu_send ;
    private  Button onu_rush;
    private TextView onu_id;
    private EditText onu_name ;
    private EditText onu_belonger;
    private EditText onu_setupTime ;
    private EditText onu_inOnu ;
    private EditText onu_outOnu ;
    private EditText onu_Lng ;
    private EditText onu_Lat;
    private EditText onu_Status;
    private EditText onu_rate;
    private String pattern = "yyyy-MM-dd HH:mm:ss";
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.title_wechat));
        setLeftBtnVisibility(View.GONE);

        mFragmentMap = new HashMap<>() ;
        Lanya = findViewById(R.id.Lanya);
        QRCode = findViewById(R.id.QRCode);
        mViewPager = (ViewPager)findViewById(R.id.activity_main_viewpager) ;
        mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                 switch (position){
                     case 0:
                         if(MyFragment.resultMe==null){
                             Toast.makeText(getFragment(position).getActivity(), "暂时没有个人信息", Toast.LENGTH_SHORT).show();
                         }
                         break;
                     case 1:
                         if(DevicesFragment.resultDevices.length()==0){
                             Toast.makeText(getFragment(position).getActivity(), "暂时没有钥匙信息", Toast.LENGTH_SHORT).show();
                         }
                         break;
                     case 2:
                         if(LockFragment.resultLock.length()==0){
                             Toast.makeText(getFragment(position).getActivity(), "暂时没有锁具信息", Toast.LENGTH_SHORT).show();
                         }
                         break;
                     case 3:
                         if(AuthFragment.resultAuth.length()==0){
                             Toast.makeText(getFragment(position).getActivity(), "暂时没有锁具信息", Toast.LENGTH_SHORT).show();
                         }
                         break;
                     case 4:
                         if(AlarmFragment.resultAlarm.length()==0){
                             Log.i("position",String.valueOf(position));
                             Toast.makeText(getFragment(position).getActivity(), "暂时没有告警信息", Toast.LENGTH_SHORT).show();
                         }
                         break;
                     case 5:
                         if(LogFragment.resultLog.length()==0){
                             Log.i("position",String.valueOf(position));
                             Toast.makeText(getFragment(position).getActivity(), "暂时没有日志信息", Toast.LENGTH_SHORT).show();
                         }
                         break;
                 }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabView = (TabLayout) findViewById(R.id.activity_main_tablayout) ;
        mTabView.setViewPager(mViewPager);//tablayout和viewpager联动
        List<PermissonItem> permissonItems = new ArrayList<PermissonItem>();
        permissonItems.add(new PermissonItem(Manifest.permission.ACCESS_FINE_LOCATION, "定位", R.drawable.permission_ic_location));


        QRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建IntentIntegrator对象
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                // 开始扫描
                intentIntegrator.initiateScan();

            }
        });

        Lanya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(),"当前设备不支持蓝牙！",Toast.LENGTH_SHORT).show();
                }
                else
                    mBluetoothAdapter.enable();//强制打开蓝牙
            }
        });
        HiPermission.create(MainActivity.this)
                .title("亲爱的用户")
                .permissions(permissonItems)
                .filterColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
                .msg("为了程序的正确运行，我们申请如下权限")
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        Toast.makeText(getApplicationContext(),"用户关闭权限申请",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(getApplicationContext(),"所有权限申请完成",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        Toast.makeText(getApplicationContext(),"所有权限申请完成",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                    }
                });


    }

    private Fragment getFragment(int position){
        Fragment fragment = mFragmentMap.get(position) ;
        if(fragment == null){
            switch (position){
                case 0:
                    fragment = new MyFragment() ;
                    break ;
                case 1:
                    fragment = new DevicesFragment();
                    break ;
                case 2:
                    fragment = new LockFragment();
                    break;
                case 3:
                    fragment = new AuthFragment() ;
                    break;
                case 4:
                    fragment = new AlarmFragment() ;
                    break;
                case 5:
                    fragment = new LogFragment() ;
                    break;
            }
            mFragmentMap.put(position,fragment) ;
        }
        return fragment ;
    }

    class PageAdapter extends FragmentPagerAdapter implements TabLayout.OnItemIconTextSelectListener{

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }
        @Override
        public int[] onIconSelect(int position) {
            int icon[] = new int[2] ;
            icon[0] = mIconSelect[position] ;
            icon[1] = mIconNormal[position] ;
            return icon;
        }
        @Override
        public String onTextSelect(int position) {
            return mTitle[position];
        }

        @Override
        public int getCount() {
            return mTitle.length;
        }
    }
    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }
    /**
     * 日期改变的监听事件
     *
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取解析结果
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "取消扫描", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(this, "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
                dialog = LayoutInflater.from(this).inflate(R.layout.dialog_qrcode, null);
                onu_rush = dialog.findViewById(R.id.onu_rush);
                onu_id = dialog.findViewById(R.id.onu_id);
                onu_name = dialog.findViewById(R.id.edit_name);
                onu_belonger = dialog.findViewById(R.id.edit_belonger);
//                onu_setupTime = dialog.findViewById(R.id.edit_text_setupTime);
                onu_inOnu = dialog.findViewById(R.id.edit_inonu);
                onu_outOnu = dialog.findViewById(R.id.edit_outonu);
                onu_Lng = dialog.findViewById(R.id.edit_lng);
                onu_Lat = dialog.findViewById(R.id.edit_lat);
                onu_Status = dialog.findViewById(R.id.edit_status);
                onu_rate = dialog.findViewById(R.id.edit_rate);
                edit_text_setupDate = dialog.findViewById(R.id.edit_text_setupDate);
                edit_text_setupTime = dialog.findViewById(R.id.edit_text_setupTime);
                Button onu_receive = dialog.findViewById(R.id.onu_receive);
                onu_send = dialog.findViewById(R.id.onu_send);
//                Onu onu = new Onu(onu_id.getText(),onu_name.getText(),onu_belonger.getText(),
//                        onu_setupTime.getText(),onu_inOnu.getText(),onu_outOnu.getText(),
//                        onu_Lng.getText(), onu_Lat.getText(),onu_Status.getText(),onu_rate.getText());
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
                // 通过LayoutInflater来加载一个xml的布局文件作为一个View对象

                onu_id.setText("光交箱id: "+result.getContents());
                builder.setView(dialog);
                final AlertDialog temp = builder.show();
                onu_receive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        temp.dismiss();
                    }
                });
                edit_text_setupDate.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            showDatePickDlg();
                            return true;
                        }
                        return false;
                    }
                });
                edit_text_setupDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            showDatePickDlg();
                        }
                    }
                });
                edit_text_setupTime.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            showTimePickDlg();
                            return true;
                        }
                        return false;
                    }
                });
                edit_text_setupTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            showTimePickDlg();
                        }
                    }
                });
            }
            onu_rush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                    MediaType JSON = null;
                    jsonObject = new JSONObject();
                    OkHttpClient client = new OkHttpClient();
                    JSON = MediaType.parse("application/json; charset=utf-8");
                    jsonObject.put("id", Integer.parseInt(result.getContents()));
                    RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                    String url = "http://47.111.79.11:8080/getOneOnu";
                    Request request = new Request.Builder().url(url).post(body).build();
                    Call mcall = client.newCall(request);
                    mcall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            DialogUtil.title = "连接服务器失败";
                            DialogUtil.message = "请等待管理员完善服务器";
                            DialogUtil.leftButton = "关闭";
                            DialogUtil.rightButton = "确定";
                            DialogUtil.dialogShow(getApplicationContext());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String value = response.body().string();
                                String value_a = value.replace("\\", "");
                                jsonResult = new JSONObject(value_a.substring(value.indexOf("{"), value_a.lastIndexOf("}") + 1));
                                int code = jsonResult.getInt("code");
                                if (code == 1) {
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                } else {
                                    DialogUtil.title = "服务器返回数据有误";
                                    DialogUtil.message = "请等待管理员检查数据";
                                    DialogUtil.leftButton = "关闭";
                                    DialogUtil.rightButton = "确定";
                                    DialogUtil.dialogShow(getApplicationContext());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });  } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            onu_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        MediaType JSON = null;
                        jsonObject = new JSONObject();
                        OkHttpClient client = new OkHttpClient();
                        JSON = MediaType.parse("application/json; charset=utf-8");
                        jsonObject.put("id", Integer.parseInt(result.getContents()));
                        jsonObject.put("name",onu_name.getText().toString());
                        jsonObject.put("belonger",onu_belonger.getText().toString());
                        String date = edit_text_setupDate.getText().toString()+" "+edit_text_setupTime.getText().toString();
                        jsonObject.put("setup_time",Timestamp.valueOf(date));
                        jsonObject.put("in_onu",onu_inOnu.getText().toString());
                        jsonObject.put("out_onu",onu_outOnu.getText().toString());
                        jsonObject.put("lng",Float.parseFloat(onu_Lng.getText().toString()));
                        jsonObject.put("lat",Float.parseFloat(onu_Lat.getText().toString()));
                        jsonObject.put("status_",Integer.parseInt(onu_Status.getText().toString()));
                        jsonObject.put("occupancy_rate",Float.parseFloat(onu_rate.getText().toString()));
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                        String url = "http://47.111.79.11:8080/appUpdateOneOnu";
                        Request request = new Request.Builder().url(url).post(body).build();
                        Call mcall = client.newCall(request);
                        mcall.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                DialogUtil.title = "连接服务器失败";
                                DialogUtil.message = "请等待管理员完善服务器";
                                DialogUtil.leftButton = "关闭";
                                DialogUtil.rightButton = "确定";
                                DialogUtil.dialogShow(getApplicationContext());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    String value = response.body().string();
                                    String value_a = value.replace("\\", "");
                                    jsonResult = new JSONObject(value_a.substring(value.indexOf("{"), value_a.lastIndexOf("}") + 1));
                                    int code = jsonResult.getInt("code");
                                    if (code == 1) {
                                        Looper.prepare();
                                        Toast.makeText(getApplicationContext(), "数据上传成功,可刷新查看", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } else {
                                        DialogUtil.title = "上传数据有误";
                                        DialogUtil.message = "请您重新检查数据";
                                        DialogUtil.leftButton = "关闭";
                                        DialogUtil.rightButton = "确定";
                                        DialogUtil.dialogShow(getApplicationContext());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });  } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    protected void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear +=1;
                edit_text_setupDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    protected void showTimePickDlg() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                edit_text_setupTime.setText(i+":"+i1+":"+"30");
            }
        },calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),true);
        timePickerDialog.show();
    }
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    jsonJX(jsonResult);
            }
        }
    };
    private void jsonJX(JSONObject jsonObject) {
        //判断数据是空
        if (jsonObject != null) {
            try {

                JSONObject result = jsonObject.getJSONObject("data");
                String date =DateUtil.getDateToString(result.getLong("setup_time"),pattern);
                String []date_process = date.split(" ");
                onu_name.setText(result.getString("name")); ;
                onu_belonger.setText(result.getString("belonger"));
                edit_text_setupDate.setText(date_process[0]);
                edit_text_setupTime.setText(date_process[1]); ;
                onu_inOnu.setText(result.getString("in_onu")); ;
                onu_outOnu.setText(result.getString("out_onu")); ;
                onu_Lng.setText(String.valueOf(result.getDouble("lng"))); ;
                onu_Lat.setText(String.valueOf(result.getDouble("lat")));
                onu_Status.setText(String.valueOf(result.getInt("status_")));
                onu_rate.setText(String.valueOf(result.getDouble("occupancy_rate")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
