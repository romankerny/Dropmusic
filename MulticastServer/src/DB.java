import java.sql.*;

public class DB {

    public static Connection con;

    public static String insertUser(String email, String password)  {

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

    public static String promoteUser(String promoter, String email) {
        PreparedStatement pstmt;
        String rspToMulticast = "";
        int rs;

        try {
            pstmt = con.prepareStatement("update user as u, (select editor from user where email = ? ) as p "+
                                                 "set u.editor = true " +
                                                    "where p.editor = true and u.email = ?");
            pstmt.setString(1, promoter);
            pstmt.setString(2, email);

            rs = pstmt.executeUpdate();

            if (rs == 0)
                rspToMulticast = "Failed to promote user";
            else if (rs == 1)
                rspToMulticast = "Updated sucessfully";
            else
                System.out.println("Unreachable code");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rspToMulticast;
    }

    public static String insertArtist(String name, String details, Connection con) {

        PreparedStatement pstmt = null;
        String rspToMulticast = null;
        int rs;

        try {
            pstmt = con.prepareStatement("INSERT INTO artist (name, details) VALUES (?,?)");
            pstmt.setString(1, name);
            pstmt.setString(2, details);
            rs = pstmt.executeUpdate();

            rspToMulticast = "Artist " + name + " added to DB with sucsses";
            System.out.println("Inserted " + rs + " new artist(s).");

        } catch (SQLException e) {
            e.printStackTrace();

            switch (e.getErrorCode()) {
                case 1062:
                    // duplicate entry
                    System.out.println("Got ERROR:1062");
                    rspToMulticast = "Artist : " + name +" already exists.";
                    break;
            }

        }
        return rspToMulticast;

    }

}
