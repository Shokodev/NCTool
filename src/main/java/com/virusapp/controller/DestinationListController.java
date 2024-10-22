package com.virusapp.controller;


import com.virusapp.bacnet.DestinationObject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.net.URL;
import java.util.ResourceBundle;


class DestinationListController implements Initializable {


    @FXML
    private TableView<DestinationObject> destinationLisTableView;
    @FXML
    private TableColumn<DestinationObject, String> processIdColumn;
    @FXML
    private TableColumn<DestinationObject, String> recipientColumn;


    DestinationListController(ObservableList<DestinationObject> destinationObjects) {
        loadRecipients();
        loadRecipientsIntoTableView(destinationObjects);
    }

    private void loadRecipients() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/destinationList.fxml"));
            loader.setController(this);
            Pane root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Empfängerliste");
            stage.show();

        } catch (Exception ignored) {
        }
    }

    private void loadRecipientsIntoTableView(ObservableList<DestinationObject> destinationObjects) {

        Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                return new EditingCell();
            }
        };

        TableColumn processIdColumn = new TableColumn("Prozess ID");
        TableColumn recipientColumn = new TableColumn("Empfänger");

        processIdColumn.setCellValueFactory(new PropertyValueFactory<DestinationObject, String>("processIdentifierID"));
        processIdColumn.setCellFactory(cellFactory);
        processIdColumn.setEditable(true);
        recipientColumn.setCellValueFactory(new PropertyValueFactory<DestinationObject, String>("deviceID"));
        recipientColumn.setCellFactory(cellFactory);
        recipientColumn.setEditable(true);

        destinationLisTableView.setItems(destinationObjects);
        destinationLisTableView.getColumns().addAll(processIdColumn, recipientColumn);
        destinationLisTableView.setEditable(true);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }
}
