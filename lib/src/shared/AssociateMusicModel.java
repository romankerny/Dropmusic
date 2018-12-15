package shared;

import java.io.Serializable;

public class AssociateMusicModel implements Serializable {


        private String fileName;
        private String albumTitle;
        private String artistName;
        private String musicTitle;

        public AssociateMusicModel(String fileName, String albumTitle, String artistName, String musicTitle) {

            setFileName(fileName);
            setMusicTitle(musicTitle);
            setAlbumTitle(albumTitle);
            setArtistName(artistName);
        }

        public AssociateMusicModel()
        {
            this(null , null, null, null);
        }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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


