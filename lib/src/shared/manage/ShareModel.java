package shared.manage;

import java.io.Serializable;

public class ShareModel implements Serializable {

    private String email;
    private String albumTitle;
    private String artistName;
    private String musicTitle;

    public ShareModel(String email, String albumTitle, String artistName, String musicTitle) {

        setEmail(email);
        setMusicTitle(musicTitle);
        setAlbumTitle(albumTitle);
        setArtistName(artistName);
    }

    public ShareModel()
    {
        this(null , null, null, null);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }
}
