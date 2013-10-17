DROP DATABASE IF EXISTS CAR_RENTAL;
CREATE DATABASE CAR_RENTAL;
USE CAR_RENTAL;

DROP TABLE IF EXISTS CUSTOMER;
CREATE TABLE CUSTOMER
(DriversLicense VARCHAR(10) NOT NULL,
CreditCard INT,
FullName VARCHAR(30),
Age INT CHECK (Age >= 18),
RentTo BOOLEAN DEFAULT TRUE,
updatedAt DATE,
PRIMARY KEY(DriversLicense)
);

DROP TABLE IF EXISTS CUSTOMER_ARCHIVE;
CREATE TABLE CUSTOMERS_ARCHIVE
(DriversLicense VARCHAR(10) NOT NULL,
CreditCard INT,
FullName VARCHAR(30),
Age INT CHECK (Age >= 18),
RentTo BOOLEAN,
updatedAt DATE,
PRIMARY KEY(DriversLicense)
);

DROP TABLE IF EXISTS CAR;
CREATE TABLE CAR
(LicensePlate VARCHAR(7) NOT NULL,
Model VARCHAR(30),
Make VARCHAR(30),
Year INT,
PRIMARY KEY(LicensePlate)
);

DROP TABLE IF EXISTS CAR_SPEC;
CREATE TABLE CAR_SPEC
(LicensePlate VARCHAR(7) NOT NULL,
MPG INT,
TwoFourDoor INT,
Color VARCHAR(30),
CurrentMiles INT CHECK (CurrentMiles >= 0),
PRIMARY KEY(LicensePlate),
FOREIGN KEY (LicensePlate) REFERENCES Car(LicensePlate)
);

DROP TABLE IF EXISTS RENTAL;
CREATE TABLE RENTAL
(LicensePlate VARCHAR(7),
DriversLicense VARCHAR(10),
DateRented DATE,
ReturnDate DATE,
OverDue BOOLEAN,
updatedAt DATE,
PRIMARY KEY(LicensePlate, DriversLicense),
FOREIGN KEY (LicensePlate) REFERENCES Car(LicensePlate),
FOREIGN KEY (DriversLicense) REFERENCES Customer(DriversLicense)
);

DROP TABLE IF EXISTS RENTAL_ARCHIVE;
CREATE TABLE RENTAL_ARCHIVE
(LicensePlate VARCHAR(7),
DriversLicense VARCHAR(10),
DateRented DATE,
ReturnDate DATE,
OverDue BOOLEAN,
updatedAt DATE,
PRIMARY KEY(LicensePlate, DriversLicense)
);

DROP TABLE IF EXISTS RETURNED;
CREATE TABLE RETURNED
(LicensePlate VARCHAR(7),
DriversLicense VARCHAR(10),
DateRented DATE,
ReturnDate DATE,
OverDue BOOLEAN,
MilesDriven INT,
Accident INT,
TimesRented INT,
updatedAt DATE,
PRIMARY KEY(LicensePlate),
FOREIGN KEY (LicensePlate) REFERENCES Car(LicensePlate),
FOREIGN KEY (DriversLicense) REFERENCES Customer(DriversLicense)
);

DROP TABLE IF EXISTS RETURNED_ARCHIVE;
CREATE TABLE RETURNED_ARCHIVE
(LicensePlate VARCHAR(7),
DriversLicense VARCHAR(10),
DateRented DATE,
ReturnDate DATE,
OverDue BOOLEAN,
MilesDriven INT,
Accident INT,
TimesRented INT,
updatedAt DATE,
PRIMARY KEY(LicensePlate)
);

LOAD DATA LOCAL INFILE 'C:\\Users\\Car Rental\\customer.txt' INTO TABLE CUSTOMER;
LOAD DATA LOCAL INFILE 'C:\\Users\\Car Rental\\car.txt' INTO TABLE CAR;
LOAD DATA LOCAL INFILE 'C:\\Users\\Car Rental\\car_spec.txt' INTO TABLE CAR_SPEC;

DROP TRIGGER IF EXISTS RemoveRentedCar;
DELIMITER //
CREATE TRIGGER RemoveRentedCar
AFTER INSERT ON Returned
FOR EACH ROW
BEGIN
	DELETE FROM Rental 
	WHERE NEW.LicensePlate = Rental.LicensePlate AND NEW.OverDue = false AND NEW.Accident = 0;
	
	UPDATE Customer 
	SET RentTo = false
	WHERE NEW.DriversLicense = Customer.DriversLicense AND NEW.Accident > 0;
	
	UPDATE Car_Spec 
	SET CurrentMiles = NEW.MilesDriven
	WHERE Car_spec.LicensePlate = NEW.LicensePlate;
END; // 
DELIMITER ;

DROP TRIGGER IF EXISTS RentOneAtATime;
DELIMITER //
CREATE TRIGGER RentOneAtATime
AFTER INSERT ON Rental
FOR EACH ROW
BEGIN
	UPDATE Customer 
	SET RentTo = false
	WHERE NEW.DriversLicense = Customer.DriversLicense;
END; // 
DELIMITER ;
