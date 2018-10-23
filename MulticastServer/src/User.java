import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class User implements Serializable {

    public String email;
    public String password;
    public boolean editor;
    public CopyOnWriteArrayList<String> notifications;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.editor = false;
        this.notifications = new CopyOnWriteArrayList<String>();
    }

    public void addNotification(String notification) {
        this.notifications.add(notification);
    }

    public void removeNotification(String notification) {
        this.notifications.remove(notification);
    }

    public boolean isEditor() {return this.editor;}

    public void becomeEditor() { this.editor = true;}

    public String getType() {
        return "user";
    }

    public String toString() {
        return "email: " + email + " ; password: " + password + " " + getType();
    }



    // inbox de de notificações
    // musica secreta do user

}


