package com.konv.spreadsheet;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.*;

public class Main extends Application {

    private SpreadsheetView mSpreadsheet;
    private SpreadsheetController mSpreadsheetController;
    private GridBase mGridBase;
    private TextField mTextField;
    private int mRowCount = 99;
    private int mColumnCount = 26;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initUi(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initUi(Stage stage) {
        VBox root = new VBox();
        mTextField = new TextField();
        mTextField.setOnAction(e -> {
            int focusedRow = mSpreadsheet.getSelectionModel().getFocusedCell().getRow();
            int focusedColumn = mSpreadsheet.getSelectionModel().getFocusedCell().getColumn();
            mSpreadsheet.getGrid().setCellValue(focusedRow, focusedColumn, mTextField.getText());
        });

        initSpreadsheet();
        VBox.setVgrow(mSpreadsheet, Priority.ALWAYS);

        ToolBar toolBar = new ToolBar();
        ToggleButton toggleButton = new ToggleButton("Show Formulas");
        toggleButton.setSelected(false);
        toggleButton.selectedProperty().addListener((e, oldValue, newValue) -> {

            mSpreadsheetController.setShowFormulas(newValue);
            mSpreadsheetController.display();
        });

        toolBar.getItems().addAll(toggleButton);
        root.getChildren().addAll(mTextField, mSpreadsheet, toolBar);
        stage.setScene(new Scene(root, 840, 600));
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.jpg")));
        stage.setTitle("Spreadsheet");
    }

    private void initSpreadsheet() {
        mGridBase = new GridBase(mRowCount, mColumnCount);
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        mSpreadsheetController = new SpreadsheetController(mGridBase);
        for (int row = 0; row < mGridBase.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < mGridBase.getColumnCount(); ++column) {
                list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
            }
            rows.add(list);
        }
        mGridBase.setRows(rows);
        mGridBase.addEventHandler(GridChange.GRID_CHANGE_EVENT, mSpreadsheetController);
        mSpreadsheet = new SpreadsheetView(mGridBase);
        mSpreadsheet.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        mSpreadsheet.addEventFilter(KeyEvent.KEY_RELEASED, e -> updateTextField());
        mSpreadsheet.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> updateTextField());
        for (SpreadsheetColumn c : mSpreadsheet.getColumns()) c.setPrefWidth(90);
    }

    private void updateTextField() {
        int focusedRow = mSpreadsheet.getSelectionModel().getFocusedCell().getRow();
        int focusedColumn = mSpreadsheet.getSelectionModel().getFocusedCell().getColumn();
        mTextField.setText(mSpreadsheetController.getCell(focusedRow, focusedColumn).getFormula());
    }
}

