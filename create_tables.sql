drop schema dropmusic;
create database dropmusic;
use dropmusic;

CREATE TABLE artist (
	name	 varchar(200),
	details varchar(5000),
	PRIMARY KEY(name)
);

CREATE TABLE album (
	id		 bigint AUTO_INCREMENT,
	title	 varchar(200),
	description	 varchar(5000),
	genre	 varchar(100),
	launch_date	 date,
	editor_label varchar(100),
	artist_name	 varchar(200) NOT NULL,
	PRIMARY KEY(id),
  UNIQUE KEY(title, artist_name)
);

CREATE TABLE review (
	critic	 varchar(300),
	rating	 int,
	user_email varchar(200),
	album_id	 bigint,
	PRIMARY KEY(user_email,album_id)
);

CREATE TABLE user (
	email	 varchar(200),
	password varchar(50),
	editor	 boolean DEFAULT false,
	token varchar(200) DEFAULT null,
	email_dropbox varchar(200) DEFAULT null,
	PRIMARY KEY(email)
);

CREATE TABLE music (
	id	 bigint AUTO_INCREMENT,
	track	 int,
	title	 varchar(200),
	lyrics	 varchar(5000),
	album_id bigint NOT NULL,
	PRIMARY KEY(id),
  UNIQUE KEY(track, title)
);

