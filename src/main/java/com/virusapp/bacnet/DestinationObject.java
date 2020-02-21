package com.virusapp.bacnet;


import com.serotonin.bacnet4j.type.constructed.Destination;
public class DestinationObject {

    //frontend
    private Integer processIdentifier;
    private Integer deviceID;

    public DestinationObject(Destination destination){
        this.processIdentifier = destination.getProcessIdentifier().intValue();
        this.deviceID = destination.getRecipient().getDevice().getInstanceNumber();
    }

    public Integer getDeviceID() {
        return deviceID;
    }

    public Integer getProcessIdentifierINT(){
        return processIdentifier;
    }
}
