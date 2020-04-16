package com.virusapp.controller;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_GREENPeer;
import com.virusapp.App;
import com.virusapp.application.AlertHelper;
import com.virusapp.bacnet.BACnetDevice;
import com.virusapp.bacnet.NotificationClassObject;
import com.virusapp.bacnet.OwnDevice;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class NCcontroller implements Initializable {

    private final OwnDevice ownDeviceModel;
    static final Logger LOG = LoggerFactory.getLogger(NCcontroller.class);

    public NCcontroller(OwnDevice model) {
        this.ownDeviceModel = model;
    }

    @FXML
    private TextField instanceNumber;
    @FXML
    private TextField instanceNumberDelete;

    @FXML
    private ProgressBar progressBar;



    @FXML
    private Button writeToDevice;
    @FXML
    private Button deleteButton;

    //Devices
    @FXML
    private TableView<BACnetDevice> remoteDeviceTableView;
    @FXML
    private TableColumn<BACnetDevice, String> deviceNameColumn;
    @FXML
    private TableColumn<BACnetDevice, String> idColumn;
    //Noticlasses
    @FXML
    private TableView<NotificationClassObject> notifiTableView;
    @FXML
    private TableColumn<NotificationClassObject, String> notificationTableColumn;
    @FXML
    private TableColumn<NotificationClassObject, String> objectNameColumn;
    @FXML
    private TableColumn<NotificationClassObject, String> descriptionColumn;
    @FXML
    private TableColumn<NotificationClassObject, String> prioToOffNormalColumn;
    @FXML
    private TableColumn<NotificationClassObject, String> prioToFaultColumn;
    @FXML
    private TableColumn<NotificationClassObject, String> prioToNormalColumn;

    @FXML
    private TableColumn<NotificationClassObject, String> notificationClassColumn;
    //Destinations
    @FXML
    private TableColumn<NotificationClassObject, NotificationClassObject> recipientListColumn;


    private void loadRemoteDevices() {
        try{

            deviceNameColumn.setCellValueFactory(new PropertyValueFactory<BACnetDevice, String>("device"));
            idColumn.setCellValueFactory(new PropertyValueFactory<BACnetDevice, String>("id"));
            remoteDeviceTableView.setItems(ownDeviceModel.getBacnetDevices());
            remoteDeviceTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    BACnetDevice baCnetDevice = remoteDeviceTableView.getSelectionModel().getSelectedItem();
                    loadNotificationClassObjects(baCnetDevice);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNotificationClassObjects(BACnetDevice baCnetDevice) {
        final Button btn = new Button("load");

        //Notifi Table
        notificationTableColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("oid"));
        objectNameColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("name"));
        objectNameColumn.setEditable(true);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("description"));
        prioToOffNormalColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("prioToOffNormal"));
        prioToFaultColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("prioToFault"));
        prioToNormalColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("prioToNormal"));
        notificationClassColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("notificationClass"));

        recipientListColumn.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue()));
        recipientListColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<NotificationClassObject, NotificationClassObject> call(TableColumn<NotificationClassObject, NotificationClassObject> param) {

                return new TableCell<>() {
                    final Button button = new Button();
                    {
                        Image imageDesti = new Image(getClass().getResourceAsStream("/view/destiMenu.png"));
                        button.setMinWidth(15);
                        button.setGraphic(new ImageView(imageDesti));
                    }
                    @Override
                    public void updateItem(NotificationClassObject notificationClassObject, boolean empty) {
                        super.updateItem(notificationClassObject, empty);
                        if (notificationClassObject != null) {
                            setGraphic(button);
                            button.setOnAction(event -> loadDestinationObjects(notificationClassObject));
                        }
                    }

                };
            }
        });

        notifiTableView.setItems(baCnetDevice.getNotificationClassObjects());
    }

    private void loadDestinationObjects(NotificationClassObject notificationClassObject){
           DestinationListController destinationListController = new DestinationListController(notificationClassObject.getRecipientList());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    loadRemoteDevices();
    AlertHelper.denyString(instanceNumberDelete);
    AlertHelper.denyString(instanceNumber);
    writeToDevice.setOnAction(event -> onWriteButtonAction());
    deleteButton.setOnAction(event -> onDeleteButtonAction());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            private double progress;
            public void run() {
                int x = App.ownDevice.getScanSeconds();
                progressBar.setProgress(this.progress = progress + (1.0 / x));
            }
        }, 0, 1000);
    }

    public void onDeleteButtonAction(){
        if(AlertHelper.showAlert(Alert.AlertType.CONFIRMATION,deleteButton.getScene().getWindow(),"Löschen Bestätigen","Wollen Sie wirklich alle Einträgen vom Empfänger " + instanceNumberDelete.getText() + "\n auf allen NC Objekten in allen Kontrollern löschen?"))
        {
            App.ownDevice.deleteDestinationOnAllNC(Integer.parseInt(instanceNumberDelete.getText()));}
        this.notifiTableView.refresh();
    }

    public void onWriteButtonAction(){
        if(AlertHelper.showAlert(Alert.AlertType.CONFIRMATION,writeToDevice.getScene().getWindow(),"Bestätigen","Wollen Sie wirklich folgenden Empfänger " + instanceNumber.getText() + "\n auf allen NC Objekten in allen Kontrollern eintragen?"))
        { ownDeviceModel.sendNewDestinationToAllNC(Integer.parseInt(instanceNumber.getText()));}
        this.notifiTableView.refresh();
    }

}
