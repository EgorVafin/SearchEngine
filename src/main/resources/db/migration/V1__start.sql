create table lemma (id integer not null auto_increment, frequency integer not null, lemma varchar(255) not null, site_id integer not null, primary key (id)) engine=InnoDB;
create table lemma_index (id integer not null auto_increment, lemma_rank float not null, lemma_id integer not null, page_id integer not null, primary key (id)) engine=InnoDB;
create table page (id integer not null auto_increment, code integer not null, content MEDIUMTEXT NOT NULL, path TEXT NOT NULL, site_id integer not null, primary key (id)) engine=InnoDB;
create table site (id integer not null auto_increment, last_error TEXT, name VARCHAR(255) not null, status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL, status_time time not null, url VARCHAR(255) not null, primary key (id)) engine=InnoDB;

alter table lemma add constraint FKfbq251d28jauqlxirb1k2cjag foreign key (site_id) references site (id) on delete cascade;
alter table lemma_index add constraint FKcg6g2on92fqq58w5t0m6271qp foreign key (lemma_id) references lemma (id) on delete cascade;
alter table lemma_index add constraint FKabycx15lbdehrjax4x0o53vt7 foreign key (page_id) references page (id) on delete cascade;
alter table page add constraint FKj2jx0gqa4h7wg8ls0k3y221h2 foreign key (site_id) references site (id) on delete cascade;

alter table page add index (path(100));
