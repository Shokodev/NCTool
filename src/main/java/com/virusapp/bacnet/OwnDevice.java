package com.virusapp.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.*;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.virusapp.App;
import com.virusapp.application.AlertHelper;
import com.virusapp.controller.NCcontroller;
import com.virusapp.controller.StartController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Version 1.0
 * @Author Andreas Vogt, Daniel Reiter & Rafael Grimm
 * @LICENCE Copyright (C)
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
public class OwnDevice extends LocalDevice {
    private final ObservableList<BACnetDevice> bacnetDevices = FXCollections.observableArrayList();
    private final ObservableMap<String,Integer> summedDestinationsIDs = FXCollections.observableHashMap();
    private Listener listener;
    private final int scanSeconds;

    static final Logger LOG = LoggerFactory.getLogger(OwnDevice.class);

    public OwnDevice(int deviceNumber, Transport transport, int scanSeconds) {
        super(deviceNumber, transport);
        this.scanSeconds = scanSeconds;
    }

    public void createLocalDevice() {

        this.listener = new Listener();
        getEventHandler().addListener(listener);
        try {
            initialize();
            System.out.println("Successfully created LocalDevice " + getInstanceNumber());
            LOG.info("Successfully created LocalDevice " + getInstanceNumber());
        } catch (Exception e) {
            System.err.println("LocalDevice initialize failed, restart the application may solve this problem");
            LOG.error("LocalDevice initialize failed, restart the application may solve this problem");
        }
        scanForRemoteDevices();
    }

    /**
     *Send WhoIs request to the BACnet network
     */
    private void scanForRemoteDevices()  {
        LOG.info("Scan for remote devices.........");
        try {
            WhoIsRequest request = new WhoIsRequest();
            sendGlobalBroadcast(request);
            Thread.sleep(1000 * scanSeconds);//1000ms * int
            //End scan after 5s if no device is found
            if(bacnetDevices.isEmpty()){
                terminate();
                getEventHandler().removeListener(listener);
                LOG.warn("No remote devices found");
            } else {
                getEventHandler().removeListener(listener);
                LOG.info("{} BACnet devices finally registered at local device", bacnetDevices.size());
            }
        }catch(InterruptedException bac){
            LOG.info("Network scan failure, restart the application may solve this problem");
        }
    }

    /**
     * Checks list for remote devices at LocalDevice after network scan
     *
     * @return massage and boolean
     */
    private boolean alertNoDeviceFound() {
        if (getBacnetDevices().isEmpty()) {
            System.err.println("No remote devices found");
            AlertHelper.showAlert(Alert.AlertType.WARNING,"Achtung!","Keine Geräte gefunden!");
            return false;
        }
        return true;
    }

    public ObservableList<BACnetDevice> getBacnetDevices() {
        return bacnetDevices;
    }

    /**
     * Reads and save more information about each remote device
     */
    public void getRemoteDeviceInformation(BACnetDevice bacnetDevice) {
            try {
                bacnetDevice.getBacNetDeviceInfo().setDeviceProperty(PropertyIdentifier.objectName,
                        RequestUtils.readProperty(this, bacnetDevice.getBacNetDeviceInfo(),
                                bacnetDevice.getBacNetDeviceInfo().getObjectIdentifier(),
                                PropertyIdentifier.objectName, null));
            } catch (BACnetException e) {
                e.printStackTrace();
            }



    }

    public void setSummedDestinationsIDs(List<DestinationObject> destinations){
        for(DestinationObject destination : destinations){
            summedDestinationsIDs.put(destination.getDeviceID(),getCounterForSummedDestinationIDs(destination));
        }
    }

    public Integer getCounterForSummedDestinationIDs(DestinationObject destinationObject){
        if(summedDestinationsIDs.containsKey(destinationObject.getDeviceID())){
           return summedDestinationsIDs.get(destinationObject.getDeviceID()) + 1;
        }
        return 1;
    }

