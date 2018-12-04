package webserver.models;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class User implements Serializable {

    private String email;
    private String password;
    private boolean editor;
    private CopyOnWriteArrayList<String> notifications;


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
    }

    public CopyOnWriteArrayList<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(CopyOnWriteArrayList<String> notifications) {
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "email: " + email + "Editor: "+this.editor;
    }

}
