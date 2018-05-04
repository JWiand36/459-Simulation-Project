package sample;


import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.PriorityQueue;

class Controller {

    private Pane visualPane = new Pane();
    private InfoPane infoPane;

    private Thread thread = new Thread();

    private int time;
    private int simulationLength = 60;
    private int selectionRate = 6;
    private int serviceRate = 6;
    private int arrivalRate = 5;
    private int serverUtil = 0;

    private int[] customerSystemTime;
    private int[] customerQueueTime;

    private boolean serverAvailable = true;


    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Customer> servedCustomers = new ArrayList<>();

    private Customer beingServedCustomer;

    private PriorityQueue<Customer> selectQueue = new PriorityQueue<>(Customer::compareTo);
    private PriorityQueue<Customer> queue = new PriorityQueue<>();

    Controller(BorderPane mainPane) {

        infoPane = new InfoPane();

        mainPane.setCenter(this.visualPane);
        mainPane.setLeft(new SelectionPane(this));
        mainPane.setRight(infoPane.display());

        initVisual();

        thread = setUpThread();
    }


    private void newCustomer(){
        int enterQueue = (int)(Math.random() * selectionRate) + 1 + time;

        Customer customer = new Customer(time, enterQueue);

        int y = ((customers.size()) * 40) % 400 + 45;
        int x = (int)Math.ceil((customers.size())/10) * 40 + 25;

        customer.setFill(Color.BLUEVIOLET);
        customer.move(x,y);

        customers.add(customer);
        visualPane.getChildren().add(customer);
        selectQueue.add(customer);

        System.out.println("New Customer!!");
    }

    private void enterQueue(Customer customer){
        int x;

        x = 495 - queue.size() * 40;

        customer.setFill(Color.RED);
        customer.move(x,100);
        customer.inQueue(true);

        queue.add(customer);
        System.out.println("Customer added to the Queue");
    }

    private void serve(Customer customer){

        customer.setFill(Color.YELLOW);
        customer.move(605,100);
        customer.inQueue(false);

        for(Customer custom: customers){
            if(custom.inQueue()){
                custom.move(custom.getX() + 40, 100);
            }
        }

        customer.setLeaveQueueTime(time);
        beingServedCustomer = customer;
        System.out.println("Serving Customer");
    }

    private void departure(Customer customer){
        int y = ((servedCustomers.size()) * 40) % 400 + 40;
        int x = (int)Math.ceil((servedCustomers.size())/10) * 40 + 720;

        customer.setFill(Color.GREEN);
        customer.move(x,y);
        customer.setDepartureTime(time);
        servedCustomers.add(customer);
        System.out.println("Customer is leaving");
    }

    void reset(){
        thread.interrupt();

        for(Customer customer: customers)
            visualPane.getChildren().removeAll(customer);

        serverAvailable = true;

        while(!selectQueue.isEmpty())
            selectQueue.poll();

        while(!queue.isEmpty())
            queue.poll();

        customers.clear();
        servedCustomers.clear();
        time = 0;
        serverUtil = 0;
        infoPane.setTime(0);
    }

    void run(Button button){

        button.setOnAction( e->{

            reset();
            thread = setUpThread();
            thread.start();

        });
    }

    void reset(Button button){
        button.setOnAction( e-> reset());
    }

    void setValues(int simulationLength, int arrivalRate, int selectionRate, int serviceRate){
        customerSystemTime = new int[simulationLength];
        customerQueueTime = new int[simulationLength];
        this.simulationLength = simulationLength;
        this.arrivalRate = arrivalRate;
        this.selectionRate = selectionRate;
        this.serviceRate = serviceRate;
        infoPane.initInfoPane(simulationLength);
    }

