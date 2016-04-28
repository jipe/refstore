create table records (
  id        serial primary key,
  ext_id    text unique not null,
  data      text
);

create index on records using btree (ext_id);

create table record_groups (
	record_id	integer,
	group_id    bigint
);

create index on record_groups using btree (group_id);
