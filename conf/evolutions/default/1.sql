# --- !Ups

create table T_USER (
  id                  varchar(255) not null,
  password             varchar(255),
  constraint pk_T_USER primary key (id))
;


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists T_USER;

SET REFERENTIAL_INTEGRITY TRUE;


