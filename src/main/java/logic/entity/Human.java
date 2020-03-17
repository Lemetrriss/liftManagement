package logic.entity;

public class Human {
    private int destinationFloor;
    private int humanLocation;
    private String direction;

    Human(int destinationFloor, int humanLocation) {
        this.destinationFloor = destinationFloor;
        this.humanLocation = humanLocation;
        direction = (destinationFloor>humanLocation) ? "up" : "down";
    }

    public String getDirection() { return direction; }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public int getHumanLocation() {
        return humanLocation;
    }

    @Override
    public String toString() {
        return "Human{" +
                "destinationFloor=" + destinationFloor +
                ", humanLocation=" + humanLocation +
                ", direction='" + direction + '\'' +
                "}\n";
    }
}
