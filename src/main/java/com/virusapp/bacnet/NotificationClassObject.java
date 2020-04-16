package com.virusapp.bacnet;

import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.virusapp.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Version 1.0
 * @Author Andreas Vogt, Daniel Reiter & Rafael Grimm
 * @LICENCE Copyright (C)
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 */
public class NotificationClassObject extends RemoteObject {

    static final Logger LOG = LoggerFactory.getLogger(NotificationClassObject.class);

    //backend
    private final BACnetDevice bacnetDevice;
    private final ObservableList<DestinationObject> recipientList = FXCollections.observableArrayList();

    //frontend
    private final String description;
    private final Integer notificationClass;
    private final String name;
    private Integer prioToOffNormal;
    private Integer prioToFault;
    private Integer prioToNormal;

    public NotificationClassObject(ObjectIdentifier oid, BACnetDevice bacnetDevice) {
        super(App.ownDevice, oid);
        this.bacnetDevice = bacnetDevice;
        recipientList.clear();
        readPriority();
        readDestinations();
        this.name = getObjectName();
        this.notificationClass = Integer.parseInt(readProperty(PropertyIdentifier.notificationClass));
        this.description = readProperty(PropertyIdentifier.description);
    }


    //backend
    @Override
    public String getObjectName() {
        try {
            return RequestUtils.readProperty(App.ownDevice, bacnetDevice.getBacNetDeviceInfo(), super.getObjectIdentifier(), PropertyIdentifier.objectName, null).toString();
        } catch (BACnetException e) {
            LOG.warn("Could not read objectName of:" + super.getObjectIdentifier());
        }
        return "COM";
    }

    public BACnetDevice getBacnetDevice() {
        return bacnetDevice;
    }

    public ObservableList<DestinationObject> getRecipientList() {
        return recipientList;
    }

    private void readDestinations() {
        try {
            List<Destination> destinations = ((SequenceOf<Destination>)
                    RequestUtils.sendReadPropertyAllowNull(
                            App.ownDevice, bacnetDevice.getBacNetDeviceInfo(), super.getObjectIdentifier(),
                            PropertyIdentifier.recipientList)).getValues();
            for (Destination destination : destinations) {
                DestinationObject destinationObject = new DestinationObject(destination, this);
                recipientList.add(destinationObject);
            }
        } catch (BACnetException e) {
            LOG.warn("Could not read recipient of:" + super.getObjectIdentifier());
        }
    }

    private String readProperty(PropertyIdentifier propertyIdentifier) {
        try {
            return RequestUtils.readProperty(App.ownDevice, bacnetDevice.getBacNetDeviceInfo(), super.getObjectIdentifier(), propertyIdentifier, null).toString();
        } catch (BACnetException e) {
            LOG.warn("Could not read objectName of:" + super.getObjectIdentifier());
        }
        return "COM";
    }

    private void readPriority() {
        try {
            // Index is setteld by BACnet
            this.prioToOffNormal = Integer.parseInt(RequestUtils.readProperty(App.ownDevice,
                    bacnetDevice.getBacNetDeviceInfo(), super.getObjectIdentifier(),
                    PropertyIdentifier.priority, new UnsignedInteger(1)).toString());
            this.prioToFault = Integer.parseInt(RequestUtils.readProperty(App.ownDevice,
                    bacnetDevice.getBacNetDeviceInfo(), super.getObjectIdentifier(),
                    PropertyIdentifier.priority, new UnsignedInteger(2)).toString());
            this.prioToNormal = Integer.parseInt(RequestUtils.readProperty(App.ownDevice,
                    bacnetDevice.getBacNetDeviceInfo(), super.getObjectIdentifier(),
                    PropertyIdentifier.priority, new UnsignedInteger(3)).toString());
        } catch (BACnetException e) {
            LOG.warn("Could not read priority of:" + super.getObjectIdentifier());
        }
    }


    // frontend
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

    public Integer getPrioToOffNormal() {
        return prioToOffNormal;
    }

    public Integer getPrioToFault() {
        return prioToFault;
    }

    public Integer getPrioToNormal() {
        return prioToNormal;
    }


}
