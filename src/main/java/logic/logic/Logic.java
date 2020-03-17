package logic.logic;

import logic.constants.Constants;
import logic.entity.Elevator;
import logic.entity.Floor;
import logic.entity.Human;
import logic.exceptions.NotAddDestinationIn_goException;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Logic {

    public final static int numberOfFloors = new Random().nextInt(15) + 5; //13
    private static Elevator elevator;
    private List<Floor> floors;
    private static Map<Integer, List<Human>> goUp = new HashMap<>();
    private static Map<Integer, List<Human>> goDown = new HashMap<>();

    private List<Floor> createFloors() {
        List<Floor> list = new ArrayList<>();
        for (int i = 0; i <= numberOfFloors; i++) {     // 0 - 13
            list.add(new Floor(i, numberOfFloors)); // 14
        }
        return list;
    }

    private void initCalls() {
        for (int i=1; i<=numberOfFloors; i++) {
            goUp.put(i, new ArrayList<>());
            goDown.put(i, new ArrayList<>());
        }
        for (int i=1; i<=numberOfFloors; i++) {
            LinkedList<Human> listOfHumans = floors.get(i).getListOfHumans();
            for (Human human : listOfHumans) {
                if (human.getDirection().equalsIgnoreCase("up")){
                    try {
                        goUp.get(human.getHumanLocation()).add(human);
                    } catch (Exception e) {
                        try{
                            throw new NotAddDestinationIn_goException("Пассажир не добавился в список вызовов лифта на движение ВВЕРХ", e);
                        } catch (NotAddDestinationIn_goException notAddex){
                            notAddex.printStackTrace();
                        }
                    }
                }
                if (human.getDirection().equalsIgnoreCase("down")){
                    try {
                        goDown.get(human.getHumanLocation()).add(human);
                    } catch (Exception e) {
                        try{
                            throw new NotAddDestinationIn_goException("Пассажир не добавился в список вызовов лифта на движение ВНИЗ", e);
                        } catch (NotAddDestinationIn_goException notAddex){
                            notAddex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private List<List<Human>> liftLoad(){
        int location = elevator.getLocation();

        List<Human> humans = elevator.getDirection().equalsIgnoreCase("up") ? goUp.get(location) : goDown.get(location);
        Iterator<Human> iterator = humans.iterator();

        while (iterator.hasNext() && elevator.isFull()){
            Human human = iterator.next();
            iterator.remove();
            floors.get(location).decrimentHuman();
            elevator.loadPassenger(human);
            elevator.setRout(human.getDestinationFloor(), true);
        }

        List<List<Human>> liftFloorHumans = new ArrayList<>();
        List<Human> onFloor = new ArrayList<>();
        if (goUp.containsKey(location)){
            onFloor.addAll(goUp.get(location));
        }
        if (goDown.containsKey(location)) {
            onFloor.addAll(goDown.get(location));
        }
        liftFloorHumans.add(elevator.getPassengers());
        liftFloorHumans.add(onFloor);

        return liftFloorHumans;
    }

    private void liftUnload(Elevator l){
        Iterator<Human> iterator = l.getPassengers().iterator();
        while (iterator.hasNext()){
            Human next = iterator.next();
            if (next.getDestinationFloor()==l.getLocation()){
                iterator.remove();
                l.decrementPassenger();
            }
        }
        l.setRout(l.getLocation(), false);
    }

    private void moveEmpty(int floor){
        elevator.setLocation(floor);
        elevator.setRout(floor, false);
    }

    private void move(Map<Integer, List<Human>> go){
        List<Boolean> rout = elevator.getRout();
        int location = elevator.getLocation();

        if (elevator.isUp()){
            for (int i=location+1; i < rout.size(); i++){
                if (rout.get(i)) {
                    elevator.setLocation(i);
                    rout.set(i,false);
                    break;
                }
                if (elevator.isFull()){
                    if (go.get(i).size()!=0){
                        elevator.setLocation(i);
                        break;
                    }
                }
            }
        } else if (elevator.isDown()){
            for (int i=location-1; i >= 1; i--){
                if (rout.get(i)) {
                    elevator.setLocation(i);
                    rout.set(i, false);
                    break;
                }
                if (elevator.isFull()){
                    if (go.get(i).size()!=0){
                        elevator.setLocation(i);
                        break;
                    }
                }
            }
        }
    }

    public void go() {
        Thread separator = new Thread(new Separator());
        separator.setDaemon(true);
        separator.start();
        floors = createFloors();
        System.out.println("Total floors: " + numberOfFloors + ".\n");
//        for (Floor floor : m.floors) {
//            System.out.println(floor);
//        }
        elevator = Elevator.getLift(numberOfFloors);

        initCalls();
//        System.out.println("UP");
//        System.out.println(goUp);
//        System.out.println();
//       System.out.println("DOWN");
//        System.out.println(goDown);

        while (true) {
            Floor floor = floors.get(elevator.getLocation());
            List<List<Human>> lists;

            if (elevator.isEmptyLift()) {
                elevator.setDirection(null);
                if (floor.isEmptyFloor()) {
                    int floorNumber = getFloorNumber(floors, elevator);
                    if (floorNumber == 0) {
                        printEnd();
                        return;
                    }
                    printEmpty();
                    elevator.setRout(floorNumber, true);
                    moveEmpty(floorNumber);
                }
                elevator.setDirection(checkLocation_setDirection());
            }

            lists = liftLoad();
            printStatus(lists);
            move(elevator.getDirection().equalsIgnoreCase("up") ? goUp : goDown);
            liftUnload(elevator);
            try {
                TimeUnit.MILLISECONDS.sleep(Constants.timeToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printEmpty() {
        System.out.println("Floor #" + elevator.getLocation() + ":");
        System.out.print("Boarded the elevator: |");
        System.out.print("| Stayed on the floor: ");
        System.out.print(" ---> rout of elevator: LOOKING FOR CALLS FROM ANY FLOOR");
        System.out.println();
    }

    private static void printEnd() {
        System.out.println("Floor #" + elevator.getLocation() + ":");
        System.out.print("Boarded the elevator: | EMPTY");
        System.out.print("| Stayed on the floor: EMPTY ");
        System.out.print(" ---> rout of elevator: STOP");
        System.out.println("\n--------------------------------------");
        System.out.println("There are no more passengers!");
        System.out.println("--------------------------------------");
    }

    private static void printStatus(List<List<Human>> lists) {
        System.out.println("Floor #" + elevator.getLocation() + ":");
        System.out.print("Boarded the elevator: " + (elevator.getDirection().equalsIgnoreCase("up") ? "Λ" : "V") + "|");
        for (Human human : lists.get(0)) {
            System.out.print(human.getDestinationFloor() + ",");
        }
        System.out.print("| Stayed on the floor: ");
        for (Human human : lists.get(1)) {
            System.out.print(human.getDestinationFloor() + ",");
        }
        System.out.print(" ---> rout of elevator: ");
        for (int i = 1; i < elevator.getRout().size(); i++) {
            if (elevator.getRout().get(i)) {
                System.out.print(i + ", ");
            }
        }
        System.out.println();
    }

    private String checkLocation_setDirection() {
        if (elevator.getLocation() == numberOfFloors) {
            return "down";
        } else if (elevator.getLocation() == 1) {
            return "up";
        } else {
            int floorNumber = elevator.getLocation();
            return goUp.get(floorNumber).size() >= goDown.get(floorNumber).size() ? "up" : "down";
        }
    }


    private int getFloorNumber(List<Floor> floors, Elevator lift) {
        int currentFloor = lift.getLocation();

        for (int i=1; i<=numberOfFloors; i++) {
            int number = floors.get(i).getNumberOfHumans();
            if (number > floors.get(currentFloor).getNumberOfHumans())
                currentFloor = i;
        }
        return floors.get(currentFloor).getNumberOfHumans() == 0 ? 0 : currentFloor;
    }

}