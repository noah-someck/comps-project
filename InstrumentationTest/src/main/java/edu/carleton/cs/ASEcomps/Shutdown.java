package edu.carleton.cs.ASEcomps;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Shutdown extends Thread {

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        long elapsedTime;
        int prevNumCalls = 0;
        int curNumCalls;

        boolean done = false;
        while (!done) {
            curNumCalls = StringSearchHolder.getInstance().getNumCalls();
            long currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - startTime;
            if (elapsedTime >= 3000) {
                startTime = currentTime;
                if (!StringSearchHolder.getInstance().isMatchFound() && (prevNumCalls == curNumCalls)) {
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
