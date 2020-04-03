package com.iweon.mtxc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SchemeFragment extends Fragment implements SchemeAdapter.OnItemActionListener{
    private RecyclerView recyclerView;
    private SchemeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<SchemeInfo> schemeInfoList;

    private View mRootView = null;
    private DeviceInfo mDeviceInfo;
    private List<Integer> mStatus = null;

    static SchemeFragment schemeFragment = null;

    public void setDeviceInfo(DeviceInfo deviceInfo)
    {
        mDeviceInfo = deviceInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if( mRootView != null )
        {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        }
        else {
            mRootView = inflater.inflate(R.layout.scheme_fragment, container, false);
            initSchemeInfo(mRootView);
        }

        schemeFragment = this;

        return mRootView;
    }

    private void initSchemeInfo(View view)
    {
        recyclerView = (RecyclerView) view.findViewById(R.id.schemeRecyclerView);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
//        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        /*
        schemeInfoList = new ArrayList<SchemeInfo>();

        for( int i = 0; i < 5; i++ ) {
            SchemeInfo schemeInfo = new SchemeInfo();

            schemeInfo.name = "My Scheme";

            schemeInfoList.add(schemeInfo);
        }
        */
        schemeInfoList = MCSetting.getSchemes(getContext(), mDeviceInfo.uuid);

        // specify an adapter (see also next example)
        mAdapter = new SchemeAdapter(schemeInfoList,this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActionTriggered(SchemeAction action, int position) {

        switch (action) {
            case AddScheme:
                break;
            case DeleteScheme:
                deleteScheme(position);
                break;
            case EditScheme:
                editScheme(position);
                break;
            case ShowScheme:
//                applyScheme(position);
                showScheme(position);
                break;
        }
    }

    private void showScheme(int position) {
        SchemeInfo schemeInfo = schemeInfoList.get(position);

        Intent intent = new Intent();
        intent.setAction("ShowSchemeActivity");
        intent.addCategory("scheme");
        intent.putExtra("SchemeInfo", schemeInfo);
        intent.putExtra("Index", position);
        intent.putIntegerArrayListExtra("Status", (ArrayList<Integer>) mStatus);
        startActivity(intent);
    }

    private void deleteScheme(int position)
    {
        final int pos = position;

        new AlertDialog.Builder(getContext()).setMessage(R.string.msg_delete_scheme_confirm)
                .setTitle(R.string.del_confirm_title)
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        schemeInfoList.remove(pos);
                        mAdapter.setSchemeInfo(schemeInfoList);
                        mAdapter.notifyDataSetChanged();
                        MCSetting.setSchemes(getContext(),mDeviceInfo.uuid,schemeInfoList);
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();

    }

    public void editScheme(int position){
        final int index = position;
        final EditText et=new EditText(getContext());

        if( mStatus == null ) {
            Toast.makeText(getContext(), getString(R.string.msg_offline_device), Toast.LENGTH_SHORT).show();
            return;
        }

        et.setText(schemeInfoList.get(index).name);
        et.selectAll();
        new AlertDialog.Builder(getContext()).setTitle(R.string.msg_resave_scheme).setView(et).
                setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String _text = et.getText().toString();
                        if (!_text.isEmpty()) {
                            SchemeInfo schemeInfo = new SchemeInfo();
                            schemeInfo.name = _text;
                            schemeInfo.channels = new ArrayList<Integer>();

                            for(int i = 0; i< mStatus.size(); i ++ )
                                schemeInfo.channels.add(mStatus.get(i)-1);

//                            schemeInfo.channels = schemeInfoList.get(index).channels;

                            modifyScheme(index, schemeInfo);
                            /*
                            schemeInfoList.remove(index);
                            schemeInfoList.add(index, schemeInfo);
                            mAdapter.setSchemeInfo(schemeInfoList);
                            mAdapter.notifyDataSetChanged();
                            MCSetting.setSchemes(getContext(), mDeviceInfo.uuid, schemeInfoList);
                            */
                        }
                    }
                }).setNegativeButton(R.string.dialog_button_cancel, null).show();

    }

    public void createScheme(){
        final EditText et=new EditText(getContext());
        if( mStatus == null ) {
            Toast.makeText(getContext(), getString(R.string.msg_offline_device), Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(getContext()).setTitle(R.string.msg_input_scheme_name).setView(et).
                setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1){
                        String _text = et.getText().toString();
                        if( !_text.isEmpty())
                        {
                            SchemeInfo schemeInfo = new SchemeInfo();
                            schemeInfo.name = _text;
                            schemeInfo.channels = new ArrayList<Integer>();

                            if( mStatus == null ) {
                                for (int i = 0; i < mDeviceInfo.outCount; i++)
                                    schemeInfo.channels.add(0);
                            }
                            else {
                                for (int i = 0; i < mStatus.size(); i++)
                                    schemeInfo.channels.add(mStatus.get(i) - 1);
                            }
                            schemeInfoList.add(schemeInfo);
                            MCSetting.setSchemes(getContext(),mDeviceInfo.uuid,schemeInfoList);
                        }
                    }
                }).setNegativeButton(R.string.dialog_button_cancel,null).show();

    }

    public void modifyScheme( int index, SchemeInfo info )
    {
        schemeInfoList.remove(index);
        schemeInfoList.add(index, info);
        mAdapter.setSchemeInfo(schemeInfoList);
        mAdapter.notifyDataSetChanged();
        MCSetting.setSchemes(getContext(), mDeviceInfo.uuid, schemeInfoList);
    }

    public void updateStatus( List<Integer> list )
    {
        mStatus = list;
    }

}
