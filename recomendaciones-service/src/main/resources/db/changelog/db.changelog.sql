--liquibase formatted sql

--changeset vicente:1
--comment: Creación de la tabla de recomendaciones
CREATE TABLE recomendaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    tipo_recomendacion VARCHAR(255) NOT NULL,
    comentario VARCHAR(255) NOT NULL,
    puntaje_afinidad DOUBLE NOT NULL
);

--changeset vicente:2
--comment: Inserción de 10 registros iniciales para pruebas coordinados con el historial
-- Recomendación 1: Para el cliente 1 basada en su compra del producto 101
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (1, 102, 'COMPLEMENTO', 'Combina excelente con tu última prenda adquirida', 95.5);

-- Recomendación 2: Para el cliente 2 basada en tendencia
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (2, 104, 'TENDENCIA', 'Lo más vendido de la temporada en tu categoría', 88.0);

-- Recomendación 3: Para el cliente 3
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (3, 101, 'POR_HISTORIAL', 'Sugerido en base a tus estilos favoritos', 92.0);

-- Recomendación 4: Para el cliente 1 de nuevo
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (1, 106, 'OFERTA', 'Prenda exclusiva con 20% de descuento para ti', 75.0);

-- Recomendación 5: Para el cliente 4
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (4, 108, 'TENDENCIA', 'Combina tu estilo urbano con esta nueva chaqueta', 85.0);

-- Recomendación 6: Para el cliente 5
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (5, 103, 'COMPLEMENTO', 'Ideal para complementar tu calzado comprado', 99.0);

-- Recomendación 7: Para el cliente 2 de nuevo
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (2, 110, 'POR_HISTORIAL', 'Artículos similares que te podrían interesar', 81.5);

-- Recomendación 8: Para el cliente 3 de nuevo
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (3, 105, 'OFERTA', 'Últimas unidades disponibles en tu talla', 90.0);

-- Recomendación 9: Para el cliente 6
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (6, 107, 'TENDENCIA', 'Destacado de la semana en Nexus-Fashion', 78.0);

-- Recomendación 10: Para el cliente 7
INSERT INTO recomendaciones (id_cliente, id_producto, tipo_recomendacion, comentario, puntaje_afinidad) 
VALUES (7, 109, 'COMPLEMENTO', 'Lleva el outfit completo agregando este accesorio', 94.0);