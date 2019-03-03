package edu.carleton.cs.ASEcomps;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientThread extends Thread {

    private RmiClient rmiClient;

    @Override
    public void run() {
        rmiClient = new RmiClient();
        boolean began = false;
        long startTime = System.currentTimeMillis();
        while (!began) {
            try {
                rmiClient.begin();
                began = true;
            } catch (RemoteException | MalformedURLException | NotBoundException e) {
                if (System.currentTimeMillis() - startTime > 5000) {
                    began = true;
                }
//                e.printStackTrace();
            }
        }
        RuntimeSearchWindow.endClient();
    }

    public void reset() {
        rmiClient.setDone();
    }
}
