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


INSERT INTO artist (name, details) VALUES ('Kendrick Lamar'
, 'Kendrick Lamar Duckworth (born June 17, 1987) is an American rapper, songwriter, and record producer. He has been branded as the "new king of hip hop" numerous times.

Raised in Compton, California, Lamar embarked on his musical career as a teenager under the stage name K-Dot, releasing a mixtape that garnered local attention and led to his signing with indie record label Top Dawg Entertainment (TDE). He began to gain recognition in 2010, after his first retail release, Overly Dedicated. The following year, he independently released his first studio album, Section.80, which included his debut single, "HiiiPoWeR". By that time, he had amassed a large online following and collaborated with several prominent hip hop artists, including The Game, Busta Rhymes, and Snoop Dogg. Lamar''s major label debut album, good kid, m.A.A.d city, was released in 2012 by TDE, Aftermath, and Interscope Records to critical acclaim. It debuted at #2 on the US Billboard 200 and was later certified platinum by the RIAA. The record contained the top 40 singles "Swimming Pools (Drank)", "Bitch, Don''t Kill My Vibe", and "Poetic Justice". His critically acclaimed third album To Pimp a Butterfly (2015) incorporated elements of funk, soul, jazz, and spoken word. It debuted atop the charts in the US and the UK, and won the Grammy Award for Best Rap Album at the 58th ceremony. In 2016, Lamar released Untitled Unmastered, a collection of unreleased demos that originated during the recording sessions for Butterfly. He released his fourth album Damn in 2017 to further acclaim; its lead single "Humble" topped the US Billboard Hot 100 chart.

Aside from his solo career, Lamar is also known as a member of the West Coast hip hop supergroup Black Hippy, alongside his TDE label-mates and fellow South Los Angeles–based rappers Ab-Soul, Jay Rock, and Schoolboy Q.

Lamar has received many accolades over the course of his career, including twelve Grammy Awards. In early 2013, MTV named him the "Hottest MC in the Game", on their annual list. Time named him one of the 100 most influential people in the world in 2016. In 2018, Damn became the first non-classical and non-jazz album to be awarded the Pulitzer Prize for Music.[7] ');