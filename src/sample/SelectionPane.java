package sample;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

class SelectionPane extends VBox {

    private Button run = new Button("Run Simulation");
    private Button reset = new Button("Reset");
    private TextField simLength = new TextField("60");
    private TextField arriveRate = new TextField("5");
    private TextField selectRate = new TextField("6");
    private TextField serviceRate = new TextField("10");

    SelectionPane(Controller controller){

        Label simlengthLbl = new Label("Simulation Length");
        Label arriveRateLbl = new Label("Arrival Rate");
        Label selectRateLbl = new Label("Selection Rate");
        Label serviceRateLbl = new Label("Service Rate");


        run.setOnAction(e-> {
            if(isNumber(simLength.getText()) && isNumber(arriveRate.getText()) &&
                    isNumber(selectRate.getText()) && isNumber(serviceRate.getText())) {

                int length = Integer.parseInt(simLength.getText());
                int arrive = Integer.parseInt(arriveRate.getText());
                int select = Integer.parseInt(selectRate.getText());
                int service = Integer.parseInt(serviceRate.getText());

                controller.setValues(length, arrive, select, service);
            }

            controller.run(run);
        });

        reset.setOnAction(e-> controller.reset(reset));

        this.getChildren().addAll(simlengthLbl, simLength, arriveRateLbl,
                arriveRate, selectRateLbl, selectRate, serviceRateLbl,
                serviceRate, run, reset);
    }

    private boolean isNumber(String s){

        char a;
        int c = 0;

        for(int i = 0; i < s.length(); i++){

            a = s.charAt(i);

            if(!Character.isDigit(a))
                c++;
        }

        return c <= 0 && s.length() != 0;
    }

}
