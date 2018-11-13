import java.sql.*;

public class DB {

    public static String insertUser(String email, String password, Connection con)  {

        PreparedStatement pstmt = null;
        String rspToMulticast = null;
        int rs;

        try {
            pstmt = con.prepareStatement("INSERT INTO user (email, password) VALUES (?,?)");
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            rs = pstmt.executeUpdate();

            rspToMulticast = "User " + email + " added to DB with sucsses";
            System.out.println("Inserted " + rs + " new user(s).");

        } catch (SQLException e) {
            e.printStackTrace();

            switch (e.getErrorCode()) {
                case 1062:
                    // duplicate entry
                    System.out.println("Got ERROR:1062");
                    rspToMulticast = "User : " + email +" already exists.";
                    break;
            }

        }
        return rspToMulticast;
    }

}