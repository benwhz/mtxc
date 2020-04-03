package com.iweon.mtxc;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SchemeActivity extends AppCompatActivity implements MatrixActivity.IStatusReceiverListener {
    private SchemeInfo mSchemeInfo;
    private List<Integer> mStatus = null;

    private RecyclerView recyclerView;
    private SchemeDetailAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int index = -1;
    private SwipeRefreshLayout mSwiperefreshlayout;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scheme);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mSchemeInfo = (SchemeInfo) getIntent().getSerializableExtra("SchemeInfo");
        index = getIntent().getIntExtra("Index", -1);
        mStatus = getIntent().getIntegerArrayListExtra("Status");

        setTitle(mSchemeInfo.name);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        initSchemeInfo();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        if( MatrixActivity.matrixActivity != null ) {
            MatrixActivity.matrixActivity.setListener(this);
//            MatrixActivity.matrixActivity.queryDeviceStatus();
        }
    }

    @Override
    protected void onDestroy() {
        if( MatrixActivity.matrixActivity != null )
            MatrixActivity.matrixActivity.setListener(null);

        super.onDestroy();
    }

    private void initSchemeInfo()
    {
        recyclerView = (RecyclerView) findViewById(R.id.schemeDetailRecyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//      recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));

//        recyclerViewTouchListener = new RecyclerViewTouchListener(this,recyclerView,this);
        mSwiperefreshlayout = (SwipeRefreshLayout) this.findViewById(R.id.scheme_swipe_refresh_layout);
        //设置刷新时动画的颜色，可以设置4个
        mSwiperefreshlayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwiperefreshlayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mSwiperefreshlayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

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

        // specify an adapter (see also next example)
        mAdapter = new SchemeDetailAdapter(mSchemeInfo, mStatus,this);
        recyclerView.setAdapter(mAdapter);

        mSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if( MatrixActivity.matrixActivity != null )
                    MatrixActivity.matrixActivity.queryDeviceStatus();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scheme_option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            super.onBackPressed();
//            finish();
            return true;
        }

        if(item.getItemId() == R.id.loadScheme ) {
            applyScheme(index);
            return true;
        }

        if(item.getItemId() == R.id.saveScheme ) {
            resaveScheme(index);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyScheme(int position)
    {
        final int pos = position;

        if( mStatus == null ) {
            Toast.makeText(this, getString(R.string.msg_offline_device), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this).setMessage(R.string.msg_apply_scheme_confirm)
                .setTitle(R.string.apply_confirm_title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        progressDialog.setMessage(getString(R.string.progress_msg_load_scheme));
                        progressDialog.show();

                        if( MatrixActivity.matrixActivity != null)
                            MatrixActivity.matrixActivity.applyScheme(mSchemeInfo.channels);

                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void resaveScheme(int position){
        final int index = position;
        final EditText et=new EditText(this);

        if( mStatus == null ) {
            Toast.makeText(this, getString(R.string.msg_offline_device), Toast.LENGTH_SHORT).show();
            return;
        }

        et.setText(mSchemeInfo.name);
        et.selectAll();
        new AlertDialog.Builder(this).setTitle(R.string.msg_resave_scheme).setView(et).
                setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String _text = et.getText().toString();
                        if (!_text.isEmpty()) {
                            SchemeInfo schemeInfo = new SchemeInfo();
                            schemeInfo.name = _text;
                            schemeInfo.channels = new ArrayList<Integer>();
                            mSchemeInfo =schemeInfo;

                            for(int i = 0; i< mStatus.size(); i ++ )
                                schemeInfo.channels.add(mStatus.get(i)-1);

//                            schemeInfo.channels = schemeInfoList.get(index).channels;
                            if( SchemeFragment.schemeFragment != null ) {
                                SchemeFragment.schemeFragment.modifyScheme(index, schemeInfo);
                                mAdapter.setSchemeInfo(schemeInfo);
                                setTitle(mSchemeInfo.name);
                            }
                        }
                    }
                }).setNegativeButton(R.string.dialog_button_cancel, null).show();

    }

    @Override
    public void updateStatus(List<Integer> list) {
        mSwiperefreshlayout.setRefreshing(false);

        progressDialog.dismiss();
//      if( list != null )
        mStatus = list;
        mAdapter.updateStatus(list);
    }
}
