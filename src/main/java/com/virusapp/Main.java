package com.virusapp;

import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.virusapp.bacnet.OwnDevice;
import com.virusapp.controller.NCcontroller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Version 1.0
 * @Author Andreas Vogt, Daniel Reiter & Rafael Grimm
 * @LICENCE Copyright (C)
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 */
public class Main extends Application {

    static final Logger LOG = LoggerFactory.getLogger(Main.class);
    public static OwnDevice ownDevice;
    private static Scene scene;

    private static Parent loadFXML(String fxml) throws Exception {
        NCcontroller ncController = new NCcontroller(ownDevice);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com.virusapp/" + fxml + ".fxml"));
        fxmlLoader.setController(ncController);
        return fxmlLoader.load();
    }

    @Override
    public void start(Stage stage) {
        IpNetworkBuilder ipNetworkBuilder = new IpNetworkBuilder();
        ipNetworkBuilder.withLocalBindAddress(IpNetwork.DEFAULT_BIND_IP);
        ipNetworkBuilder.withBroadcast("255.255.255.255", IpNetwork.BVLC_TYPE);
        ipNetworkBuilder.withPort(47808);
        DefaultTransport defaultTransport = new DefaultTransport(ipNetworkBuilder.build());
        ownDevice = new OwnDevice(1000009, defaultTransport);
        ownDevice.createLocalDevice();
        LOG.debug("Device created " + System.currentTimeMillis());

        //FXML start
        try {
            scene = new Scene(loadFXML("NCcontroller"));
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Cant load scene");
        }
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    Platform.exit();
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
