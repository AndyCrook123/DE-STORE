package com.andrewcrook.destore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DestoreApplication extends Application {
    @Override
    public void start(Stage stage) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(DestoreApplication.class.getResource("/com/andrewcrook/destore/fxml/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setTitle("DE-STORE");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e){e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}