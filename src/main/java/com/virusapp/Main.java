package com.virusapp;

import com.virusapp.bacnet.DeviceService;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        DeviceService deviceService = new DeviceService();
        deviceService.createLocalDevice();
    }



}
