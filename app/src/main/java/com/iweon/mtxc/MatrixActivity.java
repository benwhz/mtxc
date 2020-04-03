package com.iweon.mtxc;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.iweon.mtxc.UdpReceiveTask.MATRIX_COMMAND_QUERY_DEVICE_ID;
import static com.iweon.mtxc.UdpReceiveTask.MATRIX_COMMAND_QUERY_DEVICE_STATUS;
import static com.iweon.mtxc.UdpReceiveTask.MATRIX_COMMAND_SWITCH_ALL_CHANNEL;
import static com.iweon.mtxc.UdpReceiveTask.MATRIX_COMMAND_SWITCH_CHANNEL;

public class MatrixActivity extends AppCompatActivity implements UdpReceiveTask.IDataReceiverListener {
    private DeviceInfo mDeviceInfo;
    private int mIndex;

    private BottomNavigationView mNavView;
    private NoScrollViewPager mViewPager;
    private FragmentAdapter myAdapter;
    private List<Fragment> mFragmentList;

    private SwitchFragment linerSwitchFragment = null;
    private StatusFragment statusFragment = null;
    private SchemeFragment schemeFragment = null;
    private SwitchFragment gridSwitchFragment = null;

    private FloatingActionButton mFab;
    private LayoutType mLayoutType = LayoutType.LinerLayout;
    private boolean mCheckAllOutput = false;

    private IStatusReceiverListener mListener = null;

    static MatrixActivity matrixActivity = null;

    private byte mMatrixId = 0;

    private Queue<byte[]> mtxcCommandQueue = null;
    private Boolean mCmdIsSending = false;

    private ProgressDialog progressDialog = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_switch:
//                    transaction.add(R.id.frameLayout,switchFragment);
//                    transaction.show(switchFragment);
                    /*
                    switch (mLayoutType) {
                        case LinerLayout:
                            mViewPager.setCurrentItem(0);
                            break;
                        case GridLayout:
                            mViewPager.setCurrentItem(3);
                            break;
                    }
                    */
//                    mFab.setVisibility(View.INVISIBLE);
                    mViewPager.setCurrentItem(0);
                    invalidateOptionsMenu();
                    return true;
                case R.id.navigation_status:
//                    mFab.setVisibility(View.INVISIBLE);
                    mViewPager.setCurrentItem(1);
                    invalidateOptionsMenu();
                    return true;
                case R.id.navigation_scheme:
//                    mFab.setVisibility(View.INVISIBLE);
                    mViewPager.setCurrentItem(2);
                    invalidateOptionsMenu();
                    return true;
            }
//            transaction.commit();

            return false;
        }
    };

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matrixActivity = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_matrix);
        mNavView = findViewById(R.id.nav_view);
        mNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mDeviceInfo = (DeviceInfo) getIntent().getSerializableExtra("DeviceInfo");
        mIndex = getIntent().getIntExtra("Index", 0);
        mMatrixId = mDeviceInfo.id;

        setTitle(mDeviceInfo.name);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        initFragments();

        mFab = findViewById(R.id.fab);
        mFab.setVisibility(View.INVISIBLE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchChannel();
            }
        });

        UdpReceiveTask.setListener(this);

        Log.i("MatrixActivity","onCreate***********************************Thread Id = " + Thread.currentThread().getId() );

        mtxcCommandQueue = new LinkedList<byte[]>();

//        queryDeviceId();
        queryDeviceStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent startIntent = new Intent(this, MatrixService.class);
