-- Añadir campos de aprobación a la tabla restaurantes
ALTER TABLE restaurantes
    ADD COLUMN solicitado   TINYINT(1) NOT NULL DEFAULT 0    AFTER activo,
    ADD COLUMN aprobado     TINYINT(1) NOT NULL DEFAULT 0    AFTER solicitado,
    ADD COLUMN aprobado_por INT        NULL     DEFAULT NULL  AFTER aprobado,
    ADD CONSTRAINT fk_aprobado_por
        FOREIGN KEY (aprobado_por) REFERENCES usuarios(id) ON DELETE SET NULL;
