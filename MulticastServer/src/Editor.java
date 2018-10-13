public class Editor extends User {

    public Editor(String email, String password) {
        super(email, password);
    }

    public String getType() {
        return "editor";
    }

}
