--liquibase formatted sql

--changeset luisana:1
--comment: Creación de la tabla maestra compras
CREATE TABLE compras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    total DOUBLE NOT NULL
);

--changeset luisana:2
--comment: Creación de la tabla de detalle con relación a compras
CREATE TABLE detalle_compra (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_compra_ref BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    CONSTRAINT fk_compra_detalle FOREIGN KEY (id_compra_ref) REFERENCES compras(id) ON DELETE CASCADE
);

--changeset luisana:3
--comment: Inserción de 10 registros iniciales para pruebas
-- Compra 1
INSERT INTO compras (id_cliente, total) VALUES (1, 25000.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (1, 101, 1, 25000.0);
-- Compra 2
INSERT INTO compras (id_cliente, total) VALUES (2, 12000.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (2, 102, 2, 6000.0);
-- Compra 3
INSERT INTO compras (id_cliente, total) VALUES (3, 5000.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (3, 103, 1, 5000.0);
-- Compra 4
INSERT INTO compras (id_cliente, total) VALUES (1, 45000.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (4, 104, 3, 15000.0);
-- Compra 5
INSERT INTO compras (id_cliente, total) VALUES (4, 8500.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (5, 105, 1, 8500.0);
-- Compra 6
INSERT INTO compras (id_cliente, total) VALUES (5, 60000.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (6, 106, 1, 60000.0);
-- Compra 7
INSERT INTO compras (id_cliente, total) VALUES (2, 15000.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (7, 107, 5, 3000.0);
-- Compra 8
INSERT INTO compras (id_cliente, total) VALUES (3, 22000.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (8, 108, 2, 11000.0);
-- Compra 9
INSERT INTO compras (id_cliente, total) VALUES (6, 9990.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (9, 109, 1, 9990.0);
-- Compra 10
INSERT INTO compras (id_cliente, total) VALUES (7, 35000.0);
INSERT INTO detalle_compra (id_compra_ref, id_producto, cantidad, precio_unitario) VALUES (10, 110, 1, 35000.0);