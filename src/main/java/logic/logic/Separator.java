package logic.logic;

import logic.constants.Constants;

import java.util.concurrent.TimeUnit;

public class Separator implements Runnable {
    @Override
    public void run() {
        while (true){
            try {
                TimeUnit.MILLISECONDS.sleep(Constants.timeToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("----------------------------------------------------");
        }
    }
}
