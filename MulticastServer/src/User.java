import java.io.Serializable;
import java.io.SerializablePermission;

enum Status {
    ON, OFF;
}

public class User implements Serializable {

    public String name;
    private String password;
    private Status status;



    // inbox de de notificações
    // musica secreta do user

}


