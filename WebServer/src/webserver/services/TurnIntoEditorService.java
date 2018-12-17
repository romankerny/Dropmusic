package webserver.services;

import shared.RMICall;
import shared.RMIServerInterface;
import ws.WebSocketAnnotation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.WeakHashMap;

public class TurnIntoEditorService {

    public TurnIntoEditorService() {
        System.out.println("Starting TurnIntoEditorService()");
    }

    public boolean regularToEditor(String editor, String regular) {

        boolean r = false, exit = false;
        RMIServerInterface server = RMICall.waitForServer();
        String rsp;

        while(!exit) {
            try {
                if (regular != "") {

                    rsp = server.regularToEditor(editor, regular);

                    if (rsp.equals(regular + " casted to Editor with success")) {
                        r = true;
                        System.out.println("no service true" + regular + editor);
                        // notifications
                        WebSocketAnnotation.sendNotification(regular, "[*] You've promoted to Editor by " + editor + ".\n You can now edit materials!");

                    } else {
                        System.out.println("No service false");
                        r = false;
                    }
                    exit = true;
                }
            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }
        
        return r;

    }
}
