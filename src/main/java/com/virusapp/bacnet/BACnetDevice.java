package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.LinkedList;
import java.util.List;


public class BACnetDevice  {

    public int getId() {
        return bacNetDeviceInfo.getInstanceNumber();
    }

    public String getDevice() {
        return bacNetDeviceInfo.getName();
    }



    public List<NotificationClassObject> notificationClassObjects = new LinkedList<>();
    public RemoteDevice bacNetDeviceInfo;

    public BACnetDevice(RemoteDevice bacNetDeviceInfo) {
        this.bacNetDeviceInfo = bacNetDeviceInfo;
    }

    public List<NotificationClassObject> getNotificationClassObjects() {
        return notificationClassObjects;
    }

    public RemoteDevice getBacNetDeviceInfo() {
        return bacNetDeviceInfo;
    }
}
