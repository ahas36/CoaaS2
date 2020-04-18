--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

-- Started on 2019-01-02 11:17:35 AEDT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12395)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2286 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 188 (class 1259 OID 16468)
-- Name: account; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE account (
    id integer NOT NULL,
    email character varying(40) NOT NULL,
    username character varying(15) NOT NULL,
    password character varying(255) NOT NULL,
    firstname character varying(100),
    lastname character varying(100),
    active boolean,
    created timestamp without time zone,
    updated timestamp without time zone,
    token text
);


ALTER TABLE account OWNER TO coaas;

--
-- TOC entry 187 (class 1259 OID 16466)
-- Name: account_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE account_id_seq OWNER TO coaas;

--
-- TOC entry 2287 (class 0 OID 0)
-- Dependencies: 187
-- Name: account_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE account_id_seq OWNED BY account.id;


--
-- TOC entry 190 (class 1259 OID 16479)
-- Name: account_role; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE account_role (
    id integer NOT NULL,
    account_id integer,
    role_id integer
);


ALTER TABLE account_role OWNER TO coaas;

--
-- TOC entry 189 (class 1259 OID 16477)
-- Name: account_role_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE account_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE account_role_id_seq OWNER TO coaas;

--
-- TOC entry 2288 (class 0 OID 0)
-- Dependencies: 189
-- Name: account_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE account_role_id_seq OWNED BY account_role.id;


--
-- TOC entry 209 (class 1259 OID 26331)
-- Name: csd_context; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE csd_context (
    csd_id bigint,
    context character varying(255)
);


ALTER TABLE csd_context OWNER TO coaas;

--
-- TOC entry 206 (class 1259 OID 26313)
-- Name: csd_mandatoryinputs; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE csd_mandatoryinputs (
    ioname character varying(255),
    iotype character varying(255),
    csd_id bigint
);


ALTER TABLE csd_mandatoryinputs OWNER TO coaas;

--
-- TOC entry 207 (class 1259 OID 26319)
-- Name: csd_optionalinputs; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE csd_optionalinputs (
    ioname character varying(255),
    iotype character varying(255),
    csd_id bigint
);


ALTER TABLE csd_optionalinputs OWNER TO coaas;

--
-- TOC entry 208 (class 1259 OID 26325)
-- Name: csd_outputs; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE csd_outputs (
    ioname character varying(255),
    iotype character varying(255),
    csd_id bigint
);


ALTER TABLE csd_outputs OWNER TO coaas;

--
-- TOC entry 210 (class 1259 OID 26334)
-- Name: csd_supportedvocabs; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE csd_supportedvocabs (
    csd_id bigint,
    supportedvocabs character varying(255)
);


ALTER TABLE csd_supportedvocabs OWNER TO coaas;

--
-- TOC entry 205 (class 1259 OID 26304)
-- Name: csdr; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE csdr (
    id integer NOT NULL,
    accesstype integer,
    jsonaccessmethod character varying(255),
    entityname character varying(255),
    price double precision,
    pricecurrency character varying(255),
    servicetype character varying(255),
    targetedentities character varying(255)
);


ALTER TABLE csdr OWNER TO coaas;

--
-- TOC entry 204 (class 1259 OID 26302)
-- Name: csdr_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE csdr_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE csdr_id_seq OWNER TO coaas;

--
-- TOC entry 2289 (class 0 OID 0)
-- Dependencies: 204
-- Name: csdr_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE csdr_id_seq OWNED BY csdr.id;


--
-- TOC entry 200 (class 1259 OID 26269)
-- Name: detectedsituations; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE detectedsituations (
    id bigint NOT NULL,
    situation_time bigint
);


ALTER TABLE detectedsituations OWNER TO coaas;

--
-- TOC entry 193 (class 1259 OID 19803)
-- Name: query; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE query (
    query_id integer NOT NULL,
    issue_date timestamp without time zone,
    query_string text,
    query_title text,
    query_type character varying(255),
    account_id integer
);


ALTER TABLE query OWNER TO coaas;

