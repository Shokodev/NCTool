package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.virusapp.Main;

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

    private RemoteDevice remoteDevice;
    private List<Destination> recipientList = new LinkedList<>();

    public NotificationClassObject(ObjectIdentifier oid, RemoteDevice remoteDevice) {
        super(Main.ownDevice,oid);
        this.remoteDevice = remoteDevice;
    }

    @Override
    public String getObjectName() {
        try {
            return RequestUtils.readProperty(Main.ownDevice,remoteDevice,super.getObjectIdentifier(),PropertyIdentifier.objectName,null).toString();
        } catch (BACnetException e) {
            System.err.println("Could not read objectName of:" + super.getObjectIdentifier());
        }
        return "COM";
    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }

    public List<Destination> getRecipientList() {
        return recipientList;
    }

    public void readDestinations(){
        try {
            List<Destination> destinations = ((SequenceOf<Destination>)
                    RequestUtils.sendReadPropertyAllowNull(
                            Main.ownDevice, remoteDevice, super.getObjectIdentifier(),
                            PropertyIdentifier.recipientList)).getValues();
            recipientList.clear();
            recipientList.addAll(destinations);
        } catch (BACnetException e) {
            System.err.println("Could not read recipient of:" + super.getObjectIdentifier());
        }
    }

    //TODO Test effect of property array Index
    public void writeDestination(Destination destination, UnsignedInteger propertyArrayIndex)  {
        try {
            RequestUtils.writeProperty(Main.ownDevice,remoteDevice,super.getObjectIdentifier(),PropertyIdentifier.recipientList,propertyArrayIndex);
        } catch (BACnetException e) {
            System.err.println("Could not write destination: " + destination.getRecipient().toString() + "at " + super.getObjectIdentifier() + " on " + remoteDevice.getName());
        }
    }

}
