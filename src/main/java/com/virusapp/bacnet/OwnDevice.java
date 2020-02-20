package com.virusapp.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.*;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.virusapp.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @Version 1.0
 * @Author Andreas Vogt, Daniel Reiter & Rafael Grimm
 * @LICENCE Copyright (C)
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
public class OwnDevice extends LocalDevice {
    private List<RemoteDevice> remoteDevices = new LinkedList<>();

    public OwnDevice(int deviceNumber, Transport transport) {
        super(deviceNumber, transport);
    }

    public void createLocalDevice() {
        Listener listener = new Listener();
        getEventHandler().addListener(listener);
        try {
            initialize();
        } catch(Exception e){
            System.err.println("LocalDevice initialize failed, restart the application may solve this problem");
        }
        System.out.println("Successfully created LocalDevice " + getInstanceNumber());
        scanForRemoteDevices();
        getRemoteDeviceInformation();
        scanAndAddAllNCObjects();


}

    /**
     *Send WhoIs request to the BACnet network
     */
    private void scanForRemoteDevices()  {
        System.out.println("Scan for remote devices.........");
        try {
            WhoIsRequest request = new WhoIsRequest();
            sendGlobalBroadcast(request);
            Thread.sleep(1000 * 5);
            //End scan after 5s if no device is found
            if(!alertNoDeviceFound()){
                terminate();
            }

        }catch(InterruptedException bac){
            System.err.println("Network scan failure, restart the application may solve this problem");
        }
    }

    /**
     * Checks list for remote devices at LocalDevice after network scan
     * @return massage and boolean
     */
    private boolean alertNoDeviceFound(){
        if (getRemoteDevices().isEmpty()){
            System.err.println("No remote devices found");
            return false;
        }
        return true;
    }

    @Override
    public List<RemoteDevice> getRemoteDevices() {
        return remoteDevices;
    }

    /**
     * Reads and save more information about each remote device
     */
    private void getRemoteDeviceInformation() {

        for (RemoteDevice remoteDevice : getRemoteDevices()) {
            try {
                remoteDevice.setDeviceProperty(PropertyIdentifier.objectName,RequestUtils.readProperty(this,remoteDevice,remoteDevice.getObjectIdentifier(),PropertyIdentifier.objectName,null));
                System.out.println("Device " + remoteDevice.getName() + " discovered");
            } catch (BACnetException e) {
                e.printStackTrace();
            }

        }
        }

    /**
     * Reset local device if creating is called again in runtime
     */
    private void rebaseLocalDeviceIfExists(){
        if(Main.ownDevice != null){
            Main.ownDevice.terminate();
            System.err.println("*********************Reset*********************");

        }
    }

    /**
     * Reads all BACnet Objects of all remote devises
    */
    private void scanAndAddAllNCObjects(){
        for (RemoteDevice remoteDevice : getRemoteDevices()) {
            try {
                List<ObjectIdentifier> oids = ((SequenceOf<ObjectIdentifier>)
                        RequestUtils.sendReadPropertyAllowNull(
                                this, remoteDevice, remoteDevice.getObjectIdentifier(),
                                PropertyIdentifier.objectList)).getValues();
                for (ObjectIdentifier oid : oids) {
                    if(oid.getObjectType().equals(ObjectType.notificationClass)){
                    NotificationClassObject notificationClassObject = new NotificationClassObject(oid,remoteDevice);
                    remoteDevice.setObject(notificationClassObject);
                    System.out.println("NC: " + notificationClassObject.getObjectName() + " added for device " + remoteDevice.getName());
                    notificationClassObject.readDestinations();
                    }
                }} catch (BACnetException e) {
                System.err.println("Failed to read objects");
            }
        }
    }

}
