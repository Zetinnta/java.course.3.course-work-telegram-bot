-- liquibase formatted sql

-- changeSet: ydeev:1

-- creating table notification_task

CREATE TABLE notification_task
(
    id SERIAL PRIMARY KEY,
    chat_id integer,
    notification TEXT NOT NULL,
    time TIMESTAMP,
    reply BOOLEAN DEFAULT FALSE
);

-- creating table interaction

CREATE TABLE interaction
(
    id SERIAL PRIMARY KEY,
    request TEXT,
    response TEXT NOT NULL
);

-- changeSet: ydeev: 2

CREATE INDEX request_index ON interaction (request);

CREATE INDEX time_index ON notification_task (time);

-- add some basic requests into table interaction

INSERT INTO interaction (request, response)
VALUES
       ('/sup', 'Bot by Shatoshi is here. Use command /help for further information'),
       ('/help', 'Note that I was made at bench scale, so be patient, please. To start the Bot (me) type command /start');
