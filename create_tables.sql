drop schema dropmusic;
create database dropmusic;
use dropmusic;

CREATE TABLE artist (
	name	 varchar(200),
	details	 varchar(10000),
	PRIMARY KEY(name)
);

CREATE TABLE album (
	id		 varchar(80),
	title	 varchar(200),
	description	 varchar(10000),
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
	lyrics	 varchar(512),
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

INSERT into user (email, password, editor) VALUES ('admin', 'admin', true);
INSERT into user (email, password, editor) VALUES ('roman', 'roman', false);
INSERT into user (email, password, editor) VALUES ('diogo', 'diogo', false);


INSERT into artist (name, details)
VALUES ('Tool', 'Tool is an American rock band from Los Angeles, California. Formed in 1990, the group''s line-up includes drummer Danny Carey, guitarist Adam Jones, and vocalist Maynard James Keenan. Justin Chancellor has been the band''s bassist since 1995, replacing their original bassist Paul D''Amour. Tool has won three Grammy Awards, performed worldwide tours, and produced albums topping the charts in several countries.')

insert into album (id, title, description, genre, launch_date, editor_label, artist_name)
VALUES ('4c82932e-57cc-4971-9373-0ed2b7ef3027', 'Lateralus', 'Lateralus (/ˌlætəˈræləs/) is the third studio album by American rock band Tool. It was released on May 15, 2001 through Volcano Entertainment. The album was recorded at Cello Studios in Hollywood and The Hook, Big Empty Space, and The Lodge, in North Hollywood, between October 2000 and January 2001. David Bottrill, who had produced the band''s two previous releases Ænima and Salival, produced the album along with the band. On August 23, 2005, Lateralus was released as a limited edition two-picture-disc vinyl LP in a holographic gatefold package.', 'Progressive Metal', '2001-05-15', 'Volcano Entertainment', 'Tool');

insert into music (id, track, title, album_id, lyrics)
VALUES ('4f5e6c46-b408-45c4-a3c6-6e4372ca0208', 1, 'The Grudge', '4c82932e-57cc-4971-9373-0ed2b7ef3027', 'Wear the grudge like a crown of negativity
Calculate what we will or will not tolerate
Desperate to control all and everything
Unable to forgive your scarlet lettermen

Clutch it like a cornerstone, otherwise it all comes down
Justify denials and grip ''em to the lonesome end
Clutch it like a cornerstone, otherwise it all comes down
Terrified of being wrong, ultimatum prison cell

Saturn ascends
Choose one or ten
Hang on or be humbled again

Clutch it like a cornerstone, otherwise it all comes down
Justify denials and grip ''em to the lonesome end
Saturn ascends, comes round again
Saturn ascends, the one, the ten
Ignorant to the damage done

Wear the grudge like a crown of negativity
Calculate what we will or will not tolerate
Desperate to control all and everything
Unable to forgive these scarlet lettermen

Wear the grudge like a crown
Desperate to control
Unable to forgive and sinking deeper

Defining
Confining
And sinking deeper
Controlling
Defining
And we''re sinking deeper

Saturn comes back around to show you everything
Let''s you choose what you will not see and then
Drags you down like a stone or lifts you up again
Spits you out like a child, light and innocent

Saturn comes back around.
Lifts you up like a child or
Drags you down like a stone
To consume you till you choose to let this go
Choose to let this go

Give away the stone
Let the oceans take and trans mutate this cold and fated anchor
Give away the stone
Let the waters kiss and trans mutate these leaden grudges into gold

Let go

');

insert into music (id, track, title, lyrics, album_id)
VALUES ('84b9c726-a8f4-41df-aa3c-bccba985d39f ', 2, 'Eon Blue Apocalypse', '(instrumental)', '4c82932e-57cc-4971-9373-0ed2b7ef3027')