package com.virusapp;

import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.virusapp.bacnet.OwnDevice;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @Version 1.0
 * @Author Andreas Vogt, Daniel Reiter & Rafael Grimm
 * @LICENCE Copyright (C)
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
public class Main extends Application {

    public static OwnDevice ownDevice;
    private static Scene scene;

    @Override
    public void start(Stage stage) {
        IpNetworkBuilder ipNetworkBuilder = new IpNetworkBuilder();
        ipNetworkBuilder.withLocalBindAddress(IpNetwork.DEFAULT_BIND_IP);
        ipNetworkBuilder.withBroadcast("255.255.255.255",IpNetwork.BVLC_TYPE);
        DefaultTransport defaultTransport = new DefaultTransport(ipNetworkBuilder.build());
        ownDevice = new OwnDevice(145001,defaultTransport);
        ownDevice.createLocalDevice();

        //FXML start
        try {
            scene = new Scene(loadFXML("Nc"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com.virusapp/" + fxml +".fxml"));
        return fxmlLoader.load();
    }



/*    public static void setRoot(String fxml) throws Exception {
        scene.setRoot(loadFXML(fxml));
    }*/


}
