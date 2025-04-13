package com.myungsuelectronics.personaldbwinjavafx.personaldbwindowsjavafx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class master extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Personal DB");

        // Create top labels
        Label titleLabel = new Label("Personal DB");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label subtitleLabel = new Label("By. MyungSu Electronics");
        subtitleLabel.setStyle("-fx-font-size: 16px;");

        VBox topBox = new VBox(5, titleLabel, subtitleLabel);
        topBox.setAlignment(Pos.CENTER);

        // Create center buttons
        Button newTableButton = new Button("New Table");
        Button loadTableButton = new Button("Load Table");
        Button listTableButton = new Button("List Table");

        VBox buttonBox = new VBox(10, newTableButton, loadTableButton, listTableButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Create bottom copyright label
        Label copyrightLabel = new Label("(C) MyungSu Electronics 2020-2024 All Rights Reserved.");
        copyrightLabel.setStyle("-fx-font-size: 12px;");
        VBox bottomBox = new VBox(copyrightLabel);
        bottomBox.setAlignment(Pos.CENTER);

        // Create the main layout
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(topBox);
        mainPane.setCenter(buttonBox);
        mainPane.setBottom(bottomBox);

        // Create the scene
        Scene scene = new Scene(mainPane, 400, 400);

        // Set the scene and center the stage
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();

        // Request focus upon launch
        primaryStage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

