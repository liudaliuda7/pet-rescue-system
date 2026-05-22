DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  name VARCHAR(64),
  role VARCHAR(16) NOT NULL DEFAULT 'user',
  avatar VARCHAR(255),
  phone VARCHAR(32),
  email VARCHAR(64),
  status INT DEFAULT 1,
  station_id BIGINT,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS rescue_station;
CREATE TABLE rescue_station (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  address VARCHAR(255),
  phone VARCHAR(32),
  contact VARCHAR(64),
  description TEXT,
  image VARCHAR(255),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS animal_type;
CREATE TABLE animal_type (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  description VARCHAR(255),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS animal;
CREATE TABLE animal (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  type_id BIGINT,
  gender VARCHAR(8),
  age VARCHAR(32),
  color VARCHAR(64),
  description TEXT,
  image VARCHAR(255),
  status VARCHAR(16) DEFAULT 'available',
  station_id BIGINT,
  health_status VARCHAR(64),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS help_request;
CREATE TABLE help_request (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  title VARCHAR(255),
  content TEXT,
  location VARCHAR(255),
  contact VARCHAR(64),
  image VARCHAR(255),
  status VARCHAR(16) DEFAULT 'pending',
  station_id BIGINT,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS help_record;
CREATE TABLE help_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_id BIGINT,
  station_id BIGINT,
  content TEXT,
  handler VARCHAR(64),
  result VARCHAR(255),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS adoption;
CREATE TABLE adoption (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  animal_id BIGINT,
  user_id BIGINT,
  status VARCHAR(16) DEFAULT 'pending',
  reason TEXT,
  contact VARCHAR(64),
  address VARCHAR(255),
  remark VARCHAR(255),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS visit_record;
CREATE TABLE visit_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  adoption_id BIGINT,
  content TEXT,
  status VARCHAR(32),
  image VARCHAR(255),
  visitor VARCHAR(64),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS health_record;
CREATE TABLE health_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  animal_id BIGINT,
  content TEXT,
  doctor VARCHAR(64),
  image VARCHAR(255),
  record_date DATE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);

DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  content TEXT,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0
);
