package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.virusapp.Main;

public class Listener extends DeviceEventAdapter {

    @Override
    public void iAmReceived(RemoteDevice d) {
        super.iAmReceived(d);
        Main.deviceService.getRemoteDevices().add(d);
        System.out.println(d);
    }
}
