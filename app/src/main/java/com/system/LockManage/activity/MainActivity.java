package com.system.LockManage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsMessage;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.system.LockManage.R;
import com.system.LockManage.bluetooth.DeviceListActivity;
import com.system.LockManage.bluetooth.UartService;
import com.system.LockManage.des.Des;
import com.system.LockManage.fragment.AlarmFragment;
import com.system.LockManage.fragment.AuthFragment;
import com.system.LockManage.fragment.DevicesFragment;
import com.system.LockManage.fragment.LockFragment;
import com.system.LockManage.fragment.LogFragment;
import com.system.LockManage.fragment.MyFragment;
import com.system.LockManage.util.DateUtil;
import com.system.LockManage.util.DialogUtil;
import com.system.LockManage.util.LocationUtil;
import com.system.LockManage.view.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 2;
    private TextView OpenLock;
    public int mState = 0;
    private static final int UART_PROFILE_CONNECTED = 20;
    private byte[] reciveByte,keyByte,deByte,RandomThree,ComKey;
    public static final byte[] LOCK_Code = new byte[]{(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
    //这是后台朗读，实例化一个SynthesizerPlayer
    private SpeechSynthesizer mTts ;
    private static final String ROOT_PASSWORD_KEY = "SDtt6789";
    private static final byte[] LOCK_INT_KEY = "TTMJ_234".getBytes();
    public StringBuilder stringBuilder = new StringBuilder();
    private static final int REQUEST_SELECT_DEVICE = 1;
    private BluetoothDevice mDevice = null;
    public static String BluetoothAddress;
    public static String BluetoothName ="";
    private static final String KEY_INT_KEY = "TTMJ_345";
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int RET_KEY_ID =14;
    //private static final android.R.attr R = ;
    public static UartService mService = null;
    private BluetoothAdapter mBtAdapter = null;
    private long exitTime=0;
    private int PackCount = 0;
    private int comlength = 0;
    private int currOrder = 0;
    private int CommandOrder = 0;
    private boolean isConnected = false;
    public static TextView console;
    public StringBuilder CBuilder = new StringBuilder();
    public Button bundKey = null;
    public Button initLock = null;
    public Button communicationKey = null;
    public Button openDoor = null;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.title_wechat));
        setLeftBtnVisibility(View.GONE);
//        bundKey = findViewById(R.id.bund);
//        bundKey.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BandKey();
//            }
//        });
//        initLock = findViewById(R.id.init_lock);
//        initLock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InitLock();
//            }
//        });
//        communicationKey = findViewById(R.id.communicationKey);
//        communicationKey.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CommunicationKey();
//            }
//        });
//        openDoor = findViewById(R.id.OpenDoor);
//        openDoor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                OpenDoor();
//            }
//        });
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=519328ab");
        mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "vixy");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "60");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;

        }
        mService = new UartService(mBtAdapter);
        service_init();
        mFragmentMap = new HashMap<>() ;
        Lanya = findViewById(R.id.Lanya);
        QRCode = findViewById(R.id.QRCode);
        OpenLock = findViewById(R.id.OpenLock);
//        console = (TextView)findViewById(R.id.console);
//        console.setMovementMethod(ScrollingMovementMethod.getInstance());
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
        OpenLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //初始化
//                BandKey();
                //初始化锁
                InitLock();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //通信
                        CommunicationKey();
                    }
                },1000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //开门
                        OpenDoor();
                    }
                },3000);
