package cn.ds.commons.utils;

import cn.zb.commons.util.ApiLogger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.servlet.ServletInputStream;
import java.io.*;
import java.util.*;

/**
 * xml工具类
 *
 * @author zj
 */
public class XmlUtil {
    /**
     * 取得xml文件的根节点名称，即消息名称。
     *
     * @param xmlStr xml内容
     * @return String 返回名称
     */
    public static String getRootName(String xmlStr) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        Element root = doc.getRootElement();
        return root.getName();
    }

    /**
     * 把xml文件转换为map形式，其中key为有值的节点名称，并以其所有的祖先节点为前缀，用
     * "."相连接。如：SubscribeServiceReq.Send_Address.Address_Info.DeviceType
     *
     * @param xmlStr xml内容
     * @return Map 转换为map返回
     */
    public static Map<String, String> xml2Map(String xmlStr)
            throws JDOMException, IOException {
        Map<String, String> rtnMap = new HashMap<String, String>();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        // 得到根节点
        Element root = doc.getRootElement();
        String rootName = root.getName();
        //rtnMap.put("root.name", rootName);
        // 调用递归函数，得到所有最底层元素的名称和值，加入map中
        convert(root, rtnMap, rootName);
        return rtnMap;
    }

    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     *
     * @param strxml
     * @return
     * @throws Exception
     */
    public static Map doXMLParse(String strxml, String charset) throws Exception {
        if (null == strxml || "".equals(strxml)) {
            return null;
        }
        Map m = new HashMap();
        InputStream in = String2Inputstream(strxml, charset);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }
        //关闭流
        in.close();
        return m;
    }

    /**
     * 获取子结点的xml
     *
     * @param children
     * @return String
     */
    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    public static InputStream String2Inputstream(String str, String charset) {
        if (charset == null || "".equals(charset)) {
            return new ByteArrayInputStream(str.getBytes());
        }
        try {
            return new ByteArrayInputStream(str.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            ApiLogger.error("转码失败:" + str + ",charset:" + charset, e);
        }
        return null;
    }

    /**
     * 递归函数，找出最下层的节点并加入到map中，由xml2Map方法调用。
     *
     * @param e        xml节点，包括根节点
     * @param map      目标map
     * @param lastname 从根节点到上一级节点名称连接的字串
     */
    public static void convert(Element e, Map<String, String> map,
                               String lastname) {
        if (e.getAttributes().size() > 0) {
            Iterator it_attr = e.getAttributes().iterator();
            while (it_attr.hasNext()) {
                Attribute attribute = (Attribute) it_attr.next();
                String attrname = attribute.getName();
                String attrvalue = e.getAttributeValue(attrname);
                //map.put( attrname, attrvalue);
                map.put(lastname + "." + attrname, attrvalue); //key 根据根节点 进行生成
            }
        }
        List children = e.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            /*String name = lastname + "." + child.getName();*/
            String name = child.getName();
            // 如果有子节点，则递归调用
            if (child.getChildren().size() > 0) {
                convert(child, map, lastname + "." + child.getName());
            } else {
                // 如果没有子节点，则把值加入map
                map.put(name, child.getText());
                // 如果该节点有属性，则把所有的属性值也加入map
                if (child.getAttributes().size() > 0) {
                    Iterator attr = child.getAttributes().iterator();
                    while (attr.hasNext()) {
                        Attribute attribute = (Attribute) attr.next();
                        String attrname = attribute.getName();
                        String attrvalue = child.getAttributeValue(attrname);
                        map.put(lastname + "." + child.getName() + "." + attrname, attrvalue);
                        //map.put( attrname, attrvalue);
                    }
                }
            }
        }
    }

    /**
     * 把xml文件转换为list形式，其中每个元素是一个map，map中的key为有值的节点名称，并以其所有的祖先节点为前缀，用
     * "."相连接。如：SubscribeServiceReq.Send_Address.Address_Info.DeviceType
     *
     * @param xmlStr xml内容
     * @return Map 转换为map返回
     */
    public static List<Map<String, String>> xml2List(String xmlStr)
            throws JDOMException, IOException {
        List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
        Map<String, String> rtnMap = new HashMap<String, String>();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        // 得到根节点
        Element root = doc.getRootElement();
        String rootName = root.getName();
        rtnMap.put("root.name", rootName);
        // 调用递归函数，得到所有最底层元素的名称和值，加入map中
        convert2List(root, rtnMap, rootName, rtnList);
        if (rtnList.size() == 0)
            rtnList.add(rtnMap);
        return rtnList;
    }

    /**
     * 递归函数，找出最下层的节点并加入到map中，如果有相同的节点，则加入list中， 由xml2List方法调用。
     *
     * @param e        xml节点，包括根节点
     * @param map      目标map
     * @param lastname 从根节点到上一级节点名称连接的字串
     * @param list     相同节点生成map放入list中
     */
    public static void convert2List(Element e, Map<String, String> map,
                                    String lastname, List<Map<String, String>> list) {
        if (e.getAttributes().size() > 0) {
            Iterator it_attr = e.getAttributes().iterator();
            while (it_attr.hasNext()) {
                Attribute attribute = (Attribute) it_attr.next();
                String attrname = attribute.getName();
                String attrvalue = e.getAttributeValue(attrname);
                map.put(attrname, attrvalue);
            }
        }
        List children = e.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            String name = lastname + "." + child.getName();
            // 如果有子节点，则递归调用
            if (child.getChildren().size() > 0) {
                convert(child, map, name);
            } else {
                // 如果没有子节点，则把值加入map
                map.put(name, child.getText());
                // 如果该节点有属性，则把所有的属性值也加入map
                if (child.getAttributes().size() > 0) {
                    Iterator attr = child.getAttributes().iterator();
                    while (attr.hasNext()) {
                        Attribute attribute = (Attribute) attr.next();
                        String attrname = attribute.getName();
                        String attrvalue = child.getAttributeValue(attrname);
                        map.put(name + "." + attrname, attrvalue);
                    }
                }
            }
            // 如果有相同节点，则加入list中，不考虑子节点中又有相同节点的情况
            if (e.getChildren(child.getName()).size() > 1) {
                Map<String, String> aMap = new HashMap<String, String>();
                aMap.putAll(map);
                list.add(aMap);
                map = new HashMap<String, String>();
                map.put("root.name", aMap.get("root.name"));
            }
        }
    }

    /**
     * 打印map 的所有key和value
     *
     * @param map
     */
    public static void printMap(Map<String, String> map) {
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
        }
    }

    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static Map doXMLParse(ServletInputStream in) throws Exception {
        if (null == in) {
            return null;
        }
        Map m = new HashMap();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }
        //关闭流
        in.close();
        return m;
    }
}

