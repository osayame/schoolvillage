-- Table: users

-- DROP TABLE users;

CREATE TABLE users
(
  id integer NOT NULL,
  first_name text,
  email text NOT NULL,
  phone text,
  flag_comment text,
  status user_status,
  updated_at timestamp with time zone,
  last_name text,
  created_at timestamp with time zone,
  zip text,
  address1 text,
  address2 text,
  city text,
  country text,
  biography text,
  state text,
  resume text,
  photo text,
  url text,
  linkedin text,
  acuity_id text,
  birthday text,
  gender text,
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT unique_col UNIQUE (email, url),
  CONSTRAINT unique_email UNIQUE (email),
  CONSTRAINT unique_url UNIQUE (url)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO postgres;

--;;
-- Index: first_name

-- DROP INDEX first_name;

CREATE INDEX first_name
  ON users
  USING btree
  (first_name COLLATE pg_catalog."default");

--;;
-- Index: id

-- DROP INDEX id;

CREATE UNIQUE INDEX id
  ON users
  USING btree
  (id);

