import java.io.Serializable;
import java.io.SerializablePermission;
import java.util.concurrent.CopyOnWriteArrayList;

enum Status {
    ON, OFF;
}

public class User implements Serializable {

    public String email;
    public String password;
    public Status status;
    public CopyOnWriteArrayList<String> notifications;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.status = Status.OFF;
        this.notifications = new CopyOnWriteArrayList<String>();
    }

    public void addNotification(String notification) {
        this.notifications.add(notification);
    }

    public void removeNotification(String notification) {
        this.notifications.remove(notification);
    }

    public void login() {
        this.status = Status.ON;
    }

    public void logout() { this.status = Status.OFF;}

    public String getType() {
        return "user";
    }

    public String toString() {
        return "email: " + email + " ; password: " + password + " " + getType();
    }



    // inbox de de notificações
    // musica secreta do user

}


