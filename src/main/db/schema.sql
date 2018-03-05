

create table security
(
  symbol varchar(10) not null
    constraint security_pkey
    primary key,
  name varchar(64) not null,
  enlisted_date date,
  index boolean default false not null
);



CREATE TABLE marketwatch
(
  symbol VARCHAR(10) NOT NULL
    CONSTRAINT marketwatch_pkey
    PRIMARY KEY,
  target_price NUMERIC(12, 2),
  quarters_estimate NUMERIC(12, 2),
  years_estimate NUMERIC(12, 2),
  median_pe_on_cy NUMERIC(12, 2),
  next_fiscal_year NUMERIC(12, 2),
  median_pe_next_fy NUMERIC(12, 2),
  last_quarter_earnings NUMERIC(12, 2),
  year_ago_earnings NUMERIC(12, 2),
  recommendation VARCHAR(32),
  number_of_ratings INTEGER,
  buy INTEGER,
  overweight INTEGER,
  hold INTEGER,
  underweight INTEGER,
  sell INTEGER,
  last_updated DATE NOT NULL,
  history TEXT
);

CREATE TABLE price_1m
(
  symbol VARCHAR(10) NOT NULL
    CONSTRAINT price_pkey_1m
    PRIMARY KEY,
  data TEXT NOT NULL,
  last_updated TIMESTAMP,
  last_error TEXT,
  last_updated_error TIMESTAMP
);

CREATE TABLE price_5m
(
  symbol VARCHAR(10) NOT NULL
    CONSTRAINT price_pkey_5m
    PRIMARY KEY,
  data TEXT NOT NULL,
  last_updated TIMESTAMP,
  last_error TEXT,
  last_updated_error TIMESTAMP
);

CREATE TABLE market_fin_viz
(
  symbol VARCHAR(10) NOT NULL,
  parameters BYTEA,
  analysts BYTEA,
  insiders BYTEA,
  last_updated_success TIMESTAMP,
  last_updated_error TIMESTAMP,
  last_error TEXT,
  last_warning TEXT
);

CREATE TABLE public.non_trading_days
(
  date DATE PRIMARY KEY NOT NULL
);

INSERT INTO public.non_trading_days (date) VALUES ('2018-03-30');
INSERT INTO public.non_trading_days (date) VALUES ('2018-05-28');
INSERT INTO public.non_trading_days (date) VALUES ('2018-07-04');
INSERT INTO public.non_trading_days (date) VALUES ('2018-09-03');
INSERT INTO public.non_trading_days (date) VALUES ('2018-11-22');
INSERT INTO public.non_trading_days (date) VALUES ('2018-12-25');

INSERT INTO public.non_trading_days (date) VALUES ('2019-01-01');
INSERT INTO public.non_trading_days (date) VALUES ('2019-01-21');
INSERT INTO public.non_trading_days (date) VALUES ('2019-02-18');
INSERT INTO public.non_trading_days (date) VALUES ('2019-04-19');
INSERT INTO public.non_trading_days (date) VALUES ('2019-05-27');
INSERT INTO public.non_trading_days (date) VALUES ('2019-07-04');
INSERT INTO public.non_trading_days (date) VALUES ('2019-09-02');
INSERT INTO public.non_trading_days (date) VALUES ('2019-11-28');
INSERT INTO public.non_trading_days (date) VALUES ('2019-12-25');

INSERT INTO public.non_trading_days (date) VALUES ('2020-01-01');
INSERT INTO public.non_trading_days (date) VALUES ('2020-01-20');
INSERT INTO public.non_trading_days (date) VALUES ('2020-02-17');
INSERT INTO public.non_trading_days (date) VALUES ('2020-04-10');
INSERT INTO public.non_trading_days (date) VALUES ('2020-05-25');
INSERT INTO public.non_trading_days (date) VALUES ('2020-07-03');
INSERT INTO public.non_trading_days (date) VALUES ('2020-09-07');
INSERT INTO public.non_trading_days (date) VALUES ('2020-11-26');
INSERT INTO public.non_trading_days (date) VALUES ('2020-12-25');


select * from price_5m;
delete from price_5m;
