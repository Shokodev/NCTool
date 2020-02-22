package com.virusapp.bacnet;


import com.serotonin.bacnet4j.RemoteDevice;
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
    private Integer processIdentifier;
    private Integer deviceID;

    //backend
    private NotificationClassObject noti;
    private Destination destination;

    public DestinationObject(Destination destination, NotificationClassObject noti){
        this.processIdentifier = destination.getProcessIdentifier().intValue();
        this.deviceID = destination.getRecipient().getDevice().getInstanceNumber();
        this.noti = noti;
        this.destination = destination;
    }

    public Integer getDeviceID() {
        return deviceID;
    }

    public Integer getProcessIdentifierINT(){
        return processIdentifier;
    }


    public void writeDestination(Integer deviceID)  {
        Recipient recipient = new Recipient(new ObjectIdentifier(ObjectType.device,deviceID));
        Destination destination = new Destination(recipient,new UnsignedInteger(processIdentifier), Boolean.TRUE,new EventTransitionBits(true,true,true));
        try {
            RequestUtils.writeProperty(Main.ownDevice, noti.getBacnetDevice().bacNetDeviceInfo,noti.getObjectIdentifier(), PropertyIdentifier.recipientList,destination,8);
            Main.ownDevice.updateAfterPropertyWritten();
        } catch (BACnetException e) {
            System.err.println("Could not write destination: " + destination.getRecipient().toString() + "at " + noti.getObjectIdentifier() + " on " + noti.getBacnetDevice().bacNetDeviceInfo.getName());
        }
    }

    public void writeProcessIdentifier(Integer processIdentifier)  {
        Destination destination = new Destination(this.destination.getRecipient(),new UnsignedInteger(processIdentifier), Boolean.TRUE,new EventTransitionBits(true,true,true));
        try {
            RequestUtils.writeProperty(Main.ownDevice, noti.getBacnetDevice().bacNetDeviceInfo,noti.getObjectIdentifier(), PropertyIdentifier.recipientList,destination,8);
            Main.ownDevice.updateAfterPropertyWritten();
        } catch (BACnetException e) {
            System.err.println("Could not write destination: " + destination.getRecipient().toString() + "at " + noti.getObjectIdentifier() + " on " + noti.getBacnetDevice().bacNetDeviceInfo.getName());
        }
    }

}
