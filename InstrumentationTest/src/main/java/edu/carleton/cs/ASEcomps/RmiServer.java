package edu.carleton.cs.ASEcomps;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RmiServer extends UnicastRemoteObject implements RmiServerIntf {

    private static final long serialVersionUID = 123123123321L;

    private String[] breakpoint;
    private static boolean isNew = false;
    private static boolean doneSearching = false;
    private static RmiServer obj;
    volatile private String searchString;
    volatile private static boolean completionAcknowledged = false;

    private RmiServer() throws RemoteException {
        super(0);    // required to avoid the 'rmic' step, see below
    }

    public String[] getBreakpoint() {
        return breakpoint;
    }

    public void setBreakpoint(String[] breakpoint) {
        isNew = true;
        this.breakpoint = breakpoint;
    }

    public boolean isNew() {
        return isNew;
    }

    public void wasReceived() {
        isNew = false;
        try {
            Naming.rebind("//localhost/RmiServer", obj);
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void finished() {
        StringSearchHolder.getInstance().continueSearch();
    }

    public boolean searchCompleted() {
        return doneSearching;
    }

    @Override
    public void acknowledgeCompletion() throws RemoteException {
        completionAcknowledged = true;
    }

    public static RmiServer getInstance() {
        return obj;
    }

    public static void startServer() throws Exception {
        try { // special exception handler for registry creation
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            // do nothing, error means registry already exists
        }

        // Instantiate RmiServer
        obj = new RmiServer();

        // Bind this object instance to the name "RmiServer"
        Naming.rebind("//localhost/RmiServer", obj);
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public static String getSearchString()
    {
        while (obj.searchString == null);
        return obj.searchString;
    }

    public static void shutdown() throws RemoteException, NotBoundException, MalformedURLException {
        doneSearching = true;
        Naming.rebind("//localhost/RmiServer", obj);
        while (!completionAcknowledged);
        Naming.unbind("//localhost/RmiServer");
        UnicastRemoteObject.unexportObject(obj, true);
    }

}