    private void calculateValues(){

        int aRate = 0;
        int sRate = 0;
        int avgSystemTime = 0;
        int avgQueueTime = 0;
        int counter = 0;
        int timeAvgSystem = 0;
        int timeAvgQueue = 0;
        int[] arriveTime5 = new int[5];
        int[] serviceTime5 = new int[5];

        for(int i = 0; i < customers.size(); i++){
            if(i != 0) {
                aRate += customers.get(i).getArrivalTime() - customers.get(i - 1).getArrivalTime();
            }else {
                aRate += customers.get(i).getArrivalTime();
            }

            if(i < arriveTime5.length) {
                if (i != 0)
                    arriveTime5[i] = customers.get(i).getArrivalTime() - customers.get(i - 1).getArrivalTime();
                else
                    arriveTime5[i] = customers.get(i).getArrivalTime();
            }

            if(customers.get(i).getLeaveQueueTime() - customers.get(i).getEnterQueueTime() > 0) {
                avgQueueTime += customers.get(i).getLeaveQueueTime() - customers.get(i).getEnterQueueTime();
                counter++;
            }
        }

        for(int i = 0; i < servedCustomers.size(); i++){
            sRate += servedCustomers.get(i).getDepartureTime() - servedCustomers.get(i).getLeaveQueueTime();

            if(i < serviceTime5.length)
                serviceTime5[i] = servedCustomers.get(i).getDepartureTime() - servedCustomers.get(i).getLeaveQueueTime();

            avgSystemTime += servedCustomers.get(i).getDepartureTime() - servedCustomers.get(i).getArrivalTime();
        }

        for(Integer numCustomer: customerSystemTime)
            timeAvgSystem += numCustomer;


        for(Integer numCustomer: customerQueueTime)
            timeAvgQueue += numCustomer;

        if(customers.size() != 0)
            aRate /= customers.size();

        if(servedCustomers.size() != 0) {
            sRate /= servedCustomers.size();
            avgSystemTime /= servedCustomers.size();
        }

        if(customerSystemTime.length != 0)
            timeAvgSystem /= customerSystemTime.length;

        if(customerQueueTime.length != 0)
            timeAvgQueue /= customerQueueTime.length;

        if(counter != 0)
            avgQueueTime /= counter;


        infoPane.setArrival(aRate);
        infoPane.setServerRate(sRate);
        infoPane.setServerUtil(serverUtil);
        infoPane.setTimeAverageS(avgSystemTime);
        infoPane.setTimeAverageQ(avgQueueTime);
        infoPane.setCustomersInS(timeAvgSystem);
        infoPane.setCustomersInQ(timeAvgQueue);
        infoPane.setCustomersInSystem(customerSystemTime);
        infoPane.setCustomersInQueue(customerQueueTime);
        infoPane.setInnerArrivalRate(arriveTime5, serviceTime5);
    }

    private void initVisual(){

        Rectangle serverBox = new Rectangle(580,75, 50, 50);
        serverBox.setFill(Color.BLACK);

        Text serverLbl = new Text("Server");
        serverLbl.setX(585);
        serverLbl.setY(55);

        Text queueLbl = new Text("Queue");
        queueLbl.setX(400);
        queueLbl.setY(55);

        Text selectingLbl = new Text("Selecting Goods");
        selectingLbl.setX(15);
        selectingLbl.setY(15);

        Text departedLbl = new Text("Departed");
        departedLbl.setX(715);
        departedLbl.setY(15);

        visualPane.getChildren().addAll(serverBox, serverLbl, queueLbl, selectingLbl, departedLbl);

    }

    private Thread setUpThread(){
        return new Thread(new Runnable(){

            @Override
            public void run() {
                int arrival  = (int)(Math.random() * arrivalRate) + 1;
                int finishedServiceTime = 0;

                System.out.println("Started");

                for(time = 1; time <= simulationLength; time++){

                    Platform.runLater(()->infoPane.setTime(time));

                    if(time == arrival) {
                        Platform.runLater(() -> newCustomer());
                        arrival = (int)(Math.random() * arrivalRate) + 1 + time;
                    }

                    if(!selectQueue.isEmpty() && time >= selectQueue.peek().getEnterQueueTime()) {
                        Platform.runLater(() -> enterQueue(selectQueue.poll()));
                    }

                    if(!serverAvailable && time >= finishedServiceTime) {
                        departure(beingServedCustomer);
                        serverAvailable = true;
                    }

                    if(serverAvailable && !queue.isEmpty()){
                        serve(queue.poll());
                        finishedServiceTime = (int)(Math.random() * serviceRate) + 1 + time;
                        serverAvailable = false;
                    }

                    if(!serverAvailable)
                        serverUtil++;

                    customerSystemTime[time-1] = customers.size() - servedCustomers.size();
                    customerQueueTime[time-1] = queue.size();

                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        time = simulationLength+1;
                    }
                }

                calculateValues();

                System.out.println("Completed");
            }
        });
    }
}
