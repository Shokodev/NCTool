package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.virusapp.Main;
import javafx.collections.FXCollections;

public class Listener extends DeviceEventAdapter {

    @Override
    public void iAmReceived(RemoteDevice remoteDevice) {
        BACnetDevice bacnetDevice = new BACnetDevice(remoteDevice);
        Main.ownDevice.getBacnetDevices().add(bacnetDevice);
    }

}
