package com.virusapp;

import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.virusapp.bacnet.DeviceService;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static DeviceService deviceService;

    @Override
    public void start(Stage stage) {
        IpNetworkBuilder ipNetworkBuilder = new IpNetworkBuilder();
        ipNetworkBuilder.withLocalBindAddress(IpNetwork.DEFAULT_BIND_IP);
        ipNetworkBuilder.withBroadcast("255.255.255.255",IpNetwork.BVLC_TYPE);
        DefaultTransport defaultTransport = new DefaultTransport(ipNetworkBuilder.build());
        deviceService = new DeviceService(1001,defaultTransport);
        deviceService.createLocalDevice();
    }



}
