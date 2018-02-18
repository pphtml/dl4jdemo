CREATE TABLE security
(
  symbol        VARCHAR(10) NOT NULL
    CONSTRAINT security_pkey
    PRIMARY KEY,
  name          VARCHAR(64) NOT NULL,
  enlisted_date DATE
);

