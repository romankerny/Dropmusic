package webserver.services;

import shared.RMIServerInterface;
import ws.WebSocketAnnotation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.WeakHashMap;

public class TurnIntoEditorService {
    private Map<String, Object> session;

    public TurnIntoEditorService() {
        System.out.println("Starting TurnIntoEditorService()");
    }

    public boolean regularToEditor(String editor, String regular) throws RemoteException {

        boolean r;
        RMIServerInterface server = null;
        String rsp;

        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }


        rsp = server.regularToEditor(editor, regular);

        if (rsp.equals(regular + " casted to Editor with success"))
        {
            r = true;
            // notifications
            WebSocketAnnotation.sendNotification(regular, "[*] You've promoted to Editor by " + editor + ".\n You can now edit materials!");

        } else
        {
            r = false;
        }
        
        return r;


    }
}
