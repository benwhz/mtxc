package com.iweon.mtxc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MatrixService extends Service {
    private DeviceInfo mDeviceInfo = null;
    /*
    private String mIp;
    private int mPort;
    */
    private DatagramSocket socket = null;

    static WifiManager wifiManager = null;
    static SecurityManager securityManager = null;

    public MatrixService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        securityManager = System.getSecurityManager();

        wifiManager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);


        Log.i("MatrixService","onCreate ！！！！！！！！！！！！" );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("MatrixService","onDestroy ！！！！！！！！！！！！" );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*
        String ip = intent.getStringExtra("IP");
        int port = intent.getIntExtra("PORT",(short)7755);
        int timeout = intent.getIntExtra("Timeout",(short)2500);
        */
        mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");

        byte[] cmd = intent.getByteArrayExtra("CMD");

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (socket != null){
            new UdpReceiveTask(mDeviceInfo,cmd).execute(socket);
        }else {
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
