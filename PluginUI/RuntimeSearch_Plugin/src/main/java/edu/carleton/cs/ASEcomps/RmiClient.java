package edu.carleton.cs.ASEcomps;

import com.intellij.openapi.application.ApplicationManager;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RmiClient {

    volatile private boolean done;

    public RmiClient() {
        done = false;
    }

    public void setDone() {
        done = true;
    }

    public void begin() throws RemoteException, MalformedURLException {
        boolean waitToFinish = false;
        String[] breakpoint = null;
        RmiServerIntf obj = null;
        try {
            obj = (RmiServerIntf) Naming.lookup("//localhost/RmiServer");
        } catch (NotBoundException ignore) {
        }

        while (!obj.searchCompleted()) {
            try {
                obj = (RmiServerIntf) Naming.lookup("//localhost/RmiServer");
            } catch (NotBoundException e) {
                System.out.println(2);
                break;
            }
            if (done) {
                obj.finished();
                removeBreakpoint(breakpoint);
                done = false;
                waitToFinish = false;
            }
            else if (waitToFinish);
            else if (obj.isNew()) {
                obj.wasReceived();
                breakpoint = obj.getBreakpoint();
                addBreakpoint(breakpoint);
                waitToFinish = true;
                Naming.rebind("//localhost/RmiServer", obj);
            }
        }
        obj.acknowledgeCompletion();
    }

    private void addBreakpoint(String[] breakpoint) {
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                RuntimeSearchWindow.addBreakpoint(breakpoint[0], Integer.valueOf(breakpoint[1]));
            }
        });
    }

    private void removeBreakpoint(String[] breakpoint) {
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                RuntimeSearchWindow.removeBreakpoint(breakpoint[0], Integer.valueOf(breakpoint[1]));
            }
        });
    }

    public static void setSearchString(String searchString) throws RemoteException, NotBoundException, MalformedURLException {
        RmiServerIntf obj = (RmiServerIntf)Naming.lookup("//localhost/RmiServer");
        obj.setSearchString(searchString);
    }
}