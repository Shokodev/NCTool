package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;

public class Listener extends DeviceEventAdapter {

    @Override
    public void iAmReceived(RemoteDevice d) {
        super.iAmReceived(d);
        System.out.println("IAm received" + d);
    }
}
