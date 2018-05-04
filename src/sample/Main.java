package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(5));

        controller = new Controller(pane);

        primaryStage.setTitle("459 Project");
        primaryStage.setScene(new Scene(pane, 1500, 500));
        primaryStage.show();
    }

    //Terminates the Thread when the system is closed.
    @Override
    public void stop() {
        controller.reset();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
