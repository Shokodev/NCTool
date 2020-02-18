package com.virusapp;

import com.virusapp.bacnet.DeviceService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        DeviceService deviceService = new DeviceService();
        deviceService.createLocalDevice();

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
