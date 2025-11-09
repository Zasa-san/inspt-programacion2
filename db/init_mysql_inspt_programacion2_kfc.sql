-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS `inspt_programacion2_kfc`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `inspt_programacion2_kfc`;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `role` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`)
);

INSERT INTO `users` (`username`, `password`, `enabled`, `role`) VALUES
  ('Gerente1', 'Gerente1', 1, 'ROLE_ADMIN');
