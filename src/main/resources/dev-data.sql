CREATE TABLE IF NOT EXISTS "organization_event" (
  "id" char(100) NOT NULL,
  "timestamp" datetime NOT NULL,
  "organization_id" int(11) NOT NULL,
  "event" varchar(100) NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "organization_payment" (
  "id" char(100) NOT NULL,
  "timestamp" datetime NOT NULL,
  "organization_id" int(11) NOT NULL,
  "event" varchar(100) NOT NULL,
  "payment_amount" decimal(11,2) NOT NULL,
  "payment_processor" varchar(100) NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "unknown_event" (
  "id" char(100) NOT NULL,
  "read_at" datetime NOT NULL,
  "event_blob" varchar(2048) NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "user_event" (
  "id" char(100) NOT NULL,
  "timestamp" datetime NOT NULL,
  "user_id" int(11) NOT NULL,
  "event" varchar(100) NOT NULL,
  "social_network_type" varchar(100) DEFAULT NULL,
  PRIMARY KEY ("id")
);
