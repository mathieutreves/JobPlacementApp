create sequence if not exists public.action_seq
    increment by 50;

create sequence if not exists public.address_seq
    increment by 50;

create sequence if not exists public.contact_seq
    increment by 50;

create sequence if not exists public.customer_seq
    increment by 50;

create sequence if not exists public.email_seq
    increment by 50;

create sequence if not exists public.job_offer_seq
    increment by 50;

create sequence if not exists public.message_seq
    increment by 50;

create sequence if not exists public.note_seq
    increment by 50;

create sequence if not exists public.phone_seq
    increment by 50;

create sequence if not exists public.professional_seq
    increment by 50;

create sequence if not exists public.skill_seq
    increment by 50;


create table if not exists public.message
(
    id       bigint primary key not null,
    body     character varying(255),
    channel  character varying(255),
    date     timestamp(6) without time zone,
    priority bigint             not null,
    sender   character varying(255),
    state    smallint,
    subject  character varying(255)
);

create table if not exists public.action
(
    id         bigint primary key not null,
    comment    character varying(255),
    date       timestamp(6) without time zone,
    state      smallint,
    message_id bigint,
    foreign key (message_id) references public.message (id)
        match simple on update no action on delete no action
);

create table if not exists public.address
(
    id          bigint primary key not null,
    city        character varying(255),
    country     character varying(255),
    number      character varying(255),
    postal_code character varying(255),
    street      character varying(255)
);

create table if not exists public.email
(
    id    bigint primary key not null,
    email character varying(255)
);

create table if not exists public.phone
(
    id     bigint primary key not null,
    number character varying(255)
);

create table if not exists public.contact
(
    id       bigint primary key not null,
    category character varying(255),
    name     character varying(255),
    ssn_code character varying(255),
    surname  character varying(255)
);

create table if not exists public.contact_address
(
    contact_id bigint not null,
    address_id bigint not null,
    primary key (contact_id, address_id),
    foreign key (address_id) references public.address (id)
        match simple on update no action on delete no action,
    foreign key (contact_id) references public.contact (id)
        match simple on update no action on delete no action
);

create table if not exists public.contact_email
(
    contact_id bigint not null,
    email_id   bigint not null,
    primary key (contact_id, email_id),
    foreign key (email_id) references public.email (id)
        match simple on update no action on delete no action,
    foreign key (contact_id) references public.contact (id)
        match simple on update no action on delete no action
);

create table if not exists public.contact_phone
(
    contact_id bigint not null,
    phone_id   bigint not null,
    primary key (contact_id, phone_id),
    foreign key (contact_id) references public.contact (id)
        match simple on update no action on delete no action,
    foreign key (phone_id) references public.phone (id)
        match simple on update no action on delete no action
);

create table if not exists public.skill
(
    id   bigint primary key not null,
    name character varying(255)
);

create table if not exists public.customer
(
    name       character varying(255),
    surname    character varying(255),
    contact_id bigint primary key not null,
    foreign key (contact_id) references public.contact (id)
        match simple on update no action on delete no action
);

create table if not exists public.professional
(
    daily_rate       character varying(255),
    employment_state character varying(255),
    location         character varying(255),
    name             character varying(255),
    surname          character varying(255),
    contact_id       bigint primary key not null,
    foreign key (contact_id) references public.contact (id)
        match simple on update no action on delete no action
);

create table if not exists public.job_offer
(
    id                      bigint primary key not null,
    description             character varying(255),
    duration                character varying(255),
    profit_margin           integer            not null,
    status                  smallint,
    value                   character varying(255),
    customer_contact_id     bigint,
    professional_contact_id bigint,
    foreign key (professional_contact_id) references public.professional (contact_id)
        match simple on update no action on delete no action,
    foreign key (customer_contact_id) references public.customer (contact_id)
        match simple on update no action on delete no action
);

create table if not exists public.professional_skills
(
    professionals_contact_id bigint not null,
    skills_id                bigint not null,
    primary key (professionals_contact_id, skills_id),
    foreign key (professionals_contact_id) references public.professional (contact_id)
        match simple on update no action on delete no action,
    foreign key (skills_id) references public.skill (id)
        match simple on update no action on delete no action
);

create table if not exists public.job_offer_skills
(
    job_offers_id bigint not null,
    skills_id     bigint not null,
    primary key (job_offers_id, skills_id),
    foreign key (job_offers_id) references public.job_offer (id)
        match simple on update no action on delete no action,
    foreign key (skills_id) references public.skill (id)
        match simple on update no action on delete no action
);

create table if not exists public.note
(
    id                      bigint primary key not null,
    note                    character varying(255),
    customer_contact_id     bigint,
    job_offer_id            bigint,
    professional_contact_id bigint,
    foreign key (job_offer_id) references public.job_offer (id)
        match simple on update no action on delete no action,
    foreign key (professional_contact_id) references public.professional (contact_id)
        match simple on update no action on delete no action,
    foreign key (customer_contact_id) references public.customer (contact_id)
        match simple on update no action on delete no action
);

