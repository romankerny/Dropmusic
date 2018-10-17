import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int RECV_PORT = 5213;
    private MulticastSocket socket = null;
    private CopyOnWriteArrayList<User> users;
    private CopyOnWriteArrayList<Artist> artists;

    public MulticastServer() {
        artists = new CopyOnWriteArrayList<Artist>();
        users = new CopyOnWriteArrayList<User>();
    }

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }



    public void run() {
        // wait for packets

        try {
            socket = new MulticastSocket(RECV_PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            System.out.println("Multicast server ready");

            Editor admin = new Editor("admin", "admin");
            users.add(admin);

            Artist tool = new Artist("Tool");
            Album lateralus = new Album("Lateralus", "Released in 2001", "Progressive Metal");
            lateralus.tracks.add(new Music(1, "The Grudge"));
            lateralus.tracks.add(new Music(2, "Eon Blue Apocalypse"));
            lateralus.tracks.add(new Music(3, "The Patient"));
            lateralus.tracks.add(new Music(4, "Mantra"));
            lateralus.tracks.add(new Music(5, "Schism"));
            lateralus.tracks.add(new Music(6, "Parabol"));
            lateralus.tracks.add(new Music(7, "Parabola"));
            lateralus.tracks.add(new Music(8, "Ticks & Leeches"));
            lateralus.tracks.add(new Music(9, "Lateralus"));
            lateralus.tracks.add(new Music(10, "Disposition"));
            lateralus.tracks.add(new Music(11, "Reflection"));
            lateralus.tracks.add(new Music(12, "Triad"));
            lateralus.tracks.add(new Music(13, "Faaip de Oiad"));
            tool.albums.add(lateralus);

            Album tenKdays = new Album("10,000 Days", "Released in 2006", "Progressive Metal");
            tenKdays.tracks.add(new Music(1, "Vicarious"));
            tenKdays.tracks.add(new Music(2, "Jambi"));
            tenKdays.tracks.add(new Music(3, "Wings for Marie (Pt. 1)"));
            tenKdays.tracks.add(new Music(4, "10,000 Days (Wings Pt. 2)"));
            tenKdays.tracks.add(new Music(5, "The Pot"));
            tenKdays.tracks.add(new Music(6, "Lipan Conjuring"));
            tenKdays.tracks.add(new Music(7, "Lost Keys (Blame Hoffmann)"));
            tenKdays.tracks.add(new Music(8, "Rosetta Stoned"));
            tenKdays.tracks.add(new Music(9, "Intension"));
            tenKdays.tracks.add(new Music(10, "Right in Two"));
            tenKdays.tracks.add(new Music(11, "Viginti Tres"));
            tool.albums.add(tenKdays);
            this.artists.add(tool);

            Artist kendrick = new Artist("Kendrick Lamar");
            Album butterfly = new Album("To Pimp a Butterfly", "Released in 2015", "Hip-hop");
            butterfly.tracks.add(new Music(1, "Wesley's Theory"));
            butterfly.tracks.add(new Music(2, "For Free? (Interlude)"));
            butterfly.tracks.add(new Music(3, "King Kunta"));
            butterfly.tracks.add(new Music(4, "Institutionalized"));
            butterfly.tracks.add(new Music(5, "These Walls"));
            butterfly.tracks.add(new Music(6, "u"));
            butterfly.tracks.add(new Music(7, "Alright"));
            butterfly.tracks.add(new Music(8, "For Sale? (Interlude)"));
            butterfly.tracks.add(new Music(9, "Momma"));
            butterfly.tracks.add(new Music(10, "Hood Politics"));
            butterfly.tracks.add(new Music(11, "How Much a Dollar Cost"));
            butterfly.tracks.add(new Music(12, "Complexion"));
            butterfly.tracks.add(new Music(13, "The Blacker the Berry"));
            butterfly.tracks.add(new Music(14, "You Aint Gotta Lie (Momma Said)"));
            butterfly.tracks.add(new Music(15, "i"));
            butterfly.tracks.add(new Music(16, "Mortal Man"));
            kendrick.albums.add(butterfly);
            this.artists.add(kendrick);

            //System.out.println(tool);
            //System.out.println(kendrick);

            while (true) {

                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                MulticastServerResponse threadToResolvePacket = new MulticastServerResponse(packet, MULTICAST_ADDRESS, users, artists);
                threadToResolvePacket.start();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
