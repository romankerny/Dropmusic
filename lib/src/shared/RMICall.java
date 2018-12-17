package shared;

import shared.RMIServerInterface;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import static java.lang.Thread.sleep;

public class RMICall {

    static String ip = System.getenv("RMI_IP");
    public static RMIServerInterface waitForServer() {

        boolean exit = false;
        RMIServerInterface serverInterface = null;
        while(!exit) {


            try {
                sleep((long) (1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                serverInterface = (RMIServerInterface) LocateRegistry.getRegistry(ip, 1099).lookup("rmiserver");
                exit = true;
            } catch (ConnectException e) {
                System.out.println("RMI server down, retrying...");
            } catch (NotBoundException v) {
                System.out.println("RMI server down, retrying...");
            } catch (RemoteException tt) {
                System.out.println("RMI server down, retrying...");
            }
        }
        return serverInterface;
    }

}
