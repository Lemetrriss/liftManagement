package logic.entity;

import logic.logic.Logic;

import java.util.LinkedList;
import java.util.Random;

public class Floor {

    private final int floorNumber;
    private int numberOfHumans;
    private LinkedList<Human> listOfHumans = new LinkedList<>();

    public Floor(int floorNumber, int totalFloors) {
        this.floorNumber = floorNumber;
        Random r = new Random();
        numberOfHumans = r.nextInt(9)+1;
        for (int i=1; i<=numberOfHumans; i++){
            int n = r.nextInt(Logic.numberOfFloors) + 1;
            if (n == floorNumber && n < totalFloors){
                n++;
            } else if (n == floorNumber && n == totalFloors){
                n = 1;
            }
            listOfHumans.add(new Human(n, floorNumber));
        }
    }

    public int getNumberOfHumans(){ return numberOfHumans; }

    public LinkedList<Human> getListOfHumans() { return listOfHumans; }

    public boolean isEmptyFloor(){ return numberOfHumans == 0; }

    public void decrimentHuman() { numberOfHumans--; }

    @Override
    public String toString() {
        return "Floor{" +
                "floorNumber=" + floorNumber +
                ", numberOfHumans=" + numberOfHumans +
                ", Humans=" + listOfHumans +
                '}';
    }
}
