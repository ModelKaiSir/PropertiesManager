package org.qiukai.properties.manager.core.properties;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.qiukai.properties.manager.bean.PropertiesBean;
import org.qiukai.properties.manager.controller.MainController;
import org.qiukai.properties.manager.util.PropertiesReadWriteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PropertiesManager {

    private static Logger LOGGER = LoggerFactory.getLogger(PropertiesManager.class);
    static final String PROPERTIES_INI = "properties/source.ini";
    static final String INI_SOURCE = "source";
    static final String INI_WEB_SOURCE = "web_source";
    static final String INI_WEB_PAGE = "web_page";

    private PropertiesBean root;
    private PropertiesBean local;

    private Ini ini;
    private HashMap<String, PropertiesBean> propertiesMap = new HashMap<>();
    private HashMap<String, List<String>> propertiesTree = new HashMap<>();

    private String sqlServerConnectUrlTemp;

    private StringProperty selectProperties = new SimpleStringProperty("");
    private StringProperty source = new SimpleStringProperty("");
    private StringProperty webSource = new SimpleStringProperty("");
    private StringProperty webPage = new SimpleStringProperty("");
    private StringProperty properties = new SimpleStringProperty("");

    public PropertiesManager() {

        try {
            ClassPathResource resource = new ClassPathResource(PROPERTIES_INI);
            ini = new Ini(resource.getInputStream());
            sqlServerConnectUrlTemp = ini.get("DEFAULT").get("databaseconnectionurl");

            //load Tree Data
            //load Properties Data
            propertiesTree.put("POS61", Lists.newArrayList("ORA100"));
            propertiesTree.put("ORA100", Lists.newArrayList("SQLSERVER"));
            propertiesTree.put("SQLSERVER", Lists.newArrayList("SJH"));
        } catch (IOException e) {
            throw new InvalidPathException(PROPERTIES_INI, e.getMessage(), -1);
        }
    }

    public void select(String target) {

        Profile.Section section = ini.get(target);
        Preconditions.checkNotNull(section, "no root in target[%s]. ", target);
        String resource = section.get(INI_SOURCE);
        String webSource = section.get(INI_WEB_SOURCE);
        String webPage = section.get(INI_WEB_PAGE);

        Preconditions.checkNotNull(resource, "section attrib %s not found", INI_SOURCE);
        Preconditions.checkNotNull(webSource, "section attrib %s not found", INI_WEB_SOURCE);
        Preconditions.checkNotNull(webSource, "section attrib %s not found", INI_WEB_PAGE);

        LOGGER.info(String.format("select Properties %s.", target));
        this.root = PropertiesBean.pathOf(resource);
        this.local = root;

        this.source.set(resource);
        this.webSource.set(webSource);
        this.webPage.set(webPage);
        this.selectProperties.set(target);
        this.properties.set(this.local.toProperties());
    }

    public void get(String target) {

        Preconditions.checkArgument(propertiesMap.containsKey(target), "%s not found.", target);
        this.local = propertiesMap.get(target);
        this.selectProperties.set(target);
        this.properties.set(this.local.toProperties());
    }

    private Optional<String> getIniValue(String config) {

        try {
            Assert.notNull(root, "UnSelected properties.");
            Assert.notNull(ini.get(root), String.format("ini is not find %s", root.getName()));
            return Optional.of(ini.get(root).get(config));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public HashMap<String, List<String>> getPropertiesTree() {
        return propertiesTree;
    }

    public List<String> getRoots() {

        return ini.values().stream().filter(e -> {
            if (!e.getName().equals("DEFAULT")) {
                return true;
            } else {
                return false;
            }
        }).map(e -> {
            return e.getName();
        }).collect(Collectors.toList());
    }

    public void remove(TreeItem<String> selectedItem) {

        if (null != selectedItem) {

            //删掉对应的properties配置
            propertiesMap.remove(selectedItem.getValue());

            //删除树中该子项
            if (!propertiesTree.get(local.getName()).isEmpty()) {
                propertiesTree.get(local.getName()).remove(selectedItem.getValue());
            }

            //删除树中该项
            propertiesTree.remove(selectedItem.getValue());
        }
    }

    public String getSource() {
        return source.get();
    }

    public StringProperty sourceProperty() {
        return source;
    }

    public String getWebSource() {
        return webSource.get();
    }

    public StringProperty webSourceProperty() {
        return webSource;
    }

    public String getWebPage() {
        return webPage.get();
    }

    public StringProperty webPageProperty() {
        return webPage;
    }

    public String getSelectProperties() {
        return selectProperties.get();
    }

    public StringProperty selectPropertiesProperty() {
        return selectProperties;
    }

    public String getProperties() {
        return properties.get();
    }

    public StringProperty propertiesProperty() {
        return properties;
    }

    public PropertiesBean clone(String name, HashMap<String, String> updateProperties) {

        return null;
    }

    public void switched() {

        Assert.notNull(this.root, "");
        Assert.notNull(this.local, "");

        //将当前配置内容写入source和webSource的配置文件
        PropertiesReadWriteUtil.writes(source.get(), local.toProperties());
        PropertiesReadWriteUtil.writes(webSource.get(), local.toProperties());
    }
}
