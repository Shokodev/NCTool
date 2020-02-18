package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.util.List;

public class BACnetObject extends RemoteObject {

    private RemoteDevice remoteDevice;
    private ObjectType objectType;


    public BACnetObject(ObjectIdentifier oid, RemoteDevice remoteDevice) {
        super(DeviceService.localDevice,oid);
        this.remoteDevice = remoteDevice;
        this.objectType = oid.getObjectType();
    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public String getDescription() {
            return ((ReadPropertyAck) DeviceService.localDevice.send(remoteDevice, new ReadPropertyRequest(super.getObjectIdentifier(), PropertyIdentifier.description))).getValue().toString();

    }

    public Encodable getProperty(PropertyIdentifier propertyIdentifier) {
            return ((ReadPropertyAck)DeviceService.localDevice.send(remoteDevice, new ReadPropertyRequest(super.getObjectIdentifier(), propertyIdentifier))).getValue();


    }
}
