CREATE EXTENSION btree_gist;

create table tb_user (
    id serial primary key,
    user_identification varchar(50) unique not null
);


create table tb_project (
    id serial primary key,
    user_id integer not null,
    project_name varchar (255) unique not null,
    create_time timestamp not null,
    delete_time timestamp default null,
    active boolean default true,
    foreign key (user_id) references tb_user (id)
);

create table tb_task (
    id serial primary key,
    project_id integer not null,
    user_id integer not null,
    task_description varchar (255) not null,
    start_time timestamp not null,
    end_time timestamp not null,
    volume integer,
    comment varchar (255),
    delete_time timestamp default null,
    active boolean default true,
    foreign key (project_id) references tb_project (id),
    foreign key (user_id) references tb_user (id),
    constraint uq_project_task_desc unique (project_id, task_description),
    exclude using gist (user_id with =, tsrange(start_time, end_time) with &&) where (active)
);
