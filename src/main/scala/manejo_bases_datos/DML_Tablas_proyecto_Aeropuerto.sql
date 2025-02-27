Configurar la conexión con OPT_LOCA_INFILE=1 para que nos deje cargar los ficheros en las tablas


LOAD DATA LOCAL INFILE 'C:\\Users\\JOSELE\\Desktop\\Master Data Engineering - EOI\\Libros\\airlines.csv' INTO TABLE airlines
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:\\Users\\JOSELE\\Desktop\\Master Data Engineering - EOI\\Libros\\airports.csv' INTO TABLE airlines
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:\\Users\\JOSELE\\Desktop\\Master Data Engineering - EOI\\Libros\\flights.csv' INTO TABLE airlines
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES;
