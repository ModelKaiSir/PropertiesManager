package org.qiukai.properties.manager.core.properties;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import javafx.scene.control.TreeItem;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.qiukai.properties.manager.bean.PropertiesBean;
import org.qiukai.properties.manager.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertiesManager {
    private static Logger LOGGER = LoggerFactory.getLogger(PropertiesManager.class);
    static final String PROPERTIES_INI = "properties/source.ini";

    private PropertiesBean root;
    private PropertiesBean local;

    private Ini ini;
    private HashMap<String, PropertiesBean> propertiesMap = new HashMap<>();
    private HashMap<String, List<String>> propertiesTree = new HashMap<>();

    private String sqlServerConnectUrlTemp;

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
        String resource = section.get("source");
        String webSource = section.get("web_source");
        Preconditions.checkNotNull(resource, "section attrib %s not found", "source");
        Preconditions.checkNotNull(webSource, "section attrib %s not found", "web_source");

        LOGGER.info(String.format("select Properties %s.", target));
        this.root = PropertiesBean.pathOf(resource);
        this.local = root;
    }

    public void get(String target) {

        Preconditions.checkArgument(propertiesMap.containsKey(target), "%s not found.", target);
        this.local = propertiesMap.get(target);
    }

    public HashMap<String, List<String>> getPropertiesTree() {
        return propertiesTree;
    }

    public PropertiesBean getLocal(){
        return local;
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

        if(null != selectedItem){

            //删掉对应的properties配置
            propertiesMap.remove(selectedItem.getValue());

            //删除树中该子项
            if(!propertiesTree.get(local.getName()).isEmpty()){
                propertiesTree.get(local.getName()).remove(selectedItem.getValue());
            }
            //删除树中该项
            propertiesTree.remove(selectedItem.getValue());
        }
    }

    public PropertiesBean clone(String name, HashMap<String, String> updateProperties) {

        return null;
    }
}
