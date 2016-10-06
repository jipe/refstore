create table configuration (
	key   TEXT unique not null,
	value TEXT
);

create index on configuration using btree (key);