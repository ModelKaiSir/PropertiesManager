package org.qiukai.properties.manager.bean;

import com.google.common.base.Joiner;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Map;

public class PropertiesItem implements Serializable {

    static Joiner JOINER = Joiner.on("=");

    private PropertiesType type;
    private String key;
    private String value;

    public PropertiesType getType() {
        return type;
    }

    public void setType(PropertiesType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isItem() {
        return type == PropertiesType.ITEM;
    }

    public boolean isComment() {
        return type == PropertiesType.COMMENT;
    }

    public PropertiesItem refresh(Map<String, String> maps) {

        if (maps.containsKey(this.key)) {
            this.value = maps.get(this.key);
        }

        return this;
    }

    @Override
    public String toString() {

        if (type == PropertiesType.ITEM) {
            return String.format("%s=%s", key, value);
        } else {
            return String.format("#%s", key);
        }
    }

    public static PropertiesItem itemOf(String key, String value) {

        PropertiesItem result = new PropertiesItem();
        result.setType(PropertiesType.ITEM);
        result.setKey(key);
        result.setValue(value);
        return result;
    }

    public static PropertiesItem commentOf(String value) {

        PropertiesItem result = new PropertiesItem();
        result.setType(PropertiesType.COMMENT);
        result.setKey(value);
        return result;
    }

    public static PropertiesItem valueOf(String str) {

        if (!StringUtils.isEmpty(str)) {

            boolean comment = str.substring(0, 1).equals("#");
            if (comment) {

                return PropertiesItem.commentOf(str.substring(1, str.length()));
            } else {

                String[] value = str.split("=");
                if (value.length == 0) {

                    return PropertiesItem.commentOf("");
                } else if (value.length == 2) {

                    return PropertiesItem.itemOf(value[0], value[1]);
                } else if (value.length > 2) {

                    String key = value[0];
                    value[0] = null;
                    return PropertiesItem.itemOf(key, JOINER.skipNulls().join(value));
                }else {

                    return PropertiesItem.itemOf(value[0], "");
                }
            }
        }

        return null;
    }

    public static enum PropertiesType {

        ITEM, COMMENT
    }
}
