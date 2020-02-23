package com.virusapp.bacnet;


import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.Recipient;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.virusapp.Main;

public class DestinationObject {

    //frontend
    private String processIdentifierID;
    private String deviceID;

    //backend
    private NotificationClassObject noti;
    private Destination destination;

    public DestinationObject(Destination destination, NotificationClassObject noti){
        this.processIdentifierID = String.valueOf(destination.getProcessIdentifier().intValue());
        this.deviceID = (String.valueOf(destination.getRecipient().getDevice().getInstanceNumber()));
        this.noti = noti;
        this.destination = destination;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getProcessIdentifierID(){
        return processIdentifierID;
    }

    public void writeDestination(Integer deviceID)  {
        Recipient recipient = new Recipient(new ObjectIdentifier(ObjectType.device,deviceID));
        Destination destination = new Destination(recipient,new UnsignedInteger(Integer.parseInt(processIdentifierID)), Boolean.TRUE,new EventTransitionBits(true,true,true));
        sendWriteRequest(destination);
    }

    public void writeProcessIdentifier(Integer processIdentifier)  {
        Destination destination = new Destination(this.destination.getRecipient(),new UnsignedInteger(processIdentifier), Boolean.TRUE,new EventTransitionBits(true,true,true));
        sendWriteRequest(destination);
    }

    public void sendWriteRequest(Destination destination){
        try {
            RequestUtils.writeProperty(Main.ownDevice,noti.getBacnetDevice().getBacNetDeviceInfo(),noti.getObjectIdentifier(),PropertyIdentifier.recipientList,destination,8);
        } catch (BACnetException e) {
            System.err.println("Could not write destination: " + destination.getRecipient().toString() + "at " + noti.getObjectIdentifier() + " on " + noti.getBacnetDevice().bacNetDeviceInfo.getName());
        }
    }


    public Destination getDestination() {
        return destination;
    }
}
