package com.virusapp.controller;

import com.serotonin.bacnet4j.RemoteDevice;
import com.virusapp.bacnet.OwnDevice;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Nc implements Initializable {

    private OwnDevice ownDeviceModel;

    public Nc(OwnDevice model) {
        this.ownDeviceModel = model;
    }

    @FXML
    private TextField instanceNumber;
    @FXML
    private Button writeToDevice;


    private List<RemoteDevice> destinations;
    private OwnDevice ownDevice;



    private void loadDestinationList() {
        try{
             //Load Destination list

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    loadDestinationList();


    }
}
