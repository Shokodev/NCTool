package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;


public class BACnetDevice  {

    public ObservableList<NotificationClassObject> notificationClassObjects = FXCollections.observableArrayList();
    public RemoteDevice bacNetDeviceInfo;

    public BACnetDevice(RemoteDevice bacNetDeviceInfo) {
        this.bacNetDeviceInfo = bacNetDeviceInfo;
    }

    public ObservableList<NotificationClassObject> getNotificationClassObjects() {
        return notificationClassObjects;
    }

    public RemoteDevice getBacNetDeviceInfo() {
        return bacNetDeviceInfo;
    }

    public int getId() {
        return bacNetDeviceInfo.getInstanceNumber();
    }

    public String getDevice() {
        return bacNetDeviceInfo.getName();
    }

}