//        stopService(startIntent);

        UdpReceiveTask.canceled = true;
    }

    private boolean startUdpService(byte[] command)
    {
        if( command == null )   return false;
        Intent startIntent = new Intent(this, MatrixService.class);
        /*
        startIntent.putExtra("IP",mDeviceInfo.ip);
        startIntent.putExtra("PORT",mDeviceInfo.port);
        startIntent.putExtra("Timeout",mDeviceInfo.timeout);
        */
        startIntent.putExtra("DeviceInfo",mDeviceInfo);
        startIntent.putExtra("CMD",command);
        startService(startIntent);

        byte cmdId = command[4];

        if( cmdId == MATRIX_COMMAND_QUERY_DEVICE_ID )
        {
            progressDialog.setMessage(getString(R.string.progress_msg_query_id));
        }
        if( cmdId == MATRIX_COMMAND_QUERY_DEVICE_STATUS )
        {
            progressDialog.setMessage(getString(R.string.progress_msg_query_status));
        }
        if( cmdId == MATRIX_COMMAND_SWITCH_CHANNEL )
        {
            progressDialog.setMessage(getString(R.string.progress_msg_switch_channel));
        }
        if( cmdId == MATRIX_COMMAND_SWITCH_ALL_CHANNEL )
        {
            progressDialog.setMessage(getString(R.string.progress_msg_switch_all_channel));
        }

        progressDialog.show();

        return true;
    }

    private void clearCmdList( byte cmd )
    {
        for( byte[] tmpc : mtxcCommandQueue)
        {
            if( tmpc.length >= 5 )
                if( tmpc[4] == cmd )
                {
                    mtxcCommandQueue.remove(tmpc);
                }
        }
    }

    public void queryDeviceId()
    {
        //启动服务：查询设备ID
        byte[] cmd = {0x23,0x2A,0x00,0x00,0x02, (byte) 0xff};

        if( !mCmdIsSending )
        {
            if( startUdpService(cmd) )
            {
                mCmdIsSending = true;
            }
        }
        else
            mtxcCommandQueue.offer(cmd);

        /*
        Intent startIntent = new Intent(this, MatrixService.class);
        startIntent.putExtra("IP",mDeviceInfo.ip);
        startIntent.putExtra("PORT",mDeviceInfo.port);
//        cmd[1] = 0x42;
        startIntent.putExtra("CMD",cmd);
        startService(startIntent);
        */
    }

    public void queryDeviceStatus()
    {
        if( mMatrixId == 0 ) {
            queryDeviceId();
            return;
        }

        //启动服务：查询设备状态
        byte[] cmd = {0x23,mMatrixId,0x00,0x00,0x03, (byte) 0xff};

        if( !mCmdIsSending )
        {
            if( startUdpService(cmd) )
            {
                mCmdIsSending = true;
            }
        }
        else {
            clearCmdList( cmd[4] );
            mtxcCommandQueue.offer(cmd);
        }
        /*
        Intent startIntent = new Intent(this, MatrixService.class);
        startIntent.putExtra("IP",mDeviceInfo.ip);
        startIntent.putExtra("PORT",mDeviceInfo.port);
        byte[] cmd = {0x23,0x41,0x00,0x00,0x03, (byte) 0xff};
        cmd[1] = mMatrixId;
        startIntent.putExtra("CMD",cmd);
        startService(startIntent);
        */
    }

    public void switchChannel()
    {
        if( mMatrixId == 0 ) {
            queryDeviceId();
            return;
        }

        //启动服务：切换通道
        byte[] cmd = fillSwitchCommand();
        if( cmd == null )   return;

        if( !mCmdIsSending )
        {
            if( startUdpService(cmd) )
            {
                mCmdIsSending = true;
            }
        }
        else {
            clearCmdList( cmd[4] );
            mtxcCommandQueue.offer(cmd);
        }
        /*
        Intent startIntent = new Intent(getApplicationContext(), MatrixService.class);
        startIntent.putExtra("IP",mDeviceInfo.ip);
        startIntent.putExtra("PORT",mDeviceInfo.port);
        byte[] cmd = fillSwitchCommand();
        if( cmd == null )   return;
        startIntent.putExtra("CMD",cmd);
        startService(startIntent);
        */
    }

    public void applyScheme(List<Integer> channels)
    {
        if( mMatrixId == 0 ) {
            queryDeviceId();
            return;
        }

        //启动服务：调用预案
        byte[] cmd = fillSwitchCommand(channels);
        if( cmd == null )   return;

        if( !mCmdIsSending )
        {
            if( startUdpService(cmd) )
            {
                mCmdIsSending = true;
            }
        }
        else {
            clearCmdList( cmd[4] );
            mtxcCommandQueue.offer(cmd);
        }
        /*
        Intent startIntent = new Intent(getApplicationContext(), MatrixService.class);
        startIntent.putExtra("IP",mDeviceInfo.ip);
        startIntent.putExtra("PORT",mDeviceInfo.port);

        byte[] cmd = fillSwitchCommand(channels);
        if( cmd == null )   return;

        startIntent.putExtra("CMD",cmd);
        startService(startIntent);
        */
    }

    private byte[] fillSwitchCommand(List<Integer> channels) {
        int count = channels.size();
        short length = (short) (count*3);

        byte[] cmd = new byte[ length + 6 ];   // < 100
        cmd[0] = 0x23;
        cmd[1] = mMatrixId;
        cmd[2] = (byte) (length >> 8);
        cmd[3] = (byte) (length);
        cmd[4] = 0x04;

        int cct = 0;
        for( int o=0;o<channels.size();o++)
        {
                cmd[5+cct*3+0] = (byte) o;
                cmd[5+cct*3+1] = channels.get(o).byteValue();
                cmd[5+cct*3+2] = 0x46;
                cct++;
        }

        cmd[5+cct*3+0] = (byte) 0xff;

        return cmd;
    }

    private byte[] fillSwitchCommand()
    {
        List<Boolean> incs = null;
        List<Boolean> outcs = null;

        switch (mLayoutType) {
            case LinerLayout:
                incs = linerSwitchFragment.getInputCheckStatus();
                outcs = linerSwitchFragment.getOutputCheckStatus();
                break;
            case GridLayout:
                incs = gridSwitchFragment.getInputCheckStatus();
                outcs = gridSwitchFragment.getOutputCheckStatus();
                break;
        }

        if( incs == null || outcs == null ) return null;
        if( incs.size() == 0 || outcs.size() == 0 ) return null;

        boolean switchAll = true;

        boolean leastOnInChecked = false;
        byte in = 0;
        for( int i=0;i<outcs.size();i++) {
            if (incs.get(i) == true) {
                leastOnInChecked = true;
                in = (byte) i;
                break;
            }
        }
        if( !leastOnInChecked)  return null;

        short count = 0;
        for( int o=0;o<outcs.size();o++)
        {
            if( outcs.get(o) == false )
            {
                switchAll = false;
//                break;
            }
            else
                count++;
        }
        if( count == 0 )  return null;

        if( switchAll) {
            byte[] cmd = {0x23,mMatrixId,0x00,0x01,0x05, 0x00, (byte) 0xff};
            cmd[5] = in;

            return cmd;
        }
        else
        {
            short length = (short) (count*3);

            byte[] cmd = new byte[ length + 6 ];   // < 100
            cmd[0] = 0x23;
            cmd[1] = mMatrixId;
            cmd[2] = (byte) (length >> 8);
            cmd[3] = (byte) (length);
            cmd[4] = 0x04;

            int cct = 0;
            for( int o=0;o<outcs.size();o++)
            {
                if( outcs.get(o) == true ) {
                    cmd[5+cct*3+0] = (byte) o;
                    cmd[5+cct*3+1] = in;
                    cmd[5+cct*3+2] = 0x46;
                    cct++;
                }
            }

            cmd[5+cct*3+0] = (byte) 0xff;

            return cmd;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.matrix_option_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int id = mNavView.getSelectedItemId();

        if( id == R.id.navigation_switch )
        {
            for(int i = 0; i < menu.size(); i ++) {
                MenuItem item = menu.getItem(i);
                int itemId = item.getItemId();
                if( itemId == R.id.momCreatePlan  || itemId == R.id.momRefreshStatus )
                    item.setVisible(false);
                else
                    item.setVisible(true);

                if( itemId == R.id.momCheckAll )
                {
                    if(mCheckAllOutput){
                        item.setChecked(true);
                        item.setIcon(R.drawable.ic_action_checked);
                    }
                    else {
                        item.setChecked(false);
                        item.setIcon(R.drawable.ic_action_unchecked);
                    }
                }

                if( itemId == R.id.momSwitch)
                {
//                    switchChannel();
                    /*
                    if( mLayoutType == LayoutType.LinerLayout ) {
                        item.setIcon(android.R.drawable.ic_menu_sort_by_size);
                    }else
                    {
                        item.setIcon(android.R.drawable.ic_menu_today);
                    }
                    */
//@android:drawable/ic_menu_sort_by_size
//@android:drawable/ic_menu_today
                }
            }
        }

        if( id == R.id.navigation_status )
        {
            for(int i = 0; i < menu.size(); i ++) {
                MenuItem item = menu.getItem(i);
                int itemId = item.getItemId();
                if( itemId == R.id.momRefreshStatus )
                    item.setVisible(true);
                else
                    item.setVisible(false);
            }
        }

        if( id == R.id.navigation_scheme )
        {
            for(int i = 0; i < menu.size(); i ++) {
                MenuItem item = menu.getItem(i);
                int itemId = item.getItemId();
                if( itemId == R.id.momCreatePlan )
                    item.setVisible(true);
                else
                    item.setVisible(false);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.momCheckAll:
//              boolean ret = item.isChecked();
                if(item.isChecked()){
                    final MenuItem menuItem = item.setChecked(false);
                    menuItem.setIcon(R.drawable.ic_action_unchecked);
                }
                else {
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_action_checked);
                }
                linerSwitchFragment.checkAllOutput(item.isChecked());
                gridSwitchFragment.checkAllOutput(item.isChecked());
                mCheckAllOutput = item.isChecked();
                return true;
            case R.id.momCreatePlan:
                schemeFragment.createScheme();
                return true;
            case R.id.momRefreshStatus:
                queryDeviceStatus();
                return true;
            case R.id.momSwitch:
                switchChannel();
                /*
                myAdapter.swapSwitchFragment();
                mViewPager.setAdapter(myAdapter);
                queryDeviceStatus();
                if( mLayoutType == LayoutType.LinerLayout ) {
                    mLayoutType = LayoutType.GridLayout;
                    item.setIcon(android.R.drawable.ic_menu_today);
//                    mViewPager.setCurrentItem(3);
                }else
                {
                    mLayoutType = LayoutType.LinerLayout;
                    item.setIcon(android.R.drawable.ic_menu_sort_by_size);
//                    mViewPager.setCurrentItem(0);
                }
                */
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFragments()
    {
        mViewPager = (NoScrollViewPager) findViewById(R.id.view_pager);

        mFragmentList = new ArrayList<>();

        linerSwitchFragment = new SwitchFragment();
        ((SwitchFragment) linerSwitchFragment).setDeviceInfo(mDeviceInfo);
        ((SwitchFragment) linerSwitchFragment).setLayoutType(LayoutType.LinerLayout);

        gridSwitchFragment = new SwitchFragment();
        ((SwitchFragment) gridSwitchFragment).setDeviceInfo(mDeviceInfo);
        ((SwitchFragment) gridSwitchFragment).setLayoutType(LayoutType.GridLayout);

        statusFragment = new StatusFragment();
        statusFragment.setDeviceInfo(mDeviceInfo);

        schemeFragment = new SchemeFragment();
        ((SchemeFragment) schemeFragment).setDeviceInfo(mDeviceInfo);

        mFragmentList.add(linerSwitchFragment);
        mFragmentList.add(statusFragment);
        mFragmentList.add(schemeFragment);
        mFragmentList.add(gridSwitchFragment);

        myAdapter = new FragmentAdapter(getSupportFragmentManager(), this, mFragmentList);
        mViewPager.setAdapter(myAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //注意这个方法滑动时会调用多次，下面是参数解释：
                //position当前所处页面索引,滑动调用的最后一次绝对是滑动停止所在页面
                //positionOffset:表示从位置的页面偏移的[0,1]的值。
                //positionOffsetPixels:以像素为单位的值，表示与位置的偏移
            }

            @Override
            public void onPageSelected(int position) {
                //该方法只在滑动停止时调用，position滑动停止所在页面位置
//                当滑动到某一位置，导航栏对应位置被按下
//               mNavView.getMenu().getItem(position).setChecked(true);
                //这里使用navigation.setSelectedItemId(position);无效，
                //setSelectedItemId(position)的官网原句：Set the selected
                // menu item ID. This behaves the same as tapping on an item
                //未找到原因
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//这个方法在滑动是调用三次，分别对应下面三种状态
// 这个方法对于发现用户何时开始拖动，
// 何时寻呼机自动调整到当前页面，或何时完全停止/空闲非常有用。
//                state表示新的滑动状态，有三个值：
//                SCROLL_STATE_IDLE：开始滑动（空闲状态->滑动），实际值为0
//                SCROLL_STATE_DRAGGING：正在被拖动，实际值为1
//                SCROLL_STATE_SETTLING：拖动结束,实际值为2
            }
        });
    }

    @Override
    public void dataReceived(byte id, byte[] data) {

//        String string = String.valueOf(data);
        progressDialog.dismiss();

        mCmdIsSending = false;
        if( data == null )
        {
            mtxcCommandQueue.clear();
        }
        else {
            byte[] cmd = mtxcCommandQueue.poll();
            if (cmd != null) {
                if (startUdpService(cmd)) {
                    mCmdIsSending = true;
                }
            }
        }

        if(data == null) {
            if( id == MATRIX_COMMAND_QUERY_DEVICE_ID )
            {

            }
            if( id == MATRIX_COMMAND_QUERY_DEVICE_STATUS )
            {

            }
            if( id == MATRIX_COMMAND_SWITCH_CHANNEL )
            {

            }
            if( id == MATRIX_COMMAND_SWITCH_ALL_CHANNEL )
            {

            }

            statusFragment.updateStatus(null);
            linerSwitchFragment.updateStatus(null);
            schemeFragment.updateStatus(null);
            if( mListener != null )
                mListener.updateStatus(null);

//            setTitle("ERR");
            String title = mDeviceInfo.name;
            title = title + getString(R.string.msg_title_offline);
            setTitle(title);

            Toast.makeText(this, getString(R.string.msg_communication_err), Toast.LENGTH_SHORT).show();
        }
        else {
            if( id == MATRIX_COMMAND_QUERY_DEVICE_ID )
            {
                if( data.length > 2 ) {
                    if( data[1] == (byte)0x3A ) {
                        mMatrixId = data[0];
                    }
                }

                if( mMatrixId != 0 ) {
                    queryDeviceStatus();
                    mDeviceInfo.id = mMatrixId;
                    if( MainActivity.mainActivity != null )
                        MainActivity.mainActivity.updateDeviceInfo(mIndex,mDeviceInfo);
                }

                String s = new String(data);
                setTitle(s);
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }

            if( id == MATRIX_COMMAND_QUERY_DEVICE_STATUS )
            {
                List<Integer> status = parseStatusData(data);
                statusFragment.updateStatus(status);
                linerSwitchFragment.updateStatus(status);
                schemeFragment.updateStatus(status);
                if( mListener != null )
                    mListener.updateStatus(status);

                if( mCheckAllOutput == true ) {
                    this.invalidateOptionsMenu();
                    mCheckAllOutput = false;
                }
            }
            if( id == MATRIX_COMMAND_SWITCH_CHANNEL )
            {
//                String s = new String(data);
                if( data.length >= 2 && data[0] == (byte)0x4F && data[1] == (byte)0x4B ) {
                    queryDeviceStatus();
                }
                else {
//                    Toast.makeText(this, getString(R.string.msg_communication_ok), Toast.LENGTH_SHORT).show();
                    try {
                        String s_gbk = new String(data,"GBK");
                        Toast.makeText(this, s_gbk, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if( id == MATRIX_COMMAND_SWITCH_ALL_CHANNEL )
            {
//                String s = new String(data);
                if( data.length >= 2 && data[0] == (byte)0x4F && data[1] == (byte)0x4B ) {
                    queryDeviceStatus();
                }
                else {
//                    Toast.makeText(this, getString(R.string.msg_communication_ok), Toast.LENGTH_SHORT).show();
                    try {
                        String s_gbk = new String(data,"GBK");
                        Toast.makeText(this, s_gbk, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Device ID Error
            if( data.length > 8 )
            {
                if( data[0] == (byte)0xC3 && data[1] == (byte)0xFC &&
                    data[2] == (byte)0xC1 && data[3] == (byte)0xEE &&
                    data[4] == (byte)0xC9 && data[5] == (byte)0xE8 &&
                    data[6] == (byte)0xB1 && data[7] == (byte)0xB8 ) {
                    mMatrixId = 0;  // device id changed maybe.

                    queryDeviceId();
                }
            }

            String title = mDeviceInfo.name;
            title = title + getString(R.string.msg_title_online);
            setTitle(title);
        }
    }

    private List<Integer> parseStatusData(byte[] data)
    {
        List<Integer> statusList = null;

        byte[] realData = new byte[data.length];

        int index = 0;
        for( int i = 0; i < data.length; i ++ ) {
            if (data[i] != 0x4B && data[i] != 0x4F) {
                realData[index] = data[i];
                index++;
            }
        }

        if( mDeviceInfo.outCount >= 100 ) {
            if( realData.length < mDeviceInfo.outCount*4 + 6 )  return null;
            int count = realData[2];
            count = (realData[2] << 8 | 0x00FF );
            count =  count & ( realData[3] | 0xFF00 );
            count = count/4;
            if( count != mDeviceInfo.outCount ) return null;

            statusList = new ArrayList<Integer>();

            byte[] ipId = new byte[3];
            for( int i = 0; i < count; i ++)
            {
                ipId[0] = realData[5 + i*4 + 0];
                ipId[1] = realData[5 + i*4 + 1];
                ipId[2] = realData[5 + i*4 + 2];

                String s = new String(ipId);

                statusList.add(Integer.parseInt(s));
            }
        }
        else{
            if( realData.length < mDeviceInfo.outCount*3 + 6 )  return null;
            int count = realData[2];
            count = (realData[2] << 8 | 0x00FF );
            count =  count & ( realData[3] | 0xFF00 );
            count = count/3;
            if( count != mDeviceInfo.outCount ) return null;

            statusList = new ArrayList<Integer>();

            byte[] ipId = new byte[2];
            for( int i = 0; i < count; i ++)
            {
                ipId[0] = realData[5 + i*3 + 0];
                ipId[1] = realData[5 + i*3 + 1];

                String s = new String(ipId);

                statusList.add(Integer.parseInt(s));
            }
        }

        return statusList;
    }

    void setListener( IStatusReceiverListener listener)
    {
        mListener = listener;
    }

    public interface IStatusReceiverListener
    {
        void updateStatus( List<Integer> list );
    }
}
