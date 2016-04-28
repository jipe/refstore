create table job_definitions (
  id          serial primary key,
  name        text unique not null,
  class       text not null,
  created_at  timestamp not null
);

create table job_instances (
  id                  serial primary key,
  job_definition_id   integer references job_definitions on update cascade on delete cascade,
  status              text,
  data                text,
  created_at          timestamp not null
);

create table job_results (
  job_instance_id   integer references job_instances on update cascade on delete cascade,
  data              text,
  updated_at        timestamp not null
);

create table job_schedules (
  id            serial primary key,
  name          text not null,
  class         text not null,
  data          text,
  triggered_at  timestamp
);

create table scheduled_job_instances (
  job_schedule_id   integer references job_schedules on delete cascade on update cascade,
  job_instance_id   integer references job_instances on delete cascade on update cascade
);