--
-- TOC entry 195 (class 1259 OID 19861)
-- Name: query_log; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE query_log (
    query_log_id integer NOT NULL,
    enity_number integer,
    external_execution_time bigint,
    extreanl_service_call integer,
    issue_date timestamp without time zone,
    parser_execution_time bigint,
    provider_number integer,
    query_string text,
    request_sqem_number integer,
    response text,
    response_time bigint,
    sqem_exectuion_time bigint,
    account_id integer,
    query_type integer,
    status integer
);


ALTER TABLE query_log OWNER TO coaas;

--
-- TOC entry 194 (class 1259 OID 19859)
-- Name: query_log_query_log_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE query_log_query_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE query_log_query_log_id_seq OWNER TO coaas;

--
-- TOC entry 2290 (class 0 OID 0)
-- Dependencies: 194
-- Name: query_log_query_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE query_log_query_log_id_seq OWNED BY query_log.query_log_id;


--
-- TOC entry 192 (class 1259 OID 19801)
-- Name: query_query_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE query_query_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE query_query_id_seq OWNER TO coaas;

--
-- TOC entry 2291 (class 0 OID 0)
-- Dependencies: 192
-- Name: query_query_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE query_query_id_seq OWNED BY query.query_id;


--
-- TOC entry 197 (class 1259 OID 19872)
-- Name: query_status; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE query_status (
    query_status_id integer NOT NULL,
    title character varying(255)
);


ALTER TABLE query_status OWNER TO coaas;

--
-- TOC entry 196 (class 1259 OID 19870)
-- Name: query_status_query_status_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE query_status_query_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE query_status_query_status_id_seq OWNER TO coaas;

--
-- TOC entry 2292 (class 0 OID 0)
-- Dependencies: 196
-- Name: query_status_query_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE query_status_query_status_id_seq OWNED BY query_status.query_status_id;


--
-- TOC entry 199 (class 1259 OID 19880)
-- Name: query_type; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE query_type (
    query_type_id integer NOT NULL,
    query_type_title character varying(255)
);


ALTER TABLE query_type OWNER TO coaas;

--
-- TOC entry 198 (class 1259 OID 19878)
-- Name: query_type_query_type_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE query_type_query_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE query_type_query_type_id_seq OWNER TO coaas;

--
-- TOC entry 2293 (class 0 OID 0)
-- Dependencies: 198
-- Name: query_type_query_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE query_type_query_type_id_seq OWNED BY query_type.query_type_id;


--
-- TOC entry 186 (class 1259 OID 16460)
-- Name: role; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE role (
    id integer NOT NULL,
    role_name character varying(100) NOT NULL
);


ALTER TABLE role OWNER TO coaas;

--
-- TOC entry 185 (class 1259 OID 16458)
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE role_id_seq OWNER TO coaas;

--
-- TOC entry 2294 (class 0 OID 0)
-- Dependencies: 185
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE role_id_seq OWNED BY role.id;


--
-- TOC entry 201 (class 1259 OID 26274)
-- Name: sequence; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE sequence (
    seq_name character varying(50) NOT NULL,
    seq_count numeric(38,0)
);


ALTER TABLE sequence OWNER TO coaas;

--
-- TOC entry 203 (class 1259 OID 26296)
-- Name: situationmonitoringlog; Type: TABLE; Schema: public; Owner: coaas
--

CREATE TABLE situationmonitoringlog (
    id integer NOT NULL,
    event_text text,
    event_time bigint,
    exp_evaluator_time bigint,
    matching_sub_count integer,
    pre_filter_time bigint,
    query_execution_time bigint,
    realted_subs_count integer,
    total_push_time bigint,
    total_time bigint,
    triggered_sub_count integer
);


ALTER TABLE situationmonitoringlog OWNER TO coaas;

--
-- TOC entry 202 (class 1259 OID 26294)
-- Name: situationmonitoringlog_id_seq; Type: SEQUENCE; Schema: public; Owner: coaas
--

CREATE SEQUENCE situationmonitoringlog_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE situationmonitoringlog_id_seq OWNER TO coaas;

--
-- TOC entry 2295 (class 0 OID 0)
-- Dependencies: 202
-- Name: situationmonitoringlog_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coaas
--

ALTER SEQUENCE situationmonitoringlog_id_seq OWNED BY situationmonitoringlog.id;


--
-- TOC entry 191 (class 1259 OID 16495)
-- Name: v_account_role; Type: VIEW; Schema: public; Owner: coaas
--