CREATE TABLE notification (
	id		 bigint AUTO_INCREMENT,
	notification varchar(100),
	user_email	 varchar(200) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE upload (
	musicfilename varchar(300),
	music_id	 bigint,
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
	id	 BIGINT AUTO_INCREMENT,
	PRIMARY KEY(id)
);

CREATE TABLE allowed (
	upload_music_id	 bigint,
	allowed_email varchar(200),
	user_email	 varchar(200),
	PRIMARY KEY(upload_music_id,allowed_email,user_email)
);

CREATE TABLE editor (
	artist_name varchar(200),
	user_email	 varchar(200),
	PRIMARY KEY(artist_name,user_email)
);

CREATE TABLE music_playlist (
	music_id		 bigint,
	playlist_name	 varchar(512),
	PRIMARY KEY(music_id, playlist_name)
);

CREATE TABLE shows_artist (
	shows_id	 BIGINT,
	artist_name varchar(200),
	PRIMARY KEY(shows_id,artist_name)
);

ALTER TABLE album ADD CONSTRAINT album_fk1 FOREIGN KEY (artist_name) REFERENCES artist(name) ON DELETE CASCADE;
ALTER TABLE review ADD CONSTRAINT review_fk1 FOREIGN KEY (user_email) REFERENCES user(email) ON DELETE CASCADE;
ALTER TABLE review ADD CONSTRAINT review_fk2 FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE;
ALTER TABLE music ADD CONSTRAINT music_fk1 FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE;
ALTER TABLE notification ADD CONSTRAINT notification_fk1 FOREIGN KEY (user_email) REFERENCES user(email) ON DELETE CASCADE;
ALTER TABLE upload ADD CONSTRAINT upload_fk1 FOREIGN KEY (music_id) REFERENCES music(id) ON DELETE CASCADE;
ALTER TABLE upload ADD CONSTRAINT upload_fk2 FOREIGN KEY (user_email) REFERENCES user(email) ON DELETE CASCADE;
ALTER TABLE playlist ADD CONSTRAINT playlist_fk1 FOREIGN KEY (user_email) REFERENCES user(email) ON DELETE CASCADE;
ALTER TABLE allowed ADD CONSTRAINT allowed_fk1 FOREIGN KEY (upload_music_id) REFERENCES upload(music_id) ON DELETE CASCADE;
ALTER TABLE allowed ADD CONSTRAINT allowed_fk2 FOREIGN KEY (allowed_email) REFERENCES upload(user_email) ON DELETE CASCADE;
ALTER TABLE allowed ADD CONSTRAINT allowed_fk3 FOREIGN KEY (user_email) REFERENCES user(email) ON DELETE CASCADE;
ALTER TABLE editor ADD CONSTRAINT editor_fk1 FOREIGN KEY (artist_name) REFERENCES artist(name) ON DELETE CASCADE;
ALTER TABLE editor ADD CONSTRAINT editor_fk2 FOREIGN KEY (user_email) REFERENCES user(email) ON DELETE CASCADE;
ALTER TABLE music_playlist ADD CONSTRAINT music_playlist_fk1 FOREIGN KEY (music_id) REFERENCES music(id) ON DELETE CASCADE;
ALTER TABLE music_playlist ADD CONSTRAINT music_playlist_fk2 FOREIGN KEY (playlist_name) REFERENCES playlist(name) ON DELETE CASCADE;
ALTER TABLE shows_artist ADD CONSTRAINT shows_artist_fk1 FOREIGN KEY (shows_id) REFERENCES shows(id) ON DELETE CASCADE;
ALTER TABLE shows_artist ADD CONSTRAINT shows_artist_fk2 FOREIGN KEY (artist_name) REFERENCES artist(name) ON DELETE CASCADE;


INSERT into user (email, password, editor) VALUES ('admin', 'admin', true);
INSERT into user (email, password) VALUES ('roman', 'roman');
INSERT into user (email, password) VALUES ('diogo', 'diogo');

INSERT into notification (notification, user_email) VALUES ('You''ve been promoted to Editor', 'admin');


INSERT into artist (name, details)
VALUES ('Tool', 'Tool is an American rock band from Los Angeles, California. Formed in 1990, the group''s line-up includes drummer Danny Carey, guitarist Adam Jones, and vocalist Maynard James Keenan. Justin Chancellor has been the band''s bassist since 1995, replacing their original bassist Paul D''Amour. Tool has won three Grammy Awards, performed worldwide tours, and produced albums topping the charts in several countries.');

insert into album (title, description, genre, launch_date, editor_label, artist_name)
VALUES ('Lateralus', 'Lateralus is the third studio album by American rock band Tool. It was released on May 15, 2001 through Volcano Entertainment. The album was recorded at Cello Studios in Hollywood and The Hook, Big Empty Space, and The Lodge, in North Hollywood, between October 2000 and January 2001. David Bottrill, who had produced the band''s two previous releases Ænima and Salival, produced the album along with the band. On August 23, 2005, Lateralus was released as a limited edition two-picture-disc vinyl LP in a holographic gatefold package.', 'Progressive Metal', '2001-05-15', 'Volcano Entertainment', 'Tool');

insert into review (critic, rating, user_email, album_id) values ('Very nice', 5, 'roman', 1);
insert into review (critic, rating, user_email, album_id) values ('wtf is this?', 3, 'diogo', 1);

insert into music (track, title, album_id, lyrics)
VALUES (1, 'The Grudge', 1, 'Wear the grudge like a crown of negativity
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

insert into music (track, title, lyrics, album_id)
VALUES (2, 'Eon Blue Apocalypse', '(instrumental)', 1);

insert into music (track, title, lyrics, album_id)
values (3, 'The Patient', 'A groan of tedium escapes me,
Startling the fearful.
Is this a test? It has to be,
Otherwise I can''t go on.
Draining patience, drain vitality.
This paranoid, paralyzed vampire act''s a little old.

But I''m still right here
Giving blood, keeping faith,
And I''m still right here.

But I''m still right here
Giving blood, keeping faith,
And I''m still right here.

Wait it out.
Gonna wait it out.
Be patient (wait it out).

If there were no rewards to reap,
No loving embrace to see me through,
This tedious path I''ve chosen here,
I certainly would''ve walked away by now.
Gonna wait it out.

If there were no desire to heal
The damaged and broken met along
This tedious path I''ve chosen here
I certainly would''ve walked away by now.

And I still may, I still may be patient, be patient, be patient.

I must keep reminding myself of this.
I must keep reminding myself of this.
I must keep reminding myself of this.
I must keep reminding myself of this.

And if there were no rewards to reap,
No loving embrace to see me through
This tedious path I''ve chosen here,
I certainly would''ve walked away by now.
And I still may.
And I still may.
And I still may.

And I am gonna wait it out.
Gonna wait it out.
Gonna wait it out.
Gonna wait it out.', 1);

insert into music (track, title, lyrics, album_id)
values (4, 'Mantra', '(instrumental)', 1);

insert into music (track, title, lyrics, album_id)
values (5, 'Schism', 'I know the pieces fit ''cause I watched them fall away
Mildewed and smoldering. Fundamental differing
Pure intention juxtaposed will set two lovers souls in motion
Disintegrating as it goes testing our communication
The light that fueled our fire then has burned a hole between us so
We cannot seem to reach an end crippling our communication

I know the pieces fit ''cause I watched them tumble down
No fault, none to blame, it doesn''t mean I don''t desire to
Point the finger, blame the other, watch the temple topple over
To bring the pieces back together, rediscover communication

The poetry that comes from the squaring off between
And the circling is worth it
Finding beauty in the dissonance

There was a time that the pieces fit, but I watched them fall away
Mildewed and smoldering, strangled by our coveting
I''ve done the math enough to know the dangers of our second guessing
Doomed to crumble unless we grow, and strengthen our communication

Cold silence
Has a tendency
To atrophy any
Sense of compassion
Between supposed lovers
Between supposed lovers

I know the pieces fit [8x]', 1);

insert into music (track, title, lyrics, album_id)
values (6, 'Parabol', 'So familiar and overwhelmingly warm
This one, this form I hold now.
Embracing you, this reality here,
This one, this form I hold now, so
Wide eyed and hopeful.
Wide eyed and hopefully wild.

We barely remember what came before this precious moment,
Choosing to be here right now. Hold on, stay inside...
This body holding me, reminding me that I am not alone in
This body makes me feel eternal. All this pain is an illusion.', 1);

insert into music (track, title, lyrics, album_id)
values (7, 'Parabola', 'We barely remember who or what came before this precious moment
We are choosing to be here right now
Hold on, stay inside...

This holy reality, this holy experience
Choosing to be here in...
This body, this body holding me
Be my reminder here that I am not alone in...
This body, this body holding me, feeling eternal
All this pain is an illusion

Alive!

In this holy reality, in this holy experience
Choosing to be here in...
This body, this body holding me
Be my reminder here that I am not alone in...
This body, this body holding me, feeling eternal
All this pain is an illusion

Twirling round with this familiar parable
Spinning, weaving round each new experience
Recognize this as a holy gift and celebrate this chance to be alive and breathing
A chance to be alive and breathing

This body holding me reminds me of my own mortality
Embrace this moment, remember, we are eternal
All this pain is an illusion', 1);

insert into music (track, title, lyrics, album_id)
values (8, 'Ticks & Leeches', 'Suck and suck
Suckin'' up all you can
Suckin'' up all you can suck
Workin'' up under my patience like a little tick
Fat little parasite

Suck me dry
My friend, bruised and borrowed
You thieving bastards
You have turned my blood cold and bitter
Beat my compassion black and blue

Hope this is what you wanted
Hope this is what you had in mind
''Cause this is what you''re getting
I hope you''re choking. I hope you choke on this

Taken all I can, taken all I can, fuckin'' take
Taken all you can, taken all you can fuckin'' take
Got nothing left to give to you

Blood-suckin'' parasitic little tick
Blood-suckin'' parasitic little tick
Blood-suckin'' parasitic little tick
Take what you want and then go

Hope this is what you wanted
Hope this is what you had in mind
''Cause this is what you''re getting

Suck me dry suck
Suck me dry suck
Suck me dry suck
Suck me dry

Is this what you wanted?
Is this what you had in mind?
Is this what you wanted?
''Cause this this is what you''re getting
I hope, I hope, I hope you choke', 1);

insert into music (track, title, lyrics, album_id)
values (9, 'Lateralus', 'Black then white are all I see
In my infancy.
Red and yellow then came to be,
Reaching out to me,
Lets me see.

As below, so above and beyond,
I imagine drawn beyond the lines of reason.
Push the envelope. Watch it bend.

Over-thinking, over-analyzing separates the body from the mind.
Withering my intuition, missing opportunities and I must
Feed my will to feel my moment drawing way outside the lines.

Black then white are all I see
In my infancy.
Red and yellow then came to be,
Reaching out to me.
Lets me see

There is so much more
And beckons me
To look through to these
Infinite possibilities.

As below, so above and beyond,
I imagine drawn outside the lines of reason.
Push the envelope. Watch it bend.

Over-thinking, over-analyzing separates the body from the mind.
Withering my intuition, leaving opportunities behind.

Feed my will to feel this moment urging me to cross the line.
Reaching out to embrace the random.
Reaching out to embrace whatever may come.

I embrace my desire to
I embrace my desire to feel the rhythm, to feel connected
Enough to step aside and weep like a widow
To feel inspired, to fathom the power,
To witness the beauty, to bathe in the fountain,
To swing on the spiral, to swing on the spiral,

To swing on the spiral of our divinity and still be a human.

With my feet upon the ground
I lose myself between the sounds
And open wide to suck it in.
I feel it move across my skin.

I''m reaching up and reaching out.
I''m reaching for the random or whatever will bewilder me.
Whatever will bewilder me.

And following our will and wind
We may just go where no one''s been.
We''ll ride the spiral to the end
And may just go where no one''s been.

Spiral out. Keep going. [4x]', 1);

insert into music (track, title, lyrics, album_id)
values (10, 'Disposition', 'Mention this to me
Mention this to me

Watch the weather change
Watch the weather change
Watch the weather change
Watch the weather
Change

Mention this to me
Mention something
Mention anything
Mention this to me
Watch the weather change
Watch the weather change
Watch the weather change
Watch the weather change
Watch the weather
Change

Watch the weather
Change

Mention this to me
Mention something
Mention anything
Mention this to me
Watch the weather
Watch the weather
Change
Watch the weather change
Watch the weather...', 1);

insert into music (track, title, lyrics, album_id)
values (11, 'Reflection', 'I have come curiously close to the end, down
Beneath my self-indulgent pitiful hole,
Defeated, I concede and
Move closer
I may find comfort here
I may find peace within the emptiness
How pitiful

It''s calling me...

And in my darkest moment, fetal and weeping
The moon tells me a secret - my confidant
As full and bright as I am
This light is not my own and
A million light reflections pass over me

Its source is bright and endless
She resuscitates the hopeless
Without her, we are lifeless satellites drifting

And as I pull my head out I am without one doubt
Don''t wanna be down here soothing my narcissism.
I must crucify the ego before it''s far too late
I pray the light lifts me out
Before I pine away.

So crucify the ego, before it''s far too late
To leave behind this place so negative and blind and cynical,
And you will come to find that we are all one mind
Capable of all that''s imagined and all conceivable.
Just let the light touch you
And let the words spill through
And let them pass right through
Bringing out our hope and reason...
before we pine away.', 1);

insert into music (track, title, lyrics, album_id)
values (12, 'Triad', '(instrumental – includes 2:10 of silence at end of track)', 1);

insert into music (track, title, lyrics, album_id)
values (13, 'Faaip de Oiad', '', 1);


INSERT INTO artist (name, details) VALUES ('Kendrick Lamar'
, 'Kendrick Lamar Duckworth (born June 17, 1987) is an American rapper, songwriter, and record producer. He has been branded as the "new king of hip hop" numerous times.
Raised in Compton, California, Lamar embarked on his musical career as a teenager under the stage name K-Dot, releasing a mixtape that garnered local attention and led to his signing with indie record label Top Dawg Entertainment (TDE). He began to gain recognition in 2010, after his first retail release, Overly Dedicated. The following year, he independently released his first studio album, Section.80, which included his debut single, "HiiiPoWeR". By that time, he had amassed a large online following and collaborated with several prominent hip hop artists, including The Game, Busta Rhymes, and Snoop Dogg. Lamar''s major label debut album, good kid, m.A.A.d city, was released in 2012 by TDE, Aftermath, and Interscope Records to critical acclaim. It debuted at #2 on the US Billboard 200 and was later certified platinum by the RIAA. The record contained the top 40 singles "Swimming Pools (Drank)", "Bitch, Don''t Kill My Vibe", and "Poetic Justice". His critically acclaimed third album To Pimp a Butterfly (2015) incorporated elements of funk, soul, jazz, and spoken word. It debuted atop the charts in the US and the UK, and won the Grammy Award for Best Rap AlbumModel at the 58th ceremony. In 2016, Lamar released Untitled Unmastered, a collection of unreleased demos that originated during the recording sessions for Butterfly. He released his fourth album Damn in 2017 to further acclaim its lead single "Humble" topped the US Billboard Hot 100 chart.
Aside from his solo career, Lamar is also known as a member of the West Coast hip hop supergroup Black Hippy, alongside his TDE label-mates and fellow South Los Angeles–based rappers Ab-Soul, Jay Rock, and Schoolboy Q.
Lamar has received many accolades over the course of his career, including twelve Grammy Awards. In early 2013, MTV named him the "Hottest MC in the Game", on their annual list. Time named him one of the 100 most influential people in the world in 2016. In 2018, Damn became the first non-classical and non-jazz album to be awarded the Pulitzer Prize for MusicModel.[7] ');

INSERT INTO album (title, description, genre, launch_date, editor_label, artist_name)
VALUES ('To Pimp a Butterfly', 'o Pimp a Butterfly is the third studio album by American rapper Kendrick Lamar. It was released on March 15, 2015, by Aftermath Entertainment, Interscope Records and Top Dawg Entertainment.
The album was recorded in studios throughout the United States, with production from Sounwave, Terrace Martin, Taz "Tisa" Arnold, Thundercat, Rahki, LoveDragon, Flying Lotus, Pharrell Williams, Boi-1da, knxwledge, and several other high-profile hip hop producers, as well as executive production from Dr. Dre and Anthony "Top Dawg" Tiffith. The album incorporates elements of jazz, funk, soul, spoken word, and avant-garde music and explores a variety of political and personal themes concerning African-American culture, racial inequality, depression, and institutional discrimination.', 'Hip-Hop', '2015-03-15', 'Aftermath Entertainment' , 'Kendrick Lamar');

insert into review (critic, rating, user_email, album_id) VALUES ('Best album ever', 5, 'diogo', 2);
insert into review (critic, rating, user_email, album_id) VALUES ('I''ve seen better', 4, 'roman', 2);

INSERT INTO music (track, title, album_id, lyrics)
VALUES (1 , '"Wesley''s Theory" (featuring George Clinton and Thundercat)', 2, 'When the four corners of this cocoon collide
You’ll slip through the cracks hoping that you’ll survive
Gather your wind, take a deep look inside
Are you really who they idolize?
To pimp a butterfly
At first, I did love you
But now I just wanna fuck
Late night thinkin'' of you
Until I got my nut
Tossed and turned, lesson learned
You was my first girlfriend
Bridges burned, all across the board
Destroyed, but what for?
When I get signed, homie I''mma act a fool
Hit the dance floor, strobe lights in the room
Snatch your little secretary bitch for the homies
Blue eyed devil with a fat ass smokey
I''mma buy a brand new Caddy on fours
Trunk the hood up, two times, deuce four
Platinum on everything, platinum on wedding ring
Married to the game, made a bad bitch yours
When I get signed homie I''mma buy a strap
Straight from the CIA, set it on my lap
Take a few M-16s to the hood
Pass ''em all out on the block, what''s good?
I''mma put the Compton swap meet by the White House
Republican, run up, get socked out
Hit the press with a Cuban link on my neck
Uneducated but I got a million dollar check, like that
We should never gave, we should never gave
Niggas money go back home, money go back home
We should never gave, we should never gave
Niggas money go back home, money go back home
At first, I did love you
But now I just wanna fuck
Late night thinkin'' of you
Until I got my nut
Tossed and turned, lesson learned
You was my first girlfriend
Bridges burned, all across the board
Destroyed, but what for?
Yo what''s up? It''s Dre
Remember the first time you came out to the house?
You said you wanted a spot like mine
But remember, anybody can get it
The hard part is keeping it, motherfucker
What you want you? A house or a car?
Forty acres and a mule, a piano, a guitar?
Anything, see, my name is Uncle Sam on your dollar
Motherfucker you can live at the mall
I know your kind (that''s why I''m kind)
Don''t have receipts (oh man, that''s fine)
Pay me later, wear those gators
Cliche and say, fuck your haters
I can see the borrow in you
I can see the dollar in you
Little white lies with a snow white collar in you
But it''s whatever though because I''m still followin'' you
Because you make me feel forever baby, count it all together baby
Then hit the register and make me feel better baby
Your horoscope is a gemini, two sides
So you better cop everything two times
Two coupes, two chains, two c-notes
Too much and enough both we know
Christmas, tell ''em what''s on your wish list
Get it all, you deserve it Kendrick
And when you get the White House, do you
But remember, you ain''t pass economics in school
And everything you buy, taxes will deny
I''ll Wesley Snipe your ass before thirty-five
Lookin'' down is quite a drop (it''s quite a drop, drop)
Lookin'' good when you''re on top (when you''re on top you got it)
A lot of metaphors, leavin'' miracles metaphysically in a state of euphoria
Look both ways before you cross my mind
We should never gave, we should never gave
Niggas money go back home, money go back home
We should never gave, we should never gave
Niggas money go back home, money go back home
Tax man comin''');


INSERT INTO music (track, title, album_id, lyrics)
VALUES (2, 'For Free? (Interlude)', 2, 'I go on and on
Can''t understand how I last so long
I must have the superpowers
Last 223 thousand hours
And it''s cause I''m off of CC
And I''m off the Hennessy
And like your boy from Compton said
You know this dick ain''t free!
I got girls that I shoulda made pay for it
Got girls that I should made wait for it
I got girls that''ll cancel a flight back home
Stay another day for it
You got attitude on na na
And your pussy on agua
And your stomach on flat flat
And your ass on what''s that?
And, yeah, I need it all right now
Last year I had drama, girl not right now
I was never gon'' chat what we talkin'' about
You the only one I know could fit it all in them, man
I always wonder if you ask yourself
Is it just me?
Is it just me?
Or is this sex so good I shouldn''t have to fuck for free?
Is it just me?
Is it just me?
Is this sex so good I shouldn''t have to
Fuck for free
I know you workin'' day and night to get a college degree
Bet nobody that you''ve been with even know you a freak, right?
You know you only do that with me, right?
Yeah, double checkin'' on you
You know I never put the pressure on you
You know that you make your own mind up
You knew what it was when you signed up
Now you gotta run it up
I be out of words, tryna sum it up
Girl you throw it back like one love
Even let me slash on the tour bus
Yeah I talk to her but she don''t do enough
Even though you in the hood I''m still pullin'' up
Dip, dip, straight to your doorstep
This the real thing, can you feel the force yet
I always wonder if you ask yourself
Is it just me?
Is it just me?
Or is this sex so good I shouldn''t have to fuck for free?
Is it just me?
Yeah, is it just me?
Is this sex so good I shouldn''t have to
(Would you fuck me for free?)
Another one
They don''t want me to have another anthem
So I made sure I got another anthem
It''s We The Best OVO
Summers ours
It always has been
Don''t ever play yourself
One time for Future the Prince
One time for 40
One time for Drake
Another one
Bless up');

insert into music (track, title, lyrics, album_id)
values (3, 'King Kunta', 'I got a bone to pick
I don''t want you monkey mouth motherfuckers sittin'' in my throne again
(Aye aye nigga what''s happenin'' nigga, K Dot back in the hood nigga)
I''m mad (He mad), but I ain''t stressin''
True friends, one question

Bitch where you when I was walkin''?
Now I run the game got the whole world talkin'' (King Kunta)
Everybody wanna cut the legs off him (King Kunta) Kunta
Black man taking no losses
Bitch where you when I was walkin''?
Now I run the game, got the whole world talkin'' (King Kunta)
Everybody wanna cut the legs off him

When you got the yams (What''s the yams?)
The yam is the power that be
You can smell it when I''m walking down the street
(Oh yes we can, oh yes we can)
I can dig rapping, but a rapper with a ghost writer
What the fuck happened?
(Oh no) I swore I wouldn''t tell
But most of you share bars like you got the bottom bunk in a two man cell
(A two man cell)
Something''s in the water (Something''s in the water)
And if I got a brown nose for some gold then I''d rather be a bum than a motherfuckin'' baller

Bitch where you when I was walkin''?
Now I run the game got the whole world talkin'' (King Kunta)
Everybody wanna cut the legs off him (King Kunta) Kunta
Black man taking no losses
Bitch where you when I was walkin''?
Now I run the game, got the whole world talkin'' (King Kunta)
Everybody wanna cut the legs off him

When you got the yams (What''s the yams?)
The yam brought it out of Richard Pryor
Manipulated Bill Clinton with desires
24/7, 365 days times two
I was contemplatin'' gettin'' on stage
Just to go back to the hood see my enemies and say...

Bitch where you when I was walkin''?
Now I run the game got the whole world talkin'' (King Kunta)
Everybody wanna cut the legs off him (King Kunta) Kunta
Black man taking no losses
Bitch where you when I was walkin''?
Now I run the game, got the whole world talkin'' (King Kunta)
Everybody wanna cut the legs off him

You goat mouth mammy fucker
I was gonna kill a couple rappers but they did it to themselves
Everybody''s suicidal they don''t even need my help
This shit is elementary, I''ll probably go to jail
If I shoot at your identity and bounce to the left
Stuck a flag in my city, everybody''s screamin'' "Compton"
I should probably run for Mayor when I''m done, to be honest
And I put that on my Mama and my baby boo too
Twenty million walkin'' out the court buildin'' woo woo
Ah yeah fuck the judge
I made it past 25 and there I was
A little nappy headed nigga with the world behind him
Life ain''t shit but a fat vagina
Screamin'' "Annie are you ok? Annie are you ok?"
Limo tinted with the gold plates
Straight from the bottom, this the belly of the beast
From a peasant to a prince to a motherfucking king

Bitch where was you when I was walkin''-
[POP]
(By the time you hear the next pop, the funk shall be within you)
[POP]
Now I run the game got the whole world talkin'' (King Kunta)
Everybody wanna cut the legs off him (King Kunta) Kunta
Black man taking no losses
Bitch where was you when I was walkin''
Now I run the game got the whole world talkin'' (King Kunta)
Everybody wanna cut the legs off him

(Funk, funk, funk, funk, funk, funk)
We want the funk
We want the funk
(Now if I give you the funk, you gon'' take it)
We want the funk
(Now if I give you the funk, you gon'' take it)
We want the funk
(Now if I give you the funk, you gon'' take it)
We want the funk
(Do you want the funk?)
We want the funk
(Do you want the funk?)
We want the funk
(Now if I give you the funk, you gon'' take it)
We want the funk
I remember you was conflicted, misusing your influence', 2);

insert into music (track, title, lyrics, album_id)
values (4, 'Institutionalized', 'What money got to do with it
When I don''t know the full definition of a rap image?
I''m trapped inside the ghetto and I ain''t proud to admit it
Institutionalized, I keep runnin'' back for a visit
Hol'' up
Get it back
I said I''m trapped inside the ghetto and I ain''t proud to admit it
Institutionalized, I could still kill me a nigga, so what?

[Anna Wise:]
If I was the president
I''d pay my mama''s rent
Free my homies and them
Bulletproof my Chevy doors
Lay in the White House and get high, Lord
Who ever thought?
Master take the chains off me

Life can be like a box of chocolate
Quid pro quo, somethin'' for somethin'', that''s the obvious
Oh shit, flow''s so sick, don''t you swallow it
Bitin'' my style, you''re salmonella poison positive
I can just alleviate the rap industry politics
Milk the game up, never lactose intolerant
The last remainder of real shit, you know the obvious
Me scholarship? No, streets put me through colleges
Be all you can be, true, but the problem is
A dream''s only a dream if work don''t follow it
Remind me of the homies that used to know me, now follow this
I''ll tell you my hypothesis, I''m probably just way too loyal
K Dizzle would do it for you, my niggas think I''m a god
Truthfully all of ''em spoiled, usually you''re never charged
But somethin'' came over you once I took you to the fuckin'' BET Awards
You lookin'' at artists like the harvests
So many Rollies around you and you want all of them
Somebody told me you thinkin'' ''bout snatchin'' jewelry
I should''ve listened what my grandmama said to me

[Hook - Bilal:]
Shit don''t change until you get up and wash your ass nigga
Shit don''t change until you get up and wash your ass
Shit don''t change until you get up and wash your ass nigga
Oh now, slow down

[Snoop Dogg:]
And once upon a time in a city so divine
Called West Side Compton, there stood a little nigga
He was 5 foot something, God bless the kid
Took his homie to the show and this is what they said

What I''m s''posed to do when I''m lookin'' at walkin'' licks?
The convicts talk ''bout matchin'', money and foreign whips
The private jets and passports, presidential glass floor
Gold bottles, gold shared, sniffin'' up the ass for
Instagram flicks, suck a dick, fuck is this?
One more suck away from wavin'' flashy wrist
My defense mechanism tell me to get him, quickly because he got it
It''s the recession, then why the fuck he in King of Diamonds?
No more livin'' poor, meet my .44
When I see ''em, put the per diem on the floor
Now Kendrick, know they''re your co-workers
But it''s gon'' take a lot for this pistol go cold turkey
Now I can watch his watch on the TV and be okay
But see I''m on the clock once that watch landin'' in LA
Remember steal from the rich and givin'' it back to the poor?
Well that''s me at these awards
I guess my grandmama was warnin'' a boy
She said...

[Hook]

[Snoop Dogg:]
And once upon a time in a city so divine
Called West Side Compton, there stood a little nigga
He was 5 foot something, dazed and confused
Talented but still under the neighborhood ruse
You can take your boy out the hood but you can''t take the hood out the homie
Took his show money, stashed it in the mozey wozey
Hollywood''s nervous
Fuck you, goodnight, thank you much for your service', 2);

insert into music (track, title, lyrics, album_id)
values (5, 'These Walls', 'I remember you was conflicted, misusing your influence
Sometimes, I did the same

If these walls could talk [x6]

[Anna Wise:]
Sex, she just want to close her eyes and sway
If you, if you, if you exercise your right to work it out
Its true, its true, its true, shout out to the birthday girls say webserver
Say webserver, everyone deserves a night to play
And shes plays only when you tell her no

[Hook - Bilal:]
If these walls could talk
I can feel your reign when it cries gold lives inside of you
If these walls could talk
I love it when I’m in it, I love it when I’m in it

If these walls could talk they’d tell me to swim good
No boat I float better than he would
No life jacket I’m not the guard in Nazareth
But your flood can be misunderstood
Wall telling me they full of pain, resentment
Need someone to live in them just to relieve tension
Me? I’m just a tenant
My lord said these walls vacant more than a minute
These walls are vulnerable, exclamation
Interior pink, color coordinated
I interrogated every nook and cranny
I mean its still amazing before they couldn’t stand me
These walls want to cry tears
These walls happier when I’m here
These walls never could hold up
Everytime I come around demolition might crush

[Hook]

If these walls could talk they’d tell me to go deep
Yelling at me continuously I can see
Your defense mechanism is my decision
Knock these walls down that’s my religion
Walls feeling like they ready to close in
I suffocate then catch my second wind
I resonate in these walls
I don’t know how long I can wait in these walls
I’ve been on these streets too long looking at you from the outside in
They sing the same old song about how they walls are always the cleanest
I beg to differ, I must’ve missed them
I’m not involved I’d rather diss them
I’d rather call all you put your wall up
Cause when I come around demolition gon’ crush

[Hook]

If your walls could talk they’d tell you it’s too late
Your destiny accepted your fate
Burn accessories and stash them where they are
Take the recipe, the bible and god
Wall telling you that commissary is low
Race wars happening no calling CO
No calling your mother to save you
Homies say to you, you''re reputable, not acceptable
Your behavior is Sammy Da Bull like a killer that turned snitch
Walls is telling me you a bitch
You pray for appeals hoping the warden could afford them
That sentence so important
Walls telling you to listen to "Sing About Me"
Retaliation is strong you even dream about me
Killed my homeboy and God spared your life
Dumb criminal got indicted the same night
So when you play this song rewind the first verse
About me abusing my power so you can hurt
About me and her in the shower whenever she horny
About me and her in the after hours of the morning
About her baby daddy currently serving life
And how she think about you until we meet up at night
About the only girl that cared about you when you asked her
And how she fucking on a famous rapper
Walls could talk

I remember you was conflicted
Misusing your influence
Sometimes I did the same
Abusing my power full of resentment
Resentment that turned into a deep depression
Found myself screaming in a hotel room', 2);

insert into music (track, title, lyrics, album_id)
values (6, 'u', '[10x:]
Loving you is complicated

I place blame on you still
Place shame on you still
Feel like you ain''t shit
Feel like you don''t feel, confidence in yourself
Breakin'' on marble floors
Watchin'' anonymous strangers tellin'' me that I''m yours
But you ain''t shit I''m convinced your talent''s nothin'' special
What can I blame him for?
Nigga, I can name several
Situation had stopped with your little sister bakin''
A baby inside, just a teenager, where your patience?
Where was your antennas, where was the influence you speak of?
You preached in front of 100,000 but never reached her
I fuckin'' tell you, you''re fuckin'' failure, you ain''t no leader
I never liked you, forever despise you, I don''t need you
The world don''t need you, don''t let them deceive you
Numbers lie too, fuck your pride too, that''s for dedication
Thought money would change you, made you more complacent
I fuckin'' hate you, I hope you embrace it

[5x:]
Loving you is complicated

Lovin'' you, lovin'' you, not lovin'' you, one hundred proof
I can feel you vibin'', recognize that you''re ashamed of me
Yes, I hate you too

House keeping
[Knocks]
House keeping
¡Abre la puerta! ¡Abre la puerta tengo que limpiar el cuarto!
¡Es que no hay mucho tiempo tengo que limpiar el cuarto¡
!Disculpe!

And you the reason why mama and them leavin''
No, you ain''t shit, you say you love them, I know you don''t mean it
I know you''re irresponsible, selfish, in denial, can''t help it
Your trials and tribulations a burden, everyone felt it
Everyone heard it, multiple shots, corners cryin'' out
You was deserted, where was your antennas again?
Where was your presence, where was your support that you pretend?
You ain''t no brother, you ain''t no disciple, you ain''t no friend
A friend never leave Compton for profit, or leave his best friend
Little brother, you promised you''d watch him before they shot him
Where was your antennas? On the road, bottles and bitches
You FaceTime''d the one time, that''s unforgiven
You even FaceTime''d instead of a hospital visit
''Cause you thought he would recover, well
The surgery couldn''t stop the bleeding for real
Then he died, God himself will say, "You fuckin'' failed."
You ain''t try

I know your secrets nigga
Mood swings is frequent nigga
I know depression is restin'' on your heart for two reasons, nigga
I know you and a couple block boys ain''t been speaking, nigga
Y''all damn near beefin'', I seen it and you''re the reason, nigga
And if this bottle could talk [gulp] I cry myself to sleep
Bitch, everything is your fault
Faults breakin'' to pieces, earthquakes on every weekend
Because you shook as soon as you knew confinement was needed
I know your secrets
Don''t let me tell them to the world about that shit you thinkin''
And that time you [gulp] I''m ''bout to hurl
I''m fucked up, but ain''t as fucked up as you
You just can''t get right, I think your heart made of bullet proof
Shoulda killed yo ass a long time ago
You shoulda filled that black revolver blast a long time ago
And if those mirrors could talk it would say, "You gotta go."
And if I told your secrets
The world''ll know money can''t stop a suicidal weakness', 2);

insert into music (track, title, lyrics, album_id)
values (7, 'Alright', 'Alls my life I has to fight, nigga
Alls my life I...
Hard times like yah
Bad trips like "God!"
Nazareth, I''m fucked up
Homie you fucked up
But if God got us then we gon'' be alright

[Hook - Pharrell Williams:]
Nigga, we gon'' be alright
Nigga, we gon'' be alright
We gon'' be alright
Do you hear me, do you feel me? We gon'' be alright
Nigga, we gon'' be alright
Huh? We gon'' be alright
Nigga, we gon'' be alright
Do you hear me, do you feel me? We gon'' be alright

Uh, and when I wake up
I recognize you''re lookin'' at me for the pay cut
Behind my side we lookin'' at you from the face down
What mac-11 even boom with the bass down
Schemin''! And let me tell you bout my life
Painkillers only put me in the twilight
Where pretty pussy and Benjamin is the highlight
Now tell my mama I love her but this what I like
Lord knows, 20 of ''em in my Chevy
Tell ''em all to come and get me, reapin'' everything I sow
So my karma coming heavy
No preliminary hearings on my record
I''m a motherfucking gangster in silence for the record
Tell the world I know it''s too late
Boys and girls I think I''ve gone cray
Drown inside my vices all day
Won''t you please believe when I say

[Pre-hook:]
When you know, we been hurt, been down before, nigga
When our pride was low, lookin'' at the world like, "where do we go, nigga?"
And we hate Popo, wanna kill us dead in the street for sure, nigga
I''m at the preacher''s door
My knees gettin'' weak and my gun might blow but we gon'' be alright

[Hook]

What you want, you a house you a car
40 acres and a mule, a piano a guitar
Anything, see my name is Lucy, I''m your dog
Motherfucker you can live at the mall
I can see the evil, I can tell it I know it''s illegal
I don''t think about it, I deposit every other zero
Thinkin'' of my partner put the candy, paint it on the regal
Diggin'' in my pocket ain''t a profit, big enough to feed you
Everyday my logic, get another dollar just to keep you
In the presence of your chico... ah!
I don''t talk about it, be about it, everyday I see cool
If I got it then you know you got it, Heaven, I can reach you
Pet dog, pet dog, pet dog, my dog that''s all
Pick back and chat I shut the back for y''all
I rap, I''m black, on track so rest assured
My rights, my wrongs I write till I''m right with God

[Pre-hook]

[Hook]

I keep my head up high
I cross my heart and hope to die
Lovin'' me is complicated
Too afraid, a lot of changes
I''m alright and you''re a favorite
Dark nights in my prayers

I remembered you was conflicted
Misusing your influence, sometimes I did the same
Abusing my power full of resentment
Resentment that turned into a deep depression
Found myself screamin'' in the hotel room
I didn''t wanna self destruct, the evils of Lucy was all around me
So I went runnin'' for answers', 2);

insert into music (track, title, lyrics, album_id)
values (8, 'For Sale? (Interlude)', 'What''s wrong nigga?
I thought you was keeping it gangsta?
I thought this what you wanted?
They say if you scared go to church
But remember
He knows the bible too

[Hook:]
My baby when I get you get you get you get you
Ima go head to ride with you
Smoking lokin'' poking the deja till I''m idle with you
Cause I (want you)
Now baby when I''m riding here I''m riding dirty
Registration is out of service
Smoking lokin'' drinking the potion you can see me swerving
Cause I (want you)
I want you more than you know

I remember you took me to the mall last week baby
You looked me in my eyes about 4 5 times
Till I was hypnotized then you clarified
That I (want you)
You said Sherane ain''t got nothing on Lucy
I said you crazy
Roses are red violets are blue but me and you both pushing up daisies if I (want you)

[Hook]

You said to me
You said your name was Lucy
I said where''s Ricardo?
I said oh no, not the show
Than you spit a little rap to me like this
When I turned 26 I was like oh shit
You said this to me
I remember what you said too you said
My name is Lucy Kendrick
You introduced me Kendrick
Usually I don’t do this
But I see you and me Kendrick
Lucy Give you no worries
Lucy got million stories
About these rappers I came after when they was boring
Lucy gone fill your pockets
Lucy gone move your mama out of Compton
Inside the gi-gantic mansion like I promised
Lucy just want your trust and loyalty
Avoiding me?
It''s not so easy I''m at these functions accordingly
Kendrick, Lucy don''t slack a minute
Lucy work harder
Lucy gone call you even when Lucy know you love your Father
I''m Lucy
I loosely heard prayers on your first album truly
Lucy don''t mind cause at the end of the day you''ll pursue me
Lucy go get it, Lucy not timid, Lucy up front
Lucy got paper work on top of paper work
I want you to know that Lucy got you
All your life I watched you
And now you all grown up then sign this contract if that’s possible

[Hook]

I remembered you was conflicted
Misusing your influence, sometimes I did the same
Abusing my power full of resentment
Resentment that turned into a deep depression
Found myself screamin'' in the hotel room
I didn''t wanna self destruct
The evils of Lucy was all around me
So I went runnin'' for answers
Until I came home', 2);

insert into music (track, title, lyrics, album_id)
values (9, 'Momma', 'Oh shit!
Oh, I need that
I need that sloppy
That sloppy
Like a Chevy in quicksand
That sloppy

This feelin'' is unmatched
This feelin'' is brought to you by adrenaline and good rap
Black (Pendleton) ball cap
(West, west, west)
We don''t share the same synonym fall back
(West, west, west)
Been in it before internet had new acts
Mimicking radio''s nemesis may be wack
My innocence limited the experience lacked
Ten of us with no tentative tactic that cracked
The mind of a literate writer but I did it in fact
You admitted it once I submitted it wrapped in plastic
Remember scribblin'' scratchin'' dilligent sentences backwards
Visiting freestyle cyphers for your reaction
Now I can live in a stadium, pack it the fastest
Gamblin'' Benjamin benefits, sittin'' in traffic
Spinnin'' women in cartwheels, linen fabric on fashion
Winnin'' in every decision
Kendrick is master and mastered it
Isn''t it lovely how menace has turned attraction?
Pivotin'' rappers, finish your fraction while writing blue magic
Thank God for rap, I would say it got me a plaque
But what''s better than that?
The fact it brought me back home

[Hook:]
We been waitin'' for you
Waitin'' for you
Waitin'' for you
Waitin'' for you

I know everything, I know myself
I know morality, spirituality, good and bad health
I know fatality might haunt you
I know everything, I know Compton
I know street shit, I know shit that''s conscious, I know everything
I know lawyers, advertisement and sponsors
I know wisdom, I know bad religion, I know good karma
I know everything, I know history
I know the universe works mentally
I know the perks of bullshit isn''t meant for me
I know everything, I know cars, clothes, hoes and money
I know loyalty, I know respect, I know those that''s Ornery
I know everything, the highs to lows to groupies and junkies
I know if I''m generous at heart, I don''t need recognition
The way I''m rewarded, well, that''s God''s decision
I know you know that lines from Compton School District
Just give it to the kids, don’t gossip about how it was distributed
I know how people work, I know the price of life
I know how much it’s worth, I know what I know and I know it well
Not to ever forget until I realized I didn’t know shit
The day I came home

[Hook]

I met a little boy that resembled my features
Nappy afro, gap in his smile
Hand me down sneakers bounced through the crowd
Runnin'' home and the man and woman that crossed him
Sun beamin'' on his beady beads exhausted
Tossin'' footballs with his ashy black ankles
Breakin'' new laws mama passed on home trainin''
He looked at me and said Kendrick you do know my language
You just forgot because of what public schools had painted
Oh I forgot don''t kill my vibe, that''s right you''re famous
I used to watch on Channel 5, TV was takin''
But never mind you''re here right now don''t you mistake it
It''s just a new trip, take a glimpse at your family''s ancestor
Make a new list, of everything you thought was progress
And that was bullshit, I mean your life is full of turmoil
You spoiled by fantasies of who you are
I feel bad for you
I can attempt to enlighten you without frightenin'' you
If you resist, I''ll back off go catch a flight or two
But if you pick, destiny over rest in peace
Than be an advocate go tell your homies especially
To come back home

This is a world premiere
This is a world premiere
This is a world premiere

I been lookin for you my whole life, an appetite
For the feeling I can barely describe, where you reside?
Is it in a woman, is it in money, or mankind?
Tell me something got me losing my mind, AH!
You make me wanna jump
(Jump, jump, jump, jump, jump, jump, jump, jump
(Let''s talk about love))
(Jump, jump, jump, jump, jump, jump, jump, jump
(Let''s talk about love))
I been lookin for you my whole life, an appetite
For the feeling I can barely describe, where you reside?
Is it in a woman, is it in money, or mankind?
Tell me something think I''m losing my mind, AH!
I say where you at, from the front to the back
I''m lookin'' for you I react, only when you react
Ah, I thought I found you, back in the ghetto
When I was seventeen with the .38 special
Maybe you''re in a dollar bill, maybe you''re not real
Maybe only the wealthy get to know how you feel
Maybe I''m paranoid, ha, maybe I don''t need you anyway
Don''t lie to me I''m suicidal anyway
I can be your advocate
I can preach for you if you tell me what the matter is', 2);

insert into music (track, title, lyrics, album_id)
values (10, 'Hood Politics', 'K dot, pick up the phone, nigga. Every time I call, it''s going to voice mail.
Don''t tell me they got you on some weirdo rap shit, nigga.
No socks and skinny jeans and shit. Call me on Shaniqua''s phone

[Hook:]
I been A-1 since day one, you niggas boo boo
Your home boy, your block that you''re from, boo boo
Lil hoes you went to school with, boo boo
Baby mama and your new bitch, boo boo
We was in the hood, 14 with the deuce deuce
14 years later going hard, like we used to on the dead homies
On the dead homies

I don''t give a fuck about no politics in rap, my nigga
My lil homie Stunna Deuce ain''t never comin'' back, my nigga
So you better go hard every time you jump on wax, my nigga
Fuck what they talkin'' bout, your shit is where its at, my nigga
Came in this game, you stuck your fangs in this game
You wore no chain in this game your hood, your name in this game
Now you double up, time to bubble up the bread and huddle up
Stickin'' to the scripts, now here if them benjamin''s go cuddle up
Skip, hop, trip, drop, flip, flop with the white tube sock
It goes "Sherm Sticks, burn this"
Thats what the product smells like when the chemicals mix
50 nigga salute, out the Compton zoo, with the extras
El Cos, Monte Carlos, Road Kings and dressers
Rip Ridaz, P-Funkers, Mexicans, they fuck with you
Asians, they fuck with you, nobody can fuck with you

[Hook]

Hopped out the caddy, just got my dick sucked
The little homies called, they said, "The enemies done cliqued up"
Oh yeah? Puto want to squabble with mi barrio?
Oh, yeah? Tell ''em they can run it for the cardio
Oh, yeah? Everythin'' is everythin'', it''s scandalous
Slow motion for the ambulance, the project filled with cameras
The LAPD gamblin'', scramblin'', football numbers slanderin''
Niggas name on paper, you snitched all summer
The streets don’t fail me now, they tell me it''s a new gang in town
From Compton to Congress, it’s set trippin’ all around
Ain’t nothin'' new but a flow of new DemoCrips and ReBloodlicans
Red state versus a blue state, which one you governin’?
They give us guns and drugs, call us thugs
Make it they promise to fuck with you
No condom they fuck with you, Obama say, "What it do?"

[Hook]

Everybody want to talk about who this and who that
Who the realest and who wack, who white or who black
Critics want to mention that they miss when hip hop was rappin’
Motherfucker if you did, then Killer Mike''d be platinum
Y’all priorities are fucked up, put energy in wrong shit
Hennessy and Crown Vic, my memory been gone since
Don’t ask about no camera back at award shows
No, don’t ask about my bitch, no, don’t ask about my foes
''Less you askin'' me about power, yeah, I got a lot of it
I’m the only nigga next to Snoop that can push the button
Had the Coast on standby
“K. Dot, what up? I heard they opened up Pandora’s box”
I box ‘em all in, by a landslide
Nah homie we too sensitive, it spill out to the streets
I make the call and get the coast involved then history repeats
But I resolved inside that private hall while sitting down with Jay
He said "it''s funny how one verse could fuck up the game"

I remember you was conflicted
Misusing your influence
Sometimes I did the same
Abusing my power full of resentment
Resentment that turned into a deep depression
Found myself screaming in a hotel room
I didn''t want to self-destruct
The evils of Lucy was all around me
So I went running for answers
Until I came home
But that didn''t stop survivors guilt
Going back and forth
Trying to convince my self the stripes I earned
Or maybe how A-1 my foundation was
But while my loved ones was fighting
A continuous war back in the city
I was entering a new one', 2);

insert into music (track, title, lyrics, album_id)
values (11, 'How Much a Dollar Cost', 'How much a dollar really cost?
The question is detrimental, paralyzin'' my thoughts
Parasites in my stomach keep me with a gut feeling, y''all
Gotta see how I’m chillin'' once I park this luxury car
Hopping out feeling big as Mutombo
20 on pump 6, dirty Marcellus called me Dumbo
20 years ago, can''t forget
Now I can lend all my ear or two how to stack these residuals
Tenfold, the liberal concept of what men''ll do
20 on 6, he didn''t hear me
Indigenous African only spoke Zulu
My American tongue was slurry
Walked out the gas station
A homeless man with a semi-tan complexion
Asked me for 10 Rand [apprrox $1 USD], stressin'' about dry land
Deep water, powder blue skies that crack open
A piece of crack that he wanted, I knew he was smokin''
He begged and pleaded
Asked me to feed him twice, I didn''t believe it
Told him, "Beat it"
Contributin'' money just for his pipe, I couldn''t see it
He said, "My son, temptation is one thing that I''ve defeated
Listen to me, I want a single bill from you
Nothin'' less, nothin'' more"
I told him I ain''t have it and closed my door
Tell me how much a dollar cost

[Chorus - James Fauntleroy:]
It''s more to feed your mind
Water, sun and love, the one you love
All you need, the air you breathe

He''s starin'' at me in disbelief
My temper is buildin'', he''s starin'' at me, I grab my key
He''s starin'' at me, I started the car, then I tried to leave
And somethin'' told me to keep it in park until I could see
The reason why he was mad at a stranger
Like I was supposed to save him
Like I''m the reason he''s homeless and askin'' me for a favor
He''s starin'' at me, his eyes followed me with no laser
He''s starin'' at me, I notice that his stare is contagious
Cause now I''m starin'' back at him, feelin'' some type of disrespect
If I could throw a bat at him, it''d be aimin'' at his neck
I never understood someone beggin'' for goods
Askin'' for handouts, takin'' it if they could
And this particular person just had it down pat
Starin'' at me for the longest until he finally asked
Have you ever opened up Exodus 14?
A humble man is all that we ever need
Tell me how much a dollar cost

[Chorus]

Guilt trippin'' and feelin'' resentment
I never met a transient that demanded attention
They got me frustrated, indecisive and power trippin''
Sour emotions got me lookin'' at the universe different
I should distance myself, I should keep it relentless
My selfishness is what got me here, who the fuck I''m kiddin''?
So I''mma tell you like I told the last bum
Crumbs and pennies, I need all of mines
And I recognize this type of panhandlin'' all the time
I got better judgement, I know when nigga''s hustlin'', keep in mind
When I was strugglin'', I did compromise, now I comprehend
I smell grandpa''s old medicine, reekin'' from your skin
Moonshine and gin, nigga you''re babblin'', your words ain''t flatterin''
I''m imaginin'' Denzel but lookin'' at O''Neal
Kazaam is sad thrills, your gimmick is mediocre
The jig is up, I seen you from a mile away losin'' focus
And I''m insensitive, and I lack empathy
He looked at me and said, "Your potential is bittersweet"
I looked at him and said, "Every nickel is mines to keep"
He looked at me and said, "Know the truth, it''ll set you free
You''re lookin'' at the Messiah, the son of Jehovah, the higher power
The choir that spoke the word, the Holy Spirit
The nerve of Nazareth, and I''ll tell you just how much a dollar cost
The price of having a spot in Heaven, embrace your loss, I am God"

[Ronald Isley:]
I wash my hands, I said my grace
What more do you want from me?
Tears of a clown, guess I''m not all what is meant to be
Shades of grey will never change if I condone
Turn this page, help me change, so right my wrongs', 2);

insert into music (track, title, lyrics, album_id)
values (12, 'Complexion (A Zulu Love)', '[Hook:]
Complexion (two-step)
Complexion don''t mean a thing (it''s a Zulu love)
Complexion (two-step)
It all feels the same (it''s a Zulu love)

Dark as the midnight hour or bright as the mornin'' sun
Give a fuck about your complexion, I know what the Germans done
Sneak (dissin’)
Sneak me through the back window, I’m a good field nigga
I made a flower for you outta cotton just to chill with you
You know I''d go the distance, you know I''m ten toes down
Even if master listenin'', cover your ears, he ''bout to mention

[Hook]

Dark as the midnight hour, I''m bright as the mornin'' Sun
Brown skinned, but your blue eyes tell me your mama can''t run
Sneak (dissin'')
Sneak me through the back window, I’m a good field nigga
I made a flower for you outta cotton just to chill with you
You know I''d go the distance, you know I''m ten toes down
Even if master''s listenin'', I got the world''s attention
So I''mma say somethin'' that''s vital and critical for survival
Of mankind, if he lyin'', color should never rival
Beauty is what you make it, I used to be so mistaken
By different shades of faces
Then wit told me, "You''re womanless, woman love the creation"
It all came from God then you was my confirmation
I came to where you reside
And looked around to see more sights for sore eyes
Let the Willie Lynch theory reverse a million times with...

[Hook]

You like it, I love it
You like it, I love it

[Rapsody:]
Let me talk my Stu Scott, ‘scuse me on my 2pac
Keep your head up, when did you stop? Love and die
Color of your skin, color of your eyes
That’s the real blues, baby, like you met Jay’s baby
You blew me away, you think more beauty in blue green and grey
All my Solomon up north, 12 years a slave
12 years of age, thinkin’ my shade too dark
I love myself, I no longer need Cupid
And forcin’ my dark side like a young George Lucas
Light don’t mean you smart, bein’ dark don’t make you stupid
And frame of mind for them bustas, ain’t talkin’ “Woohah!”
Need a paradox for the pair of dots they tutored
Like two ties, L-L, you lose two times
If you don’t see you beautiful in your complexion
It ain’t complex to put it in context
Find the air beneath the kite, that’s the context
Yeah, baby, I’m conscious, ain’t no contest
If you like it, I love it, all your earth tones been blessed
Ain’t no stress, jigga boos wanna be
I ain’t talkin’ Jay, I ain’t talkin’ Bey
I’m talkin’ days we got school watchin’ movie screens
And spike yourself esteem
The new James Bond gon’ be black as me
Black as brown, hazelnut, cinnamon, black tea
And it’s all beautiful to me
Call your brothers magnificent, call all the sisters queens
We all on the same team, blues and pirus, no colors ain’t a thing

Barefoot babies with no cares
Teenage gun toters that don’t play fair, should I get out the car?
I don’t see Compton, I see something much worse
The land of the landmines, the hell that’s on earth', 2);

insert into music (track, title, lyrics, album_id)
values (13, 'The Blacker the Berry', 'Everything black, I don''t want black
I want everything black, I ain''t need black
Some white some black, I ain''t mean black
I want everything black
[x2]

Six in the mornin'', fire in the street
Burn, baby burn, that''s all I wanna see
And sometimes I get off watchin'' you die in vain
It''s such a shame they may call me crazy
They may say I suffer from schizophrenia or somethin''
But homie you made me
Black don''t crack my nigga

I''m the biggest hypocrite of 2015
Once I finish this, witnesses will convey just what I mean
Been feeling this way since I was 16, came to my senses
You never liked us anyway, fuck your friendship, I meant it
I''m African-American, I''m African
I''m black as the moon, heritage of a small village
Pardon my residence
Came from the bottom of mankind
My hair is nappy, my dick is big, my nose is round and wide
You hate me don''t you?
You hate my people, your plan is to terminate my culture
You''re fuckin'' evil I want you to recognize that I''m a proud monkey
You vandalize my perception but can''t take style from me
And this is more than confession
I mean I might press the button just so you know my discretion
I''m guardin'' my feelins, I know that you feel it
You sabotage my community, makin'' a killin''
You made me a killer, emancipation of a real nigga

[Pre-Hook:]
The blacker the berry, the sweeter the juice
The blacker the berry, the sweeter the juice
The blacker the berry, the sweeter the juice
The blacker the berry, the bigger I shoot

[Hook:]
I said they treat me like a slave, cah'' me black
Woi, we feel a whole heap of pain, cah'' we black
And man a say they put me in a chain, cah'' we black
Imagine now, big gold chain full of rocks
How you no see the whip, left scars pon'' me back
But now we have a big whip, parked pon'' the block
All them say we doomed from the start, cah'' we black
Remember this, every race start from the black, just remember that

I''m the biggest hypocrite of 2015
Once I finish this, witnesses will convey just what I mean
I mean, it''s evident that I''m irrelevant to society
That''s what you''re telling me, penitentiary would only hire me
Curse me till I''m dead
Church me with your fake prophesyzing that I''mma be just another slave in my head
Institutionalize manipulation and lies
Reciprocation of freedom only live in your eyes
You hate me don''t you?
I know you hate me just as much as you hate yourself
Jealous of my wisdom and cards I dealt
Watchin'' me as I pull up, fill up my tank, then peel out
Muscle cars like pull ups, show you what these big wheels ''bout, ah
Black and successful, this black man meant to be special
CAT scans on my radar bitch, how can I help you?
How can I tell you I''m making a killin''?
You made me a killer, emancipation of a real nigga

[Pre-Hook]

[Hook]

I''m the biggest hypocrite of 2015
When I finish this if you listenin'' sure you will agree
This plot is bigger than me, it''s generational hatred
It''s genocism, it''s grimy, little justification
I''m African-American, I''m African
I''m black as the heart of a fuckin'' Aryan
I''m black as the name of Tyrone and Darius
Excuse my French but fuck you — no, fuck y''all
That''s as blunt as it gets, I know you hate me, don''t you?
You hate my people, I can tell cause it''s threats when I see you
I can tell cause your ways deceitful
Know I can tell because you''re in love with the Desert Eagle
Thinkin'' maliciously, he get a chain then you gone bleed him
It''s funny how Zulu and Xhosa might go to war
Two tribal armies that want to build and destroy
Remind me of these Compton Crip gangs that live next door
Beefin'' with Piru''s, only death settle the score
So don''t matter how much I say I like to preach with the Panthers
Or tell Georgia State "Marcus Garvey got all the answers"
Or try to celebrate February like it''s my B-Day
Or eat watermelon, chicken, and Kool-Aid on weekdays
Or jump high enough to get Michael Jordan endorsements
Or watch BET cause urban support is important
So why did I weep when Trayvon Martin was in the street?
When gang banging make me kill a nigga blacker than me?
Hypocrite!', 2);

insert into music (track, title, lyrics, album_id)
values (14, 'You Ain''t Gotta Lie (Momma Said)', 'Study long, study wrong, nigga
Hey, y''all close that front door, ya''ll let flies in this motherfucker
Close that door!
My OG up in this motherfucker right now
My pops man with the bottle in his hand, actin'' a fool
Hey, webserver, babe check it out, Imma tell you what my mama had said, she like:

I could spot you a mile away
I could see your insecurities written all on your face
So predictable your words, I know what you gonna say
Who you foolin''? Oh, you assuming you can just come and hang
With the homies but your level of realness ain''t the same
Circus acts only attract those that entertain
Small talk, we know that it''s all talk
We live in the Laugh Factory every time they mention your name

[Bridge:]
Askin'', "where the hoes at?" to impress me
Askin'', "where the moneybags?" to impress me
Say you got to burn your stash to impress me
It''s all in your head, homie
Askin'' "where the plug at?" to impress me
Askin'' "where the juug at?" to impress me
Askin'' "where it''s at?" only upsets me
You sound like the feds, homie

[Hook x2:]
You ain''t gotta lie to kick it, my nigga
You ain''t gotta lie, you ain''t gotta lie
You ain''t gotta lie to kick it, my nigga
You ain''t gotta try so hard

And the world don''t respect you and the culture don''t accept you
But you think it''s all love
And the girls gon'' neglect you once your parody is done
Reputation can''t protect you if you never had one
Jealousy (complex), emotional (complex)
Self-pity (complex), under oath (complex)
The loudest one in the room, nigga, that''s a complex
Let me put it back in proper context

[Hook x2]

[Bridge]

What do you got to offer?
Tell me before you we off ya, put you deep in the coffin
Been allergic to talkin'', been aversion to bullshit
Instead of dreamin'' the auction, tell me just who your boss is
Niggas be fugazie, bitches be fugazie
This is for fugazie niggas and bitches who make habitual line babies, bless them little hearts
You can never persuade me
You can never relate me to him, to her, or that to them
Or you, the truth you love to bend
In the back, in the bed, on the floor, that''s your ho
On the couch, in the mouth, I''ll be out, really though
So loud, rich niggas got low money
And loud, broke niggas got no money
The irony behind it is so funny
And I seen it all this past year
Pass on some advice we feel:

[Hook]', 2);

insert into music (track, title, lyrics, album_id)
values (15, 'i', 'Is this mic on? (Hey, move this way, this way)
Hey, Hey! Hey! Turn the mic up, c''mon, c''mon
Is the mic on or not? I want the mic
We''re bringing up nobody, nobody...
Nobody but the number one rapper in the world
He done traveled all over the world
He came back just to give you some game
All of the little boys and girls, come up here
(One two, one two, what''s happening, fool?)
Come right here, this is for you, come on up
Kendrick Lamar, make some noise, brother

I done been through a whole lot
Trial, tribulation, but I know God
The Devil wanna put me in a bow tie
Pray that the holy water don''t go dry
As I look around me
So many motherfuckers wanna down me
But an enemigo never drown me
In front of a dirty double-mirror they found me

And (I love myself)
When you lookin'' at me, tell me what do you see?
(I love myself)
Ahh, I put a bullet in the back of the back of the head of the police
(I love myself)
Illuminated by the hand of God, boy don''t seem shy
(I love myself)
One day at a time, uhh

They wanna say it''s a war outside, bomb in the street
Gun in the hood, mob of police
Rock on the corner with a line for the fiend
And a bottle full of lean and a shared on the scheme uh
These days of frustration keep y''all on tuck and rotation (Come to the front)
I duck these cold faces, post up fi-fie-fo-fum basis
Dreams of reality''s peace
Blow steam in the face of the beast
Sky could fall down, wind could cry now
Look at me motherfucker I smile

And (I love myself)
When you lookin'' at me, tell me what do you see?
(I love myself)
Ahh, I put a bullet in the back of the back of the head of the police
(I love myself)
Illuminated by the hand of God, boy don''t seem shy
(I love myself)
One day at a time, uhh

(Crazy)
(What you gon'' do?)
Lift up your head and keep moving, (Keep moving) turn the mic up
(Haunt you)
Peace to fashion police, I wear my heart
On my sleeve, let the runway start
You know the miserable do love company
What do you want from me and my scars?
Everybody lack confidence, everybody lack confidence
How many times my potential was anonymous?
How many times the city making me promises?
So I promise this, nigga

And (I love myself)
When you lookin'' at me, tell me what do you see?
(I love myself)
Ahh, I put a bullet in the back of the back of the head of the police
(I love myself)
Illuminated by the hand of God, boy don''t seem shy
(I love myself)
One day at a time, uhh

Huh (Walk my bare feet) huh (Walk my bare feet)
Huh (Down, down valley deep) huh (Down, down valley deep)
(I love myself) huh (Fi-fie-fo-fum) huh (fi-fie-fo-fum)
(I love myself) huh (My heart undone) one, two, three

And (I love myself)
When you lookin'' at me, tell me what do you see?
(I love myself)
Ahh, I put a bullet in the back of the back of the head of the police
(I love myself)
Illuminated by the hand of God, boy don''t seem shy
(I love myself)
One day at a time, uhh

I went to war last night
With an automatic weapon, don''t nobody call a medic
I''m a do it till I get it right
I went to war last night (Night, night, night, night, night)
I''ve been dealing with depression ever since an adolescent
Duckin'' every other blessin'', I can never see the message
I could never take the lead, I could never bob and weave
From a negative and letting them annihilate me
And it''s evident I''m moving at a meteor speed
Finna run into a building, lay my body...

(Offstage Argument)
Not on my time, Not while I''m up here
Not on my time, kill the music
Not on my time
We could save that shit for the streets
We could save that shit, this for the kids bro
2015, niggas tired of playin'' victim dog
Niggas ain''t trying to play vic- TuTu, how many niggas we done lost?
Yan-Yan, how many we done lost?
No for real, answer the question, how many niggas we done lost bro?
This, this year alone
Exactly. So we ain''t got time to waste time my nigga
Niggas gotta make time bro
The judge make time, you know that, the judge make time right?
The judge make time so it ain''t shit
It shouldn''t be shit for us to come out here and appreciate the little bit of life we got left, dawg
On the dead homies. Charlie P, you know that bro
You know that
It''s mando. Right, it''s mando
And I say this because I love you niggas man
I love all my niggas bro
Exac- enough said, enough said
We gon'' get back to the show and move on, because that shit petty my nigga
Mic check, mic check, mic check, mic check, mic check
We gon'' do some acapella shit before we get back to-
All my niggas listen
Listen to this:', 2);

insert into music (track, title, lyrics, album_id)
values (16, 'Mortal Man', 'The ghost of Mandela, hope my flows they propel it
Let these words be your earth and moon
You consume every message
As I lead this army make room for mistakes and depression
And with that being said my nigga, let me ask this question:

When shit hit the fan, is you still a fan?
When shit hit the fan (one two, one two)
When shit hit the fan, is you still a fan?
When shit hit the fan, is you still a fan?

The ghost of Mandela, hope my flows they propel it
Let these words be your earth and moon
You consume every message
As I lead this army make room for mistakes and depression
And with that being said my nigga, let me ask this question:

When shit hit the fan, is you still a fan?
When shit hit the fan, is you still a fan?
Want you look to your left and right, make sure you ask your friends
When shit hit the fan, is you still a fan?

Do you believe in me? Are you deceiving me?
Could I let you down easily, is your heart where it need to be?
Is your smile on permanent? Is your vow on lifetime?
Would you know where the sermon is if I died in this next line?
If I''m tried in a court of law, if the industry cut me off
If the government want me dead, plant cocaine in my car
Would you judge me a drug-head or see me as K. Lamar
Or question my character and degrade me on every blog
Want you to love me like Nelson, want you to hug me like Nelson
I freed you from being a slave in your mind, you''re very welcome
You tell me my song is more than a song, it''s surely a blessing
But a prophet ain''t a prophet til they ask you this question:

When shit hit the fan, is you still a fan?
When shit hit the fan, is you still a fan?
Want you look to your left and right, make sure you ask your friends
When shit hit the fan, is you still a fan?

The ghost of Mandela, hope my flows they propel it
Let my words be your earth and moon you consume every message
As I lead this army make room for mistakes and depression
And with that

Do you believe in me? How much you believe in her?
You think she gon'' stick around if them 25 years occur?
You think he can hold you down when you down behind bars hurt?
You think y''all on common ground if you promise to be the first? Can you be immortalised without your life being expired?
Even though you share the same blood is it worth the time?
Like who got your best interest?
Like how much are you dependent?
How clutch are the people that say they love you?
And who pretending?
How tough is your skin when they turn you in?
Do you show forgiveness?
What brush do you bend when dusting your shoulders from being offended?
What kind of den did they put you in when the lions start hissing?
What kind of bridge did they burn?
Revenge or your mind when it''s mentioned?
You wanna love like Nelson, you wanna be like Nelson
You wanna walk in his shoes but you peacemaking seldom
You wanna be remembered that delivered the message
That considered the blessing of everyone
This your lesson for everyone, say

When shit hit the fan, is you still a fan?
When shit hit the fan, is you still a fan?
Want you look to your left and right, make sure you ask your friends
When shit hit the fan, is you still a fan?

The voice of Mandela, hope this flow stay propellin''
Let my word be your Earth and moon
You consume every message
As I lead this army make room for mistakes and depression
And if you riding with me, nigga

I been wrote off before, I got abandonment issues
I hold grudges like bad judges, don''t let me resent you
That''s not Nelson-like, want you to love me like Nelson
I went to Robben''s Island analysing, that''s where his cell is
So I could find clarity, like how much you cherish me
Is this relationship a fake or real as the heavens be?
See I got to question it all, family, friends, fans, cats, dogs
Trees, plants, grass, how the wind blow
Murphy''s Law, generation X, will I ever be your X?
Floss off a baby step, mauled by the mouth of
Pit bulls, put me under stress
Crawled under rocks, ducking y''all, it''s respect
But then tomorrow, put my back against the wall
How many leaders you said you needed then left ''em for dead?
Is it Moses, is it Huey Newton or Detroit Red?
Is it Martin Luther, JFK, shoot or you assassin
Is it Jackie, is it Jesse, oh I know, it''s Michael Jackson, oh

When shit hit the fan, is you still a fan?
When shit hit the fan, is you still a fan?
That nigga gave us "Billie Jean", you say he touched those kids?
When shit hit the fan, is you still a fan?

The ghost of Mandela, hope my flows they propel it
Let my word be your earth and moon you consume every message
As I lead this army make room for mistakes and depression
And if you riding with me nigga, let me ask this question nigga', 2);

insert into artist (name, details)
values ('Intronaut', 'Intronaut is a progressive metal band from Los Angeles, California that incorporates complex polyrhythms, progressive rock, and jazz. Intronaut features former members of Anubis Rising, Exhumed, Uphill Battle, and Impaled.
');
insert into artist (name, details)
values ('YOB', 'Yob is an American doom metal band from Eugene, Oregon, composed of singer/guitarist Mike Scheidt, bassist Aaron Rieseberg, and drummer Travis Foster. Their most recent album Our Raw Heart was released in June 2018.
');

insert into artist (name, details)
values ('Jakob', 'Jakob is a New Zealand post-rock band, based in the Hawkes Bay city of Napier.[1] The band consists of guitarist Jeff Boyle, bassist Maurice Beckett, and drummer Jason Johnston.[1] They have been compared to such bands as Mogwai, Sonic Youth, and High Dependency Unit, though they largely eschew any vocals or samples in their songs.');

insert into shows (place, show_date)
values ('Reno Events Center', '2012-01-14');

insert into shows (place, show_date)
values ('Makuhari Messe', '2013-05-12');

insert into shows_artist (shows_id, artist_name)
values (1, 'Tool');
insert into shows_artist (shows_id, artist_name)
values (1, 'Intronaut');
insert into shows_artist (shows_id, artist_name)
values (1, 'YOB');

insert into shows_artist (shows_id, artist_name)
values (2, 'Tool');
insert into shows_artist (shows_id, artist_name)
values (2, 'Jakob');

insert into playlist (name, public, user_email) VALUES ("Funk ao Jantar", 0, "diogo");
insert into playlist (name, public, user_email) VALUES ("Funk ao Almoço", 1, "roman");

insert into music_playlist (music_id, playlist_name)
VALUES (1, "Funk ao Jantar");


insert into music_playlist (music_id, playlist_name)
VALUES (2, "Funk ao Jantar");


insert into music_playlist (music_id, playlist_name)
VALUES (1, "Funk ao Almoço");