    /**
     * Reset local device if creating is called again in runtime
     */
    private void rebaseLocalDeviceIfExists() {
        if (App.ownDevice != null) {
            App.ownDevice.terminate();
            System.err.println("*********************Reset*********************");

        }
    }

    public void sendNewDestinationToAllNC(Integer deviceID) {
        for (BACnetDevice bacnetDevice : getBacnetDevices()) {
            for (NotificationClassObject notificationClassObject : bacnetDevice.getNotificationClassObjects()) {
                Recipient recipient = new Recipient(new ObjectIdentifier(ObjectType.device, deviceID));
                Destination destination = new Destination(recipient, new UnsignedInteger(1), Boolean.TRUE, new EventTransitionBits(true, true, true));
                sendAddDestinationRequest(destination,notificationClassObject);
            }
            scanAndAddAllNCObjects(bacnetDevice);
        }

    }

    public void deleteDestinationOnAllNC(Integer deviceID) {
        for (BACnetDevice bacnetDevice : getBacnetDevices()) {
            if(!bacnetDevice.getNotificationClassObjects().isEmpty()){
                for (NotificationClassObject notificationClassObject : bacnetDevice.getNotificationClassObjects()) {
                    for (DestinationObject destinationObject : notificationClassObject.getRecipientList()) {
                        if (destinationObject.getDeviceID().equals(String.valueOf(deviceID))) {
                            try {
                                RequestUtils.removeListElement(this,bacnetDevice.getBacNetDeviceInfo(),notificationClassObject.getObjectIdentifier(),PropertyIdentifier.recipientList,destinationObject.getDestination());
                            } catch (BACnetException e) {
                               System.out.println("Could not delete " + destinationObject.getDeviceID() + " at " + notificationClassObject);
                            }
                        }
                    }
                }
                System.out.println("Deleted");
            } else{
                System.out.println("No NC object on this device");
            }
            scanAndAddAllNCObjects(bacnetDevice);
        }

    }

    /**
     * Reads all BACnet Objects of all remote devises
    */
    public void scanAndAddAllNCObjects(BACnetDevice bacnetDevice) {
        bacnetDevice.getNotificationClassObjects().clear();
        try {
            List<ObjectIdentifier> oids = ((SequenceOf<ObjectIdentifier>)
                    RequestUtils.sendReadPropertyAllowNull(
                            this, bacnetDevice.getBacNetDeviceInfo(), bacnetDevice.getBacNetDeviceInfo().getObjectIdentifier(),
                            PropertyIdentifier.objectList)).getValues();
            for (ObjectIdentifier oid : oids) {
                if (oid.getObjectType().equals(ObjectType.notificationClass)) {
                    NotificationClassObject notificationClassObject = new NotificationClassObject(oid, bacnetDevice);
                    bacnetDevice.getNotificationClassObjects().add(notificationClassObject);
                    LOG.info("NC: " + notificationClassObject.getObjectName()
                            + " added for device " + bacnetDevice.getBacNetDeviceInfo().getName());
                    setSummedDestinationsIDs(notificationClassObject.getRecipientList());
                }
            }
        } catch (BACnetException e) {
            LOG.warn("Failed to read objects");
        }
    }

    public void sendAddDestinationRequest(Destination destination, NotificationClassObject notificationClassObject){
        try {
                RequestUtils.addListElement(this,notificationClassObject.getBacnetDevice().getBacNetDeviceInfo(),notificationClassObject.getObjectIdentifier(),PropertyIdentifier.recipientList,destination);
        } catch (BACnetException e) {
            System.err.println("Could not write destination: " + destination.getRecipient().toString() + "at " + notificationClassObject.getObjectIdentifier() + " on " + notificationClassObject.getBacnetDevice().bacNetDeviceInfo.getName());
        }
    }

    public int getScanSeconds() {
        return scanSeconds;
    }
}

