package com.virusapp.controller;

import com.virusapp.Main;
import com.virusapp.bacnet.BACnetDevice;
import com.virusapp.bacnet.DestinationObject;
import com.virusapp.bacnet.NotificationClassObject;
import com.virusapp.bacnet.OwnDevice;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Nc implements Initializable {

    private OwnDevice ownDeviceModel;

    public Nc(OwnDevice model) {
        this.ownDeviceModel = model;
    }

    @FXML
    private TextField instanceNumber;
    @FXML
    private Button writeToDevice;
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



    private ObservableList<BACnetDevice> obsListRemoteDevices;

    private ObservableList<NotificationClassObject> obsListNCobjects;

    private ObservableList<DestinationObject> obsListDestinations;


    private void loadRemoteDevices() {
        try{
            List<BACnetDevice> bacnetDevices = ownDeviceModel.getBacnetDevices();
            obsListRemoteDevices = FXCollections.observableArrayList(bacnetDevices);

            deviceNameColumn.setCellValueFactory(new PropertyValueFactory<BACnetDevice, String>("device"));
            idColumn.setCellValueFactory(new PropertyValueFactory<BACnetDevice, String>("id"));

            remoteDeviceTableView.setItems(obsListRemoteDevices);
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

        List<NotificationClassObject> notis = baCnetDevice.notificationClassObjects;
        obsListNCobjects = FXCollections.observableArrayList(notis);
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
                        Image imageDesti = new Image(getClass().getResourceAsStream("/com.virusapp/destiMenu.png"));
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
        notifiTableView.setItems(obsListNCobjects);
    }

    private void loadDestinationObjects(NotificationClassObject notificationClassObject){
        List<DestinationObject> destinations = notificationClassObject.getRecipientList();
        obsListDestinations = FXCollections.observableArrayList(destinations);
        DestinationListController destinationListController = new DestinationListController(obsListDestinations);

       for (DestinationObject destinationObject : obsListDestinations) {
           System.out.println("destis =");
           System.out.println(destinationObject.getDeviceID());
           System.out.println(destinationObject.getProcessIdentifierID());
           System.out.println("*******************************");

       }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    loadRemoteDevices();

    writeToDevice.setOnAction(event -> Main.ownDevice.deleteDestinationOnAllNC(145001));




    }
}
