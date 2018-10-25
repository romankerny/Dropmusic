import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObjectFiles {


    public static CopyOnWriteArrayList<Artist> readArtistsFromMemory(){
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
            System.out.println("readArtistsFromMemory(): Exception: "+e);
        } catch(ClassNotFoundException e){
            System.out.println("readArtistsFromMemory(): Exception: "+e);
        }

        return artists;
    }

    public static CopyOnWriteArrayList<User> readUsersFromMemory(){
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
            System.out.println("readUsersFromMemory(): Exception: "+e);
        } catch(ClassNotFoundException e){
            System.out.println("readUsersFromMemory(): Exception: "+e);
        }

        return users;
    }


    public static void writeArtistsToMemory(CopyOnWriteArrayList<Artist> artists) {
        try {
            File f = new File("artists.obj");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            for (Artist a : artists)
                os.writeObject(a);
            os.writeObject(null);
            os.close();
        } catch (IOException e) {
            System.out.println("writeArtistsToMemory(): Exception: "+e);
        }
    }

    public static void writeUsersToMemory(CopyOnWriteArrayList<User> users) {
        try {
            File f = new File("users.obj");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            for (User u : users)
                os.writeObject(u);
            os.writeObject(null);
            os.close();
        } catch (IOException e) {
            System.out.println("writeUsersToMemory(): Exception: "+e);
        }
    }


    }

