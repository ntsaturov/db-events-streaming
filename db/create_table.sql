SET SCHEMA 'public';

CREATE TABLE tasks (
    id character varying(255),
    execution_timestamp timestamp without time zone,
    creation_timestamp timestamp without time zone,
    status integer,
    action character varying(255),
    data text
);

insert into tasks (id, creation_timestamp, status, action, data) values (34, now(), 0, 'test_action_1', 'some test data');
insert into tasks (id, creation_timestamp, status, action, data) values (54, now(), 0, 'test_action_2', 'some test data');
insert into tasks (id, creation_timestamp, status, action, data) values (65, now(), 0, 'test_action_3', 'some test data');
insert into tasks (id, creation_timestamp, status, action, data) values (67, now(), 0, 'test_action_4', 'some test data');
insert into tasks (id, creation_timestamp, status, action, data) values (78, now(), 0, 'test_action_5', 'some test data');
