import java.io.Serializable;
import java.io.SerializablePermission;

enum Status {
    ON, OFF;
}

public class User implements Serializable {

    public String name;
    private String password;
    private Status status;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String toString() {
        return "User: "+ name;
    }

    // inbox de de notificações
    // musica secreta do user

}


