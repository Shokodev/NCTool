package com.virusapp;

import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.virusapp.bacnet.OwnDevice;
import com.virusapp.controller.NCcontroller;
import com.virusapp.controller.StartController;
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
public class App extends Application {

    static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static OwnDevice ownDevice;


    @Override
    public void start(Stage stage) {
        loadStartView();
        ownDevice.createLocalDevice();
        LOG.debug("Device created " + System.currentTimeMillis());
        loadNcView(stage);
    }

    private void loadStartView() {
        try {
            StartController startController = new StartController();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/view/Start.fxml"));
            fxmlLoader.setController(startController);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Cant load scene");
        }
    }

    private void loadNcView(Stage stage) {

        try {
            NCcontroller ncController = new NCcontroller(ownDevice);
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/view/NCcontroller.fxml"));
            fxmlLoader.setController(ncController);
            Scene scene = new Scene(fxmlLoader.load());
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

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Cant load scene");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
