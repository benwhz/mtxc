package com.iweon.mtxc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import static com.iweon.mtxc.LayoutType.GridLayout;
import static com.iweon.mtxc.LayoutType.LinerLayout;

public class SwitchFragment extends Fragment implements SwitchAdapter.OnItemActionListener {
    private RecyclerView mInRecyclerView = null;
    private SwitchAdapter mInAdapter = null;
    private RecyclerView.LayoutManager mInLayoutManager = null;

    private RecyclerView mOutRecyclerView = null;
    private SwitchAdapter mOutAdapter = null;
    private RecyclerView.LayoutManager mOutLayoutManager = null;

    private View rootView = null;
    private DeviceInfo mDeviceInfo;

    private LayoutType mLayoutType;

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    public void setLayoutType(LayoutType type) {
        mLayoutType = type;
    }

    public void checkAllOutput(boolean checked) {
        if (mOutAdapter != null)
            mOutAdapter.checkAll(checked);
    }

    public List<Boolean> getInputCheckStatus() {
        if (mInAdapter != null)
            return mInAdapter.getStatus();
        else
            return null;
    }

    public List<Boolean> getOutputCheckStatus() {
        if (mOutAdapter != null)
            return mOutAdapter.getStatus();
        else
            return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            switch (mLayoutType) {
                case LinerLayout:
                    rootView = inflater.inflate(R.layout.switch_fragment, container, false);
                    break;
                case GridLayout:
                    rootView = inflater.inflate(R.layout.switch_fragment, container, false);
                    break;
            }
            initInputChannelInfo(mLayoutType, rootView);
            initOutputChannelInfo(mLayoutType, rootView);
        }
        return rootView;
    }

    private void initInputChannelInfo(LayoutType type, View view) {
        List<String> inTitles = MCSetting.getInTitles(getContext(), mDeviceInfo.uuid);
        int spanCount = 3; // 3 columns
        int spacing = 32; // 50px
        boolean includeEdge = false;

        switch (type) {
            case LinerLayout:
                mInRecyclerView = (RecyclerView) view.findViewById(R.id.switchInputRV);

                mInRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                mInRecyclerView.setHasFixedSize(true);

                // use a linear layout manager
                mInLayoutManager = new LinearLayoutManager(getContext());
                mInRecyclerView.setLayoutManager(mInLayoutManager);

                // specify an adapter (see also next example)
                mInAdapter = new SwitchAdapter(getContext(), LinerLayout, ChannelType.InputChannel, inTitles, this);

                mInRecyclerView.setAdapter(mInAdapter);

                break;
            case GridLayout:
                mInRecyclerView = (RecyclerView) view.findViewById(R.id.switchInputRV);

                mInRecyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, includeEdge));
                mInRecyclerView.setHasFixedSize(true);

                // use a linear layout manager
                mInLayoutManager = new GridLayoutManager(getContext(), 3);
                mInRecyclerView.setLayoutManager(mInLayoutManager);

                // specify an adapter (see also next example)
                mInAdapter = new SwitchAdapter(getContext(), GridLayout, ChannelType.InputChannel, inTitles, this);
                mInRecyclerView.setAdapter(mInAdapter);

                break;
        }
    }

    private void initOutputChannelInfo(LayoutType type, View view) {
        List<String> outTitles = MCSetting.getOutTitles(getContext(), mDeviceInfo.uuid);
        int spanCount = 3; // 3 columns
        int spacing = 32; // 50px
        boolean includeEdge = false;

        switch (type) {
            case LinerLayout:
                mOutRecyclerView = (RecyclerView) view.findViewById(R.id.switchOutputRV);

                mOutRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                mOutRecyclerView.setHasFixedSize(true);

                // use a linear layout manager
                mOutLayoutManager = new LinearLayoutManager(getContext());
                mOutRecyclerView.setLayoutManager(mOutLayoutManager);

                // specify an adapter (see also next example)
                mOutAdapter = new SwitchAdapter(getContext(), LinerLayout, ChannelType.OutputChannel, outTitles, this);

                mOutRecyclerView.setAdapter(mOutAdapter);
                break;
            case GridLayout:
                mOutRecyclerView = (RecyclerView) view.findViewById(R.id.switchOutputRV);

                mOutRecyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, includeEdge));

                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                mOutRecyclerView.setHasFixedSize(true);

                // use a linear layout manager
                mOutLayoutManager = new GridLayoutManager(getContext(), 3);
                mOutRecyclerView.setLayoutManager(mOutLayoutManager);


                // specify an adapter (see also next example)
                mOutAdapter = new SwitchAdapter(getContext(), GridLayout, ChannelType.OutputChannel, outTitles, this);

                mOutRecyclerView.setAdapter(mOutAdapter);
                break;
        }
    }

    @Override
    public void onActionTriggered(ChannelType type, SwitchAction action, int position) {
        List<String> inTitles = MCSetting.getInTitles(getContext(), mDeviceInfo.uuid);
        List<String> outTitles = MCSetting.getOutTitles(getContext(), mDeviceInfo.uuid);

        switch (type) {
            case InputChannel:
                switch (action) {
                    case Selected:
                        break;
                    case Deselected:
                        break;
                    case EditTitle:
                        getTitle(type, position, inTitles.get(position));
                        break;
                    case SwitchNow:
                        break;
                }
                break;
            case OutputChannel:
                switch (action) {
                    case Selected:
                        break;
                    case Deselected:
                        break;
                    case EditTitle:
                        getTitle(type, position, outTitles.get(position));
                        break;
                    case SwitchNow:
                        break;
                }
                break;
        }
    }

    private void updateChannelTitle(ChannelType type, int index, String text) {
        List<String> inTitles = MCSetting.getInTitles(getContext(), mDeviceInfo.uuid);
        List<String> outTitles = MCSetting.getOutTitles(getContext(), mDeviceInfo.uuid);

        switch (type) {
            case InputChannel:
                inTitles.remove(index);
                inTitles.add(index, text);
                MCSetting.setInTitles(getContext(), mDeviceInfo.uuid, inTitles);
                mInAdapter.setTitles(inTitles);
                break;
            case OutputChannel:
                outTitles.remove(index);
                outTitles.add(index, text);
                MCSetting.setOutTitles(getContext(), mDeviceInfo.uuid, outTitles);
                mOutAdapter.setTitles(outTitles);
                break;
        }
    }

    private void getTitle(ChannelType type, int index, String text) {
        final ChannelType cType = type;
        final int _index = index;

        final String[] title = new String[1];
        final EditText et = new EditText(getContext());

        et.setText(text);
        et.selectAll();
        new AlertDialog.Builder(getContext()).setTitle(R.string.msg_input_channel_title).setView(et).
                setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String _text = et.getText().toString();
                        if (!_text.isEmpty()) {
                            updateChannelTitle(cType, _index, _text);
                        }
                    }
                }).setNegativeButton(R.string.dialog_button_cancel, null).show();

    }

    public void updateStatus(List<Integer> list) {
        mInAdapter.updateStatus(list);
        mOutAdapter.updateStatus(list);
    }
}
