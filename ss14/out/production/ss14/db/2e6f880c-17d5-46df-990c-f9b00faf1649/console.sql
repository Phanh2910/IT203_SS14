CREATE DATABASE IF NOT EXISTS ss12;
USE ss12;

CREATE TABLE IF NOT EXISTS Doctors (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       doctor_code VARCHAR(20) NOT NULL UNIQUE,
                                       full_name VARCHAR(100) NOT NULL,
                                       password VARCHAR(255) NOT NULL,
                                       specialization VARCHAR(100),
                                       email VARCHAR(100)
);

-- 3. Insert sample data (Matching your Java code)
INSERT INTO Doctors (doctor_code, full_name, password, specialization)
VALUES ('D001', 'Dr. Nguyen Van A', '123', 'Cardiology');

INSERT INTO Doctors (doctor_code, full_name, password, specialization)
VALUES ('D002', 'Dr. Tran Thi B', '456', 'Pediatrics');