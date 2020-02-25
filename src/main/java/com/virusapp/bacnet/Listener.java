package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.virusapp.App;

public class Listener extends DeviceEventAdapter {

    @Override
    public void iAmReceived(RemoteDevice remoteDevice) {
        BACnetDevice bacnetDevice = new BACnetDevice(remoteDevice);
        App.ownDevice.getBacnetDevices().add(bacnetDevice);
    }

}
