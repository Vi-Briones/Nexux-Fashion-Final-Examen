--liquibase formatted sql

--changeset luisana:1
--comment: Creacion de la tabla envios compatible con Envio.java
CREATE TABLE envios (
    id_envio BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_compra BIGINT NOT NULL,
    estado_envio VARCHAR(50) NOT NULL,
    fecha_envio DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--changeset luisana:2
--comment: Creacion de la tabla detalles_envio compatible con DetalleEnvio.java
CREATE TABLE detalles_envio (
    id_detalle_envio BIGINT AUTO_INCREMENT PRIMARY KEY,
    direccion_destino VARCHAR(255) NOT NULL,
    comuna VARCHAR(100) NOT NULL,
    id_envio BIGINT NOT NULL,
    CONSTRAINT fk_detalles_envio_envio FOREIGN KEY (id_envio) REFERENCES envios(id_envio) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--changeset luisana:3
--comment: Carga inicial de 10 inserts corregidos con los nombres reales de las columnas
INSERT INTO envios (id_envio, id_compra, estado_envio, fecha_envio) VALUES
(1, 55, 'PENDIENTE', '2026-05-24 10:00:00'),
(2, 56, 'EN_RUTA', '2026-05-24 10:30:00'),
(3, 57, 'ENTREGADO', '2026-05-24 11:00:00'),
(4, 58, 'PENDIENTE', '2026-05-24 11:15:00'),
(5, 59, 'EN_RUTA', '2026-05-24 11:45:00'),
(6, 60, 'ENTREGADO', '2026-05-24 12:00:00'),
(7, 61, 'PENDIENTE', '2026-05-24 12:30:00'),
(8, 62, 'EN_RUTA', '2026-05-24 13:00:00'),
(9, 63, 'ENTREGADO', '2026-05-24 13:15:00'),
(10, 64, 'PENDIENTE', '2026-05-24 13:45:00');

INSERT INTO detalles_envio (id_detalle_envio, direccion_destino, comuna, id_envio) VALUES
(1, 'Gran Avenida José Miguel Carrera 3456', 'San Miguel', 1),
(2, 'Álvarez de Toledo 789', 'San Miguel', 2),
(3, 'Salesianos 1420', 'San Miguel', 3),
(4, 'El Llano Subercaseaux 2210', 'San Miguel', 4),
(5, 'Santa Rosa 5000', 'San Joaquín', 5),
(6, 'Las Industrias 2340', 'San Joaquín', 6),
(7, 'Vicuña Mackenna 4500', 'Macul', 7),
(8, 'Alameda 1230', 'Santiago Centro', 8),
(9, 'Providencia 1950', 'Providencia', 9),
(10, 'Avenida Departamental 650', 'San Miguel', 10);