//                //锁具初始化
//                InitLock();
//
//                //通信秘钥
//                CommunicationKey();
//
//                //开门
//                OpenDoor();
                showMessage("正在开锁");
            }
        });

        Lanya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DeviceListActivity.class);
                startActivityForResult(intent,REQUEST_SELECT_DEVICE);
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
    SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };
    private void savaTxt()
    {
        saveToSDCard(stringBuilder.toString());            //以追加的方式保存到文本
        CBuilder.append(stringBuilder.toString());        //以追加的方式保存到字符串
        console.setText(CBuilder);                          //显示到文本框
        stringBuilder.delete(0,stringBuilder.length());  //删除数据
    }
    // save infomation in the SDCard
    public boolean saveToSDCard(String content) {
        // judge weather the SDCard exits,and can be read and written
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }

        FileOutputStream fileOutputStream = null;
        File file = new File(Environment.getExternalStorageDirectory()+"/SystemLog.txt");
        try {
            fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write(content.getBytes());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //初始化锁
    protected void InitLock()
    {
        if ( mState != UART_PROFILE_CONNECTED)
        {
            mTts.startSpeaking("蓝牙钥匙未连接",mSynListener);
            return;
        }
        byte[] bytes = LOCK_INT_KEY;
        int[] sendData = new int[10];
        for (int i=0;i<8;i++)
        {
            sendData[i] = bytes[i];
        }
        sendData[8] =0x01;
        sendData[9] =0x01;
        stringBuilder.append(mService.sendCommand(2,sendData,10,ROOT_PASSWORD_KEY.getBytes()));
//        savaTxt();
    }
    //获取通信秘钥
    protected void CommunicationKey()
    {
        if ( mState != UART_PROFILE_CONNECTED)
        {
            mTts.startSpeaking("蓝牙钥匙未连接",mSynListener);
            return;
        }

        int[] sendData=new int[8];
        sendData[0] =0x01;
        sendData[1] =0x01;
        sendData[2] =0x01;
        sendData[3] =0x01;
        sendData[4] =0x01;
        sendData[5] =0x01;
        sendData[6] =0x01;
        sendData[7] =0x01;
        stringBuilder.append(mService.sendCommand(0x14,sendData,8,LOCK_INT_KEY));
//        savaTxt();
    }
    //开门
    protected void OpenDoor()
    {
        if ( mState != UART_PROFILE_CONNECTED)
        {
            mTts.startSpeaking("蓝牙钥匙未连接",mSynListener);
            return;
        }
        if ( ComKey == null)
        {
            mTts.startSpeaking("请先协商通讯秘钥",mSynListener);
            return;
        }

        //***钥匙串开始***//
        byte[] sendData=new byte[64];
        //用户编号
        sendData[0] =0x01;
        sendData[1] =0x01;
        sendData[2] =0x01;
        sendData[3] =0x01;
        //锁识别号
        for (int i=4;i<20;i++)
        {
            sendData[i] = LOCK_Code[i-4];
        }
        //起始时间
        String starTime ="20160901101010";
        for (int i=0;i<14;i++)
        {
            sendData[20+i] = (byte)starTime.charAt(i);
        }
        //结束时间
        String endTime = "20990901101010";
        for (int i=0;i<14;i++)
        {
            sendData[34+i] = (byte)endTime.charAt(i);
        }
        //授权时间
        String time ="20160901101010";
        for (int i= 0;i<14;i++)
        {
            sendData[48+i] = (byte)time.charAt(i);
        }
        //***钥匙串结束***//

        stringBuilder.append(getTime()+"加密前钥匙串:");
        for (int i=0;i<64;i++)
        {
            stringBuilder.append(("0"+Integer.toHexString(0xff&sendData[i])).substring(Integer.toHexString(0xff&sendData[i]).length()-1)+" ");
        }
        stringBuilder.append("\n\n");

        //对钥匙串用 初始秘钥加密
        try {
            byte[] en_byte = Des.encrypt(sendData,LOCK_INT_KEY);

            stringBuilder.append(getTime()+"加密的秘钥是:");
            for (int i=0;i<8;i++)
            {
                stringBuilder.append(("0"+Integer.toHexString(0xff&LOCK_INT_KEY[i])).substring(Integer.toHexString(0xff&LOCK_INT_KEY[i]).length()-1)+" ");
            }
            stringBuilder.append("\n\n");

            int[] finalData = new int[69];

            stringBuilder.append(getTime()+"加密后钥匙串:");
            for (int i=0;i<64;i++)
            {
                finalData[i] = en_byte[i] & 0xff;
                stringBuilder.append(("0"+Integer.toHexString(0xff&finalData[i])).substring(Integer.toHexString(0xff&finalData[i]).length()-1)+" ");
            }
            stringBuilder.append("\n\n");

            //用户编号
            finalData[64] =0x01;
            finalData[65] =0x01;
            finalData[66] =0x01;
            finalData[67] =0x01;
            //操作类型 1开 2关
            finalData[68] =0x01;

            stringBuilder.append(mService.sendCommand(3,finalData,69,ComKey));
//            savaTxt();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getTime()
    {
        String time ="";
        Calendar c = Calendar.getInstance();
        String year =c.get(Calendar.YEAR)+"";
        String month =("0"+(c.get(Calendar.MONTH)+1)).substring(("0"+(c.get(Calendar.MONTH)+1)).length()-2);
        String day =("0"+c.get(Calendar.DAY_OF_MONTH)).substring(("0"+c.get(Calendar.DAY_OF_MONTH)).length()-2);;
        String hour =("0"+c.get(Calendar.HOUR_OF_DAY)).substring(("0"+c.get(Calendar.HOUR_OF_DAY)).length()-2);
        String min =("0"+c.get(Calendar.MINUTE)).substring(("0"+c.get(Calendar.MINUTE)).length()-2);
        String second =("0"+c.get(Calendar.SECOND)).substring(("0"+c.get(Calendar.SECOND)).length()-2);
        time = month+"-"+day+" "+hour+":"+min+":"+second+": ";
        return time;
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
    //绑定蓝牙钥匙
    protected void BandKey()
    {
        if ( mState != UART_PROFILE_CONNECTED)
        {
            mTts.startSpeaking("蓝牙钥匙未连接",mSynListener);
            return;
        }
        int[] sendData=new int[12];
        sendData[0] =0x01;
        sendData[1] =0x01;
        sendData[2] =0x01;
        sendData[3] =0x01;
        for (int i= 4;i<12;i++)
        {
            sendData[i] =KEY_INT_KEY.charAt(i-4);
        }
        stringBuilder.append(mService.sendCommand(7,sendData,12,ROOT_PASSWORD_KEY.getBytes()));
//        savaTxt();
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

    //实例化 发送蓝牙命令服务
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder)rawBinder).getService();
            if (!mService.initialize()) {
                //finish();
            }
        }
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };
    //异步消息提示
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
            }
        }
    };
    //蓝牙钥匙的回调函数
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, final Intent intent) {
            String strRes = "android.provider.Telephony.SMS_RECEIVED";
            String action = intent.getAction();
            //*********************//
            if(strRes.equals(action)){
                StringBuilder sb = new StringBuilder();
                Bundle bundle = intent.getExtras();
                if(bundle!=null){
                    Object[] pdus = (Object[])bundle.get("pdus");
                    SmsMessage[] msg = new SmsMessage[pdus.length];
                    for(int i = 0 ;i<pdus.length;i++){
                        msg[i] = SmsMessage.createFromPdu((byte[])
                                pdus[i]);
                    }
                    for(SmsMessage curMsg:msg){
                        sb.append(curMsg.getDisplayMessageBody());
                    }
                }
            }

            //*********************//蓝牙连接成功
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mState = UART_PROFILE_CONNECTED;
                        invalidateOptionsMenu();
                    }
                });
            }

            //*********************//蓝牙连接断开
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED))
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                        invalidateOptionsMenu();
                    }
                });
            }

            //*********************//发现蓝牙服务
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }

            //*********************//蓝牙回发的数据
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try
                        {
                            if (txValue.length==0)return;

                            //如果起始位是0xfa,0xfb，则开始一个新的接收数据数组
                            if ((txValue[0]&0xff) == 0xfa && (txValue[1]&0xff) == 0xfb )
                            {
                                CommandOrder = (int)txValue[2];
                                //数据内容长度
                                comlength = (int)txValue[4]-2;
                                //接收数据的包数
                                PackCount = (comlength+6)/20+1;;
                                //当前包数
                                currOrder = 1;
                                //实例化 reciveByte
                                reciveByte = new byte[20*PackCount];
                                //需要解密的数据
                                keyByte = new byte[comlength];
                                //解密后的数据
                                deByte = new byte[comlength];
                                //打印包数
                                stringBuilder.append(getTime()+"接收到的数据:");
                                //console.setText(console.getText()+getTime()+"共收到"+PackCount+"包数据！！\n");
                            }

                            //开始组合数据
                            for(int i=0;i<txValue.length;i++)
                            {
                                reciveByte[20*(currOrder-1)+i] = txValue[i];
                                stringBuilder.append(("0"+Integer.toHexString(0xff&(int)txValue[i])).substring(Integer.toHexString(0xff&(int)txValue[i]).length()-1)+" ");
                            }

                            //开始解密数据(指令3，6，8为明文)
                            if (currOrder == PackCount)
                            {
                                stringBuilder.append("\n\n");
                                if (CommandOrder == 0x03 || CommandOrder == 0x06 || CommandOrder == 0x08  || CommandOrder == 0x0d ) {}
                                else
                                {
                                    //组合需要解密的数据
                                    stringBuilder.append(getTime() + "要解密的数据:");
                                    for (int i = 0; i < comlength; i++) {
                                        keyByte[i] = reciveByte[i + 5];
                                        stringBuilder.append(("0"+Integer.toHexString(0xff&(int)keyByte[i])).substring(Integer.toHexString(0xff&(int)keyByte[i]).length()-1) + " ");
                                    }
                                    stringBuilder.append("\n\n");

                                    stringBuilder.append(getTime() + "解密的秘钥是:");
                                    //根据指令，用相对应的密钥解密
                                    if ( CommandOrder == 0x09 || CommandOrder == 0x0a) //用通讯秘钥解密
                                    {
                                        deByte = Des.decrypt(keyByte, ComKey);
                                        for (int i = 0; i < 8; i++) {
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)ComKey[i])).substring(Integer.toHexString(0xff&(int)ComKey[i]).length()-1) + " ");
                                        }
                                    } else if (CommandOrder == 0x05 || CommandOrder == 0x0c || (CommandOrder == 0x04 && BluetoothName.substring(7,8).equals("0")))  //用钥匙的初始秘钥解密
                                    {
                                        deByte = Des.decrypt(keyByte, KEY_INT_KEY.getBytes());
                                        for (int i = 0; i < 8; i++) {
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)KEY_INT_KEY.getBytes()[i])).substring(Integer.toHexString(0xff&(int)KEY_INT_KEY.getBytes()[i]).length()-1) + " ");
                                        }
                                    } else if (CommandOrder == 0x14 || CommandOrder == 0x15 || CommandOrder == 0x16 | (CommandOrder == 0x04 && BluetoothName.substring(7,8).equals("1"))) //用锁的初始秘钥解密
                                    {
                                        deByte = Des.decrypt(keyByte, LOCK_INT_KEY);
                                        for (int i = 0; i < 8; i++) {
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)LOCK_INT_KEY[i])).substring(Integer.toHexString(0xff&(int)LOCK_INT_KEY[i]).length()-1) + " ");
                                        }
                                    } else if (CommandOrder == 0x17) //用随机3解密
                                    {
                                        deByte = Des.decrypt(keyByte, RandomThree);
                                        for (int i = 0; i < 8; i++) {
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)RandomThree[i])).substring(Integer.toHexString(0xff&(int)RandomThree[i]).length()-1) + " ");
                                        }
                                    } else //用根秘钥解密
                                    {
                                        deByte = Des.decrypt(keyByte, ROOT_PASSWORD_KEY.getBytes());
                                        for (int i = 0; i < 8; i++) {
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)ROOT_PASSWORD_KEY.getBytes()[i])).substring(Integer.toHexString(0xff&(int)ROOT_PASSWORD_KEY.getBytes()
                                                    [i]).length()-1) + " ");
                                        }
                                    }
                                    stringBuilder.append("\n\n");

                                    //打印解密后的数据
                                    stringBuilder.append(getTime() + "解密后的数据:");
                                    for (int i = 0; i < comlength; i++) {
                                        stringBuilder.append(("0"+Integer.toHexString(0xff&(int)deByte[i])).substring(Integer.toHexString(0xff&(int)deByte[i]).length()-1) + " ");
                                    }
                                    stringBuilder.append("\n\n");
                                }

                                stringBuilder.append( getTime() +"解析后的数据:");

                                switch (reciveByte[2]) {
                                    case 0x01:
//                                        mTts.startSpeaking("获取门锁信息成功",mSynListener);
                                        stringBuilder.append( "门锁识别号:");
                                        for (int i=0;i<16;i++)
                                        {
                                            LOCK_Code[i] = deByte[i]; //锁具识别号
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)deByte[i])).substring(Integer.toHexString(0xff&(int)deByte[i]).length()-1) + " ");
                                        }

                                        if(deByte[16] == 0x00 || deByte[16] == 0x01 || deByte[16] == 0x02) {
                                            stringBuilder.append("锁舌状态：伸出" + " ");
                                        }
                                        if(deByte[16] == 0x10 || deByte[16] == 0x11 || deByte[16] == 0x12) {
                                            stringBuilder.append("锁舌状态：收回" + " ");
                                        }
                                        if(deByte[16] == 0x20 || deByte[16] == 0x21 || deByte[16] == 0x22) {
                                            stringBuilder.append("锁舌状态：未知" + " ");
                                        }
                                        if(deByte[16] == 0x00 || deByte[16] == 0x10 || deByte[16] == 0x20) {
                                            stringBuilder.append("门磁状态：关门" + " ");
                                        }
                                        if(deByte[16] == 0x01 || deByte[16] == 0x11 || deByte[16] == 0x21) {
                                            stringBuilder.append("门磁状态：开门" + " ");
                                        }
                                        if(deByte[16] == 0x02 || deByte[16] == 0x12 || deByte[16] == 0x22) {
                                            stringBuilder.append("门磁状态：未知" + " ");
                                        }
                                        if(deByte[17] == 0x00) {
                                            stringBuilder.append("安装状态：未安装" + " ");
                                        }
                                        if(deByte[17] == 0x01) {
                                            stringBuilder.append("安装状态：已安装" + " ");
                                        }

                                        stringBuilder.append("当前时间：" + " ");
                                        for (int i=18;i<32;i++)
                                        {
                                            stringBuilder.append(Integer.toHexString(0xff&(int)deByte[i]-48));
                                        }
                                        stringBuilder.append("\n\n");

                                        break;
                                    case 0x02:
                                        if (deByte[0] == (byte) 0x01) {
//                                            mTts.startSpeaking("初始秘钥写入成功",mSynListener);
                                            stringBuilder.append("初始秘钥写入成功" + "\n\n ");
                                        } else if (deByte[0] == (byte) 0x02) {
//                                            mTts.startSpeaking("锁具已安装，不允许修改初始秘钥",mSynListener);
                                            stringBuilder.append("锁具已安装，不允许修改初始秘钥" + "\n\n ");
                                        } else {
//                                            mTts.startSpeaking("初始秘钥写入失败",mSynListener);
                                            stringBuilder.append("初始秘钥写入失败" + "\n\n ");
                                        }
                                        break;
                                    case 0x03:
                                        if (reciveByte[5] == 0x01) {
                                            mTts.startSpeaking("开门成功",mSynListener);
                                            stringBuilder.append("开门成功" + "\n\n ");
                                        } else {
                                            mTts.startSpeaking("开门失败",mSynListener);
                                            stringBuilder.append("开门失败" + "\n\n ");
                                        }
                                        break;
                                    case 0x04:
                                        int[] yde_Str = new int[5];
                                        yde_Str[4] = 0x01 ;