CREATE VIEW v_account_role AS
 SELECT account_role.account_id,
    account_role.role_id,
    account.username,
    account.password,
    role.role_name
   FROM ((account_role
     JOIN account ON ((account_role.account_id = account.id)))
     JOIN role ON ((account_role.role_id = role.id)));


ALTER TABLE v_account_role OWNER TO coaas;

--
-- TOC entry 2096 (class 2604 OID 16471)
-- Name: account id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY account ALTER COLUMN id SET DEFAULT nextval('account_id_seq'::regclass);


--
-- TOC entry 2097 (class 2604 OID 16482)
-- Name: account_role id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY account_role ALTER COLUMN id SET DEFAULT nextval('account_role_id_seq'::regclass);


--
-- TOC entry 2103 (class 2604 OID 26307)
-- Name: csdr id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY csdr ALTER COLUMN id SET DEFAULT nextval('csdr_id_seq'::regclass);


--
-- TOC entry 2098 (class 2604 OID 19806)
-- Name: query query_id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query ALTER COLUMN query_id SET DEFAULT nextval('query_query_id_seq'::regclass);


--
-- TOC entry 2099 (class 2604 OID 19864)
-- Name: query_log query_log_id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_log ALTER COLUMN query_log_id SET DEFAULT nextval('query_log_query_log_id_seq'::regclass);


--
-- TOC entry 2100 (class 2604 OID 19875)
-- Name: query_status query_status_id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_status ALTER COLUMN query_status_id SET DEFAULT nextval('query_status_query_status_id_seq'::regclass);


--
-- TOC entry 2101 (class 2604 OID 19883)
-- Name: query_type query_type_id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_type ALTER COLUMN query_type_id SET DEFAULT nextval('query_type_query_type_id_seq'::regclass);


--
-- TOC entry 2095 (class 2604 OID 16463)
-- Name: role id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY role ALTER COLUMN id SET DEFAULT nextval('role_id_seq'::regclass);


--
-- TOC entry 2102 (class 2604 OID 26299)
-- Name: situationmonitoringlog id; Type: DEFAULT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY situationmonitoringlog ALTER COLUMN id SET DEFAULT nextval('situationmonitoringlog_id_seq'::regclass);


--
-- TOC entry 2258 (class 0 OID 16468)
-- Dependencies: 188
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: coaas
--

insert into account (id, email, username, password, firstname, lastname, active, created, updated, token)
    values (1,'admin@mail.com','admin','0iUTLtCCIDlvtSjHW2GpaQSuNXUpSAwIMKrk7uL4Svw=','admin','admin','t',current_timestamp,current_timestamp,'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImNvYWFzX3NlY3VyaXR5IiwiaWF0IjoxNTE4NzYwNzE1LCJleHAiOjE1NTAyOTY3MTV9.ee_zQlYSt1GBGhd-fNrhDER7mBX3lSZ2OdGk2qeLZ1a5q27DnJAiR2k60mtMsbcKTfIzBjX3IIcO24SdC_b-sA');

insert into role values (1,'admin');
insert into role values (2,'consumer');
insert into account_role values (1,1,1);


--
-- TOC entry 2107 (class 2606 OID 16476)
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- TOC entry 2109 (class 2606 OID 16484)
-- Name: account_role account_role_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY account_role
    ADD CONSTRAINT account_role_pkey PRIMARY KEY (id);


--
-- TOC entry 2125 (class 2606 OID 26312)
-- Name: csdr csdr_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY csdr
    ADD CONSTRAINT csdr_pkey PRIMARY KEY (id);


--
-- TOC entry 2119 (class 2606 OID 26273)
-- Name: detectedsituations detectedsituations_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY detectedsituations
    ADD CONSTRAINT detectedsituations_pkey PRIMARY KEY (id);


--
-- TOC entry 2113 (class 2606 OID 19869)
-- Name: query_log query_log_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_log
    ADD CONSTRAINT query_log_pkey PRIMARY KEY (query_log_id);


--
-- TOC entry 2111 (class 2606 OID 19811)
-- Name: query query_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query
    ADD CONSTRAINT query_pkey PRIMARY KEY (query_id);


--
-- TOC entry 2115 (class 2606 OID 19877)
-- Name: query_status query_status_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_status
    ADD CONSTRAINT query_status_pkey PRIMARY KEY (query_status_id);


