package com.iweon.mtxc;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;

import static com.iweon.mtxc.MatrixService.wifiManager;


public class UdpReceiveTask extends AsyncTask<DatagramSocket, String, Integer> {
    public static final byte MATRIX_COMMAND_QUERY_DEVICE_ID = 2;
    public static final byte MATRIX_COMMAND_QUERY_DEVICE_STATUS = 3;
    public static final byte MATRIX_COMMAND_SWITCH_CHANNEL = 4;
    public static final byte MATRIX_COMMAND_SWITCH_ALL_CHANNEL = 5;

    public static final int RETRY_SEND_COUNT = 1;
    public static final int RETRY_RECEIVE_COUNT = 1;   // receive

    private byte[] mCmd;
    private byte cmdId;

    private byte[] receivedBuf = new byte[1024];
    private DatagramPacket sendPacket = null;
    private DatagramPacket receivePacket = null;
    private byte[] receivedData = new byte[1024];
    private int length = 0;

    private DeviceInfo mDeviceInfo = null;
    static public boolean canceled = false;

    public interface IDataReceiverListener {

        void dataReceived(byte id, byte[] data);
    }

    private static  IDataReceiverListener listener;
    public static void setListener(IDataReceiverListener listener) {
        UdpReceiveTask.listener = listener;
    }

    public UdpReceiveTask(DeviceInfo info, byte[] cmd)
    {
        this.cmdId = cmd[4];
        this.mDeviceInfo = info;
        this.mCmd = cmd;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("UdpReceiveTask","onPreExecute -----------------------> command id is :"+cmdId + ", Thread Id = " + Thread.currentThread().getId() );

        canceled = false;

        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(mDeviceInfo.ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        receivePacket = new DatagramPacket(receivedBuf, receivedBuf.length);
        sendPacket = new DatagramPacket(mCmd, mCmd.length, ipAddress, mDeviceInfo.port);
    }

    @Override
    protected Integer doInBackground(DatagramSocket... datagramSockets) {
        Integer result =  new Integer(0);
        DatagramSocket socket = datagramSockets[0];

        /*
        int breakDataLength = 0;
        // calculate the exit data length here
        if (cmdId == MATRIX_COMMAND_QUERY_DEVICE_ID) {
            breakDataLength = 2;
        }
        if (cmdId == MATRIX_COMMAND_QUERY_DEVICE_STATUS) {
            if (mDeviceInfo.outCount < 100) {
                breakDataLength = 6 + mDeviceInfo.outCount * 3;
            } else {
                breakDataLength = 6 + mDeviceInfo.outCount * 4;
            }
        }
        if (cmdId == MATRIX_COMMAND_SWITCH_CHANNEL) {
            int cnt = mCmd[2];
            cnt = (mCmd[2] << 8 | 0x00FF);
            cnt = cnt & (mCmd[3] | 0xFF00);
            cnt = cnt / 3;

            breakDataLength = 2*cnt;    //"OKOKOKOKOK"
        }
        if (cmdId == MATRIX_COMMAND_SWITCH_ALL_CHANNEL) {
            breakDataLength = 2;   // "OK"
        }
        */

        int tcount = 0;
        while(tcount++ < RETRY_SEND_COUNT) {
            if( canceled )  return length;

            // try to resend command
            length = 0;
            sendCommand(socket);

            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int rcount = 0;
            while (rcount++ < RETRY_RECEIVE_COUNT) {
                Log.i("UdpReceiveTask", "socket try to Receive +++++++++++++++++++ Count :" + rcount + ", Total Count = " + tcount);
                if( canceled )  return length;

                receiveResponse(socket);
            }

            if( length > 0 )    return length;
        }

        result = length;

        return result;
    }

    private boolean sendCommand(DatagramSocket socket)
    {
        boolean result = false;

        if( socket != null && sendPacket != null ) {
            try {
                socket.send(sendPacket);
                socket.setSoTimeout(this.mDeviceInfo.timeout);
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void receiveResponse(DatagramSocket socket)
    {
        while (!canceled) {
            try {
                socket.receive(receivePacket);
                int dataLength = receivePacket.getLength();
                int offset = receivePacket.getOffset();
                byte[] data = receivePacket.getData();

                Log.i("UdpReceiveTask", "receiveResponse +++++++++++++++++++ total length = " + length + ", offset = " + offset + ", data length = " + dataLength );
                System.arraycopy(data, offset, receivedData, length, dataLength);
                length += dataLength;

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if( integer == 0 )
        {
            listener.dataReceived(cmdId,null);

            Log.i("UdpReceiveTask","onPostExecute <----------------------- command id is :"+cmdId + ", Length = " + length + ", Thread Id = " + Thread.currentThread().getId() );
        }
        else
        {
            byte[] retData = new byte[length];

            System.arraycopy(receivedData, 0, retData, 0, length);
//            byte[] retData = receivePacket.getData();

            // print data here.
            String dataMsg = "DATA: ";
            for (int i = 0; i < retData.length; i++) {
                dataMsg = dataMsg + ",0x" + Integer.toHexString(0xFF & retData[i]);
            }
            Log.i("UdpReceiveTask", dataMsg );

            Log.i("UdpReceiveTask","onPostExecute <----------------------- command id is :"+cmdId + ", Length = " + retData.length + ", Thread Id = " + Thread.currentThread().getId() );

            listener.dataReceived(cmdId,retData);
        }

        /*
        if( integer != null ) {
            int size = receivePacket.getLength();
            if (receivePacket.getLength() > 0) {
                byte[] data = receivePacket.getData();

                listener.dataReceived(cmdId,data);
            }
        }
        else
            listener.dataReceived(cmdId,null);
        */
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        canceled = true;
    }
}
