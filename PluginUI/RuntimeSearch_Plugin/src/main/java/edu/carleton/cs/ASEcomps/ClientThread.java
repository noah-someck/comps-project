package edu.carleton.cs.ASEcomps;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientThread extends Thread {

    private RmiClient rmiClient;

    @Override
    public void run() {
        rmiClient = new RmiClient();
        try {
            rmiClient.begin();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
        RuntimeSearchWindow.endClient();
    }

    public void reset() {
        rmiClient.setDone();
    }
}
