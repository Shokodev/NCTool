package com.virusapp.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.*;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.virusapp.Main;

import java.util.LinkedList;
import java.util.List;

public class DeviceService extends LocalDevice {
    public static LocalDevice localDevice;
    private List<RemoteDevice> remoteDevices = new LinkedList<>();

    public DeviceService(int deviceNumber, Transport transport) {
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
        scanAndAddAllNCObjects();
        System.out.println("stop");

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
     * Create a destination object to send to notificationClass objects in remote devices
     * @return destination as required
     */
    private Destination creatAlarmDestination(){
        Recipient recipient = new Recipient(localDevice.getId());
        EventTransitionBits eventTransitionBits = new EventTransitionBits(true,true,true);
        return new Destination(recipient,new UnsignedInteger(1), Boolean.TRUE,eventTransitionBits);
    }

    /**
     * Looks in all remote devices for notificationsClass objects ad set the LocalDevice as receiver destination
     *
    private void setLocalDeviceAsAlarmReceiver() {
        if (localDevice.getRemoteDevices().size() > 0){
            for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {
                List<ObjectIdentifier> oids = null;
                try {
                    oids = ((SequenceOf<ObjectIdentifier>)
                            RequestUtils.sendReadPropertyAllowNull(
                                    localDevice, remoteDevice, remoteDevice.getObjectIdentifier(),
                                    PropertyIdentifier.objectList)).getValues();
                } catch (BACnetException bac1){
                    System.err.println("No objects in " + remoteDevice + "found");
                }

                if (oids != null ) {
                    for (ObjectIdentifier oid : oids) {
                        if (oid.getObjectType().equals(ObjectType.notificationClass)){
                            WritePropertyRequest request = new WritePropertyRequest(oid, PropertyIdentifier.recipientList, null, creatAlarmDestination(), new UnsignedInteger(16));

                            try {
                                localDevice.send(remoteDevice, request);
                            }catch(BACnetException bac2){
                                System.err.println("No notification classes found at remote " + remoteDevice);
                            }
                            System.out.println("LocalDevice " + localDevice.getInstanceNumber() + " as receiver registered to: " + oid.toString() + " @ " + remoteDevice.getObjectIdentifier());

                        }}}
            }
        } else {
            System.err.println("No destinations added");
        }

    }*/

    /**
     * Reads and save more information about each remote device
    private void getRemoteDeviceInformation() {

        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {

            try {
                ObjectIdentifier oid = remoteDevice.getObjectIdentifier();

                ReadPropertyAck ack = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.protocolServicesSupported));
                remoteDevice.setServicesSupported((ServicesSupported) ack.getValue());

                ack = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.objectName));
                remoteDevice.setName(ack.getValue().toString());

                ack = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.protocolVersion));
                remoteDevice.setProtocolVersion((UnsignedInteger) ack.getValue());

                ack = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.protocolRevision));
                remoteDevice.setProtocolRevision((UnsignedInteger) ack.getValue());

                ack = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.description));
                remoteDevice.setVendorName(ack.getValue().toString());

                ack = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.applicationSoftwareVersion));
                remoteDevice.setApplicationSoftwareVersion(ack.getValue().toString());

            } catch (BACnetException bac){
                System.err.println("Cant read remote device information of" + remoteDevice.getVendorName());
            }
        }
    }

    *//**
     * Reset local device if creating is called again in runtime
     *//*
    private void rebaseLocalDeviceIfExists(){
        if(localDevice != null){
            localDevice.terminate();
            System.out.println("*********************Reset*********************");

        }
    }

    //**
     * Reads all BACnet Objects of all remote devises
    */
    private void scanAndAddAllNCObjects(){

        for (RemoteDevice remoteDevice : getRemoteDevices()) {
            try {
                List<ObjectIdentifier> oids = ((SequenceOf<ObjectIdentifier>)
                        RequestUtils.sendReadPropertyAllowNull(
                                Main.deviceService, remoteDevice, remoteDevice.getObjectIdentifier(),
                                PropertyIdentifier.objectList)).getValues();
                for (ObjectIdentifier oid : oids) {
                    if(oid.getObjectType().equals(ObjectType.notificationClass)){
                    BACnetObject bacnetObject = new BACnetObject(oid,remoteDevice);
                    remoteDevice.setObject(bacnetObject);
                    System.out.println(bacnetObject.getObjectName());
                    }
                }} catch (BACnetException | NullPointerException e) {
                System.out.println("Failed to read objects");
            }
        }
    }

}
