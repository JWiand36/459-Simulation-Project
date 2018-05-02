package sample;

import javafx.scene.shape.Circle;

public class Customer extends Circle implements Comparable<Customer>{

    private int arrivalTime;
    private int enterQueueTime;
    private int leaveQueueTime;
    private int departureTime;
    private double x;
    private boolean queue;

    Customer(int arrivalTime, int enterQueueTime){
        super(20,20,20);
        this.arrivalTime = arrivalTime;
        this.enterQueueTime = enterQueueTime;
        this.leaveQueueTime = 0;
        this.queue = false;
    }

    int getArrivalTime() {
        return arrivalTime;
    }

    int getEnterQueueTime() {
        return enterQueueTime;
    }

    int getLeaveQueueTime() {
        return leaveQueueTime;
    }

    void setLeaveQueueTime(int leaveQueueTime) {
        this.leaveQueueTime = leaveQueueTime;
    }

    int getDepartureTime() { return departureTime; }

    void setDepartureTime(int departureTime) {
        this.departureTime = departureTime;
    }

    double getX(){ return x; }

    void move(double x, double y){
        this.x = x;
        setCenterX(x);
        setCenterY(y);
    }

    void inQueue(boolean inQueue){
        this.queue = inQueue;
    }

    boolean inQueue(){ return queue;}

    @Override
    public int compareTo(Customer o) {
        if(this.getEnterQueueTime() > o.getEnterQueueTime())
            return 1;
        else
            return 0;
    }
}
