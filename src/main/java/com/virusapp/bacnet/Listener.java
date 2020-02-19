package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.virusapp.Main;

public class Listener extends DeviceEventAdapter {

    @Override
    public void iAmReceived(RemoteDevice remoteDevice) {
        super.iAmReceived(remoteDevice);
        Main.ownDevice.getRemoteDevices().add(remoteDevice);
    }
}
