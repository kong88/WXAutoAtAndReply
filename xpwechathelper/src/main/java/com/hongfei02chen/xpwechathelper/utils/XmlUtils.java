package com.hongfei02chen.xpwechathelper.utils;

import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * created by chenhongfei on 2018/6/6
 */
public class XmlUtils {
    private static final String TAG = "xml";
    //<![CDATA[kong]]>
    private static final String REGEX_1 = "<sysmsgtemplate>([\\s\\S]*?)</sysmsgtemplate>";

    public static Map<String, Object> parseXml(String content) {
        String str = "";
        Pattern pattern1 = Pattern.compile(REGEX_1);
        Matcher matcher1 = pattern1.matcher(content);
        while (matcher1.find()) {

            if (matcher1.groupCount() >= 1) {
                str = matcher1.group(1);
            }
        }


        Map<String, Object> retMap = new HashMap<>();
        String template = "";
        List<String> nicknameList = new ArrayList<>();
        List<String> usernameList = new ArrayList<>();
        InputStream is = null;

        if (TextUtils.isEmpty(str)) {
            return retMap;
        }

        Log.d(TAG, "source text:" + str);

        try {
            is = new ByteArrayInputStream(str.getBytes("UTF-8"));

            if (is == null) {
                Log.d(TAG, "null =============");
                return retMap;
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(is);
            //获取根标签
            Element element = document.getDocumentElement();

            Log.i(TAG, "根标签：" + element.getNodeName());
            System.out.println("根标签：" + element.getNodeName());

            NodeList templateList = element.getElementsByTagName("template");

            if (templateList != null && templateList.getLength() > 0) {
                template = templateList.item(0).getTextContent();
            }

            NodeList nodeList = element.getElementsByTagName("link");
            if (nodeList == null || nodeList.getLength() <= 0) {
                return retMap;
            }
            for (int i = 0; i < nodeList.getLength(); i++) {
                //获取单个
                Element e = (Element) nodeList.item(i);
                String name = e.getAttribute("name");

                if (TextUtils.isEmpty(name) || (!name.equals("adder") && !name.equals("names"))) {
                    continue;
                }
                NodeList memberList = e.getElementsByTagName("member");

                if (memberList == null || memberList.getLength() <= 0) {
                    continue;
                }
                for (int j = 0; j < memberList.getLength(); j++) {
                    Element mElement = (Element) memberList.item(j);
                    String u = mElement.getElementsByTagName("username").item(0).getTextContent();
                    String n = mElement.getElementsByTagName("nickname").item(0).getTextContent();
                    usernameList.add(u);
                    nicknameList.add(n);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("==xpose temList:" + template + " usernameList:" + usernameList.size() + "  nicknameList:" + nicknameList.size() + " 0:" + nicknameList.get(0) + " 1:" + nicknameList.get(1));
        Log.d(TAG, "xpose temList:" + template + " usernameList:" + usernameList.size() + "  nicknameList:" + nicknameList.size());
        retMap.put("template", template);
        retMap.put("usernameList", usernameList);
        retMap.put("nicknameList", nicknameList);

        return retMap;
    }
}
