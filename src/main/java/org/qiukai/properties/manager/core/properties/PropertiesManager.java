package org.qiukai.properties.manager.core.properties;

import com.google.common.base.Preconditions;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.qiukai.properties.manager.bean.PropertiesBean;
import org.qiukai.properties.manager.util.PropertiesReadWriteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PropertiesManager {

    private static Logger LOGGER = LoggerFactory.getLogger(PropertiesManager.class);
    static final String PROPERTIES_INI = "properties/source.ini";
    static final String JDL_PROPERTIES_DATA = "properties/jdl/data.jdl";
    static final String JDL_PROPERTIES_TREE = "properties/jdl/tree.jdl";
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
            FileSystemResource resource = new FileSystemResource(PROPERTIES_INI);
            ini = new Ini(resource.getInputStream());
            sqlServerConnectUrlTemp = ini.get("DEFAULT").get("databaseconnectionurl");

            //load Tree Data
            FileSystemResource tree = new FileSystemResource(JDL_PROPERTIES_TREE);
            HashMap<String, List<String>> jdl2tree = PropertiesReadWriteUtil.read2Jdl(tree.getFile().getAbsolutePath(), HashMap.class);

            if (null != jdl2tree) {
                propertiesTree.putAll(jdl2tree);
            }

            //load Properties Data
            FileSystemResource data = new FileSystemResource(JDL_PROPERTIES_DATA);
            HashMap<String, PropertiesBean> jdl2data = PropertiesReadWriteUtil.read2Jdl(data.getFile().getAbsolutePath(), HashMap.class);

            if (null != jdl2data) {
                propertiesMap.putAll(jdl2data);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

        if(null == propertiesMap.get(target)){

            this.root = PropertiesBean.pathOf(resource);
            propertiesMap.put(target, root);
        }else{

            this.root = propertiesMap.get(target);
        }

        this.root.setName(target);
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

    private void updateTree(String root, String child) {

        if (propertiesTree.containsKey(root)) {
            propertiesTree.get(root).add(child);
        } else {
            propertiesTree.put(root, new ArrayList<>());
            updateTree(root, child);
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

    public void remove() {

        if(null != local){

            //删掉对应的properties配置
            propertiesMap.remove(local.getName());

            if(null != local.getParentName()){

                List<String> child = propertiesTree.get(local.getParentName());

                if(null != child && !child.isEmpty()){

                    child.remove(local.getName());
                }else{
                    propertiesTree.remove(local.getName());
                }
            }else{

                propertiesTree.remove(local.getName());
            }
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

        PropertiesBean l = PropertiesBean.extendOf(local);
        l.setName(name);
        propertiesMap.put(name, l);
        updateTree(local.getName(), name);
        this.local = l;
        return l;
    }

    public PropertiesBean clone(String name, String properties) {

        PropertiesBean l = PropertiesBean.valueOf(properties);
        l.setName(name);
        l.setParentName(local.getName());
        propertiesMap.put(name, l);
        updateTree(local.getName(), name);
        this.local = l;
        return l;
    }

    public void switched() {

        Assert.notNull(this.root, "没有选中根配置！");
        Assert.notNull(this.local, "没有选中配置！");

        //将当前配置内容写入source和webSource的配置文件
        PropertiesReadWriteUtil.writes(source.get(), local.toProperties());
        PropertiesReadWriteUtil.writes(webSource.get(), local.toProperties());
    }


    public void update(String key, String value) {

        propertiesMap.put(key, PropertiesBean.valueOf(value));
        properties.set(value);
    }

    public void destroy() {

        FileSystemResource data = new FileSystemResource(JDL_PROPERTIES_DATA);
        PropertiesReadWriteUtil.save2Jdl(data.getFile().getAbsolutePath(), propertiesMap);
        FileSystemResource tree = new FileSystemResource(JDL_PROPERTIES_TREE);
        PropertiesReadWriteUtil.save2Jdl(tree.getFile().getAbsolutePath(), propertiesTree);
    }
}