--
-- TOC entry 2117 (class 2606 OID 19885)
-- Name: query_type query_type_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_type
    ADD CONSTRAINT query_type_pkey PRIMARY KEY (query_type_id);


--
-- TOC entry 2105 (class 2606 OID 16465)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 2121 (class 2606 OID 26278)
-- Name: sequence sequence_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY sequence
    ADD CONSTRAINT sequence_pkey PRIMARY KEY (seq_name);


--
-- TOC entry 2123 (class 2606 OID 26301)
-- Name: situationmonitoringlog situationmonitoringlog_pkey; Type: CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY situationmonitoringlog
    ADD CONSTRAINT situationmonitoringlog_pkey PRIMARY KEY (id);


--
-- TOC entry 2126 (class 2606 OID 16485)
-- Name: account_role account_role_account_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY account_role
    ADD CONSTRAINT account_role_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id);


--
-- TOC entry 2127 (class 2606 OID 16490)
-- Name: account_role account_role_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY account_role
    ADD CONSTRAINT account_role_role_id_fkey FOREIGN KEY (role_id) REFERENCES role(id);


--
-- TOC entry 2135 (class 2606 OID 26352)
-- Name: csd_context fk_csd_context_csd_id; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY csd_context
    ADD CONSTRAINT fk_csd_context_csd_id FOREIGN KEY (csd_id) REFERENCES csdr(id);


--
-- TOC entry 2132 (class 2606 OID 26337)
-- Name: csd_mandatoryinputs fk_csd_mandatoryinputs_csd_id; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY csd_mandatoryinputs
    ADD CONSTRAINT fk_csd_mandatoryinputs_csd_id FOREIGN KEY (csd_id) REFERENCES csdr(id);


--
-- TOC entry 2133 (class 2606 OID 26342)
-- Name: csd_optionalinputs fk_csd_optionalinputs_csd_id; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY csd_optionalinputs
    ADD CONSTRAINT fk_csd_optionalinputs_csd_id FOREIGN KEY (csd_id) REFERENCES csdr(id);


--
-- TOC entry 2134 (class 2606 OID 26347)
-- Name: csd_outputs fk_csd_outputs_csd_id; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY csd_outputs
    ADD CONSTRAINT fk_csd_outputs_csd_id FOREIGN KEY (csd_id) REFERENCES csdr(id);


--
-- TOC entry 2136 (class 2606 OID 26357)
-- Name: csd_supportedvocabs fk_csd_supportedvocabs_csd_id; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY csd_supportedvocabs
    ADD CONSTRAINT fk_csd_supportedvocabs_csd_id FOREIGN KEY (csd_id) REFERENCES csdr(id);


--
-- TOC entry 2128 (class 2606 OID 19812)
-- Name: query fk_query_account_id; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query
    ADD CONSTRAINT fk_query_account_id FOREIGN KEY (account_id) REFERENCES account(id);


--
-- TOC entry 2130 (class 2606 OID 19891)
-- Name: query_log fk_query_log_account_id; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_log
    ADD CONSTRAINT fk_query_log_account_id FOREIGN KEY (account_id) REFERENCES account(id);


--
-- TOC entry 2131 (class 2606 OID 19896)
-- Name: query_log fk_query_log_query_type; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_log
    ADD CONSTRAINT fk_query_log_query_type FOREIGN KEY (query_type) REFERENCES query_type(query_type_id);


--
-- TOC entry 2129 (class 2606 OID 19886)
-- Name: query_log fk_query_log_status; Type: FK CONSTRAINT; Schema: public; Owner: coaas
--

ALTER TABLE ONLY query_log
    ADD CONSTRAINT fk_query_log_status FOREIGN KEY (status) REFERENCES query_status(query_status_id);


-- Completed on 2019-01-02 11:17:48 AEDT

--
-- PostgreSQL database dump complete
--

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

