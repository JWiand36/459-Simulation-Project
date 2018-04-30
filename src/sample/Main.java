package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane pane = new BorderPane();
    private Controller controller = new Controller(pane);

    @Override
    public void start(Stage primaryStage) throws Exception{


        primaryStage.setTitle("459 Project");
        primaryStage.setScene(new Scene(pane, 1300, 500));
        primaryStage.show();
    }

    //Terminates the Thread when the system is closed.
    @Override
    public void stop(){
        controller.reset();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
