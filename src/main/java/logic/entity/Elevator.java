package logic.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Elevator {

    private String direction;
    private int location;
    private final int capacity;
    private int fill;
    private List<Boolean> rout;
    private List<Human> passengers = new LinkedList<>();
    private static Elevator elevator;

    private Elevator(int capacity, int fill, int location, List<Boolean> rout) {
        this.capacity = capacity;
        this.fill = fill;
        this.location = location;
        this.rout = rout;
    }

    public static Elevator getLift(int numberOfFloors){
        if (elevator ==null)
            createLift(numberOfFloors);
        return elevator;
    }

    private static void createLift(int numberOfFloors){
        List<Boolean> rout = new ArrayList<>();
        for (int i = 0; i <= numberOfFloors; i++) {
            rout.add(false);
        }

        elevator = new Elevator(5, 0, 1, rout);
    }

    public List<Human> getPassengers() { return passengers; }

    public List<Boolean> getRout() {
        return rout;
    }

    public void setRout(int n, boolean b) { rout.set(n, b); }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) { this.location = location; }

    public String getDirection() { return direction; }

    public void setDirection(String direction) { this.direction = direction; }

    public boolean isUp(){ return direction.trim().equalsIgnoreCase("up"); }

    public boolean isDown(){ return direction.trim().equalsIgnoreCase("down"); }

    public boolean isFull(){ return capacity-fill != 0;}

    public boolean isEmptyLift(){ return fill == 0; }

    public void loadPassenger(Human human){
        passengers.add(human);
        fill++;
    }

    public void decrementPassenger() { fill--; }

    @Override
    public String toString() {
        return "Elevator{" +
                "direction='" + direction + '\'' +
                ", location=" + location +
                ", capacity=" + capacity +
                ", fill=" + fill +
                ", rout=" + rout +
                ", passengers=" + passengers +
                '}';
    }
}
