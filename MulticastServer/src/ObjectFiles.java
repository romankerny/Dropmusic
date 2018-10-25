import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObjectFiles {


    public static CopyOnWriteArrayList<Artist> readArtistsFromDisk(){
        CopyOnWriteArrayList<Artist> artists = new CopyOnWriteArrayList<Artist>();

        try {
            FileInputStream fis = new FileInputStream("artists.obj");
            ObjectInputStream is = new ObjectInputStream(fis);
            Artist a;
            while ((a = (Artist) is.readObject()) != null)
                artists.add(a);
            is.close();

        } catch (FileNotFoundException ex){
            System.out.println("File artists.obj does not exist");
        } catch (IOException e) {
            System.out.println("readArtistsFromDisk(): Exception: "+e);
        } catch(ClassNotFoundException e){
            System.out.println("readArtistsFromDisk(): Exception: "+e);
        }

        return artists;
    }

    public static CopyOnWriteArrayList<User> readUsersFromDisk(){
        CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<User>();

        try {
            FileInputStream fis = new FileInputStream("users.obj");
            ObjectInputStream is = new ObjectInputStream(fis);
            User u;
            while ((u = (User) is.readObject()) != null)
                users.add(u);
            is.close();

        } catch (FileNotFoundException ex){
            System.out.println("File users.obj does not exist");
        } catch (IOException e) {
            System.out.println("readUsersFromDisk(): Exception: "+e);
        } catch(ClassNotFoundException e){
            System.out.println("readUsersFromDisk(): Exception: "+e);
        }

        return users;
    }


    public static void writeArtistsToDisk(CopyOnWriteArrayList<Artist> artists) {
        try {
            File f = new File("artists.obj");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            for (Artist a : artists)
                os.writeObject(a);
            os.writeObject(null);
            os.close();
        } catch (IOException e) {
            System.out.println("writeArtistsToDisk(): Exception: "+e);
        }
    }

    public static void writeUsersToDisk(CopyOnWriteArrayList<User> users) {
        try {
            File f = new File("users.obj");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            for (User u : users)
                os.writeObject(u);
            os.writeObject(null);
            os.close();
        } catch (IOException e) {
            System.out.println("writeUsersToDisk(): Exception: "+e);
        }
    }


    }

