package webserver.models;

public class RegisterModel {

    private String email; // email and password supplied by the user
    private String password;

    public RegisterModel(String email, String password) {
        setEmail(email);
        setPassword(password);
    }

    public RegisterModel()
    {
        this(null, null);
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

}
