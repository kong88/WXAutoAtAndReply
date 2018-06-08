package com.hongfei02chen.xpwechathelper;

import android.util.Log;
import android.util.Xml;

import com.hongfei02chen.xpwechathelper.utils.MessageUtils;
import com.hongfei02chen.xpwechathelper.utils.XmlUtils;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        System.out.print("123123");
        parseGroup();
        assertEquals(4, 2 + 2);
    }

    private void parseGroup() {
        String content = "激活${木头人}";
        String group = MessageUtils.parseActiveGroup(content);
        System.out.println("group:" + group);
    }

    public void parseXml2() {
        String str = "4361306333@chatroom:\n" +
                "    <sysmsg type=\"sysmsgtemplate\">\n" +
                "<sysmsgtemplate>\n" +
                "<content_template type=\"tmpl_type_profile\">\n" +
                "    \t\t\t<plain><![CDATA[]]></plain>\n" +
                "    \t\t\t<template><![CDATA[\"$username$\"邀请\"$names$\"加入了群聊]]></template>\n" +
                "    \t\t\t<link_list>\n" +
                "    \t\t\t\t<link name=\"username\" type=\"link_profile\">\n" +
                "    \t\t\t\t\t<memberlist>\n" +
                "    \t\t\t\t\t\t<member>\n" +
                "    \t\t\t\t\t\t\t<username><![CDATA[hongfei_scut]]></username>\n" +
                "    \t\t\t\t\t\t\t<nickname><![CDATA[kong]]></nickname>\n" +
                "    \t\t\t\t\t\t</member>\n" +
                "    \t\t\t\t\t</memberlist>\n" +
                "    \t\t\t\t</link>\n" +
                "    \t\t\t\t<link name=\"names\" type=\"link_profile\">\n" +
                "    \t\t\t\t\t<memberlist>\n" +
                "    \t\t\t\t\t\t<member>\n" +
                "    \t\t\t\t\t\t\t<username><![CDATA[ybbking]]></username>\n" +
                "    \t\t\t\t\t\t\t<nickname><![CDATA[三重奏]]></nickname>\n" +
                "    \t\t\t\t\t\t</member>\n" +
                "    \t\t\t\t\t\t<member>\n" +
                "    \t\t\t\t\t\t\t<username><![CDATA[wxid_3tny9juxq7w521]]></username>\n" +
                "    \t\t\t\t\t\t\t<nickname><![CDATA[lin⛺️\uD83C\uDFAF\uD83D\uDC38]]></nickname>\n" +
                "    \t\t\t\t\t\t</member>\n" +
                "    \t\t\t\t\t</memberlist>\n" +
                "    \t\t\t\t\t<separator><![CDATA[、]]></separator>\n" +
                "    \t\t\t\t</link>\n" +
                "    \t\t\t</link_list>\n" +
                "</content_template>\n" +
                "</sysmsgtemplate>\n" +
                "    </sysmsg>\n" +
                "     lvbuffer=[B@d246f35 type=570425393 isSend=0 bizChatId=-1 talkerId=52 flag=0\n";
        Map<String, Object> map = XmlUtils.parseXml(str);
        System.out.println("template:" + map.get("template"));
    }

    public void parseXml() {
        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>  \n" +
                "<persons>  \n" +
                "  <person id=\"1\">  \n" +
                "    <name>zhangsan</name>  \n" +
                "    <age>21</age>  \n" +
                "  </person>  \n" +
                "  <person id=\"2\">  \n" +
                "    <name>lisi</name>  \n" +
                "    <age>22</age>  \n" +
                "  </person>  \n" +
                "  <person id=\"3\">  \n" +
                "    <name>wangwu</name>  \n" +
                "    <age>222</age>  \n" +
                "  </person>  \n" +
                "</persons>  ";
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (is == null) {
            return;
        }
        //使用PULL解析
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(is, "UTF-8");

            //获取解析的标签的类型
            int type = xmlPullParser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        //获取开始标签的名字
                        String starttgname = xmlPullParser.getName();
                        String id = xmlPullParser.getAttributeValue(0);
                        String name = xmlPullParser.nextText();
                        Log.d("xml", String.format("%s, %s, %s", starttgname, id, name));
                        System.out.println(String.format("====%s, %s, %s", starttgname, id, name));
//                        if ("person".equals(starttgname)) {
//                            //获取id的值
//                            String id = xmlPullParser.getAttributeValue(0);
//                            Log.i("test", id);
//                        } else if ("name".equals(starttgname)) {
//                            String name = xmlPullParser.nextText();
//                            Log.i("test", name);
//                        } else if ("age".equals(starttgname)) {
//                            String age = xmlPullParser.nextText();
//                            Log.i("test", age);
//                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }//细节：
                type = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}