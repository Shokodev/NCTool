package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.virusapp.Main;
import javafx.beans.property.SimpleStringProperty;

import java.util.LinkedList;
import java.util.List;

/**
 * @Version 1.0
 * @Author Andreas Vogt, Daniel Reiter & Rafael Grimm
 * @LICENCE Copyright (C)
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
public class NotificationClassObject extends RemoteObject {

    private BACnetDevice bacnetDevice;
    private List<Destination> recipientList = new LinkedList<>();
    private String description;
    private Integer notificationClass;
    private String name;




    public NotificationClassObject(ObjectIdentifier oid, BACnetDevice bacnetDevice) {
        super(Main.ownDevice, oid);
        this.bacnetDevice = bacnetDevice;
        this.name = getObjectName();
        this.notificationClass = Integer.parseInt(readProperty(PropertyIdentifier.notificationClass));
        this.description = readProperty(PropertyIdentifier.description);

    }

    public String getOid() {
        return getObjectIdentifier().toString();
    }

    public String getDescription() {
        return description;
    }
    public Integer getNotificationClass() {
        return notificationClass;
    }
    public String getName() {
        return name;
    }

    @Override
    public String getObjectName() {
        try {
            return RequestUtils.readProperty(Main.ownDevice, bacnetDevice.getBacNetDeviceInfo(),super.getObjectIdentifier(),PropertyIdentifier.objectName,null).toString();
        } catch (BACnetException e) {
            System.err.println("Could not read objectName of:" + super.getObjectIdentifier());
        }
        return "COM";
    }

    public BACnetDevice getBacnetDevice() {
        return bacnetDevice;
    }

    public List<Destination> getRecipientList() {
        return recipientList;
    }

    public void readDestinations(){
        try {
            recipientList.clear();
            List<Destination> destinations = ((SequenceOf<Destination>)
                    RequestUtils.sendReadPropertyAllowNull(
                            Main.ownDevice, bacnetDevice.getBacNetDeviceInfo(), super.getObjectIdentifier(),
                            PropertyIdentifier.recipientList)).getValues();
            recipientList.addAll(destinations);
        } catch (BACnetException e) {
            System.err.println("Could not read recipient of:" + super.getObjectIdentifier());
        }
    }

    private String readProperty(PropertyIdentifier propertyIdentifier){
        try {
            return RequestUtils.readProperty(Main.ownDevice, bacnetDevice.getBacNetDeviceInfo(),super.getObjectIdentifier(),propertyIdentifier,null).toString();
        } catch (BACnetException e) {
            System.err.println("Could not read objectName of:" + super.getObjectIdentifier());
        }
        return "COM";
    }

    //TODO Test effect of property array Index
    public void writeDestination(Destination destination, UnsignedInteger propertyArrayIndex)  {
        try {
            RequestUtils.writeProperty(Main.ownDevice, bacnetDevice.getBacNetDeviceInfo(),super.getObjectIdentifier(),PropertyIdentifier.recipientList,propertyArrayIndex);
        } catch (BACnetException e) {
            System.err.println("Could not write destination: " + destination.getRecipient().toString() + "at " + super.getObjectIdentifier() + " on " + bacnetDevice.getBacNetDeviceInfo().getName());
        }
    }

}
