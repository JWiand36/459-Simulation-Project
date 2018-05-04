package sample;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

class InfoPane{

    private ScrollPane scroll = new ScrollPane();
    private VBox pane = new VBox(5);

    private Text timeTxt = new Text("Time: ");
    private Text arrivalRate = new Text("Arrival Rate: ");
    private Text serverRate = new Text("Server Rate: ");
    private Text serverUtil = new Text("Server Utilization: ");
    private Text customersInS = new Text("Long-run time-average of Customers in the System: ");
    private Text customersInQ = new Text("Long-run time-average of Customers in the Queue: ");
    private Text timeAverageQ = new Text("Average time spent in the Queue per Customer: ");
    private Text timeAverageS = new Text("Average time spent in the System per Customer: ");

    private Text[] interarrivalRate = new Text[5]; //For the first 5 customers
    private Text[] serverTime = new Text[5]; //For the first 5 customers
    private Text[] customersInSystem;
    private Text[] customersInQueue;

    InfoPane(){

        pane.getChildren().addAll(timeTxt, arrivalRate, serverRate, serverUtil);
        pane.setPadding(new Insets(5));

        for(int i = 1; i <= interarrivalRate.length; i++){

            interarrivalRate[i-1] = new Text("Interarrival rate of Customer "+i+": ");
            serverTime[i-1] = new Text("Server time for Customer "+i+": ");
            pane.getChildren().addAll(interarrivalRate[i-1],serverTime[i-1]);
        }

        pane.getChildren().addAll(customersInS, customersInQ, timeAverageS, timeAverageQ);

        scroll.setMinWidth(400);
        scroll.setContent(pane);
    }

    ScrollPane display(){
        return scroll;
    }

    void setTime(int time){
        timeTxt.setText("Time: "+time);
    }

    void setArrival(double arrival){ arrivalRate.setText(String.format("Arrival Rate: %1$.2f", arrival));}

    void setServerRate(double server){ serverRate.setText(String.format("Server Rate: %1$.2f", server));}

    void setServerUtil(int server){ serverUtil.setText("Server Utilization: "+ server);}

    void setCustomersInS(double customerIn){ customersInS.setText(String.format("Long-run time-average of Customers in the System: %1$.2f", customerIn));}

    void setCustomersInQ(double customerIn){ customersInQ.setText(String.format("Long-run time-average of Customers in the Queue: %1$.2f", customerIn));}

    void setTimeAverageS(double time){ timeAverageS.setText(String.format("Average time spent in the System per Customer: %1$.2f", time));}

    void setTimeAverageQ(double time){ timeAverageQ.setText(String.format("Average time spent in the Queue per Customer: %1$.2f", time));}

    void initInfoPane(int timeLength){
        customersInSystem = new Text[timeLength];
        customersInQueue = new Text[timeLength];

        for(int i = 0; i < timeLength; i ++){
            customersInSystem[i] = new Text("Number of Customers in the System at time "+i+": ");
            customersInQueue[i] = new Text("Number of Customers in the Queue at time "+i+": ");
        }

        for(Text text: customersInQueue)
            pane.getChildren().add(text);

        for(Text text: customersInSystem)
            pane.getChildren().add(text);

    }

    void setInnerArrivalRate(int[] arrivalRate, int[] serviceRate){
        for(int i = 1; i <= interarrivalRate.length; i++){
            interarrivalRate[i-1].setText("Interarrival rate of Customer "+i+": " + arrivalRate[i-1]);
            serverTime[i-1].setText("Server time for Customer "+i+": " + serviceRate[i-1]);
        }
    }

    void setCustomersInSystem(int[] customersIn) {
        for(int i = 1; i <= customersIn.length; i++)
            customersInSystem[i-1].setText("Number of Customers in the System at time "+i+": "+ customersIn[i-1]);
    }

    void setCustomersInQueue(int[] customersIn) {
        for(int i = 1; i <= customersIn.length; i++)
            customersInQueue[i-1].setText("Number of Customers in the Queue at time "+i+": "+ customersIn[i-1]);
    }
}
