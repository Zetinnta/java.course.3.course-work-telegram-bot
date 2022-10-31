-- liquibase formatted sql

-- changeSet: ydeev:1

-- creating table notification_task

CREATE TABLE notification_task
(
    id SERIAL PRIMARY KEY,
    chat_id integer,
    notification TEXT NOT NULL,
    time TIMESTAMP
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
       ('/help', 'Sup! I''m bot Anku. Note that I was made at bench scale, so be patient, please. You can use command /commands for further information about the list of available commands or you can try to type random command that starts with / duh.'),
       ('/commands', 'Here is the list of commands: \n /sup \n /help \n /commands \n /notification'),
       ('/notification', 'You can add notification typing line using the next format: dd.MM.yyyy HH:mm yours_text. Example : 23.11.2022 10:00 Pay the bills');
