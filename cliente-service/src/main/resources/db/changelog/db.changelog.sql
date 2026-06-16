--liquibase formatted sql

--changeset autor:1
-- Creación de la tabla Rol
CREATE TABLE IF NOT EXISTS rol (
    id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

--changeset autor:2
-- Inserción de datos iniciales en Rol
INSERT INTO rol (nombre) VALUES ('ADMIN');
INSERT INTO rol (nombre) VALUES ('CLIENTE');
INSERT INTO rol (nombre) VALUES ('VENDEDOR');

--changeset autor:3
-- Creación de la tabla Cliente
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(255) NOT NULL,
    id_rol BIGINT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

--changeset autor:4
-- Inserción de datos iniciales en Cliente (10+ registros)
INSERT INTO cliente (nombre, correo, contraseña, id_rol) VALUES
('Juan García', 'juan.garcia@nexusfashion.com', '$2a$10$hashedpassword1', 2),
('María López', 'maria.lopez@nexusfashion.com', '$2a$10$hashedpassword2', 2),
('Carlos Martínez', 'carlos.martinez@nexusfashion.com', '$2a$10$hashedpassword3', 2),
('Ana Rodríguez', 'ana.rodriguez@nexusfashion.com', '$2a$10$hashedpassword4', 2),
('Pedro Fernández', 'pedro.fernandez@nexusfashion.com', '$2a$10$hashedpassword5', 2),
('Laura González', 'laura.gonzalez@nexusfashion.com', '$2a$10$hashedpassword6', 2),
('Diego Sánchez', 'diego.sanchez@nexusfashion.com', '$2a$10$hashedpassword7', 2),
('Sofia Pérez', 'sofia.perez@nexusfashion.com', '$2a$10$hashedpassword8', 2),
('Roberto Díaz', 'roberto.diaz@nexusfashion.com', '$2a$10$hashedpassword9', 2),
('Isabela Torres', 'isabela.torres@nexusfashion.com', '$2a$10$hashedpassword10', 2),
('Andrés Morales', 'andres.morales@nexusfashion.com', '$2a$10$hashedpassword11', 2);
