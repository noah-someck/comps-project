package edu.carleton.cs.ASEcomps;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServerIntf extends Remote {

    public String[] getBreakpoint() throws RemoteException;
    public void setBreakpoint(String[] breakpoint) throws RemoteException;
    public boolean isNew() throws RemoteException;
    public void wasReceived() throws RemoteException;
    public void finished() throws RemoteException;
    public void setSearchString(String searchString) throws RemoteException;
    public boolean searchCompleted() throws RemoteException;
    public void acknowledgeCompletion() throws RemoteException;

}