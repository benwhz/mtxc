package com.iweon.mtxc;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DeviceAdapter.OnItemActionListener, DeviceDialogFragment.NoticeDialogListener{

    private RecyclerView recyclerView;
    private DeviceAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<DeviceInfo> deviceInfoList;
//  private RecyclerViewTouchListener recyclerViewTouchListener;
    static MainActivity mainActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initDeviceInfo();

        mainActivity = this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_adddevice) {
            DeviceDialogFragment ddialog = new DeviceDialogFragment();
//            ddialog.setCancelable(false);
            Bundle args = new Bundle();
            args.putInt("edit", 0);
            ddialog.setArguments(args);
            ddialog.show(getSupportFragmentManager(),"add device");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Uri uri = Uri.parse("http://www.iweon.com/mtxc");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Uri uri = Uri.parse("http://www.dynavt.com/mtxc/mtxc.apk");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateDeviceInfo(int index, DeviceInfo info)
    {
        deviceInfoList.remove(index);
        deviceInfoList.add(index, info);

        MCSetting.setDevicesInfo(this, deviceInfoList);
    }

    private void initDeviceInfo()
    {
        recyclerView = (RecyclerView) findViewById(R.id.deviceRecyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//      recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));

//        recyclerViewTouchListener = new RecyclerViewTouchListener(this,recyclerView,this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
//        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        /*
        deviceInfoList = new ArrayList<DeviceInfo>();

        for( int i = 0; i < 5; i++ ) {
            DeviceInfo deviceInfo = new DeviceInfo();

            deviceInfo.name = "My Matrix";
            deviceInfo.id = 'a';
            deviceInfo.inCount = 16;
            deviceInfo.outCount = 16;
            deviceInfo.showInCount = 16;
            deviceInfo.showOutCount = 16;
            deviceInfo.switchType = SwitchType.OneToOne;
            deviceInfo.ip = "192.168.3.17";
            deviceInfo.port = 1234;

            deviceInfoList.add(deviceInfo);
        }
        */
        deviceInfoList = MCSetting.getDevicesInfo(this);
        // specify an adapter (see also next example)
        mAdapter = new DeviceAdapter(deviceInfoList,this, this);
        recyclerView.setAdapter(mAdapter);

    }

    private void editDevice(int index)
    {
        DeviceDialogFragment ddialog = new DeviceDialogFragment();
//        ddialog.setCancelable(false);
//        ddialog.setDeviceInfo(index, deviceInfoList.get(index));
        Bundle args = new Bundle();
        args.putInt("edit", 1);
        args.putInt("index", index);
        args.putSerializable("deviceInfo", deviceInfoList.get(index));
        ddialog.setArguments(args);
        ddialog.show(getSupportFragmentManager(),"edit device");
    }

    private void removeDevice(int index)
    {

    }

    private void openDevice(int position) {
        DeviceInfo deviceInfo = deviceInfoList.get(position);

        Intent intent = new Intent();
        intent.setAction("ShowMatrixActivity");
        intent.addCategory("matrix");
        intent.putExtra("DeviceInfo",deviceInfo);
        intent.putExtra("Index", position);
        startActivity(intent);
    }

    private void deleteDevice(int position) {
        final int index = position;

        new AlertDialog.Builder(this).setMessage(R.string.msg_delete_device_confirm)
                .setTitle(R.string.del_confirm_title)
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeviceInfo removedDeviceInfo = deviceInfoList.get(index);
                        deviceInfoList.remove(index);
                        mAdapter.setDeviceInfoList(deviceInfoList);
                        mAdapter.notifyDataSetChanged();
                        saveDevicesInfo(deviceInfoList);
                        removeDeviceInfo(removedDeviceInfo);
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    void saveDevicesInfo(List<DeviceInfo> infoList)
    {
        MCSetting.setDevicesInfo(this, deviceInfoList);
    }

    void removeDeviceInfo(DeviceInfo info)
    {
        if( deviceInfoList.size() == 0 )
            MCSetting.removeDeviceInfo(this, info, true);
        else
            MCSetting.removeDeviceInfo(this, info, false);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int edit, int index, DeviceInfo deviceInfo) {

        if(edit == 0) {
            deviceInfoList.add(deviceInfo);

            mAdapter.setDeviceInfoList(deviceInfoList);
            mAdapter.notifyDataSetChanged();

            saveDevicesInfo(deviceInfoList);

            // add In & Out Titles
            ArrayList<String> inTitles = new ArrayList<String>();
            ArrayList<String> outTitles = new ArrayList<String>();
            for (int i = 0; i < deviceInfo.inCount; i++)
                inTitles.add(this.getString(R.string.in_title) + (i + 1));
            MCSetting.setInTitles(this, deviceInfo.uuid, inTitles);
            for (int i = 0; i < deviceInfo.outCount; i++)
                outTitles.add(this.getString(R.string.out_title) + (i + 1));
            MCSetting.setOutTitles(this, deviceInfo.uuid, outTitles);
        }
        else
        {
            deviceInfoList.remove(index);

            deviceInfoList.add(index, deviceInfo);

            mAdapter.setDeviceInfoList(deviceInfoList);
            mAdapter.notifyDataSetChanged();

            saveDevicesInfo(deviceInfoList);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onActionTriggered(DeviceAction action, int position) {

        switch (action) {
            case AddDevice:
                break;
            case DeleteDevice:
                deleteDevice(position);
                break;
            case EditDevice:
                editDevice(position);
//                Snackbar.make(this.recyclerView, R.string.edit_tips, Snackbar.LENGTH_LONG).setAction("Action", null);
//                Snackbar.make(this.recyclerView, R.string.edit_tips, Snackbar.LENGTH_LONG).show();
                break;
            case OpenDevice:
                openDevice(position);
                break;
        }
    }
}
