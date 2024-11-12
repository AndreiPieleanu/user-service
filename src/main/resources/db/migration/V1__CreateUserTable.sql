CREATE TABLE kwex_users
(
    id        int         NOT NULL AUTO_INCREMENT,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    email varchar(100) NOT NULL,
    password varchar(300) NOT NULL,
    createdAt DATE NOT NULL,
    updatedAt DATE NOT NULL,
    role varchar(20) not null,
    bio varchar(160) not null,
    location varchar(100) not null,
    website varchar(100) not null,
    PRIMARY KEY (id)
);
CREATE INDEX idx_email ON kwex_users (email);