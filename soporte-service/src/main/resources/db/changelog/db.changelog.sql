--liquibase formatted sql

--changeset luisana:1
--comment: Creación de la tabla SOPORTE para el microservicio de soporte
CREATE TABLE IF NOT EXISTS soporte (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    asunto VARCHAR(150) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    prioridad VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
);

--changeset luisana:2
--comment: Inserción de 10 registros de soporte iniciales para pruebas
-- Ticket 1
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (1, 'Error en inicio de sesión', 'No puedo entrar con mis credenciales de Google.', 'PENDIENTE', 'ALTA', CURRENT_TIMESTAMP);
-- Ticket 2
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (2, 'Fallo al procesar el pago', 'Me cobraron doble al usar tarjeta de débito.', 'EN_PROCESO', 'CRITICA', CURRENT_TIMESTAMP);
-- Ticket 3
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (3, 'Problema con talla de prenda', 'Deseo cambiar una polera de talla M a L.', 'PENDIENTE', 'MEDIA', CURRENT_TIMESTAMP);
-- Ticket 4
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (1, 'Retraso en el envío', 'Mi pedido lleva 5 días de retraso con Starken.', 'PENDIENTE', 'MEDIA', CURRENT_TIMESTAMP);
-- Ticket 5
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (4, 'Cupón de descuento inválido', 'El código NEXUS2026 no se aplica en el carrito.', 'RESUELTO', 'BAJA', CURRENT_TIMESTAMP);
-- Ticket 6
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (5, 'Página caída en checkout', 'Da error 500 al intentar finalizar la compra.', 'EN_PROCESO', 'ALTA', CURRENT_TIMESTAMP);
-- Ticket 7
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (2, 'Error en datos de facturación', 'Ingresé mal mi RUT en la boleta de compra.', 'RESUELTO', 'BAJA', CURRENT_TIMESTAMP);
-- Ticket 8
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (3, 'Reembolso no recibido', 'Aún no veo reflejado el dinero de la devolución.', 'PENDIENTE', 'ALTA', CURRENT_TIMESTAMP);
-- Ticket 9
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (6, 'Falta stock de producto', 'El vestido azul figura sin stock pero se puede seleccionar.', 'CERRADO', 'BAJA', CURRENT_TIMESTAMP);
-- Ticket 10
INSERT INTO soporte (id_cliente, asunto, descripcion, estado, prioridad, fecha_creacion) 
VALUES (7, 'Modificación de dirección', 'Necesito cambiar la calle de despacho de mi compra.', 'EN_PROCESO', 'MEDIA', CURRENT_TIMESTAMP);