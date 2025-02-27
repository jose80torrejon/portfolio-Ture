create table airlines
(
	iata_code         varchar(50),
	airline           varchar(500)
);


create table airports
(
	iata_code         varchar(50),
	airport           varchar(500),
	city              varchar(100),
	state             varchar(100),
	country           varchar(100),
	latitude          double,
	longitude         double
);

create table flights
(
	year              smallint,
	month             smallint,
	day               smallint,
	day_of_week       smallint,
	airline           varchar(50),
	flight_number     smallint,
	tail_number       varchar(50),
	origin_airport    varchar(50),
	destination_airport    varchar(50),
	scheduled_departure    varchar(15),
	departure_time        varchar(15),
	departure_delay   smallint,
	taxi_out          smallint,
	wheels_off        varchar(15),
	scheduled_time    smallint,
	elapsed_time      smallint,
	air_time          smallint,
	distance          smallint,
	wheels_on         varchar(15),
	taxi_in           smallint,
	scheduled_arrival varchar(15),
	arrival_time      varchar(15),
	arrival_delay     smallint,
	diverted          smallint,
	cancelled         smallint,
	cancellation_reason    varchar(500),
	air_system_delay  smallint,
	security_delay    smallint,
	airline_delay     smallint,
	late_aircraft_delay    smallint,
	weather_delay     smallint
);
