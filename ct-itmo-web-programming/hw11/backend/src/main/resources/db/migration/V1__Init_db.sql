create table post (
                      id bigint not null auto_increment,
                      creation_time datetime(6),
                      text longtext,
                      title varchar(100),
                      user_id bigint not null,
                      primary key (id)
) engine=InnoDB;

create table user (
                      id bigint not null auto_increment,
                      admin bit not null,
                      creation_time datetime(6),
                      login varchar(24),
                      name varchar(100),
                      primary key (id)
) engine=InnoDB;

alter table user
    drop index if exists UKew1hvam8uwaknuaellwhqchhb;

alter table user
    add constraint UKew1hvam8uwaknuaellwhqchhb unique (login);

alter table post
    add constraint FK72mt33dhhs48hf9gcqrq4fxte
        foreign key (user_id)
            references user (id);

alter table user add password_sha varchar(256) null after login;
