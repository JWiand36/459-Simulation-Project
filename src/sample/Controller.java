package sample;


import javafx.application.Platform;
import javafx.scene.control.Button;
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

    //This is just a placeholder, to allow the system to find the customer being served easily
    private Customer beingServedCustomer;

    //This Queue is to help sort customers when adding to the actual queue. So the system doesn't have to search every
    //customer that is in the system.
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


    //Adds the customer to a customers list and to the queue representing a customer shopping
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

    //When a customer is ready to move to the queue, they are inserted into the queue, they are moved and their color changes to red
    private void enterQueue(Customer customer){
        int x;

        x = 495 - queue.size() * 40;

        customer.setFill(Color.RED);
        customer.move(x,100);
        customer.inQueue(true);

        queue.add(customer);
        System.out.println("Customer added to the Queue");
    }

    //When the server is available, a customer moves to the server area, taking out of the queue and their color is changed to yellow.
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

    //When a customer is finished being served, they are moved to the departure area and considered out of the system.
    //Their color changes to green representing as being served.
    private void departure(Customer customer){
        int y = ((servedCustomers.size()) * 40) % 400 + 40;
        int x = (int)Math.ceil((servedCustomers.size())/10) * 40 + 720;

        customer.setFill(Color.GREEN);
        customer.move(x,y);
        customer.setDepartureTime(time);
        servedCustomers.add(customer);
        System.out.println("Customer is leaving");
    }


    //Resets the entire program
    void reset(){
        thread.interrupt();

        //Removes all customers from the visual pane without deleting every thing but customers.
        for(Customer customer: customers)
            visualPane.getChildren().removeAll(customer);

        serverAvailable = true;

        //Empties the queue
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

    //Allows the user to change the default values
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

        double avgSystemTime = 0;
        double avgQueueTime = 0;
        int counter = 0;
        int[] arriveTime5 = new int[5];
        int[] serviceTime5 = new int[5];

        for(int i = 0; i < customers.size(); i++){

            //This is used to find the arrival time for the first 5 customers
            if(i < arriveTime5.length) {
                if (i != 0)
                    arriveTime5[i] = customers.get(i).getArrivalTime() - customers.get(i - 1).getArrivalTime();
                else
                    arriveTime5[i] = customers.get(i).getArrivalTime();
            }

            //Adds up the average time of customers that have successfully completed the queue
            //Calculating occurs here and not in the servedCustomers cause of the customer being served.
            if(customers.get(i).getLeaveQueueTime() - customers.get(i).getEnterQueueTime() > 0) {
                avgQueueTime += customers.get(i).getLeaveQueueTime() - customers.get(i).getEnterQueueTime();
                counter++;
            }
        }

        for(int i = 0; i < servedCustomers.size(); i++){

            //Finds the service time of the first 5 customers.
            if(i < serviceTime5.length)
                serviceTime5[i] = servedCustomers.get(i).getDepartureTime() - servedCustomers.get(i).getLeaveQueueTime();

            avgSystemTime += servedCustomers.get(i).getDepartureTime() - servedCustomers.get(i).getArrivalTime();
        }


        /*
        These are to find the long-run time-average of customers in the system.  (N/time)*W
        finds the long-run time average. AvgSystemTime is used to find W which is (departure time d(t) - start time s(t))/N
        but long-run time-average is W*N/time. Therefore the N's will cancel out and L = (d(t)-s(t))/time. Wq is the same but
        with queue values instead of the system.
        */
        infoPane.setCustomersInS(avgSystemTime / simulationLength);
        infoPane.setCustomersInQ(avgQueueTime / simulationLength);


        //Finishes finding W
        if(servedCustomers.size() != 0) {
            avgSystemTime /= servedCustomers.size();
        }

        //Finishes finding Wq
        if(counter != 0)
            avgQueueTime /= counter;

        //Arrival rate = customers/time and service rate is customers-served/time serving
        infoPane.setArrival((double)customers.size() / (double)time);
        infoPane.setServerRate((double)servedCustomers.size() / (double)serverUtil);
        infoPane.setServerUtil(serverUtil);
        infoPane.setTimeAverageS(avgSystemTime*((double)customers.size() / (double)time)); //Little's Law L = arrival rate * W
        infoPane.setTimeAverageQ(avgQueueTime*((double)customers.size() / (double)time)); //Little's Law L = arrival rate * Wq
        infoPane.setCustomersInSystem(customerSystemTime);
        infoPane.setCustomersInQueue(customerQueueTime);
        infoPane.setInnerArrivalRate(arriveTime5, serviceTime5);
    }

    //Just adds some visual effects and text. Nothing special
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

                    //Adds a customer to the customers array and to the selecting queue, to represent a customer shopping
                    if(time == arrival) {
                        Platform.runLater(() -> newCustomer());
                        arrival = (int)(Math.random() * arrivalRate) + 1 + time;
                    }

                    //If there is a customer shopping, checks to see if the customer is ready to checkout
                    if(!selectQueue.isEmpty() && time >= selectQueue.peek().getEnterQueueTime()) {
                        Platform.runLater(() -> enterQueue(selectQueue.poll()));
                    }

                    //If the server has finished serving a customer, they depart from the system and the server is available
                    if(!serverAvailable && time >= finishedServiceTime) {
                        departure(beingServedCustomer);
                        serverAvailable = true;
                    }

                    //If the server is available, then a customer moves to the server to be served.
                    if(serverAvailable && !queue.isEmpty()){
                        serve(queue.poll());
                        finishedServiceTime = (int)(Math.random() * serviceRate) + 1 + time;
                        serverAvailable = false;
                    }

                    //Checks to see if the server is occupied
                    if(!serverAvailable)
                        serverUtil++;

                    //Records how many customers are in the system and queue at a given time.
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
