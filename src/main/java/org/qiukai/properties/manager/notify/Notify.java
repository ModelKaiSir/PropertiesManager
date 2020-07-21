package org.qiukai.properties.manager.notify;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class Notify {

    private Notify() {

    }

    public static Optional<ButtonType> confirm(String message) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.setTitle("系统提示");
        alert.setHeaderText("警告！");
        return alert.showAndWait();
    }

    public static Optional<String> input(String message) {

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("请输入");
        inputDialog.setHeaderText("需要参数：");
        inputDialog.setContentText(message);
        return inputDialog.showAndWait();
    }

    public static void info(String message) {

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("系统提示");
        info.setHeaderText(message);
        info.show();
    }
}
