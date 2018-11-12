drop schema dropmusic;
create database dropmusic;
use dropmusic;

CREATE TABLE artist (
	name	 varchar(200),
	details	 varchar(512),
	album_id varchar(80) NOT NULL,
	PRIMARY KEY(name)
);

CREATE TABLE album (
	id		 varchar(80),
	title	 varchar(200),
	description	 varchar(512),
	genre	 varchar(100),
	launch_date	 date,
	editor_label varchar(100),
	artist_name	 varchar(200) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE review (
	id	 boolean,
	critic	 varchar(300),
	rating	 int,
	user_email varchar(200),
	album_id	 varchar(80) NOT NULL,
	PRIMARY KEY(id,user_email)
);

CREATE TABLE user (
	email	 varchar(200),
	password varchar(50),
	editor	 boolean,
	PRIMARY KEY(email)
);

CREATE TABLE music (
	id	 varchar(512),
	track	 int,
	title	 varchar(200),
	lyrics	 varchar(512) DEFAULT 2000,
	album_id varchar(80) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE notification (
	notification varchar(100),
	user_email	 varchar(200),
	PRIMARY KEY(notification,user_email)
);

CREATE TABLE upload (
	musicfilename varchar(300),
	music_id	 varchar(512),
	user_email	 varchar(200),
	PRIMARY KEY(music_id,user_email)
);

CREATE TABLE playlist (
	name	 varchar(512),
	public	 boolean,
	user_email varchar(200),
	PRIMARY KEY(name,user_email)
);

CREATE TABLE shows (
	place	 varchar(512),
	show_date date,
	id	 varchar(512),
	PRIMARY KEY(id)
);

CREATE TABLE upload_user (
	upload_music_id	 varchar(512),
	upload_user_email varchar(200),
	user_email	 varchar(200),
	PRIMARY KEY(upload_music_id,upload_user_email,user_email)
);

CREATE TABLE artist_user (
	artist_name varchar(200),
	user_email	 varchar(200),
	PRIMARY KEY(artist_name,user_email)
);

CREATE TABLE music_playlist (
	music_id		 varchar(512),
	playlist_name	 varchar(512),
	playlist_user_email varchar(200),
	PRIMARY KEY(music_id,playlist_name,playlist_user_email)
);

CREATE TABLE shows_artist (
	shows_id	 varchar(512),
	artist_name varchar(200),
	PRIMARY KEY(shows_id,artist_name)
);

ALTER TABLE artist ADD CONSTRAINT artist_fk1 FOREIGN KEY (album_id) REFERENCES album(id);
ALTER TABLE album ADD CONSTRAINT album_fk1 FOREIGN KEY (artist_name) REFERENCES artist(name);
ALTER TABLE album ADD CONSTRAINT main_constrains CHECK (id != "" AND title != "" AND description != "" AND genre != "" AND editor_label != "");
ALTER TABLE album ADD CONSTRAINT datas CHECK (launch_date > date('1900-01-01') AND launch_date < sysdate);
ALTER TABLE review ADD CONSTRAINT review_fk1 FOREIGN KEY (user_email) REFERENCES user(email);
ALTER TABLE review ADD CONSTRAINT review_fk2 FOREIGN KEY (album_id) REFERENCES album(id);
ALTER TABLE review ADD CONSTRAINT critic CHECK (critic != "" );
ALTER TABLE review ADD CONSTRAINT rating CHECK (rating >= 0 AND rating <= 5);
ALTER TABLE user ADD CONSTRAINT email CHECK (email like '%@%.%' AND email != "");
ALTER TABLE user ADD CONSTRAINT password CHECK (password != "" );
ALTER TABLE music ADD CONSTRAINT music_fk1 FOREIGN KEY (album_id) REFERENCES album(id);
ALTER TABLE music ADD CONSTRAINT id CHECK (id != "");
ALTER TABLE music ADD CONSTRAINT track CHECK (track > 0 );
ALTER TABLE music ADD CONSTRAINT title CHECK (title != "");
ALTER TABLE notification ADD CONSTRAINT notification_fk1 FOREIGN KEY (user_email) REFERENCES user(email);
ALTER TABLE notification ADD CONSTRAINT notification CHECK (text != "");
ALTER TABLE upload ADD CONSTRAINT upload_fk1 FOREIGN KEY (music_id) REFERENCES music(id);
ALTER TABLE upload ADD CONSTRAINT upload_fk2 FOREIGN KEY (user_email) REFERENCES user(email);
ALTER TABLE upload ADD CONSTRAINT constraint_0 CHECK (musicFileName != "");
ALTER TABLE playlist ADD CONSTRAINT playlist_fk1 FOREIGN KEY (user_email) REFERENCES user(email);
ALTER TABLE playlist ADD CONSTRAINT name CHECK (name != "");
ALTER TABLE upload_user ADD CONSTRAINT upload_user_fk1 FOREIGN KEY (upload_music_id) REFERENCES upload(music_id);
ALTER TABLE upload_user ADD CONSTRAINT upload_user_fk2 FOREIGN KEY (upload_user_email) REFERENCES upload(user_email);
ALTER TABLE upload_user ADD CONSTRAINT upload_user_fk3 FOREIGN KEY (user_email) REFERENCES user(email);
ALTER TABLE artist_user ADD CONSTRAINT artist_user_fk1 FOREIGN KEY (artist_name) REFERENCES artist(name);
ALTER TABLE artist_user ADD CONSTRAINT artist_user_fk2 FOREIGN KEY (user_email) REFERENCES user(email);
ALTER TABLE music_playlist ADD CONSTRAINT music_playlist_fk1 FOREIGN KEY (music_id) REFERENCES music(id);
ALTER TABLE music_playlist ADD CONSTRAINT music_playlist_fk2 FOREIGN KEY (playlist_name) REFERENCES playlist(name);
ALTER TABLE music_playlist ADD CONSTRAINT music_playlist_fk3 FOREIGN KEY (playlist_user_email) REFERENCES playlist(user_email);
ALTER TABLE shows_artist ADD CONSTRAINT shows_artist_fk1 FOREIGN KEY (shows_id) REFERENCES shows(id);
ALTER TABLE shows_artist ADD CONSTRAINT shows_artist_fk2 FOREIGN KEY (artist_name) REFERENCES artist(name);