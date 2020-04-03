package com.iweon.mtxc;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_WRITEABLE;
import static java.lang.Integer.parseInt;

public class MCSetting {

    static boolean setSchemes(Context context, UUID uuid, List<SchemeInfo> infoList)
    {
        boolean ret = false;

        try {
            int size = infoList.size();

            String xmlFileName = uuid.toString() + "-scheme.xml";

            FileOutputStream fos = context.openFileOutput(xmlFileName,MODE_PRIVATE);
            // 获得一个序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            // 设置文件头
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "schemes");
            serializer.attribute(null, "count", size +"");    // In channel title.

            for (int i = 0; i < infoList.size(); i++) {
                SchemeInfo schemeInfo = infoList.get(i);

                serializer.startTag(null, "item");
                serializer.attribute(null, "name", schemeInfo.name);    // In channel title.
                String status = new String();
                for(int j=0;j<schemeInfo.channels.size();j++)
                    status += schemeInfo.channels.get(j) + ",";
                serializer.text(status);
                serializer.endTag(null, "item");
            }
            serializer.endTag(null, "schemes");
            serializer.endDocument();
            fos.close();
            ret = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    static List<SchemeInfo> getSchemes(Context context, UUID uuid)
    {
        List<SchemeInfo> titleList = new ArrayList<>();

        try {
            String xmlFileName = uuid.toString() + "-scheme.xml";
            FileInputStream fis = context.openFileInput(xmlFileName);

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("schemes".equals(tagName)) { // <persons>

                        } else if ("item".equals(tagName)) { // <person id="1">
                            SchemeInfo schemeInfo = new SchemeInfo();
                            schemeInfo.channels = new ArrayList<Integer>();

                            schemeInfo.name = parser.getAttributeValue(null, "name");
                            String status = parser.nextText();

                            String ch[] = status.split(",");
                            for( int i=0; i<ch.length; i++)
                                schemeInfo.channels.add(Integer.parseInt(ch[i]));

                            titleList.add(schemeInfo);
                        }
                        break;

                    case XmlPullParser.END_TAG: // </persons>
                        if ("schemes".equals(tagName)) {
                        }else if("item".equals(tagName))
                        {

                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }
        }
        catch (Exception e)
        {

        }

        return titleList;
    }

    static boolean setInTitles(Context context, UUID uuid, List<String> infoList)
    {
        boolean ret = false;

        try {
            String xmlFileName = uuid.toString() + "-in.xml";

            FileOutputStream fos = context.openFileOutput(xmlFileName,MODE_PRIVATE);
            // 获得一个序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            // 设置文件头
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "titles");
            serializer.attribute(null, "type", "0");    // In channel title.

            for (int i = 0; i < infoList.size(); i++) {
                serializer.startTag(null, "title");
                serializer.text(infoList.get(i));
                serializer.endTag(null, "title");
            }
            serializer.endTag(null, "titles");
            serializer.endDocument();
            fos.close();
            ret = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    static boolean setOutTitles(Context context, UUID uuid, List<String> infoList)
    {
        boolean ret = false;

        try {
            String xmlFileName = uuid.toString() + "-out.xml";

            FileOutputStream fos = context.openFileOutput(xmlFileName,MODE_PRIVATE);
            // 获得一个序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            // 设置文件头
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "titles");
            serializer.attribute(null, "type", "1");    // Out channel title.

            for (int i = 0; i < infoList.size(); i++) {
                serializer.startTag(null, "title");
                serializer.text(infoList.get(i));
                serializer.endTag(null, "title");
            }
            serializer.endTag(null, "titles");
            serializer.endDocument();
            fos.close();
            ret = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    static List<String> getInTitles(Context context, UUID uuid)
    {
        List<String> titleList = new ArrayList<>();

        try {
            String xmlFileName = uuid.toString() + "-in.xml";
            FileInputStream fis = context.openFileInput(xmlFileName);

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("titles".equals(tagName)) { // <persons>

                        } else if ("title".equals(tagName)) { // <person id="1">
                            titleList.add(parser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG: // </persons>
                        if ("titles".equals(tagName)) {
                        }else if("title".equals(tagName))
                        {
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }
        }
        catch (Exception e)
        {

        }

        return titleList;
    }

    static List<String> getOutTitles(Context context, UUID uuid)
    {
        List<String> titleList = new ArrayList<>();

        try {
            String xmlFileName = uuid.toString() + "-out.xml";
            FileInputStream fis = context.openFileInput(xmlFileName);

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("titles".equals(tagName)) { // <persons>

                        } else if ("title".equals(tagName)) { // <person id="1">
                            titleList.add(parser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG: // </persons>
                        if ("titles".equals(tagName)) {
                        }else if("title".equals(tagName))
                        {
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }
        }
        catch (Exception e)
        {

        }

        return titleList;
    }

    static boolean setDevicesInfo(Context context, List<DeviceInfo> infoList)
    {
        boolean ret = false;

        try {
//            File file = new File(Environment.getExternalStorageDirectory(),"devices.xml");
//            FileOutputStream fos = new FileOutputStream(file);
            FileOutputStream fos = context.openFileOutput("devices.xml",MODE_PRIVATE);
            // 获得一个序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            // 设置文件头
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "devices");
            for (int i = 0; i < infoList.size(); i++) {
                DeviceInfo deviceInfo = infoList.get(i);

                serializer.startTag(null, "device");
                serializer.attribute(null, "id", String.valueOf(deviceInfo.id));
                serializer.attribute(null, "uuid", deviceInfo.uuid.toString());
                // 写姓名
                serializer.startTag(null, "name");
                serializer.text(deviceInfo.name);
                serializer.endTag(null, "name");
                // 写IP
                serializer.startTag(null, "ip");
                serializer.text(deviceInfo.ip);
                serializer.endTag(null, "ip");
                // 写port
                serializer.startTag(null, "port");
                serializer.text(String.valueOf(deviceInfo.port));
                serializer.endTag(null, "port");

                serializer.startTag(null, "in");
                serializer.text(String.valueOf(deviceInfo.inCount));
                serializer.endTag(null, "in");
                serializer.startTag(null, "out");
                serializer.text(String.valueOf(deviceInfo.outCount));
                serializer.endTag(null, "out");
                serializer.startTag(null, "sin");
                serializer.text(String.valueOf(deviceInfo.showInCount));
                serializer.endTag(null, "sin");
                serializer.startTag(null, "sout");
                serializer.text(String.valueOf(deviceInfo.showOutCount));
                serializer.endTag(null, "sout");
                serializer.startTag(null, "stype");
                serializer.text(String.valueOf(deviceInfo.switchType));
                serializer.endTag(null, "stype");
                serializer.startTag(null, "timeout");
                serializer.text(String.valueOf(deviceInfo.timeout));
                serializer.endTag(null, "timeout");

                serializer.endTag(null, "device");
            }
            serializer.endTag(null, "devices");
            serializer.endDocument();
            fos.close();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    static List<DeviceInfo> getDevicesInfo(Context context)
    {
        List<DeviceInfo> deviceInfoList = new ArrayList<>();

        try {
//            File path = new File(Environment.getExternalStorageDirectory(),"persons.xml");
//            FileInputStream fis = new FileInputStream(path);
            FileInputStream fis = context.openFileInput("devices.xml");

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

            DeviceInfo deviceInfo = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("devices".equals(tagName)) { // <persons>

                        } else if ("device".equals(tagName)) { // <person id="1">
                            deviceInfo = new DeviceInfo();

                            deviceInfo.id = (byte) parseInt(parser.getAttributeValue(null, "id"));
                            deviceInfo.uuid = UUID.fromString(parser.getAttributeValue(null, "uuid"));
                        } else if ("name".equals(tagName)) { // <name>
                            if(deviceInfo!=null)
                                deviceInfo.name = parser.nextText();
                        }else if ("ip".equals(tagName)) { // <ip>
                            if(deviceInfo!=null)
                                deviceInfo.ip = parser.nextText();
                        } else if ("port".equals(tagName)) { // <port>
                            if(deviceInfo!=null)
                                deviceInfo.port = parseInt(parser.nextText());
                        } else if ("in".equals(tagName)) { // <in>
                            if(deviceInfo!=null)
                                deviceInfo.inCount = parseInt(parser.nextText());
                        } else if ("out".equals(tagName)) { // <out>
                            if(deviceInfo!=null)
                                deviceInfo.outCount = parseInt(parser.nextText());
                        } else if ("sin".equals(tagName)) { // <show in>
                            if(deviceInfo!=null)
                                deviceInfo.showInCount = parseInt(parser.nextText());
                        } else if ("sout".equals(tagName)) { // <show out>
                            if(deviceInfo!=null)
                                deviceInfo.showOutCount = parseInt(parser.nextText());
                        } else if ("stype".equals(tagName)) { // <switch type>
                            if(deviceInfo!=null)
                                deviceInfo.switchType = SwitchType.valueOf(parser.nextText());
                        } else if("timeout".equals(tagName)){   // timeout
                            deviceInfo.timeout = parseInt(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG: // </persons>
                        if ("devices".equals(tagName)) {
                            int a = 0;
                        }else if("device".equals(tagName))
                        {
                            deviceInfoList.add(deviceInfo);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{

        }

        return deviceInfoList;
    }

    static boolean removeDeviceInfo(Context context, DeviceInfo info, boolean all)
    {
        boolean ret = false;

        String xmlFileName = info.uuid.toString() + "-scheme.xml";
        ret = context.deleteFile(xmlFileName);

        xmlFileName = info.uuid.toString() + "-in.xml";
        ret = context.deleteFile(xmlFileName);
        xmlFileName = info.uuid.toString() + "-out.xml";
        ret = context.deleteFile(xmlFileName);

        if( all ) {
            xmlFileName = "devices.xml";
            ret = context.deleteFile(xmlFileName);
        }

        return ret;
    }
}
