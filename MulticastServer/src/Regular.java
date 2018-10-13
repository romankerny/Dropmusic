public class Regular extends User {

    public Regular (String email, String password) {
        super(email, password);
    }

    public String getType() {
        return "regular";
    }
}
