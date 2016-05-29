create table record_events (
	id               serial primary key,
	record_id        text not null,
	data             text,
	created_at       timestamp not null,
);

create index on record_events using btree (ext_id);

create table record_groups (
	record_id    text not null,
	group_id     bigint not null
);

create index on record_groups using btree (group_id);
