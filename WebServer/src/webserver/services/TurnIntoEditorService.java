package webserver.services;

import rmiserver.RMIServerInterface;

import javax.websocket.Session;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

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
        } else
        {
            r = false;
        }

        // notifications

        return r;


    }
}
