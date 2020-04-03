package com.iweon.mtxc;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment {
    private RecyclerView recyclerView;
    private StatusAdapter mAdapter = null;
    private RecyclerView.LayoutManager layoutManager;
    private DeviceInfo mDeviceInfo;
    private SwipeRefreshLayout mSwiperefreshlayout;

    public void setDeviceInfo(DeviceInfo deviceInfo)
    {
        this.mDeviceInfo = deviceInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.status_fragment, container, false);
        initStatusInfo(view);
        return view;
    }

    private void initStatusInfo(View view)
    {
        recyclerView = (RecyclerView) view.findViewById(R.id.statusRecyclerView);
        mSwiperefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        //设置刷新时动画的颜色，可以设置4个
        mSwiperefreshlayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwiperefreshlayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mSwiperefreshlayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));


        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
//        layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new StatusAdapter(getContext(), mDeviceInfo);

        recyclerView.setAdapter(mAdapter);

        mSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MatrixActivity)getActivity()).queryDeviceStatus();
            }
        });

    }

    public void updateStatus( List<Integer> list )
    {
        mSwiperefreshlayout.setRefreshing(false);
        if( mAdapter != null ) {
//            mAdapter.setDeviceInfo(mDeviceInfo);
            mAdapter.updateStatus(list);
        }
    }

}
