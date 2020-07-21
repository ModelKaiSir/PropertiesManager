package org.qiukai.properties.manager.bean;

import org.qiukai.properties.manager.util.PropertiesReadWriteUtil;

import java.io.*;
import java.util.*;

public class PropertiesBean {


    private String name;

    private PropertiesBean parent;
    private List<PropertiesBean> child;

    private List<PropertiesItem> items = new ArrayList<>();
    private Map<String, String> maps = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void update(String key, String value) {
        maps.put(key, value);
    }

    public PropertiesBean getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toProperties() {

        StringJoiner joiner = new StringJoiner("\n");

        for (PropertiesItem item : items) {

            if (item.isComment()) {

                joiner.add(item.toString());
            } else {

                joiner.add(item.refresh(maps).toString());
            }
        }
        return joiner.toString();
    }

    public static PropertiesBean pathOf(String propertiesPath) {

        final PropertiesBean result = new PropertiesBean();
        PropertiesReadWriteUtil.reads(propertiesPath, str -> {

            if (!str.isEmpty()) {
                PropertiesItem item = PropertiesItem.valueOf(str);
                result.items.add(item);

                if (item.isItem()) {
                    result.maps.put(item.getKey(), item.getValue());
                }
            }
        });
        return result;
    }

    public static PropertiesBean extendOf(PropertiesBean parent) {

        PropertiesBean result = new PropertiesBean();

        result.parent = parent;
        result.child.add(result);

        result.items.addAll(parent.items);
        parent.maps.forEach((k, v) -> {

            result.maps.put(k, v);
        });
        return result;
    }

    public static void main(String[] args) {

        PropertiesBean b = PropertiesBean.pathOf("D:/config.properties");
        b.update("DatabaseDriver", "123");
        System.out.println(b.toProperties());
    }
}
