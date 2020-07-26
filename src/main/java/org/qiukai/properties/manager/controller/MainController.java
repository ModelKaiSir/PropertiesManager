package org.qiukai.properties.manager.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import org.qiukai.properties.manager.bean.PropertiesBean;
import org.qiukai.properties.manager.core.properties.PropertiesManager;
import org.qiukai.properties.manager.notify.Notify;
import org.qiukai.properties.manager.root.AppMain;
import org.qiukai.properties.manager.views.PropertiesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.crypto.Data;
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
    Text source;
    @FXML
    Text webSource;
    @FXML
    Text webPagePath;

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

    @Autowired
    DataBinder b;

    private TreeItem<String> rootTree = new TreeItem<>("Root");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //init label
        selectProperties.textProperty().bind(manager.selectPropertiesProperty());
        source.textProperty().bind(manager.sourceProperty());
        webSource.textProperty().bind(manager.webSourceProperty());
        webPagePath.textProperty().bind(manager.webPageProperty());
        showPropertiesArea.textProperty().bind(manager.propertiesProperty());
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

    /**
     *
     * 切换为选中的配置
     * @param event
     */
    @FXML
    void switched(ActionEvent event) {

        manager.switched();
        propertiesData.getSelectionModel().select(-1);
    }

    @FXML
    void clone(ActionEvent event) {

        //添加一个属性

        AppMain.showView(PropertiesView.class, Modality.APPLICATION_MODAL);
        //获取属性的值，已经过绑定
        /*
        String name = "";
        HashMap<String, String> updateProperties = null;

        changePropertiesItem(manager.clone(name, updateProperties));
         */
    }

    @FXML
    void remove(ActionEvent event) {

        //Confirm
        TreeItem<String> item = propertiesData.getSelectionModel().getSelectedItem();

        if (null == item || rootTree == item.getParent()) {
            return;
        }

        Optional<ButtonType> result = Notify.confirm("确定删除该配置吗？");
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Notify.info("删除成功！");

            manager.remove(item);
            item.getParent().getChildren().remove(item);
        }
    }

    @FXML
    void startMall(ActionEvent event) {

        Notify.info(webPagePath.getText());
        //启动cmd执行python命令 auto_login start --path=webPage
    }
}
