package sample;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

class InfoPane{

    private VBox pane = new VBox();

    private Text timeTxt = new Text("Time: ");

    InfoPane(){
        pane.getChildren().add(timeTxt);
    }

    VBox display(){
        return pane;
    }

    public void setTime(int time){
        timeTxt.setText("Time: "+time);
    }


//    •	λ: arrival rate,
//•	μ: service rate of one server,
//            •	ρ: server utilization,
//•	An: interarrival time between customers n-1 and n (n = first five customers)
//•	Sn: service time of the nth arriving customer (n = first five customers)
//•	L(t): the number of customers in system at time t (t = all 60 minutes)
//•	LQ(t): the number of customers in queue at time t (t = all 60 minutes)
//•	L: long-run time-average number of customers in system
//•	LQ: long-run time-average number of customers in queue
//•	w: long-run average time spent in system per customer
//•	wQ: long-run average time spent in queue per customer

}
