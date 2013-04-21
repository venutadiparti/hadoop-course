CREATE TABLE address (street STRING, city STRING, zip STRING) 
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ','
    STORED AS TEXTFILE;
LOAD DATA LOCAL INPATH 'data/address.txt' OVERWRITE INTO TABLE address;


CREATE TABLE user (address STRUCT<street:STRING, city:STRING, zip:STRING>);

INSERT OVERWRITE TABLE user SELECT struct(street,city,zip) from address; 