//                                        mTts.startSpeaking("日志上传成功",mSynListener);
                                        stringBuilder.append("日志编号:");
                                        for (int i =0;i<4;i++)
                                        {
                                            stringBuilder.append( Integer.toHexString(0xff&(int)deByte[i])+" ");
                                            yde_Str[i] = 0xff&(int)deByte[i];
                                        }

                                        stringBuilder.append( "开锁时间:");
                                        for (int i =4;i<18;i++)
                                        {
                                            stringBuilder.append( Integer.toHexString(0xff&(int)deByte[i]-48)+"");
                                        }
                                        if (deByte[18] == 1){  stringBuilder.append( "操作类型:蓝牙钥匙开门 ");}
                                        if (deByte[18] == 5){  stringBuilder.append( "操作类型:FSU开门 ");}
                                        if (deByte[18] == 7){  stringBuilder.append( "操作类型:手机开门 ");}
                                        if (deByte[19] == 1){  stringBuilder.append( "操作结果:成功 ");}
                                        else { stringBuilder.append("操作结果:"+"失败 ");}

                                        stringBuilder.append( "用户编号:");
                                        for (int i = 20;i<24;i++)
                                        {
                                            stringBuilder.append( Integer.toHexString(0xff&(int)deByte[i])+" ");
                                        }

                                        stringBuilder.append( "锁识别号:");
                                        for (int i =24;i<40;i++)
                                        {
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)deByte[i])).substring(Integer.toHexString(0xff&(int)deByte[i]).length()-1) + " ");
                                        }
                                        stringBuilder.append("\n\n");

                                        stringBuilder.append("****************************************************\n");

                                        if (BluetoothName.substring(7,8).equals("0"))
                                            stringBuilder.append(mService.sendCommand(0x04,yde_Str,5,KEY_INT_KEY.getBytes()));
                                        else
                                            stringBuilder.append(mService.sendCommand(0x04,yde_Str,5,LOCK_INT_KEY));
                                        break;
                                    case 0x05:
                                        if (deByte[0] == (byte) 0x01) {
//                                            mTts.startSpeaking("操作成功",mSynListener);
                                            stringBuilder.append( "授权钥匙成功" + "\n\n ");
                                        }else {
//                                            mTts.startSpeaking("操作失败",mSynListener);
                                            stringBuilder.append("授权钥匙失败" + "\n\n ");
                                        }
                                        break;
                                    case 0x06:
