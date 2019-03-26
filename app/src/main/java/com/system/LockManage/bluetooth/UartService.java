package com.system.LockManage.bluetooth;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.bluetooth.BluetoothGattService;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.system.LockManage.activity.MainActivity;
import com.system.LockManage.des.Des;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class UartService extends Service {
    private final static String TAG = UartService.class.getSimpleName();
    private UartService uartService;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public  BluetoothGatt mBluetoothGatt=null;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.nordicsemi.nrfUART.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.nordicsemi.nrfUART.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.nordicsemi.nrfUART.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.nordicsemi.nrfUART.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.nordicsemi.nrfUART.EXTRA_DATA";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART = "com.nordicsemi.nrfUART.DEVICE_DOES_NOT_SUPPORT_UART";
    public final static String ACTION_GATT_ONWRITE= "GATT_WRITE_SUCCESS";

    public static UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public  UUID UUID_SERVICE = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
    public  UUID UUID_READ = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
    public  UUID UUID_WRITE = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
    public  UUID UUID_NOTIFY = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");

    /*public static final UUID UUID_SERVICE = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9E");
    public static final UUID UUID_WRITE = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9E");
    public static final UUID UUID_NOTIFY = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9E");*/


    public UartService(BluetoothAdapter bluetoothAdapter)
    {
        mBluetoothAdapter = bluetoothAdapter;
        uartService = this ;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "mBluetoothGatt = " + mBluetoothGatt );
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            //if(status==BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_GATT_ONWRITE, characteristic);
            }
            super.onCharacteristicWrite(gatt, characteristic, status);

        }
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            broadcastUpdate(ACTION_GATT_ONWRITE, null);
            super.onReliableWriteCompleted(gatt, status);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (UUID_NOTIFY.equals(characteristic.getUuid())) {
            intent.putExtra(EXTRA_DATA, characteristic.getValue());
        }
        else
        {
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public UartService getService() {
            return UartService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
       /* if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }*/

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        // mBluetoothGatt.close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        Log.w(TAG, "mBluetoothGatt closed");
        mBluetoothDeviceAddress = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    /*
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);


        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }*/

    /**
     * Enable TXNotification
     *
     * @return
     */
    public String enableTXNotification()
    {

        if (mBluetoothGatt == null) {
            showMessage("mBluetoothGatt null" + mBluetoothGatt);
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return "mBluetoothGatt is null";
        }

        BluetoothGattService RxService = mBluetoothGatt.getService(UUID_SERVICE);
        if (RxService == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return "Rx service not found!";
        }

        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(UUID_NOTIFY);
        if (TxChar == null) {
            showMessage("Tx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return "Tx charateristic not found!";
        }

        //mBluetoothGatt.setCharacteristicNotification(TxChar,true);

        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
        mBluetoothGatt.setCharacteristicNotification(TxChar, true);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);

        return "Ture";
    }

    public boolean writeRXCharacteristic(byte[] value)
    {

        byte[] temp_value =new byte[20];
        for(int i=0;i<20;i++)
            temp_value[i]=value[i];
        BluetoothGattService RxService = mBluetoothGatt.getService(UUID_SERVICE);
        // showMessage("mBluetoothGatt null"+ mBluetoothGatt);
        if (RxService == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);

            return false;
        }
        final BluetoothGattCharacteristic RxWRITE = RxService.getCharacteristic(UUID_WRITE);
        if (RxWRITE == null) {
            showMessage("RxWRITE charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }

        final BluetoothGattCharacteristic NxWRITE = RxService.getCharacteristic(UUID_NOTIFY);
        if (NxWRITE == null) {
            showMessage("RxWRITE charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }

        mBluetoothGatt.setCharacteristicNotification(RxWRITE, true);
        RxWRITE.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(RxWRITE);
        mBluetoothGatt.setCharacteristicNotification(NxWRITE, true);
        return status;

    }

    private void showMessage(String msg) {
        Log.e(TAG, msg);
    }
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
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

    //密文发送 ,分包发送 ，每包20byte
    public String sendMessage(final int[] srcData) {
        String  CmdInfo ="";
        new Thread(new Runnable() {
            @Override
            public void run() {
                //分包发送数据
                int packCount = (srcData.length - 1) / 20 + 1; //包数
                for (int i = 0; i < packCount;i++) {
                    byte[] sendSecretBuffer = new byte[20];
                    for (int n = 0; n < 20; n++) {
                        sendSecretBuffer[n] = (byte) srcData[20 * i + n];
                    }
                    boolean isTrue = writeRXCharacteristic(sendSecretBuffer);//发送数据，返回指示 是否发送成功
                    if (isTrue) {
                    } else {
                    }
                    try {
                        Thread.sleep(100);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return CmdInfo;
    }

    //添加头尾校验
    public String sendCommand(int type,int[] srcData,int length,byte[] key)
    {
        int le = (((((length-1)/8+1)*8+7)-1)/20+1)*20;
        int[] packData = new int[le];
        byte[] SrcData = new byte[((length-1)/8+1)*8];

        //***加密前**********
        //同步字
        packData[0]=0xfa;
        packData[1]=0xfb;
        //指令编号
        packData[2]=type;
        //长度(数据内容长度)
        packData[3]=0x00;
        packData[4]=length+2;
        int a = type + length + 2;
        //数据内容
        for(int i=0;i<length;i++)
        {
            packData[5+i] = SrcData[i] = (byte)srcData[i];
            a = a + srcData[i];
        }
        //校验
        a= ~a&0xffff;
        packData[length+5] = a/256;
        packData[length+6] = a%256;

        //打印明文命令
        String CmdInfo = getTime()+ "加密前命令是:";
        for (int n =0;n<length+7;n++)
        {
            CmdInfo =CmdInfo +("0"+Integer.toHexString(0xff&packData[n])).substring(Integer.toHexString(0xff&packData[n]).length()-1) + " ";
        }
        CmdInfo = CmdInfo+"\n\n";

        //***********加密后
        if (type!=0x06 && type!=0x08  && type!=0x19 && type!=0x0d)
        {
            //内容加密
            try {

                CmdInfo = CmdInfo + getTime()+ "加密的秘钥是:";
                for (int n =0;n<key.length;n++)
                {
                    CmdInfo =CmdInfo +("0"+Integer.toHexString(0xff&(int)key[n])).substring(Integer.toHexString(0xff&(int)key[n]).length()-1) + " ";
                }
                CmdInfo = CmdInfo+"\n\n";

                SrcData = Des.encrypt(SrcData, key);
                packData[4] = SrcData.length + 2;
                a = type + packData[4];
                for(int i=0;i<SrcData.length;i++)
                {
                    packData[5+i] = SrcData[i];
                    a = a + (SrcData[i]&0xff);
                }
                //校验
                a= ~a&0xffff;
                packData[SrcData.length+5] = a/256;
                packData[SrcData.length+6] = a%256;

                //打印加密命令
                CmdInfo = CmdInfo+ getTime()+ "加密后命令是:";
                for (int n =0;n<SrcData.length+7;n++)
                {
                    CmdInfo =CmdInfo +("0"+Integer.toHexString(0xff&packData[n])).substring(Integer.toHexString(0xff&packData[n]).length()-1) + " ";
                }
                CmdInfo = CmdInfo+"\n\n";
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (type==0x05) {
            CmdInfo = CmdInfo + (getTime() + "发送命令解析:用户编号:1 1 1 1 操作类别:"+(srcData[68]==0x01?"添加":"清除")+" 设备编号");
            for (int i = 69; i < 85; i++) {
                CmdInfo = CmdInfo + (("0" + Integer.toHexString(0xff & srcData[i])).substring(Integer.toHexString(0xff & srcData[i]).length() - 1) + " ");
            }
            CmdInfo = CmdInfo + ("蓝牙地址:" + MainActivity.BluetoothAddress.toLowerCase() + " 存储位置:00 有效时间为:10分钟 有效次数为:10次\n\n");
        }
        else if(type==0x02)
        {
            CmdInfo = CmdInfo + (getTime() + "发送命令解析:初始秘钥是: 54 54 4d 4a 5f 32 33 34\n\n");
        }
        else if(type==0x03)
        {
            CmdInfo = CmdInfo + (getTime() + "发送命令解析:用户编号：1111，锁识别号：");
            for (int i = 0; i < 16; i++) {
                CmdInfo = CmdInfo + (("0" + Integer.toHexString(0xff & MainActivity.LOCK_Code[i])).substring(Integer.toHexString(0xff & MainActivity.LOCK_Code[i]).length() - 1) + " ");
            }
            CmdInfo = CmdInfo + ("，授权开始时间：20160901101010，授权结束时间：20170901101010，授权时间：20160901101010，操作类别："+(srcData[68]==0x01?"开门":"关门")+"\n\n");
        }
        else if(type==0x0a)
        {
            CmdInfo = CmdInfo + (getTime() + "发送命令解析:重置的秘钥是: 54 54 4d 4a 5f 32 33 34\n\n");
        }
        else if(type==0x07)
        {
            CmdInfo = CmdInfo + (getTime() + "发送命令解析:用户编号：1 1 1 1，蓝牙钥匙初始秘钥:54 54 4d 4a 5f 33 34 35\n\n");
        }
        else if(type==0x09 || type==0x0c)
        {
            CmdInfo = CmdInfo + (getTime() + "发送命令解析:校对的时间是:");
            for (int i = 0; i < 14; i++) {
                CmdInfo = CmdInfo + Integer.toHexString(srcData[i] -48);
                if (i==3) CmdInfo = CmdInfo +"年";
                if (i==5) CmdInfo = CmdInfo +"月";
                if (i==7) CmdInfo = CmdInfo +"日";
                if (i==9) CmdInfo = CmdInfo +"时";
                if (i==11) CmdInfo = CmdInfo +"分";
                if (i==13) CmdInfo = CmdInfo +"秒";
            }
            CmdInfo = CmdInfo+"\n\n";
        }
        else if(type==0x0b)
        {
            CmdInfo = CmdInfo + (getTime() + "发送命令解析:修改后的锁识别号: 00 25 00 ff 00 01 00 01 00 02 ff ff ff ff ff ff\n\n");
        }
        CmdInfo = CmdInfo + "****************************************************\n";
        Log.e(TAG,CmdInfo);
        sendMessage(packData);
        return CmdInfo;
    }

    //添加头尾校验
    public String sendCommand1(int type,int[] srcData,int length,byte[] key)
    {
        int le = (((((length-1)/8+1)*8+7)-1)/20+1)*20;
        int[] packData = new int[le];
        byte[] SrcData = new byte[((length-1)/8+1)*8];

        //***加密前**********
        //同步字
        packData[0]=0xef;
        packData[1]=0x01;
        //指令编号
        packData[2]=0;
        packData[3]=type;
        //长度(数据内容长度)
        packData[4]=0x00;
        packData[5]=length+2;
        int a = type + length + 2;
        //数据内容
        for(int i=0;i<length;i++)
        {
            packData[6+i] = SrcData[i] = (byte)srcData[i];
            a = a + srcData[i];
        }
        //校验
        a= ~a&0xffff;
        packData[length+6] = a/256;
        packData[length+7] = a%256;

        //打印明文命令
        String CmdInfo = getTime()+ "加密前命令是:";
        for (int n =0;n<length+8;n++)
        {
            CmdInfo =CmdInfo +("0"+Integer.toHexString(0xff&packData[n])).substring(Integer.toHexString(0xff&packData[n]).length()-1) + " ";
        }
        CmdInfo = CmdInfo+"\n\n";
        Log.e(TAG,CmdInfo);

        return CmdInfo;
    }
}

