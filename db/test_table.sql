SET SCHEMA 'public';

CREATE TABLE tasks (
    id character varying(255),
    execution_timestamp timestamp without time zone,
    status integer,
    action character varying(255),
    data text
);
