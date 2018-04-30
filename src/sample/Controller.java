package sample;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

class Controller {

    private Pane visualPane = new Pane();
    private InfoPane infoPane;

    private Thread thread = new Thread();

    private int time;
    private int simulationLength = 60;
    private int selectionRate = 6;
    private int serviceRate = 10;
    private int arrivalRate = 5;

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

        thread = setUpThread();
    }


    private void newCustomer(){
        int enterQueue = (int)(Math.random() * selectionRate) + 1 + time;

        Customer customer = new Customer(time, enterQueue);

        int y = ((customers.size()) * 40) % 400 + 20;
        int x = (int)Math.ceil((customers.size())/10) * 40 + 20;

        customer.move(x,y);

        customers.add(customer);
        visualPane.getChildren().add(customer);
        selectQueue.add(customer);

        System.out.println("New Customer!!");
    }

    private void enterQueue(Customer customer){
        int x = 500;

        x = 500 - queue.size() * 40;

        customer.move(x,100);
        customer.inQueue(true);

        queue.add(customer);
        System.out.println("Customer added to the Queue");
    }

    private void serve(Customer customer){

        customer.move(600,100);
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
        int y = ((servedCustomers.size()) * 40) % 400 + 20;
        int x = (int)Math.ceil((servedCustomers.size())/10) * 40 + 720;

        customer.move(x,y);
        customer.setDepartureTime(time);
        servedCustomers.add(customer);
        System.out.println("Customer is leaving");
    }

    void reset(){
        thread.interrupt();
        for(Customer customer: customers)
            visualPane.getChildren().removeAll(customer);
        customers.clear();
        time = 0;
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
        this.simulationLength = simulationLength;
        this.arrivalRate = arrivalRate;
        this.selectionRate = selectionRate;
        this.serviceRate = serviceRate;
    }

    private Thread setUpThread(){
        return new Thread(new Runnable(){

            @Override
            public void run() {
                int arrival  = (int)(Math.random() * arrivalRate) + 1;
                int finishedServiceTime = 0;
                boolean serverAvailable = true;

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

                    if(!serverAvailable && time >= finishedServiceTime){
                        departure(beingServedCustomer);
                        serverAvailable = true;
                    }

                    if(serverAvailable && !queue.isEmpty()){
                        serve(queue.poll());
                        finishedServiceTime = (int)(Math.random() * serviceRate) + 1 + time;
                        serverAvailable = false;
                    }

                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        time = simulationLength+1;
                    }
                }

                System.out.println("Completed");
            }
        });
    }
}
