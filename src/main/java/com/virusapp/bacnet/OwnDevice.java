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
import com.virusapp.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Version 1.0
 * @Author Andreas Vogt, Daniel Reiter & Rafael Grimm
 * @LICENCE Copyright (C)
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
public class OwnDevice extends LocalDevice {
    private ObservableList<BACnetDevice> bacnetDevices = FXCollections.observableArrayList();
    static final Logger LOG = LoggerFactory.getLogger(OwnDevice.class);

    public OwnDevice(int deviceNumber, Transport transport) {
        super(deviceNumber, transport);
    }

    public void createLocalDevice() {
        Listener listener = new Listener();
        getEventHandler().addListener(listener);
        try {
            initialize();
        } catch (Exception e) {
            System.err.println("LocalDevice initialize failed, restart the application may solve this problem");
            LOG.error("LocalDevice initialize failed, restart the application may solve this problem");
        }
        System.out.println("Successfully created LocalDevice " + getInstanceNumber());
        LOG.debug("Successfully created LocalDevice " + getInstanceNumber());
        scanForRemoteDevices();
        getRemoteDeviceInformation();
        scanAndAddAllNCObjects();


    }

    /**
     * Send WhoIs request to the BACnet network
     */
    private void scanForRemoteDevices() {
        System.out.println("Scan for remote devices.........");
        try {
            WhoIsRequest request = new WhoIsRequest();
            sendGlobalBroadcast(request);
            Thread.sleep(1000 * 5);
            //End scan after 5s if no device is found
            if (!alertNoDeviceFound()) {
                terminate();
            }

        } catch (InterruptedException bac) {
            System.err.println("Network scan failure, restart the application may solve this problem");
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
    private void getRemoteDeviceInformation() {

        for (BACnetDevice bacnetDevice : getBacnetDevices()) {
            try {
                bacnetDevice.getBacNetDeviceInfo().setDeviceProperty(PropertyIdentifier.objectName,
                        RequestUtils.readProperty(this, bacnetDevice.getBacNetDeviceInfo(),
                                bacnetDevice.getBacNetDeviceInfo().getObjectIdentifier(),
                                PropertyIdentifier.objectName, null));
                System.out.println("Device " + bacnetDevice.getBacNetDeviceInfo().getName() + " discovered");
            } catch (BACnetException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Reset local device if creating is called again in runtime
     */
    private void rebaseLocalDeviceIfExists() {
        if (Main.ownDevice != null) {
            Main.ownDevice.terminate();
            System.err.println("*********************Reset*********************");

        }
    }

    public void updateAfterPropertyWritten() {
        scanForRemoteDevices();
        scanAndAddAllNCObjects();
    }

    public void sendNewDestinationToAllNC(Integer deviceID) {
        for (BACnetDevice bacnetDevice : getBacnetDevices()) {
            for (NotificationClassObject notificationClassObject : bacnetDevice.getNotificationClassObjects()) {
                Recipient recipient = new Recipient(new ObjectIdentifier(ObjectType.device, deviceID));
                Destination destination = new Destination(recipient, new UnsignedInteger(1), Boolean.TRUE, new EventTransitionBits(true, true, true));
                try {
                    RequestUtils.writeProperty(Main.ownDevice, notificationClassObject.getBacnetDevice().bacNetDeviceInfo, notificationClassObject.getObjectIdentifier(), PropertyIdentifier.recipientList, destination, 8);
                    Main.ownDevice.updateAfterPropertyWritten();
                } catch (BACnetException e) {
                    System.err.println("Could not write destination: " + destination.getRecipient().toString() + "at " + notificationClassObject.getObjectIdentifier() + " on " + notificationClassObject.getBacnetDevice().bacNetDeviceInfo.getName());
                }
            }


        }

    }

    public void deleteDestinationOnAllNC(Integer deviceID) {
        for (BACnetDevice bacnetDevice : getBacnetDevices()) {
            for (NotificationClassObject notificationClassObject : bacnetDevice.getNotificationClassObjects()) {
                for (DestinationObject destinationObject : notificationClassObject.getRecipientList()){
                    if(destinationObject.getProcessIdentifierINT().equals(deviceID)){
                        try {
                            RequestUtils.writeProperty(Main.ownDevice, notificationClassObject.getBacnetDevice().bacNetDeviceInfo, notificationClassObject.getObjectIdentifier(), PropertyIdentifier.recipientList, null, 8);
                            Main.ownDevice.updateAfterPropertyWritten();
                        } catch (BACnetException e) {
                            System.err.println("Could not delete destination at " + notificationClassObject.getObjectIdentifier() + " on " + notificationClassObject.getBacnetDevice().bacNetDeviceInfo.getName());
                        }
                    }
                }


            }


        }

    }

    /**
     * Reads all BACnet Objects of all remote devises
    */
    private void scanAndAddAllNCObjects(){
        for (BACnetDevice bacnetDevice : getBacnetDevices()) {
            try {
                List<ObjectIdentifier> oids = ((SequenceOf<ObjectIdentifier>)
                        RequestUtils.sendReadPropertyAllowNull(
                                this, bacnetDevice.getBacNetDeviceInfo(), bacnetDevice.getBacNetDeviceInfo().getObjectIdentifier(),
                                PropertyIdentifier.objectList)).getValues();
                for (ObjectIdentifier oid : oids) {
                    if(oid.getObjectType().equals(ObjectType.notificationClass)){
                    NotificationClassObject notificationClassObject = new NotificationClassObject(oid,bacnetDevice);
                    bacnetDevice.getNotificationClassObjects().add(notificationClassObject);
                    System.out.println("NC: " + notificationClassObject.getObjectName()
                            + " added for device " + bacnetDevice.getBacNetDeviceInfo().getName());

                    }

                }} catch (BACnetException e) {
                System.err.println("Failed to read objects");
            }
        }
    }

}
