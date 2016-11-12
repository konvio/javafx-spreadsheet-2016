package com.konv.spreadsheet;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType;

public class DialogHelper {

    public static void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("dolphin.png")));

        alert.showAndWait();
    }

    public static void showExpandableAlert(AlertType alertType, String title, String header, String content,
                                           String expandableContent) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        TextArea textArea = new TextArea(expandableContent);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(textArea, Priority.ALWAYS);
        alert.getDialogPane().setExpandableContent(new VBox(textArea));
        alert.getDialogPane().setExpanded(true);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("dolphin.png")));

        alert.showAndWait();
    }

    public static boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("dolphin.png")));

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static boolean showExpandableConfirmationDialog(String title, String header, String content,
                                                           String expandableContent) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        TextArea textArea = new TextArea(expandableContent);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(textArea, Priority.ALWAYS);
        alert.getDialogPane().setExpandableContent(new VBox(textArea));
        alert.getDialogPane().setExpanded(true);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("dolphin.png")));

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static String showTextInputDialog(String title, String header, String content, String hint) {
        TextInputDialog dialog = new TextInputDialog(hint);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("dolphin.png")));

        Optional<String> result = dialog.showAndWait();
        return result.isPresent() ? result.get() : null;
    }

    public static void showException(Exception e, String title, String header) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String exceptionText = printWriter.toString();

        showExpandableAlert(AlertType.ERROR, "Dolphin Explorer", "Something went wrong", e.toString(), exceptionText);
    }
}