//                                        mTts.startSpeaking("当前电量为" + reciveByte[5] + "%",mSynListener);
                                        stringBuilder.append("当前电量为" + reciveByte[5] + "% 厂家:" + reciveByte[6] + " 版本:" + reciveByte[7] +" "+reciveByte[8] +" 加密方式:" + (reciveByte[9]==1?"DES":"未知")+ "\n\n");
                                        break;
                                    case 0x07:
                                        if (deByte[0] == (byte) 0x01) {
//                                            mTts.startSpeaking("钥匙绑定成功",mSynListener);
                                            stringBuilder.append("钥匙绑定成功" + "\n\n ");
                                        } else {
//                                            mTts.startSpeaking("钥匙绑定失败",mSynListener);
                                            stringBuilder.append("钥匙绑定失败" + "\n\n ");
                                        }
                                        break;
                                    case 0x08:
//                                        mTts.startSpeaking("查询锁详细信息成功",mSynListener);
                                        stringBuilder.append("锁识别号:");
                                        for (int i =5;i<21;i++)
                                        {
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)reciveByte[i])).substring(Integer.toHexString(0xff&(int)reciveByte[i]).length()-1) +" ");
                                        }
                                        stringBuilder.append("厂家编号:"+reciveByte[21]);
                                        stringBuilder.append("硬件版本号:V"+reciveByte[22]+""+ reciveByte[23]);
                                        stringBuilder.append("软件版本号:V"+reciveByte[24]+""+ reciveByte[25]);
                                        stringBuilder.append("加密方式:"+(reciveByte[26]==1?"DES":"未知"));
                                        stringBuilder.append("\n\n");
                                        break;
                                    case 0x09:
                                        stringBuilder.append("锁具校时:");
                                        if (deByte[0] == (byte) 0x01) {
//                                            mTts.startSpeaking("校时成功",mSynListener);
                                            stringBuilder.append("成功" + "\n\n ");
                                        } else {
//                                            mTts.startSpeaking("校时失败",mSynListener);
                                            stringBuilder.append("失败" + "\n\n ");
                                        }
                                        break;
                                    case 0x0a:
                                        stringBuilder.append("重置密钥:");
                                        if (deByte[0] == (byte) 0x01) {
//                                            mTts.startSpeaking("重置密钥成功",mSynListener);
                                            stringBuilder.append("成功" + "\n\n ");
                                        } else {
//                                            mTts.startSpeaking("重置密钥失败",mSynListener);
                                            stringBuilder.append("失败" + "\n\n ");
                                        }
                                        break;
                                    case 0x0b:
                                        stringBuilder.append("修改锁识别号:");
                                        if (deByte[0] == (byte) 0x01) {
//                                            mTts.startSpeaking("修改锁识别号成功",mSynListener);
                                            stringBuilder.append( "成功" + "\n\n ");
                                        } else {
//                                            mTts.startSpeaking("修改锁识别号失败",mSynListener);
                                            stringBuilder.append( "失败" + "\n\n ");
                                        }
                                        break;
                                    case 0x0d:
                                        stringBuilder.append("MAC 地址:");
                                        for (int i =5;i<11;i++){
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)reciveByte[i])).substring(Integer.toHexString(0xff&(int)reciveByte[i]).length()-1) + " ");
                                        }
                                        stringBuilder.append("\n\n ");
                                        break;
                                    case 0x0c:
                                        stringBuilder.append("钥匙校时:");
                                        if (deByte[0] == (byte) 0x01) {
//                                            mTts.startSpeaking("校时成功",mSynListener);
                                            stringBuilder.append( "成功" + "\n\n ");
                                        } else {
//                                            mTts.startSpeaking("校时失败",mSynListener);
                                            stringBuilder.append( "失败" + "\n\n ");
                                        }
                                        break;
                                    case 0x14:
                                        boolean isTrue = true;
                                        for (int i=0;i<deByte.length;i++)
                                        {
                                            if ((deByte[i]&0xff) != 0x5b)
                                            {
                                                isTrue = false;
                                                break;
                                            }
                                        }
                                        if (isTrue) {
                                            stringBuilder.append( "返回异或后的随机值1:正确" + "\n\n ");
                                        } else {
                                            stringBuilder.append( "返回异或后的随机值1:错误" + "\n\n ");
                                        }
                                        break;
                                    case 0x15://如果指令是21，则解密后的数据和0x5A异或
                                        stringBuilder.append("收到的随机值2 :");
                                        for (int i=0;i<comlength;i++) {
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)deByte[i])).substring(Integer.toHexString(0xff&(int)deByte[i]).length()-1) +" ");
                                        }
                                        stringBuilder.append("\n\n");

                                        stringBuilder.append( getTime() + "异或后随机值2 :");
                                        yde_Str = new int[comlength];
                                        for (int i=0;i<comlength;i++) {
                                            yde_Str[i] = (0xff & (int) deByte[i]) ^ 0x5a;
                                            stringBuilder.append( ("0"+Integer.toHexString(0xff&yde_Str[i])).substring(Integer.toHexString(0xff&yde_Str[i]).length()-1) +" ");
                                        }
                                        stringBuilder.append("\n\n");

                                        stringBuilder.append("****************************************************\n");

                                        stringBuilder.append(mService.sendCommand(0x15,yde_Str,8,LOCK_INT_KEY));
                                        break;
                                    case 0x16://如果指令是22，则解密后的数据以自身为秘钥加密
                                        yde_Str = new int[comlength];
                                        RandomThree = new byte[8];
                                        stringBuilder.append("收到的随机值3 :");
                                        for (int i=0;i<comlength;i++)
                                        {
                                            yde_Str[i] = 0xff & deByte[i];
                                            RandomThree[i] = (byte)(0xff & deByte[i]);
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)deByte[i])).substring(Integer.toHexString(0xff&(int)deByte[i]).length()-1) +" ");
                                        }
                                        stringBuilder.append("\n\n");
                                        stringBuilder.append("****************************************************\n");

                                        stringBuilder.append(mService.sendCommand(0x16,yde_Str,8,RandomThree));
                                        break;
                                    case 0x17:
