package org.qiukai.properties.manager.bean;

import com.google.common.base.Splitter;
import org.qiukai.properties.manager.util.PropertiesReadWriteUtil;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

public class PropertiesBean implements Serializable {

    private String name;
    private String parentName;

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

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentName() {
        return parentName;
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

    public static PropertiesBean valueOf(String lines) {

        final PropertiesBean result = new PropertiesBean();
        Splitter.on("\n").splitToList(lines).forEach(str -> {

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
        result.parentName = parent.name;
        result.child.add(result);

        result.items.addAll(parent.items);
        parent.maps.forEach((k, v) -> {

            result.maps.put(k, v);
        });

        return result;
    }
}
