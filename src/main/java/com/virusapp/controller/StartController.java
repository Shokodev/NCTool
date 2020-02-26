package com.virusapp.controller;

import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.virusapp.App;
import com.virusapp.application.AlertHelper;
import com.virusapp.bacnet.OwnDevice;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    @FXML
    private TextField deviceID;
    @FXML
    private ComboBox<String> bacport;
    @FXML
    private javafx.scene.control.Button ok;
    @FXML
    private javafx.scene.control.Button cancel;

    private void create(){
        IpNetworkBuilder ipNetworkBuilder = new IpNetworkBuilder();
        ipNetworkBuilder.withLocalBindAddress(IpNetwork.DEFAULT_BIND_IP);
        ipNetworkBuilder.withBroadcast("255.255.255.255", IpNetwork.BVLC_TYPE);
        ipNetworkBuilder.withPort(Integer.parseInt(bacport.getValue()));
        DefaultTransport defaultTransport = new DefaultTransport(ipNetworkBuilder.build());
        App.ownDevice = new OwnDevice(Integer.parseInt(deviceID.getText()), defaultTransport);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bacport.setItems(FXCollections.observableArrayList("47808","47809","47810"));
        AlertHelper.denyString(deviceID);
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
               create();
               Stage stage = (Stage) ok.getScene().getWindow();
               stage.close();
            }
        });
        cancel.setOnAction(event -> System.exit(0));
    }

}
