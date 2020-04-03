package com.iweon.mtxc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

public class DeviceDialogFragment extends DialogFragment {
    private DeviceInfo mDeviceInfo = null;
    private int mEditDevice = 0;
    private int mIndex = -1;

    AlertDialog.Builder builder;
    LayoutInflater inflater;

    private View view;
    private EditText nameTv;
    private EditText inTv;
    private EditText outTv;
    private EditText ipTv;
    private EditText portTv;
    private EditText timeoutTv;

    private Button cancelBtn;
    private Button okBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();

        mEditDevice = getArguments().getInt("edit");
        if( mEditDevice == 1 ) {
            mIndex = getArguments().getInt("index");
            mDeviceInfo = (DeviceInfo) getArguments().getSerializable("deviceInfo");
        }

//        setStyle(DialogFragment.STYLE_NO_FRAME,android.R.style.Theme_DeviceDefault_Dialog_Alert);
    }

    /*
    public void setDeviceInfo(int index, DeviceInfo deviceInfo)
    {
        mIndex = index;
        mDeviceInfo = deviceInfo;
        mEditDevice = 0;
    }
    */
    private boolean checkName( EditText name )
    {
        if( name.getText().toString().isEmpty() )
            return false;
        else
            return true;
    }

    private boolean checkCount( EditText count )
    {
        if( count.getText().toString().isEmpty() )
            return false;
        else {
            if( Integer.parseInt(count.getText().toString()) <= 0 || Integer.parseInt(count.getText().toString()) > 144 )
                return false;
            else
                return true;
        }
    }

    private boolean checkPort( EditText port )
    {
        if( port.getText().toString().isEmpty() )
            return false;
        else {
            if( Integer.parseInt(port.getText().toString()) <= 0 || Integer.parseInt(port.getText().toString()) > 65535 )
                return false;
            else
                return true;
        }
    }

    private boolean checkIp( EditText ipEt )
    {
        if( ipEt.getText().toString().isEmpty() )
            return false;
        else {
            String ipStr = ipEt.getText().toString();

            String[] splitA = new String[10];

            splitA = ipStr.split("\\.");
            if( splitA.length == 4 )
            {
                try {
                    if (Integer.parseInt(splitA[0]) < 0 || Integer.parseInt(splitA[0]) > 255)
                        return false;
                    if (Integer.parseInt(splitA[1]) < 0 || Integer.parseInt(splitA[1]) > 255)
                        return false;
                    if (Integer.parseInt(splitA[2]) < 0 || Integer.parseInt(splitA[2]) > 255)
                        return false;
                    if (Integer.parseInt(splitA[3]) < 0 || Integer.parseInt(splitA[3]) > 255)
                        return false;
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();

                    return false;
                }

                return true;
            }else
                return false;
        }
    }

    private boolean checkTimeout( EditText timeoutTv )
    {
        if( timeoutTv.getText().toString().isEmpty() )
            return false;
        else {
            if( Integer.parseInt(timeoutTv.getText().toString()) < 100 || Integer.parseInt(timeoutTv.getText().toString()) > 10000 )
                return false;
            else
                return true;
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.device_info_dialog, null);
        nameTv = view.findViewById(R.id.devNameEditText);
        inTv = view.findViewById(R.id.devInCountEditText);
        outTv = view.findViewById(R.id.devOutCountEditText);
        ipTv = view.findViewById(R.id.devIpEditText);
        portTv = view.findViewById(R.id.devPortEditText);
        timeoutTv = view.findViewById(R.id.devTimeoutEditText);

//        cancelBtn = view.findViewById(R.id.cancelButton);
        okBtn = view.findViewById(R.id.myokButton);

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.device_info_dialog, null);
        final EditText nameTv = view.findViewById(R.id.devNameEditText);
        final EditText inTv = view.findViewById(R.id.devInCountEditText);
        final EditText outTv = view.findViewById(R.id.devOutCountEditText);
        final EditText ipTv = view.findViewById(R.id.devIpEditText);
        final EditText portTv = view.findViewById(R.id.devPortEditText);

        final Button cancelBtn = view.findViewById(R.id.cancelButton);
        final Button okBtn = view.findViewById(R.id.okButton);
        */

        String dlgTitle = new String();
        if(mDeviceInfo!= null)
        {
            dlgTitle = getString(R.string.dlg_title_edit_device);

            nameTv.setText(mDeviceInfo.name);
            inTv.setText(String.valueOf(mDeviceInfo.inCount));
            outTv.setText(String.valueOf(mDeviceInfo.outCount));
            ipTv.setText(mDeviceInfo.ip);
            portTv.setText(String.valueOf(mDeviceInfo.port));
            timeoutTv.setText(String.valueOf(mDeviceInfo.timeout));

            inTv.setEnabled(false);
            outTv.setEnabled(false);
        }
        else
            dlgTitle = getString(R.string.dlg_title_create_device);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceInfo deviceInfo = new DeviceInfo();

                if(mEditDevice == 0) {
                    deviceInfo.uuid = UUID.randomUUID();
                    deviceInfo.id = 0x41;
                }
                else
                {
                    deviceInfo.uuid = mDeviceInfo.uuid;
                    deviceInfo.id = mDeviceInfo.id;
                }

                if( checkName(nameTv) == false ) {
                    nameTv.selectAll();
                    nameTv.requestFocus();
                    return;
                }
                if( checkCount(inTv) == false ) {
                    inTv.selectAll();
                    inTv.requestFocus();
                    return;
                }
                if( checkCount(outTv) == false ) {
                    outTv.selectAll();
                    outTv.requestFocus();
                    return;
                }
                if( checkIp(ipTv) == false ) {
                    ipTv.selectAll();
                    ipTv.requestFocus();
                    return;
                }
                if( checkPort(portTv) == false ) {
                    portTv.selectAll();
                    portTv.requestFocus();
                    return;
                }
                if( checkTimeout(timeoutTv) == false ) {
                    timeoutTv.selectAll();
                    timeoutTv.requestFocus();
                    return;
                }

                deviceInfo.name = nameTv.getText().toString();
                deviceInfo.inCount = Integer.parseInt(inTv.getText().toString());
                deviceInfo.outCount = Integer.parseInt(outTv.getText().toString());;
                deviceInfo.showInCount = deviceInfo.inCount;
                deviceInfo.showOutCount = deviceInfo.outCount;
                deviceInfo.switchType = SwitchType.OneToOne;
                deviceInfo.ip = ipTv.getText().toString();
                deviceInfo.port = Integer.parseInt(portTv.getText().toString());
                deviceInfo.timeout = Integer.parseInt(timeoutTv.getText().toString());

                mListener.onDialogPositiveClick(DeviceDialogFragment.this, mEditDevice, mIndex, deviceInfo);
                dismiss();

            }
        });

        builder.setView(view).setTitle(dlgTitle);
        /*
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        DeviceInfo deviceInfo = new DeviceInfo();

                        if(mIsCreateDevice) {
                            deviceInfo.uuid = UUID.randomUUID();
                            deviceInfo.id = 'a';
                        }
                        else
                        {
                            deviceInfo.uuid = mDeviceInfo.uuid;
                            deviceInfo.id = mDeviceInfo.id;
                        }
                        deviceInfo.name = nameTv.getText().toString();
                        deviceInfo.inCount = Integer.parseInt(inTv.getText().toString());
                        deviceInfo.outCount = Integer.parseInt(outTv.getText().toString());;
                        deviceInfo.showInCount = deviceInfo.inCount;
                        deviceInfo.showOutCount = deviceInfo.outCount;
                        deviceInfo.switchType = SwitchType.OneToOne;
                        deviceInfo.ip = ipTv.getText().toString();
                        deviceInfo.port = (short) Integer.parseInt(portTv.getText().toString());

                        if( deviceInfo.port  > 1234 )   return;
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(DeviceDialogFragment.this, mIsCreateDevice, mIndex, deviceInfo);
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(DeviceDialogFragment.this);
                    }
                });
                */
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int edit, int index, DeviceInfo deviceInfo);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