//                                        mTts.startSpeaking("通讯秘钥协商成功",mSynListener);
                                        //解出的数据为通讯秘钥
                                        stringBuilder.append("通讯秘钥是:");
                                        ComKey = new byte[8];
                                        yde_Str = new int[comlength];
                                        for (int i = 0; i < comlength; i++) {
                                            ComKey[i] = (byte) (0xff & deByte[i]);
                                            yde_Str[i] = 0xff & deByte[i];
                                            stringBuilder.append(("0"+Integer.toHexString(0xff&(int)deByte[i])).substring(Integer.toHexString(0xff&(int)deByte[i]).length()-1) + " ");
                                        }
                                        stringBuilder.append("\n\n");

                                        stringBuilder.append("****************************************************\n");

                                        stringBuilder.append(mService.sendCommand(0x17,yde_Str,8,ComKey));
                                        break;
                                }

                                if (reciveByte[2] != 0x04 && reciveByte[2] != 0x15 && reciveByte[2] != 0x16  && reciveByte[2] != 0x17) {
                                    stringBuilder.append("****************************************************\n");
                                }
                            }
//                            savaTxt();
                            currOrder = currOrder + 1;
                        }
                        catch (Exception e) {
                            //Log.e(TAG, e.toString());
                        }
                    }
                });
            }

            //*********************//不支持VART
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART))
            {
                showMessage("Device doesn't support UART.Disconnecting");
                mState=UART_PROFILE_DISCONNECTED;
                mService.disconnect();
            }

            if(action.equals(UartService.ACTION_GATT_ONWRITE))
            {
            }
        }
    };
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction
                (UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction
                (UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
    }



    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



    //设置蓝牙连接图标
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mState == UART_PROFILE_CONNECTED)
        {
//            menu.findItem(R.id.bluetooth_status).setIcon(R.drawable.actionbar_statuson_icon);
//            mTts.startSpeaking("蓝牙连接成功",mSynListener);
        }
        else if(mState == UART_PROFILE_DISCONNECTED)
        {
//            menu.findItem(R.id.bluetooth_status).setIcon(R.drawable.actionbar_status_icon);
//            mTts.startSpeaking("蓝牙连接已断开",mSynListener);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_SELECT_DEVICE: //蓝牙扫描界面返回
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                    BluetoothAddress = deviceAddress;
                    BluetoothName = mDevice.getName();
                    mService.connect(deviceAddress);
                    BandKey();
                }
                break;
            case REQUEST_ENABLE_BT:  //蓝牙是否开启
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
                } else
                {
                    // User did not enable Bluetooth or an erroroccurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    //finish();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
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
    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }
    @Override
    public void onStart() {
        super.onStart();
        final String strRes = "android.provider.Telephony.SMS_RECEIVED";
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(strRes);
        //添加动态广播的Action
        registerReceiver(UARTStatusChangeReceiver, dynamic_filter);
        //注册自定义动态广播消息
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        if(isConnected) {
            unbindService(mServiceConnection);
            isConnected = false;
        }
        if (mState == UART_PROFILE_CONNECTED) {
            if (mService != null)mService.disconnect();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int content[] =new int[10];
//        switch (item.getItemId())
//        {
//            case R.id.bluetooth_status:
//                if (!mBtAdapter.isEnabled()) {
//                    Log.i(TAG, "onClick - BT not enabled yet");
//                    Intent enableIntent = new Intent
//                            (BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableIntent,
//                            REQUEST_ENABLE_BT);
//                }
//                else
//                {
//                    if (mState == UART_PROFILE_CONNECTED)
//                    {
//                        if (mService != null)mService.disconnect();
//                    }
//                    else
//                    {
//                        Log.i(TAG, "onClick - BT not enabled yet");
//                        Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
//                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
//                    }
//                }
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void getData(int kinds,int []content) throws UnsupportedEncodingException
    {
        for (int i =0;i<content.length;i++)
        {
            if (i==content.length-1)
                System.out.println(Integer.toHexString(content[i])+" ");
            else
                System.out.print(Integer.toHexString(content[i])+" ");
        }
        switch(kinds){

        }
    }

    @Override
    //返回键的功能
    public boolean onKeyDown(int keyCode,KeyEvent event){
        //showMessage(keyCode+"");

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() ==0)
        {
            if(exitTime==-1){
            }
            else
            {
                if((System.currentTimeMillis()-exitTime)>2000){
                    Toast.makeText(this,
                            R.string.click_exit,Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    this.finish();
                }
                exitTime=System.currentTimeMillis();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
