package org.qiukai.properties.manager.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
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
import org.qiukai.properties.manager.views.UpdateView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    Button update;
    @FXML
    Button remove;

    @Autowired
    PropertiesManager manager;

    @Autowired
    DataBinder dataBinder;

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

            if (null == n) {
                return;
            }

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

    private boolean checkRoot() {

        //Confirm
        TreeItem<String> item = propertiesData.getSelectionModel().getSelectedItem();

        if (null == item || rootTree == item.getParent()) {
            return false;
        }
        return true;
    }

    private boolean checkSelect() {

        //Confirm
        TreeItem<String> item = propertiesData.getSelectionModel().getSelectedItem();

        if (null == item) {
            return false;
        }
        return true;
    }

    /**
     * 切换为选中的配置
     *
     * @param event
     */
    @FXML
    void switched(ActionEvent event) {

        try {
            manager.switched();
            propertiesData.getSelectionModel().select(-1);
            Notify.info("切换配置成功！");
        } catch (NullPointerException | IllegalStateException e) {
            Notify.info(e.getMessage());
        }
    }

    @FXML
    void clone(ActionEvent event) {

        if (!checkSelect()) {
            return;
        }
        //添加一个属性
        dataBinder.register(PropertiesController.class, new DataBinding() {
            @Override
            public Property start() {
                return showPropertiesArea.textProperty();
            }

            @Override
            public void end(Property property) {

                SimpleObjectProperty<AbstractMap.SimpleEntry<String, String>> p = (SimpleObjectProperty<AbstractMap.SimpleEntry<String, String>>) property;
                manager.clone(p.get().getKey(), p.get().getValue());
                //
                propertiesData.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<>(p.get().getKey()));
                Notify.info("操作成功！");

            }
        });

        AppMain.showView(PropertiesView.class, Modality.APPLICATION_MODAL);
    }

    @FXML
    void update(ActionEvent event) {

        if (!checkRoot()) {
            return;
        }
        //添加一个属性
        dataBinder.register(UpdateController.class, new DataBinding() {
            @Override
            public Property start() {
                return showPropertiesArea.textProperty();
            }

            @Override
            public void end(Property property) {

                SimpleObjectProperty<AbstractMap.SimpleEntry<String, String>> p = (SimpleObjectProperty<AbstractMap.SimpleEntry<String, String>>) property;

                TreeItem<String> item = propertiesData.getSelectionModel().getSelectedItem();
                manager.update(item.getValue(), p.get().getValue());
                //
                Notify.info("修改成功！");
            }
        });

        AppMain.showView(UpdateView.class, Modality.APPLICATION_MODAL);
    }

    @FXML
    void remove(ActionEvent event) {

        //Confirm
        if (!checkRoot()) {
            return;
        }

        TreeItem<String> item = propertiesData.getSelectionModel().getSelectedItem();
        Optional<ButtonType> result = Notify.confirm("确定删除该配置吗？");
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Notify.info("删除成功！");
            manager.remove();
            item.getParent().getChildren().remove(item);
        }
    }

    @FXML
    void startMall(ActionEvent event) {

        Notify.info(webPagePath.getText());
        //启动cmd执行python命令 auto_login start --path=webPage
    }

    public void destroy() {

        manager.destroy();
    }
}
