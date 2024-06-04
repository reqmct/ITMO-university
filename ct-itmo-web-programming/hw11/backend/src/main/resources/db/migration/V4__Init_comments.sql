create table comment
(
    id            bigint   not null,
    creation_time datetime(6),
    text          longtext not null,
    post_id       bigint   not null,
    user_id       bigint   not null,
    primary key (id)
) engine = InnoDB;

create index IDX7xrf7my1p0mc0oxfjlst1ey32 on comment (creation_time);
create sequence hibernate_sequence start with 1 increment by 1;
alter table comment
    add constraint FKs1slvnkuemjsq2kj4h3vhx7i1
        foreign key (post_id)
            references post (id);
alter table comment
    add constraint FK8kcum44fvpupyw6f5baccx25c
        foreign key (user_id)
            references user (id);