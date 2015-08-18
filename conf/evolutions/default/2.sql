# --- !Ups
insert into  T_USER values('user1', '0a041b9462caa4a31bac3567e0b6e6fd9100787db2ab433d96f6d178cabfce90');
# --- !Downs
delete from T_USER where id='user1';