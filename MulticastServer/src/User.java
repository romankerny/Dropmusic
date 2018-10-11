import java.io.Serializable;
import java.io.SerializablePermission;

enum Status {
    ON, OFF;
}

public class User implements Serializable {

    public String email;
    private String password;
    private Status status;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String toString() {
        return "email: " + email + " ; password: " + password;
    }



    // inbox de de notificações
    // musica secreta do user

}


