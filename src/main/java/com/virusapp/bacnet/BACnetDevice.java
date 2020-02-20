package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;

import java.util.LinkedList;
import java.util.List;


public class BACnetDevice  {
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
