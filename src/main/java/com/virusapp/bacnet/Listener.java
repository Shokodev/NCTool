package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.virusapp.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener extends DeviceEventAdapter {
    static final Logger LOG = LoggerFactory.getLogger(Listener.class);
    @Override
    public void iAmReceived(RemoteDevice remoteDevice) {
        LOG.info("New device discovered " + remoteDevice.getName());
        BACnetDevice bacnetDevice = new BACnetDevice(remoteDevice);
        App.ownDevice.getRemoteDeviceInformation(bacnetDevice);
        App.ownDevice.scanAndAddAllNCObjects(bacnetDevice);
        App.ownDevice.getBacnetDevices().add(bacnetDevice);
    }

}
