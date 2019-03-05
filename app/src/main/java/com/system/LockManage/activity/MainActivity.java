package com.system.LockManage.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.system.LockManage.R;
import com.system.LockManage.fragment.AlarmFragment;
import com.system.LockManage.fragment.AuthFragment;
import com.system.LockManage.fragment.DevicesFragment;
import com.system.LockManage.fragment.LockFragment;
import com.system.LockManage.fragment.LogFragment;
import com.system.LockManage.fragment.MyFragment;
import com.system.LockManage.view.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

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

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.title_wechat));
        setLeftBtnVisibility(View.GONE);
        mFragmentMap = new HashMap<>() ;
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

}
