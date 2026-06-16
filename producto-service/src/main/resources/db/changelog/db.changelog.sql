--liquibase formatted sql

--changeset autor:1
-- Creación de la tabla Categoría
CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

--changeset autor:2
-- Inserción de datos iniciales en Categoría
INSERT INTO categoria (nombre) VALUES ('Ropa');
INSERT INTO categoria (nombre) VALUES ('Accesorios');
INSERT INTO categoria (nombre) VALUES ('Calzado');
INSERT INTO categoria (nombre) VALUES ('Deportiva');

--changeset autor:3
-- Creación de la tabla Producto
CREATE TABLE IF NOT EXISTS producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL,
    id_categoria BIGINT NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

--changeset autor:4
-- Inserción de datos iniciales en Producto
INSERT INTO producto (nombre, precio, stock, id_categoria) VALUES
('Camiseta Negra', 10000, 50, 1),
('Pantalon Jeans', 20000, 30, 1),
('Chaqueta de Cuero', 70000, 15, 1),
('Gorro Lana', 10000, 80, 2),
('Cinturon Cuero', 10000, 40, 2),
('Zapatillas Deportivas', 89990, 25, 3),
('Zapatos Formales', 119990, 20, 3),
('Shorts Deportivos', 15000, 60, 4),
('Camiseta Deportiva', 20000, 100, 4);