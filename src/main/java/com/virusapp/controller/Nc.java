package com.virusapp.controller;

import com.serotonin.bacnet4j.RemoteDevice;
import com.virusapp.bacnet.BACnetDevice;
import com.virusapp.bacnet.NotificationClassObject;
import com.virusapp.bacnet.OwnDevice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TableColumn<NotificationClassObject, String> notificationClassColumn;
    @FXML
    private TableColumn<NotificationClassObject, String> recipientListColumn;



    private ObservableList<BACnetDevice> obsListRemoteDevices;

    private ObservableList<NotificationClassObject> destinations;


    private void loadDestinationList() {
        try{

            List<BACnetDevice> bacnetDevices = ownDeviceModel.getBacnetDevices();
            obsListRemoteDevices = FXCollections.observableArrayList(bacnetDevices);

            List<NotificationClassObject> notis = ownDeviceModel.getBacnetDevices().get(0).notificationClassObjects;
            destinations = FXCollections.observableArrayList(notis);

            deviceNameColumn.setCellValueFactory(new PropertyValueFactory<BACnetDevice, String>("device"));
            idColumn.setCellValueFactory(new PropertyValueFactory<BACnetDevice, String>("id"));

            //Notifi Table
            notificationTableColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("oid"));
            objectNameColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("name"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("description"));
            notificationClassColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("notificationClass"));
            recipientListColumn.setCellValueFactory(new PropertyValueFactory<NotificationClassObject, String>("recipientList"));




            notifiTableView.setItems(destinations);
            remoteDeviceTableView.setItems(obsListRemoteDevices);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    loadDestinationList();


    }
}
