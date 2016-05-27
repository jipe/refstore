create table repositories (

);

create table repository_events (
);

create table records (
  id          serial primary key,
  ext_id      text unique not null,
  data        text
  created_at  timestamp not null,
);

create index on records using btree (ext_id);

create table record_groups (
	record_id	integer not null,
	group_id    bigint not null,
);

create index on record_groups using btree (group_id);
