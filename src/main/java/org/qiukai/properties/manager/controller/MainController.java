package org.qiukai.properties.manager.controller;

import com.sun.org.apache.xpath.internal.operations.Or;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.qiukai.properties.manager.bean.PropertiesBean;
import org.qiukai.properties.manager.core.properties.PropertiesManager;
import org.qiukai.properties.manager.notify.Notify;
import org.qiukai.properties.manager.root.AppMain;
import org.qiukai.properties.manager.util.PropertiesReadWriteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.YamlPropertySourceLoader;
import sun.rmi.runtime.Log;

import java.net.URL;
import java.util.*;

@FXMLController
public class MainController implements Initializable {

    private static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    static final String ROOT = "ROOT";
    static final String ITEM = "ITEM";

    @FXML
    TreeView<String> propertiesData;
    @FXML
    Label selectProperties;
    @FXML
    Text resource;
    @FXML
    Text webResource;

    @FXML
    TextArea showPropertiesArea;

    @FXML
    Button clone;
    @FXML
    Button cloneByDataBase;
    @FXML
    Button cloneByDataBaseSqlServer;
    @FXML
    Button update;
    @FXML
    Button remove;

    @Autowired
    PropertiesManager manager;

    private TreeItem<String> rootTree = new TreeItem<>("Root");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //init label
        selectProperties.setText("");
        resource.setText("");
        webResource.setText("");
        //
        rootTree.setExpanded(true);
        propertiesData.setRoot(rootTree);
        propertiesData.setShowRoot(false);
        initPropertiesTree();

        addPropertiesSelectedListener();
    }

    private TreeItem<String> findParentItem(TreeItem<String> item, String name) {

        if (item.getValue().equals(name)) {

            return item;
        } else if (!item.getChildren().isEmpty()) {

            for (TreeItem<String> _item : item.getChildren()) {

                return findParentItem(_item, name);
            }
        }

        return null;
    }

    private void changePropertiesItem(PropertiesBean target) {

        if (null != target.getParent()) {

            TreeItem<String> local;
            for (TreeItem<String> item : propertiesData.getRoot().getChildren()) {

                local = findParentItem(item, target.getParent().getName());
                if (null != local) {
                    local.getChildren().add(new TreeItem<>(target.getName()));
                }
            }
        }
    }

    private void addPropertiesSelectedListener() {

        propertiesData.getSelectionModel().selectedItemProperty().addListener((observable, o, n) -> {

            if (rootTree == n.getParent()) {
                manager.select(n.getValue());
            } else {
                manager.get(n.getValue());
            }
            //
            showPropertiesArea.setText(manager.getLocal().toProperties());
        });
    }

    protected void initPropertiesTree() {

        TreeItem<String> local;
        for (String target : manager.getRoots()) {

            local = new TreeItem<>(target);
            rootTree.getChildren().add(local);
            appendTree(manager.getPropertiesTree(), target, local);
        }
    }

    protected void appendTree(HashMap<String, List<String>> data, String k, TreeItem<String> root) {

        if (null != data.get(k) && !data.get(k).isEmpty()) {

            TreeItem<String> local;
            for (String i : data.get(k)) {
                local = new TreeItem<>(i);
                root.getChildren().add(local);
                appendTree(data, i, local);
            }
        }
    }

    @FXML
    void clone(ActionEvent event) {

        String name = "";
        HashMap<String, String> updateProperties = null;

        changePropertiesItem(manager.clone(name, updateProperties));
    }

    @FXML
    void remove(ActionEvent event) {

        //Confirm
        TreeItem<String> item = propertiesData.getSelectionModel().getSelectedItem();
        if(null == item){
            return;
        }

        Optional<ButtonType> result = Notify.confirm("确定删除该配置吗？");
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Notify.info("删除成功！");

            manager.remove(item);
            item.getParent().getChildren().remove(item);
        }
    }
}
