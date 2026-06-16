--liquibase formatted sql

--changeset autor:1
-- Creación de la tabla MetodoPago
CREATE TABLE IF NOT EXISTS metodo_pago (
    id_metodo_pago BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

--changeset autor:2
-- Inserción de datos iniciales en MetodoPago
INSERT INTO metodo_pago (nombre) VALUES ('TARJETA_CREDITO');
INSERT INTO metodo_pago (nombre) VALUES ('TARJETA_DEBITO');
INSERT INTO metodo_pago (nombre) VALUES ('TRANSFERENCIA');
INSERT INTO metodo_pago (nombre) VALUES ('EFECTIVO');

--changeset autor:3
-- Creación de la tabla Venta
CREATE TABLE IF NOT EXISTS venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    fecha DATE NOT NULL,
    total DOUBLE NOT NULL,
    id_metodo_pago BIGINT NOT NULL,
    FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id_metodo_pago)
);

--changeset autor:4
-- Inserción de datos iniciales en Venta
INSERT INTO venta (id_cliente, fecha, total, id_metodo_pago) VALUES
(1, '2026-05-01', 29999, 1),
(2, '2026-05-02', 59999, 2),
(1, '2026-05-05', 14999, 3),
(3, '2026-05-10', 89999, 1),
(4, '2026-05-15', 19999, 4);

--changeset autor:5
-- Creación de la tabla DetalleVenta
CREATE TABLE IF NOT EXISTS detalle_venta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE
);

--changeset autor:6
-- Inserción de datos iniciales en DetalleVenta
INSERT INTO detalle_venta (id_venta, id_producto, cantidad) VALUES
(1, 1, 2),
(1, 3, 1),
(2, 2, 3),
(3, 5, 2),
(4, 1, 1),
(4, 4, 2),
(5, 3, 5);