CREATE TABLE QRTZ_JOB_DETAILS
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  JOB_NAME          VARCHAR(200) NOT NULL,
  JOB_GROUP         VARCHAR(200) NOT NULL,
  DESCRIPTION       VARCHAR(250) NULL,
  JOB_CLASS_NAME    VARCHAR(250) NOT NULL,
  IS_DURABLE        BOOL         NOT NULL,
  IS_NONCONCURRENT  BOOL         NOT NULL,
  IS_UPDATE_DATA    BOOL         NOT NULL,
  REQUESTS_RECOVERY BOOL         NOT NULL,
  JOB_DATA          BYTEA        NULL,
  PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
(
  SCHED_NAME     VARCHAR(120) NOT NULL,
  TRIGGER_NAME   VARCHAR(200) NOT NULL,
  TRIGGER_GROUP  VARCHAR(200) NOT NULL,
  JOB_NAME       VARCHAR(200) NOT NULL,
  JOB_GROUP      VARCHAR(200) NOT NULL,
  DESCRIPTION    VARCHAR(250) NULL,
  NEXT_FIRE_TIME BIGINT       NULL,
  PREV_FIRE_TIME BIGINT       NULL,
  PRIORITY       INTEGER      NULL,
  TRIGGER_STATE  VARCHAR(16)  NOT NULL,
  TRIGGER_TYPE   VARCHAR(8)   NOT NULL,
  START_TIME     BIGINT       NOT NULL,
  END_TIME       BIGINT       NULL,
  CALENDAR_NAME  VARCHAR(200) NULL,
  MISFIRE_INSTR  SMALLINT     NULL,
  JOB_DATA       BYTEA        NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
  REFERENCES QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(200) NOT NULL,
  TRIGGER_GROUP   VARCHAR(200) NOT NULL,
  REPEAT_COUNT    BIGINT       NOT NULL,
  REPEAT_INTERVAL BIGINT       NOT NULL,
  TIMES_TRIGGERED BIGINT       NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
(
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(200) NOT NULL,
  TRIGGER_GROUP   VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(120) NOT NULL,
  TIME_ZONE_ID    VARCHAR(80),
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
  SCHED_NAME    VARCHAR(120)   NOT NULL,
  TRIGGER_NAME  VARCHAR(200)   NOT NULL,
  TRIGGER_GROUP VARCHAR(200)   NOT NULL,
  STR_PROP_1    VARCHAR(512)   NULL,
  STR_PROP_2    VARCHAR(512)   NULL,
  STR_PROP_3    VARCHAR(512)   NULL,
  INT_PROP_1    INT            NULL,
  INT_PROP_2    INT            NULL,
  LONG_PROP_1   BIGINT         NULL,
  LONG_PROP_2   BIGINT         NULL,
  DEC_PROP_1    NUMERIC(13, 4) NULL,
  DEC_PROP_2    NUMERIC(13, 4) NULL,
  BOOL_PROP_1   BOOL           NULL,
  BOOL_PROP_2   BOOL           NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_NAME  VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA     BYTEA        NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  CALENDAR_NAME VARCHAR(200) NOT NULL,
  CALENDAR      BYTEA        NOT NULL,
  PRIMARY KEY (SCHED_NAME, CALENDAR_NAME)
);


CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  ENTRY_ID          VARCHAR(95)  NOT NULL,
  TRIGGER_NAME      VARCHAR(200) NOT NULL,
  TRIGGER_GROUP     VARCHAR(200) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  FIRED_TIME        BIGINT       NOT NULL,
  SCHED_TIME        BIGINT       NOT NULL,
  PRIORITY          INTEGER      NOT NULL,
  STATE             VARCHAR(16)  NOT NULL,
  JOB_NAME          VARCHAR(200) NULL,
  JOB_GROUP         VARCHAR(200) NULL,
  IS_NONCONCURRENT  BOOL         NULL,
  REQUESTS_RECOVERY BOOL         NULL,
  PRIMARY KEY (SCHED_NAME, ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT       NOT NULL,
  CHECKIN_INTERVAL  BIGINT       NOT NULL,
  PRIMARY KEY (SCHED_NAME, INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME  VARCHAR(40)  NOT NULL,
  PRIMARY KEY (SCHED_NAME, LOCK_NAME)
);

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY
  ON QRTZ_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP
  ON QRTZ_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J
  ON QRTZ_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG
  ON QRTZ_TRIGGERS (SCHED_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C
  ON QRTZ_TRIGGERS (SCHED_NAME, CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME
  ON QRTZ_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE
  ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE
  ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP
  ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

