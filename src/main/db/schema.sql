CREATE TABLE security
(
  symbol        VARCHAR(10) NOT NULL
    CONSTRAINT security_pkey
    PRIMARY KEY,
  name          VARCHAR(64) NOT NULL,
  enlisted_date DATE
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

CREATE TABLE price
(
  symbol VARCHAR(10) NOT NULL
    CONSTRAINT price_pkey
    PRIMARY KEY,
  last TEXT NOT NULL,
  history TEXT,
  last_updated DATETIME NOT NULL
);

