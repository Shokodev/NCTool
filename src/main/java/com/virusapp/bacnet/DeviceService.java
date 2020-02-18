package com.virusapp.bacnet;


import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public class DeviceService {

    public void createLocalDevice()  {
        IpNetworkBuilder ipNetworkBuilder = new IpNetworkBuilder();
        ipNetworkBuilder.withLocalBindAddress(IpNetwork.DEFAULT_BIND_IP);
        ipNetworkBuilder.withBroadcast("255.255.255.255",IpNetwork.BVLC_TYPE);
        DefaultTransport transport = new DefaultTransport(ipNetworkBuilder.build());
        int localDevice_ID = 10001;
        Listener listener = new Listener();
        LocalDevice localDevice = new LocalDevice(localDevice_ID, transport);
        localDevice.getEventHandler().addListener(listener);
        try {
            localDevice.initialize();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        localDevice.sendGlobalBroadcast(new WhoIsRequest());
        // Wait a bit for responses to come in.
        getRemoteDeviceInformation(localDevice);
}
    private static void getRemoteDeviceInformation(LocalDevice localDevice) {

        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {
            System.out.println("Device with ID: " +remoteDevice.getInstanceNumber());
            ObjectIdentifier oid = remoteDevice.getObjectIdentifier();
            ReadPropertyAck ack0 = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.objectName));
            System.out.println("Object name:" + ack0.getValue());
            ReadPropertyAck ack1 = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.description));
            System.out.println("Description: " + ack1.getValue());
            ReadPropertyAck ack2 = (ReadPropertyAck) localDevice.send(remoteDevice, new ReadPropertyRequest(oid, PropertyIdentifier.protocolVersion));
            System.out.println("Protocol version:" + ack2.getValue());

        }
    }
}
