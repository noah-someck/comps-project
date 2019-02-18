package edu.carleton.cs.ASEcomps;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Shutdown extends Thread {

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        int prevNumCalls = 0;
        int curNumCalls = 0;

        boolean done = false;
        while (!done) {
            curNumCalls = StringSearchHolder.getInstance().getNumCalls();
            long currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - startTime;
            if (elapsedTime >= 1000) {
                startTime = currentTime;
                if (!StringSearchHolder.getInstance().paused() && (prevNumCalls == curNumCalls)) {
                    System.out.println("FINISHED");
                    try {
                        RmiServer.shutdown();
                    } catch (RemoteException | NotBoundException | MalformedURLException e) {
                        e.printStackTrace();
                    }
                    done = true;
                }
            }
            prevNumCalls = curNumCalls;
        }
    }

}
