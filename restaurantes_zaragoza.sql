-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 06, 2026 at 09:41 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `restaurantes_zaragoza`
--

-- --------------------------------------------------------

--
-- Table structure for table `fotos_restaurante`
--

CREATE TABLE `fotos_restaurante` (
  `id` int(11) NOT NULL,
  `restaurante_id` int(11) NOT NULL,
  `url_foto` varchar(500) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `es_portada` tinyint(1) DEFAULT 0,
  `orden` int(11) DEFAULT 0,
  `fecha_subida` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `fotos_restaurante`
--

INSERT INTO `fotos_restaurante` (`id`, `restaurante_id`, `url_foto`, `descripcion`, `es_portada`, `orden`, `fecha_subida`) VALUES
(1, 7, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800', 'Interior de la taberna', 1, 1, '2026-01-11 10:00:00'),
(2, 7, 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800', 'Platos principales', 0, 2, '2026-01-11 10:01:00'),
(3, 8, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800', 'Chuletón a la brasa', 1, 1, '2026-01-11 10:02:00'),
(4, 8, 'https://images.unsplash.com/photo-1558030006-450675393462?w=800', 'Terraza junto al Ebro', 0, 2, '2026-01-11 10:03:00'),
(5, 9, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=800', 'Zona de desayunos', 1, 1, '2026-01-11 10:04:00'),
(6, 11, 'https://images.unsplash.com/photo-1559339352-11d035aa65de?w=800', 'Marisco fresco del día', 1, 1, '2026-01-11 10:05:00'),
(7, 12, 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=800', 'Tacos y salsas', 1, 1, '2026-01-11 10:06:00'),
(8, 13, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800', 'Bowl de quinoa', 1, 1, '2026-01-11 10:07:00'),
(9, 14, 'https://images.unsplash.com/photo-1569050467447-ce54b3bbc37d?w=800', 'Ramen tonkotsu', 1, 1, '2026-01-11 10:08:00'),
(10, 15, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800', 'La Baturra burger', 1, 1, '2026-01-11 10:09:00'),
(11, 16, 'https://images.unsplash.com/photo-1528736235302-52922df5c122?w=800', 'Barra de pinchos', 1, 1, '2026-01-11 10:10:00');

-- --------------------------------------------------------

--
-- Table structure for table `horarios`
--

CREATE TABLE `horarios` (
  `id` int(11) NOT NULL,
  `restaurante_id` int(11) NOT NULL,
  `dia_semana` tinyint(4) NOT NULL,
  `hora_apertura` time DEFAULT NULL,
  `hora_cierre` time DEFAULT NULL,
  `cerrado` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `horarios`
--

INSERT INTO `horarios` (`id`, `restaurante_id`, `dia_semana`, `hora_apertura`, `hora_cierre`, `cerrado`) VALUES
(1, 1, 0, '13:00:00', '16:00:00', 0),
(2, 1, 0, '20:00:00', '23:30:00', 0),
(3, 1, 1, '13:00:00', '16:00:00', 0),
(4, 1, 1, '20:00:00', '23:30:00', 0),
(5, 1, 2, '13:00:00', '16:00:00', 0),
(6, 1, 2, '20:00:00', '23:30:00', 0),
(7, 1, 3, '13:00:00', '16:00:00', 0),
(8, 1, 3, '20:00:00', '23:30:00', 0),
(9, 1, 4, '13:00:00', '16:00:00', 0),
(10, 1, 4, '20:00:00', '23:30:00', 0),
(11, 1, 5, '13:00:00', '17:00:00', 0),
(12, 1, 5, '20:00:00', '00:00:00', 0),
(13, 1, 6, NULL, NULL, 1),
(14, 7, 0, NULL, NULL, 1),
(15, 7, 1, '12:00:00', '16:00:00', 0),
(16, 7, 1, '19:30:00', '23:30:00', 0),
(17, 7, 2, '12:00:00', '16:00:00', 0),
(18, 7, 2, '19:30:00', '23:30:00', 0),
(19, 7, 3, '12:00:00', '16:00:00', 0),
(20, 7, 3, '19:30:00', '23:30:00', 0),
(21, 7, 4, '12:00:00', '16:00:00', 0),
(22, 7, 4, '19:30:00', '23:30:00', 0),
(23, 7, 5, '12:00:00', '17:00:00', 0),
(24, 7, 5, '19:30:00', '00:00:00', 0),
(25, 7, 6, '12:00:00', '17:00:00', 0),
(26, 7, 6, '19:30:00', '00:00:00', 0),
(27, 8, 0, NULL, NULL, 1),
(28, 8, 1, NULL, NULL, 1),
(29, 8, 2, '13:00:00', '16:30:00', 0),
(30, 8, 2, '20:00:00', '23:30:00', 0),
(31, 8, 3, '13:00:00', '16:30:00', 0),
(32, 8, 3, '20:00:00', '23:30:00', 0),
(33, 8, 4, '13:00:00', '16:30:00', 0),
(34, 8, 4, '20:00:00', '23:30:00', 0),
(35, 8, 5, '13:00:00', '17:00:00', 0),
(36, 8, 5, '20:00:00', '00:00:00', 0),
(37, 8, 6, '13:00:00', '17:00:00', 0),
(38, 8, 6, '20:00:00', '00:00:00', 0),
(39, 9, 0, '08:00:00', '22:00:00', 0),
(40, 9, 1, '08:00:00', '22:00:00', 0),
(41, 9, 2, '08:00:00', '22:00:00', 0),
(42, 9, 3, '08:00:00', '22:00:00', 0),
(43, 9, 4, '08:00:00', '22:00:00', 0),
(44, 9, 5, '08:00:00', '23:00:00', 0),
(45, 9, 6, '09:00:00', '23:00:00', 0),
(46, 10, 0, '13:00:00', '16:00:00', 0),
(47, 10, 0, '20:00:00', '23:30:00', 0),
(48, 10, 1, '13:00:00', '16:00:00', 0),
(49, 10, 1, '20:00:00', '23:30:00', 0),
(50, 10, 2, '13:00:00', '16:00:00', 0),
(51, 10, 2, '20:00:00', '23:30:00', 0),
(52, 10, 3, '13:00:00', '16:00:00', 0),
(53, 10, 3, '20:00:00', '23:30:00', 0),
(54, 10, 4, '13:00:00', '16:00:00', 0),
(55, 10, 4, '20:00:00', '23:30:00', 0),
(56, 10, 5, '13:00:00', '17:00:00', 0),
(57, 10, 5, '20:00:00', '00:30:00', 0),
(58, 10, 6, NULL, NULL, 1),
(59, 11, 0, NULL, NULL, 1),
(60, 11, 1, '13:00:00', '16:30:00', 0),
(61, 11, 1, '20:00:00', '23:30:00', 0),
(62, 11, 2, '13:00:00', '16:30:00', 0),
(63, 11, 2, '20:00:00', '23:30:00', 0),
(64, 11, 3, '13:00:00', '16:30:00', 0),
(65, 11, 3, '20:00:00', '23:30:00', 0),
(66, 11, 4, '13:00:00', '16:30:00', 0),
(67, 11, 4, '20:00:00', '23:30:00', 0),
(68, 11, 5, '13:00:00', '17:00:00', 0),
(69, 11, 5, '20:00:00', '00:00:00', 0),
(70, 11, 6, '13:00:00', '17:00:00', 0),
(71, 11, 6, '20:00:00', '00:00:00', 0),
(72, 12, 0, NULL, NULL, 1),
(73, 12, 1, '20:00:00', '23:30:00', 0),
(74, 12, 2, '20:00:00', '23:30:00', 0),
(75, 12, 3, '20:00:00', '23:30:00', 0),
(76, 12, 4, '20:00:00', '23:30:00', 0),
(77, 12, 5, '13:00:00', '16:00:00', 0),
(78, 12, 5, '20:00:00', '00:30:00', 0),
(79, 12, 6, '13:00:00', '16:00:00', 0),
(80, 12, 6, '20:00:00', '00:30:00', 0),
(81, 13, 0, NULL, NULL, 1),
(82, 13, 1, NULL, NULL, 1),
(83, 13, 2, '13:00:00', '16:00:00', 0),
(84, 13, 3, '13:00:00', '16:00:00', 0),
(85, 13, 4, '13:00:00', '16:00:00', 0),
(86, 13, 4, '19:30:00', '22:30:00', 0),
(87, 13, 5, '13:00:00', '16:30:00', 0),
(88, 13, 5, '19:30:00', '22:30:00', 0),
(89, 13, 6, '12:00:00', '16:30:00', 0),
(90, 13, 6, '19:30:00', '22:30:00', 0),
(91, 14, 0, NULL, NULL, 1),
(92, 14, 1, '13:00:00', '15:30:00', 0),
(93, 14, 1, '20:00:00', '23:00:00', 0),
(94, 14, 2, '13:00:00', '15:30:00', 0),
(95, 14, 2, '20:00:00', '23:00:00', 0),
(96, 14, 3, '13:00:00', '15:30:00', 0),
(97, 14, 3, '20:00:00', '23:00:00', 0),
(98, 14, 4, '13:00:00', '15:30:00', 0),
(99, 14, 4, '20:00:00', '23:00:00', 0),
(100, 14, 5, '13:00:00', '16:00:00', 0),
(101, 14, 5, '20:00:00', '23:30:00', 0),
(102, 14, 6, '13:00:00', '16:00:00', 0),
(103, 14, 6, '20:00:00', '23:30:00', 0),
(104, 15, 0, '20:00:00', '23:00:00', 0),
(105, 15, 1, '20:00:00', '23:00:00', 0),
(106, 15, 2, '20:00:00', '23:00:00', 0),
(107, 15, 3, '20:00:00', '23:00:00', 0),
(108, 15, 4, '20:00:00', '23:00:00', 0),
(109, 15, 5, '13:00:00', '16:00:00', 0),
(110, 15, 5, '20:00:00', '23:30:00', 0),
(111, 15, 6, '13:00:00', '16:00:00', 0),
(112, 15, 6, '20:00:00', '23:30:00', 0),
(113, 16, 0, '11:00:00', '15:30:00', 0),
(114, 16, 0, '19:00:00', '23:00:00', 0),
(115, 16, 1, '11:00:00', '15:30:00', 0),
(116, 16, 1, '19:00:00', '23:00:00', 0),
(117, 16, 2, '11:00:00', '15:30:00', 0),
(118, 16, 2, '19:00:00', '23:00:00', 0),
(119, 16, 3, '11:00:00', '15:30:00', 0),
(120, 16, 3, '19:00:00', '23:00:00', 0),
(121, 16, 4, '11:00:00', '15:30:00', 0),
(122, 16, 4, '19:00:00', '23:00:00', 0),
(123, 16, 5, '11:00:00', '17:00:00', 0),
(124, 16, 5, '19:00:00', '00:00:00', 0),
(125, 16, 6, '11:00:00', '17:00:00', 0),
(126, 16, 6, '19:00:00', '00:00:00', 0);

-- --------------------------------------------------------

--
-- Table structure for table `menu_categorias`
--

CREATE TABLE `menu_categorias` (
  `id` int(11) NOT NULL,
  `restaurante_id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `orden` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menu_categorias`
--

INSERT INTO `menu_categorias` (`id`, `restaurante_id`, `nombre`, `orden`) VALUES
(1, 1, 'Entrantes', 1),
(2, 1, 'Carnes', 2),
(3, 1, 'Pescados', 3),
(4, 1, 'Postres', 4),
(5, 2, 'Entrantes', 1),
(6, 2, 'Pastas', 2),
(7, 2, 'Postres', 3),
(8, 7, 'Pinchos y tapas', 1),
(9, 7, 'Raciones', 2),
(10, 7, 'Platos principales', 3),
(11, 7, 'Postres', 4),
(12, 8, 'Entrantes', 1),
(13, 8, 'Carnes a la brasa', 2),
(14, 8, 'Verduras a la brasa', 3),
(15, 8, 'Postres', 4),
(16, 9, 'Desayunos', 1),
(17, 9, 'Bocadillos', 2),
(18, 9, 'Ensaladas', 3),
(19, 9, 'Brunch', 4),
(20, 10, 'Pinchos', 1),
(21, 10, 'Tablas', 2),
(22, 10, 'Platos', 3),
(23, 11, 'Mariscos al vapor', 1),
(24, 11, 'Pescados a la plancha', 2),
(25, 11, 'Arroces', 3),
(26, 11, 'Postres', 4),
(27, 12, 'Guacamoles y salsas', 1),
(28, 12, 'Tacos', 2),
(29, 12, 'Tostadas', 3),
(30, 12, 'Postres', 4),
(31, 13, 'Aperitivos', 1),
(32, 13, 'Ensaladas', 2),
(33, 13, 'Platos calientes', 3),
(34, 13, 'Postres veganos', 4),
(35, 14, 'Entrantes japoneses', 1),
(36, 14, 'Nigiris y makis', 2),
(37, 14, 'Ramen', 3),
(38, 14, 'Postres', 4),
(39, 15, 'Para empezar', 1),
(40, 15, 'Hamburguesas', 2),
(41, 15, 'Sides', 3),
(42, 15, 'Postres', 4),
(43, 16, 'Pinchos fríos', 1),
(44, 16, 'Pinchos calientes', 2),
(45, 16, 'Montaditos', 3),
(46, 16, 'Bebidas', 4),
(47, 18, 'Especialidades', 1),
(48, 18, 'Platos principales', 2),
(49, 18, 'entrantes', 3),
(50, 18, '45', 4),
(51, 19, 'Entrante', 1),
(52, 19, 'Primer plato', 2),
(53, 19, 'Segundo Plato', 3),
(54, 19, 'Postre', 4);

-- --------------------------------------------------------

--
-- Table structure for table `menu_platos`
--

CREATE TABLE `menu_platos` (
  `id` int(11) NOT NULL,
  `categoria_id` int(11) NOT NULL,
  `restaurante_id` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio` decimal(6,2) NOT NULL,
  `foto_url` varchar(500) DEFAULT NULL,
  `disponible` tinyint(1) DEFAULT 1,
  `alergenos` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menu_platos`
--

INSERT INTO `menu_platos` (`id`, `categoria_id`, `restaurante_id`, `nombre`, `descripcion`, `precio`, `foto_url`, `disponible`, `alergenos`) VALUES
(1, 1, 1, 'Croquetas de jamón', 'Cremosas croquetas caseras', 8.50, NULL, 1, NULL),
(2, 1, 1, 'Ensalada César', 'Con pollo a la plancha', 10.00, NULL, 1, NULL),
(3, 2, 1, 'Chuletón de Ternasco', '500g con guarnición', 28.00, NULL, 1, NULL),
(4, 2, 1, 'Magret de pato', 'Con salsa de frutos rojos', 22.00, NULL, 1, NULL),
(5, 4, 1, 'Flan de huevo', 'Con nata montada', 5.00, NULL, 1, NULL),
(6, 5, 2, 'Burrata', 'Con tomate pera y albahaca', 9.50, NULL, 1, NULL),
(7, 6, 2, 'Tagliatelle carbonara', 'Receta tradicional italiana', 14.00, NULL, 1, NULL),
(8, 8, 7, 'Pincho de tortilla', 'Tortilla española con cebolla caramelizada', 2.00, NULL, 1, 'huevo, gluten'),
(9, 8, 7, 'Pincho de jamón ibérico', 'Pan de cristal con jamón ibérico D.O.', 3.50, NULL, 1, 'gluten'),
(10, 8, 7, 'Pimiento del piquillo', 'Relleno de bacalao al pil-pil', 2.80, NULL, 1, 'pescado, gluten'),
(11, 9, 7, 'Ración de patatas bravas', 'Con salsa brava casera y alioli', 7.00, NULL, 1, 'huevo'),
(12, 9, 7, 'Ración de queso curado', 'Queso manchego curado con membrillo', 9.00, NULL, 1, 'lacteos'),
(13, 10, 7, 'Migas a la pastora', 'Migas aragonesas con chorizo y pimiento', 14.00, NULL, 1, 'gluten'),
(14, 10, 7, 'Ternasco al horno', 'Paletilla de ternasco I.G.P. con patatas panadera', 22.00, NULL, 1, NULL),
(15, 11, 7, 'Helado de turrón', 'Artesanal con almendra marcona', 5.50, NULL, 1, 'frutos secos, lacteos'),
(16, 11, 7, 'Crema catalana', 'Con su costra de azúcar quemada', 4.50, NULL, 1, 'huevo, lacteos'),
(17, 12, 8, 'Cogote de merluza', 'Al horno con aceite de oliva virgen y ajo', 16.00, NULL, 1, 'pescado'),
(18, 12, 8, 'Ensalada de temporada', 'Lechugas, tomate cherry, pepino y vinagreta', 9.00, NULL, 1, NULL),
(19, 13, 8, 'Chuletón de buey', '600g madurado 45 días, con sal Maldon', 42.00, NULL, 1, NULL),
(20, 13, 8, 'Secreto ibérico', 'A la brasa con mojo verde y patatas', 19.00, NULL, 1, NULL),
(21, 13, 8, 'Cordero al sarmiento', 'Costillar de cordero lechal a las brasas de vid', 24.00, NULL, 1, NULL),
(22, 14, 8, 'Alcachofas a la brasa', 'Con vinagreta de albahaca y piñones', 11.00, NULL, 1, 'frutos secos'),
(23, 14, 8, 'Pimientos asados', 'Del piquillo asados al fuego con aceite y sal', 8.00, NULL, 1, NULL),
(24, 15, 8, 'Torrijas con helado', 'Torrijas de brioche con helado de vainilla', 7.00, NULL, 1, 'gluten, lacteos, huevo'),
(25, 16, 9, 'Tostada con tomate', 'Pan de masa madre con tomate rallado y AOVE', 4.50, NULL, 1, 'gluten'),
(26, 16, 9, 'Tostada con aguacate', 'Aguacate, huevo poché y semillas', 6.00, NULL, 1, 'gluten, huevo'),
(27, 17, 9, 'Bocadillo de calamares', 'Baguete crujiente con calamares fritos y limón', 7.50, NULL, 1, 'gluten, pescado'),
(28, 17, 9, 'Bocadillo vegetal', 'Con hummus, zanahoria rallada y rúcula', 6.50, NULL, 1, 'gluten, sesamo'),
(29, 18, 9, 'Ensalada griega', 'Tomate, pepino, cebolla, aceitunas y feta', 9.00, NULL, 1, 'lacteos'),
(30, 19, 9, 'Brunch completo', 'Huevos, tostadas, fruta, zumo y café', 14.00, NULL, 1, 'gluten, huevo, lacteos'),
(31, 20, 10, 'Pincho de txistorra', 'Txistorra a la plancha sobre pan de hogaza', 2.50, NULL, 1, 'gluten'),
(32, 20, 10, 'Pincho de queso Idiazabal', 'Con tomate seco y albahaca', 2.80, NULL, 1, 'lacteos, gluten'),
(33, 21, 10, 'Tabla ibérica', 'Jamón, lomo, chorizo y queso curado', 16.00, NULL, 1, 'lacteos, gluten'),
(34, 21, 10, 'Tabla de quesos', 'Variedad de quesos españoles con frutos secos', 14.00, NULL, 1, 'lacteos, frutos secos'),
(35, 22, 10, 'Fabada asturiana', 'Con compango asturiano y pan de maíz', 15.00, NULL, 1, 'gluten'),
(36, 22, 10, 'Cachopo de ternera', 'Relleno de jamón y queso, empanado', 18.00, NULL, 1, 'gluten, lacteos, huevo'),
(37, 23, 11, 'Mejillones al vapor', 'Con vinagreta de tomate y cebolla', 9.00, NULL, 1, 'moluscos'),
(38, 23, 11, 'Almejas a la marinera', 'Salsa de ajo, perejil y vino blanco', 14.00, NULL, 1, 'moluscos, gluten'),
(39, 23, 11, 'Gambas al ajillo', 'Con aceite picante, ajo y guindilla', 16.00, NULL, 1, 'crustaceos'),
(40, 24, 11, 'Lubina a la plancha', 'Con patatas y ensalada', 22.00, NULL, 1, 'pescado'),
(41, 24, 11, 'Rodaballo al horno', 'Con patatas panadera y salsa verde', 28.00, NULL, 1, 'pescado'),
(42, 25, 11, 'Arroz negro', 'Con calamar y alioli casero', 18.00, NULL, 1, 'moluscos, crustaceos, huevo'),
(43, 25, 11, 'Arroz de bogavante', 'Con bogavante del Cantábrico', 38.00, NULL, 1, 'crustaceos'),
(44, 26, 11, 'Tarta de queso', 'Estilo vasco, suave y cremosa', 6.00, NULL, 1, 'lacteos, gluten, huevo'),
(45, 27, 12, 'Guacamole clásico', 'Con chips de maíz artesanos', 7.50, NULL, 1, NULL),
(46, 27, 12, 'Pico de gallo', 'Tomate, cebolla, cilantro y jalapeño', 5.00, NULL, 1, NULL),
(47, 28, 12, 'Taco de carnitas', 'Cerdo confitado, cebolla morada y cilantro', 4.50, NULL, 1, 'gluten'),
(48, 28, 12, 'Taco de pescado', 'Tilapia rebozada, col y mayonesa de chipotle', 4.50, NULL, 1, 'pescado, gluten, huevo'),
(49, 28, 12, 'Taco vegano de jackfruit', 'Jackfruit guisado con salsa verde', 4.00, NULL, 1, 'gluten'),
(50, 29, 12, 'Tostada de tinga', 'Pollo adobado, frijoles y crema', 6.50, NULL, 1, 'gluten, lacteos'),
(51, 30, 12, 'Churros con chocolate', 'Churros crujientes con chocolate mexicano caliente', 5.50, NULL, 1, 'gluten'),
(52, 31, 13, 'Hummus con crudités', 'Garbanzos, tahini y aceite de oliva', 6.00, NULL, 1, 'sesamo'),
(53, 31, 13, 'Gyozas de verduras', 'Al vapor con salsa de soja y jengibre', 7.50, NULL, 1, 'gluten, soja'),
(54, 32, 13, 'Bowl de quinoa', 'Quinoa, aguacate, edamame y vinagreta de limón', 11.00, NULL, 1, 'soja'),
(55, 32, 13, 'Ensalada de remolacha', 'Con nueces, queso vegano y vinagreta de naranja', 10.00, NULL, 1, 'frutos secos'),
(56, 33, 13, 'Curry de garbanzos', 'Con leche de coco, espinacas y arroz basmati', 13.00, NULL, 1, NULL),
(57, 33, 13, 'Burger vegana', 'Albóndiga de legumbres, lechuga, tomate y cebolla', 12.00, NULL, 1, 'gluten, soja'),
(58, 34, 13, 'Tarta de zanahoria', 'Con frosting de anacardos y canela', 5.50, NULL, 1, 'frutos secos, gluten'),
(59, 35, 14, 'Edamame', 'Vainas de soja al vapor con sal', 4.50, NULL, 1, 'soja'),
(60, 35, 14, 'Gyozas de cerdo', 'Fritas o al vapor con salsa ponzu', 8.00, NULL, 1, 'gluten, soja'),
(61, 36, 14, 'Salmón nigiri (2 pzs)', 'Salmón fresco sobre arroz de sushi', 5.00, NULL, 1, 'pescado'),
(62, 36, 14, 'California roll (8 pzs)', 'Cangrejo, aguacate y pepino', 9.00, NULL, 1, 'crustaceos, soja, gluten'),
(63, 36, 14, 'Spicy tuna roll (8 pzs)', 'Atún, mayonesa picante y pepino', 10.00, NULL, 1, 'pescado, soja, gluten, huevo'),
(64, 37, 14, 'Ramen tonkotsu', 'Caldo de cerdo 12h, chashu, huevo marinado y nori', 15.00, NULL, 1, 'gluten, huevo, soja'),
(65, 37, 14, 'Ramen miso vegano', 'Caldo de miso, tofu, setas y bambú', 13.00, NULL, 1, 'soja, gluten'),
(66, 38, 14, 'Mochi de helado', 'Tres variedades: vainilla, fresa y matcha', 6.00, NULL, 1, 'lacteos'),
(67, 39, 15, 'Aros de cebolla', 'Crujientes, con mayonesa de trufa', 6.50, NULL, 1, 'gluten, huevo'),
(68, 39, 15, 'Nuggets de pollo', 'De pollo de corral, con salsa BBQ', 7.00, NULL, 1, 'gluten, huevo'),
(69, 40, 15, 'La Baturra', 'Ternasco I.G.P., queso Tronchón, cebolla y aioli', 14.00, NULL, 1, 'gluten, lacteos, huevo'),
(70, 40, 15, 'La Vegana', 'Burger de legumbres, tofu ahumado y aguacate', 12.00, NULL, 1, 'gluten, soja'),
(71, 40, 15, 'La BBQ Clásica', 'Ternera, queso cheddar, bacon y salsa BBQ', 13.00, NULL, 1, 'gluten, lacteos'),
(72, 40, 15, 'La Picante', 'Doble de ternera, jalapeños, habanero y queso azul', 15.00, NULL, 1, 'gluten, lacteos'),
(73, 41, 15, 'Patatas fritas caseras', 'Con sal Maldon y romero', 4.50, NULL, 1, NULL),
(74, 41, 15, 'Ensalada coleslaw', 'Col, zanahoria y mayonesa', 3.50, NULL, 1, 'huevo'),
(75, 42, 15, 'Brownie con helado', 'Chocolate 70%, nueces y helado de vainilla', 6.00, NULL, 1, 'gluten, frutos secos, lacteos, huevo'),
(76, 43, 16, 'Pincho de boquerón', 'En vinagre con pimiento rojo', 2.20, NULL, 1, 'pescado, gluten'),
(77, 43, 16, 'Pincho de salmón', 'Con queso crema y eneldo sobre pan de centeno', 2.80, NULL, 1, 'pescado, lacteos, gluten'),
(78, 44, 16, 'Croqueta de morcilla', 'Con pimiento del piquillo', 2.50, NULL, 1, 'gluten, huevo, lacteos'),
(79, 44, 16, 'Champiñón relleno', 'Con jamón y queso fundido', 2.80, NULL, 1, 'lacteos'),
(80, 44, 16, 'Pimiento relleno', 'Bacalao al pil-pil con salsa verde', 3.00, NULL, 1, 'pescado, gluten'),
(81, 45, 16, 'Montadito de foie', 'Foie mi-cuit con cebolla caramelizada', 4.50, NULL, 1, 'gluten'),
(82, 45, 16, 'Montadito de morcilla', 'Morcilla de Aragón con pera a la plancha', 3.50, NULL, 1, 'gluten'),
(83, 46, 16, 'Vino de la casa (copa)', 'Tinto, blanco o rosado del Somontano', 2.50, NULL, 1, NULL),
(84, 46, 16, 'Sidra natural (botella)', 'Sidra asturiana natural, 750ml', 7.00, NULL, 1, NULL),
(86, 47, 18, 'fish', '', 34.00, NULL, 1, ''),
(88, 48, 18, 'calabacin con setas', '', 22.00, NULL, 1, ''),
(89, 48, 18, 'preubaplato', 'descripcon', 3.00, NULL, 1, 'gluten'),
(90, 49, 18, 'patatas alioli', 'con alioli', 12.00, NULL, 1, 'gluten'),
(91, 50, 18, 'platoEstrella', 'ggn', 34.00, NULL, 1, ''),
(92, 51, 19, 'Pollo al chilindron', 'Pollo', 21.00, NULL, 1, ''),
(93, 52, 19, 'Alcachifas en tempura', '', 16.00, NULL, 1, ''),
(94, 53, 19, 'Carabineros', '', 43.00, NULL, 1, ''),
(95, 54, 19, 'Tarta de queso', '', 10.00, NULL, 1, '');

-- --------------------------------------------------------

--
-- Table structure for table `propinas`
--

CREATE TABLE `propinas` (
  `id` int(11) NOT NULL,
  `reserva_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `restaurante_id` int(11) NOT NULL,
  `cantidad` decimal(6,2) NOT NULL,
  `mensaje` varchar(255) DEFAULT NULL,
  `fecha` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `propinas`
--

INSERT INTO `propinas` (`id`, `reserva_id`, `usuario_id`, `restaurante_id`, `cantidad`, `mensaje`, `fecha`) VALUES
(1, 4, 11, 7, 3.00, '¡Todo perfecto!', '2026-02-14 23:00:00'),
(2, 5, 12, 8, 5.00, 'Servicio excelente', '2026-02-20 16:00:00'),
(3, 6, 13, 11, 4.00, 'El marisco era increíble', '2026-02-28 23:00:00'),
(4, 8, 15, 16, 2.00, 'Muy buen ambiente', '2026-03-10 16:00:00'),
(5, 12, 19, 15, 3.50, 'Volveremos seguro', '2026-04-01 23:30:00'),
(6, 14, 5, 7, 2.50, 'Tapas de 10', '2026-04-08 16:00:00'),
(7, 37, 15, 8, 8.00, 'La mejor comida de negocios posible', '2026-03-12 16:30:00'),
(8, 45, 7, 7, 3.00, 'Siempre genial', '2026-04-17 15:30:00');

-- --------------------------------------------------------

--
-- Table structure for table `reservas`
--

CREATE TABLE `reservas` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `restaurante_id` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `hora` time NOT NULL,
  `num_personas` int(11) NOT NULL DEFAULT 1,
  `estado` enum('pendiente','esperando_usuario','confirmada','cancelada','completada') DEFAULT 'pendiente',
  `notas` text DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  `fecha_actualizacion` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservas`
--

INSERT INTO `reservas` (`id`, `usuario_id`, `restaurante_id`, `fecha`, `hora`, `num_personas`, `estado`, `notas`, `fecha_creacion`, `fecha_actualizacion`) VALUES
(1, 7, 3, '2026-04-15', '22:00:00', 2, 'cancelada', '', '2026-04-13 20:19:14', '2026-04-16 22:30:14'),
(2, 7, 4, '2026-04-16', '22:00:00', 6, 'confirmada', 'mesa lejos de la cocina', '2026-04-13 20:44:42', '2026-04-14 17:20:05'),
(3, 10, 4, '2026-04-15', '15:00:00', 5, 'confirmada', '', '2026-04-14 17:23:40', '2026-04-14 17:24:27'),
(4, 11, 7, '2026-02-14', '20:30:00', 2, 'completada', 'San Valentín, mesa con velas', '2026-02-10 10:00:00', '2026-02-15 23:00:00'),
(5, 12, 8, '2026-02-20', '13:30:00', 4, 'completada', '', '2026-02-17 12:00:00', '2026-02-21 22:00:00'),
(6, 13, 11, '2026-02-28', '21:00:00', 3, 'completada', 'Alergía al marisco de uno', '2026-02-24 09:00:00', '2026-03-01 22:00:00'),
(7, 14, 14, '2026-03-05', '20:00:00', 2, 'completada', '', '2026-03-01 11:00:00', '2026-03-06 22:00:00'),
(8, 15, 16, '2026-03-10', '13:00:00', 5, 'completada', 'Cumpleaños, necesitamos tarta', '2026-03-06 18:00:00', '2026-03-11 22:00:00'),
(9, 16, 12, '2026-03-15', '21:00:00', 2, 'completada', '', '2026-03-12 10:00:00', '2026-03-16 22:00:00'),
(10, 17, 9, '2026-03-20', '10:30:00', 3, 'completada', 'Brunch de trabajo', '2026-03-18 08:00:00', '2026-03-21 22:00:00'),
(11, 18, 13, '2026-03-25', '14:00:00', 1, 'completada', '', '2026-03-22 09:00:00', '2026-03-26 22:00:00'),
(12, 19, 15, '2026-04-01', '21:00:00', 4, 'completada', 'Mesa cerca de la ventana', '2026-03-28 19:00:00', '2026-04-02 22:00:00'),
(13, 20, 10, '2026-04-05', '20:30:00', 6, 'completada', '', '2026-04-01 10:00:00', '2026-04-06 22:00:00'),
(14, 5, 7, '2026-04-08', '13:30:00', 2, 'completada', '', '2026-04-04 11:00:00', '2026-04-09 22:00:00'),
(15, 6, 8, '2026-04-10', '21:00:00', 3, 'completada', '', '2026-04-06 09:00:00', '2026-04-11 22:00:00'),
(16, 11, 11, '2026-03-02', '20:00:00', 2, 'cancelada', 'Imprevisto de trabajo', '2026-02-26 10:00:00', '2026-03-01 18:00:00'),
(17, 12, 14, '2026-03-08', '21:00:00', 4, 'cancelada', '', '2026-03-05 09:00:00', '2026-03-07 17:00:00'),
(18, 13, 15, '2026-03-18', '20:30:00', 2, 'cancelada', 'Enfermedad', '2026-03-14 11:00:00', '2026-03-17 20:00:00'),
(19, 11, 8, '2026-04-25', '13:30:00', 4, 'confirmada', 'Mesa exterior si hay', '2026-04-18 10:00:00', '2026-04-18 11:00:00'),
(20, 12, 7, '2026-04-26', '21:00:00', 2, 'confirmada', '', '2026-04-18 12:00:00', '2026-04-18 12:30:00'),
(21, 13, 14, '2026-04-27', '20:00:00', 3, 'confirmada', 'Celiaco en el grupo', '2026-04-17 09:00:00', '2026-04-17 10:00:00'),
(22, 14, 11, '2026-04-28', '21:30:00', 2, 'confirmada', 'Aniversario', '2026-04-16 18:00:00', '2026-04-16 19:00:00'),
(23, 15, 12, '2026-04-30', '21:00:00', 6, 'confirmada', 'Fiesta de cumpleaños', '2026-04-19 10:00:00', '2026-04-19 10:30:00'),
(24, 16, 9, '2026-05-02', '10:00:00', 2, 'confirmada', '', '2026-04-19 09:00:00', '2026-04-19 09:15:00'),
(25, 17, 16, '2026-05-03', '13:30:00', 5, 'confirmada', 'Comida de empresa', '2026-04-18 14:00:00', '2026-04-18 15:00:00'),
(26, 18, 8, '2026-05-05', '14:00:00', 3, 'pendiente', '', '2026-04-19 08:00:00', '2026-04-19 08:00:00'),
(27, 19, 13, '2026-05-06', '13:30:00', 1, 'pendiente', 'Intolerante a la soja', '2026-04-19 09:00:00', '2026-04-19 09:00:00'),
(28, 20, 15, '2026-05-07', '21:00:00', 2, 'pendiente', '', '2026-04-19 10:00:00', '2026-04-19 10:00:00'),
(29, 5, 10, '2026-05-08', '20:30:00', 4, 'pendiente', '', '2026-04-19 11:00:00', '2026-04-19 11:00:00'),
(30, 6, 14, '2026-05-09', '21:00:00', 2, 'pendiente', '', '2026-04-19 12:00:00', '2026-04-19 12:00:00'),
(31, 11, 9, '2026-01-20', '10:30:00', 2, 'completada', '', '2026-01-17 09:00:00', '2026-01-21 22:00:00'),
(32, 11, 12, '2026-01-28', '21:00:00', 3, 'completada', '', '2026-01-24 10:00:00', '2026-01-29 22:00:00'),
(33, 12, 10, '2026-02-05', '13:30:00', 5, 'completada', '', '2026-02-01 11:00:00', '2026-02-06 22:00:00'),
(34, 12, 16, '2026-02-12', '20:30:00', 2, 'completada', '', '2026-02-08 09:00:00', '2026-02-13 22:00:00'),
(35, 13, 7, '2026-02-18', '14:00:00', 4, 'completada', '', '2026-02-14 10:00:00', '2026-02-19 22:00:00'),
(36, 14, 15, '2026-03-01', '21:30:00', 2, 'completada', '', '2026-02-25 12:00:00', '2026-03-02 22:00:00'),
(37, 15, 8, '2026-03-12', '13:30:00', 6, 'completada', 'Comida de negocios', '2026-03-08 09:00:00', '2026-03-13 22:00:00'),
(38, 16, 11, '2026-03-22', '21:00:00', 4, 'completada', '', '2026-03-18 11:00:00', '2026-03-23 22:00:00'),
(39, 17, 13, '2026-04-02', '14:00:00', 2, 'completada', '', '2026-03-30 09:00:00', '2026-04-03 22:00:00'),
(40, 18, 16, '2026-04-09', '20:30:00', 3, 'completada', '', '2026-04-05 10:00:00', '2026-04-10 22:00:00'),
(41, 19, 7, '2026-04-12', '13:30:00', 2, 'completada', '', '2026-04-08 11:00:00', '2026-04-13 22:00:00'),
(42, 20, 9, '2026-04-14', '10:30:00', 4, 'completada', '', '2026-04-10 09:00:00', '2026-04-15 22:00:00'),
(43, 5, 12, '2026-04-15', '21:00:00', 2, 'completada', '', '2026-04-11 12:00:00', '2026-04-16 22:00:00'),
(44, 6, 14, '2026-04-16', '20:30:00', 3, 'completada', '', '2026-04-12 10:00:00', '2026-04-17 22:00:00'),
(45, 7, 7, '2026-04-17', '13:30:00', 2, 'completada', '', '2026-04-13 11:00:00', '2026-04-18 22:00:00'),
(46, 7, 16, '2026-03-20', '13:00:00', 4, 'completada', '', '2026-03-16 10:00:00', '2026-03-21 22:00:00'),
(47, 7, 8, '2026-02-25', '14:00:00', 2, 'completada', '', '2026-02-21 09:00:00', '2026-02-26 22:00:00'),
(48, 7, 13, '2026-01-30', '13:30:00', 1, 'completada', '', '2026-01-26 11:00:00', '2026-01-31 22:00:00'),
(49, 33, 11, '2026-05-06', '20:00:00', 2, 'pendiente', '', '2026-05-02 00:50:20', '2026-05-02 00:50:20'),
(50, 33, 11, '2026-05-19', '14:00:00', 2, 'pendiente', '', '2026-05-02 00:55:28', '2026-05-02 00:55:28'),
(51, 33, 18, '2026-04-28', '20:00:00', 2, 'cancelada', '', '2026-05-02 00:59:19', '2026-05-02 01:18:30'),
(52, 33, 18, '2026-05-29', '13:00:00', 5, 'esperando_usuario', '', '2026-05-02 01:29:18', '2026-05-02 01:30:35'),
(53, 33, 18, '2026-06-30', '14:00:00', 2, 'esperando_usuario', '', '2026-05-02 01:29:33', '2026-05-02 01:30:32'),
(54, 33, 18, '2026-05-18', '21:00:00', 2, 'cancelada', '', '2026-05-02 01:32:01', '2026-05-02 01:32:52'),
(55, 33, 18, '2026-05-26', '14:30:00', 2, 'confirmada', '', '2026-05-02 18:21:21', '2026-05-02 18:38:24'),
(56, 33, 18, '2026-04-29', '12:30:00', 6, 'cancelada', '', '2026-05-02 18:30:12', '2026-05-02 19:19:06'),
(57, 33, 18, '2026-04-27', '15:00:00', 2, 'cancelada', '', '2026-05-02 19:04:11', '2026-05-02 19:19:16'),
(58, 33, 18, '2026-05-25', '21:00:00', 2, 'esperando_usuario', '', '2026-05-02 19:14:40', '2026-05-02 19:16:00'),
(59, 33, 18, '2026-05-31', '21:30:00', 2, 'esperando_usuario', 'gluten', '2026-05-02 20:07:47', '2026-05-02 20:08:45'),
(60, 33, 18, '2026-05-21', '17:00:00', 2, 'esperando_usuario', 'espeviz', '2026-05-02 20:13:20', '2026-05-02 20:14:25'),
(61, 33, 18, '2026-05-29', '21:30:00', 4, 'cancelada', 'juan viene ', '2026-05-04 11:28:53', '2026-05-04 11:29:42'),
(62, 33, 18, '2026-05-26', '21:00:00', 2, 'esperando_usuario', 'prueba para Juacko', '2026-05-04 11:31:56', '2026-05-04 11:32:17'),
(63, 33, 18, '2026-05-26', '21:30:00', 3, 'esperando_usuario', 'Juan , Ivo y yo', '2026-05-04 11:40:33', '2026-05-04 11:40:42'),
(64, 38, 18, '2026-05-06', '14:30:00', 3, 'cancelada', 'Juacko , Ivo y Samu', '2026-05-04 18:43:55', '2026-05-06 08:19:06'),
(65, 38, 18, '2026-05-06', '14:00:00', 3, 'cancelada', 'juacko , IVO y samu', '2026-05-04 18:52:52', '2026-05-06 08:19:12'),
(66, 38, 18, '2026-05-12', '14:00:00', 2, 'confirmada', 'p1', '2026-05-04 19:06:39', '2026-05-04 19:09:55'),
(67, 38, 18, '2026-05-20', '14:30:00', 3, 'confirmada', 'p3', '2026-05-04 19:10:54', '2026-05-04 19:11:27'),
(68, 39, 19, '2026-05-09', '21:00:00', 4, 'confirmada', 'lejos de la cocina', '2026-05-06 09:15:58', '2026-05-06 09:27:05');

-- --------------------------------------------------------

--
-- Table structure for table `restaurantes`
--

CREATE TABLE `restaurantes` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `direccion` varchar(255) NOT NULL,
  `ciudad` varchar(100) DEFAULT 'Zaragoza',
  `latitud` decimal(10,8) DEFAULT NULL,
  `longitud` decimal(11,8) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `email_contacto` varchar(200) DEFAULT NULL,
  `categoria` varchar(80) DEFAULT NULL,
  `precio_medio` decimal(6,2) DEFAULT NULL,
  `aforo_total` int(11) DEFAULT 50,
  `activo` tinyint(1) DEFAULT 1,
  `solicitado` tinyint(1) NOT NULL DEFAULT 0,
  `aprobado` tinyint(1) NOT NULL DEFAULT 0,
  `aprobado_por` int(11) DEFAULT NULL,
  `fecha_registro` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `restaurantes`
--

INSERT INTO `restaurantes` (`id`, `usuario_id`, `nombre`, `descripcion`, `direccion`, `ciudad`, `latitud`, `longitud`, `telefono`, `email_contacto`, `categoria`, `precio_medio`, `aforo_total`, `activo`, `solicitado`, `aprobado`, `aprobado_por`, `fecha_registro`) VALUES
(1, 31, 'La Bodegón Aragonés', 'Cocina tradicional aragonesa con los mejores vinos de la región.', 'Calle Alfonso I, 14, Zaragoza', 'Zaragoza', 41.64880000, -0.88910000, '976 20 10 01', NULL, 'española', 28.50, 60, 1, 0, 1, 35, '2026-04-08 18:31:37'),
(2, 3, 'La Casa de Ana', 'Restaurante familiar especializado en cocina mediterránea.', 'Paseo de la Independencia, 22, Zaragoza', 'Zaragoza', 41.65120000, -0.88730000, '976 20 10 02', NULL, 'pasta', 22.00, 40, 1, 0, 1, 35, '2026-04-08 18:31:37'),
(3, 4, 'El Rincón Secreto', 'Alta cocina con producto de temporada y bodega selecta.', 'Plaza del Pilar, 3, Zaragoza', 'Zaragoza', 41.65730000, -0.87730000, '976 20 10 03', NULL, 'sushi', 45.00, 30, 1, 0, 1, 35, '2026-04-08 18:31:37'),
(4, 9, 'Chicago', 'Restaurante de buena calidad , donde serviremos comida tradicional de buena calidad', 'Almozara 18', 'Zaragoza', NULL, NULL, '678909876', 'chicago@gmail.com', 'Otras', 20.00, 35, 1, 0, 1, 35, '2026-04-13 20:43:29'),
(5, 10, 'Montessori', 'Comida de colegio de mala calidad', 'Calle Lagasca , 3', 'Zaragoza', NULL, NULL, '676789865', 'Montessori@gmail.com', 'Alta cocina', 15.00, 20, 1, 0, 1, 35, '2026-04-15 00:37:36'),
(6, 10, 'Juanko Pizza', 'Las mejores pizzas italianas', 'Maria Zayas Sotomayor', 'Zaragoza', NULL, NULL, '654786523', '', 'Italiana', 35.00, 50, 1, 0, 1, 35, '2026-04-15 10:11:30'),
(7, 21, 'Taberna del Pilar', 'Tapas y vinos aragoneses junto a la Basílica del Pilar.', 'Plaza del Pilar, 12, Zaragoza', 'Zaragoza', 41.65720000, -0.87800000, '976300107', 'info@tabernapilar.com', 'española', 18.00, 45, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(8, 22, 'Asador El Ebro', 'Carnes y verduras a la brasa a orillas del Ebro.', 'Paseo Echegaray y Caballero, 5, Zaragoza', 'Zaragoza', 41.65200000, -0.88100000, '976300208', 'info@asadorzgz.com', 'española', 38.00, 55, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(9, 23, 'Café Aragón', 'Desayunos, brunchs y cocina de fusión con producto local.', 'Calle Don Jaime I, 33, Zaragoza', 'Zaragoza', 41.65400000, -0.87950000, '976300309', 'info@cafearagon.com', 'fusión', 15.00, 35, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(10, 24, 'Sidrería Zaragozana', 'Sidra natural asturiana y pinchos de txistorra y queso.', 'Calle Predicadores, 8, Zaragoza', 'Zaragoza', 41.65600000, -0.88500000, '976300410', 'info@sidreriazgz.com', 'sidreria', 20.00, 40, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(11, 25, 'Marisquería del Ebro', 'El mejor marisco y pescado fresco del Cantábrico y Mediterráneo.', 'Avenida César Augusto, 19, Zaragoza', 'Zaragoza', 41.65000000, -0.88200000, '976300511', 'info@marisqueriaebro.com', 'mariscos', 50.00, 50, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(12, 26, 'Tacos & Mezcal', 'Auténtica cocina mexicana con tacos, tostadas y mezcales artesanos.', 'Calle San Blas, 22, Zaragoza', 'Zaragoza', 41.65300000, -0.89000000, '976300612', 'info@tacosmexzgz.com', 'mexicana', 22.00, 38, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(13, 27, 'Verde & Sano', 'Restaurante 100% vegano con ingredientes de proximidad.', 'Calle Manifestación, 14, Zaragoza', 'Zaragoza', 41.64900000, -0.88000000, '976300713', 'info@veganzgz.com', 'vegana', 19.00, 30, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(14, 28, 'Yami Sushi & Ramen', 'Cocina japonesa tradicional: sushi, ramen y gyozas.', 'Calle Coso, 55, Zaragoza', 'Zaragoza', 41.65500000, -0.88300000, '976300814', 'info@yamizgz.com', 'japonesa', 28.00, 42, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(15, 29, 'Burger Artesanal Zgz', 'Hamburguesas premium con carne de ternasco y panes de masa madre.', 'Paseo de las Damas, 7, Zaragoza', 'Zaragoza', 41.64750000, -0.87700000, '976300915', 'info@burgeriaartesanal.com', 'americana', 16.00, 48, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(16, 30, 'Bar de Pinchos Baturro', 'Pinchos y montaditos al estilo aragonés, imprescindible.', 'Calle Temple, 3, Zaragoza', 'Zaragoza', 41.65100000, -0.87600000, '976301016', 'info@pinchoszgz.com', 'kebab', 12.00, 30, 1, 0, 1, 35, '2026-01-10 10:00:00'),
(17, 36, 'Taberna del chipi', 'un sitio para comidas familiares o comidas de empresa , te sentiras como en casa', 'Avenida tenor fleta , 65', 'Zaragoza', NULL, NULL, '976343434', NULL, 'Parrilla', NULL, 50, 1, 0, 1, 35, '2026-04-30 23:16:16'),
(18, 37, 'la recamara', '.', 'calle mayor', 'Zaragoza', NULL, NULL, '976212321', 'recamara@info.com', 'Fusión', 34.00, 33, 1, 1, 1, 35, '2026-04-30 23:32:21'),
(19, 40, 'La Buganvilla', 'Lo mejor en El centro', 'Plaza Ariño, 1, Zaragoza, ES 50003', 'Zaragoza', NULL, NULL, '976656565', NULL, 'Alta cocina', NULL, 50, 1, 1, 1, 8, '2026-05-06 09:10:21');

-- --------------------------------------------------------

--
-- Table structure for table `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellidos` varchar(150) NOT NULL,
  `email` varchar(200) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `rol` enum('usuario','restaurante','admin') NOT NULL DEFAULT 'usuario',
  `foto_perfil` varchar(255) DEFAULT NULL,
  `fecha_registro` datetime DEFAULT current_timestamp(),
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `apellidos`, `email`, `password_hash`, `telefono`, `rol`, `foto_perfil`, `fecha_registro`, `activo`) VALUES
(1, 'Admin', 'Sistema', 'admin@restauranteszgz.com', '$2y$10$examplehashadmin', NULL, 'admin', NULL, '2026-04-08 18:31:37', 1),
(2, 'Carlos', 'Martínez', 'carlos@labodegon.com', '$2y$10$hash1', '976111001', 'restaurante', NULL, '2026-04-08 18:31:37', 1),
(3, 'Ana', 'López', 'ana@lacasadeana.com', '$2y$10$hash2', '976111002', 'restaurante', NULL, '2026-04-08 18:31:37', 1),
(4, 'Miguel', 'Sánchez', 'miguel@elrinconsecret.com', '$2y$10$hash3', '976111003', 'restaurante', NULL, '2026-04-08 18:31:37', 1),
(5, 'Laura', 'García', 'laura@gmail.com', '$2y$10$hash4', '612000001', 'usuario', NULL, '2026-04-08 18:31:37', 1),
(6, 'Jorge', 'Fernández', 'jorge@gmail.com', '$2y$10$hash5', '612000002', 'usuario', NULL, '2026-04-08 18:31:37', 1),
(7, 'Juan', 'Cano', 'juank@gmail.com', '$2y$10$xHkvZ.E3CWJ4QNZz/I37mOCFhvLy0oznksCqNwMGrggeuhLX7jb46', '640026831', 'usuario', NULL, '2026-04-13 19:24:50', 1),
(8, 'Admin', 'Prueba', 'admin@test.com', '$2y$12$gdgxhQJ1hPowS7GGvp11guGiVEAkKE0.WOb3dwmMG6oL1cmwvSanu', '600000001', 'admin', NULL, '2026-04-13 20:23:44', 1),
(9, 'Restaurante', 'Prueba', 'restaurante@test.com', '$2y$12$l.oSj2kY.ZovBLuUi8od/u6EIh/EOAER.2AVsxcvBPiGvL8uTTmVC', '600000002', 'restaurante', NULL, '2026-04-13 20:23:44', 1),
(10, 'Juan', 'Cano Mediel', 'juancanomediel@gmail.com', '$2y$10$mhMC3kbzjZESgKyGCdTHNe1ypztGfOStYJ4VlNd8PI91tCpGIE0Xm', '640026830', 'restaurante', NULL, '2026-04-14 17:22:52', 1),
(11, 'María', 'Jiménez Ruiz', 'maria.jimenez@gmail.com', '$2y$10$examplehash11', '611100011', 'usuario', NULL, '2026-01-05 10:00:00', 1),
(12, 'Pedro', 'Moreno Blanco', 'pedro.moreno@gmail.com', '$2y$10$examplehash12', '611100012', 'usuario', NULL, '2026-01-10 11:00:00', 1),
(13, 'Sofía', 'Navarro Pardo', 'sofia.navarro@gmail.com', '$2y$10$examplehash13', '611100013', 'usuario', NULL, '2026-01-15 12:00:00', 1),
(14, 'Alejandro', 'Torres Gil', 'alejandro.torres@gmail.com', '$2y$10$examplehash14', '611100014', 'usuario', NULL, '2026-01-20 09:00:00', 1),
(15, 'Lucía', 'Romero Castro', 'lucia.romero@gmail.com', '$2y$10$examplehash15', '611100015', 'usuario', NULL, '2026-01-25 14:00:00', 1),
(16, 'Marcos', 'Vega Ortega', 'marcos.vega@gmail.com', '$2y$10$examplehash16', '611100016', 'usuario', NULL, '2026-02-01 10:30:00', 1),
(17, 'Elena', 'Ramos Herrero', 'elena.ramos@gmail.com', '$2y$10$examplehash17', '611100017', 'usuario', NULL, '2026-02-08 08:00:00', 1),
(18, 'David', 'Molina Serrano', 'david.molina@gmail.com', '$2y$10$examplehash18', '611100018', 'usuario', NULL, '2026-02-14 19:00:00', 1),
(19, 'Carmen', 'Núñez Valdés', 'carmen.nunez@gmail.com', '$2y$10$examplehash19', '611100019', 'usuario', NULL, '2026-02-20 16:00:00', 1),
(20, 'Raúl', 'Peña Domingo', 'raul.pena@gmail.com', '$2y$10$examplehash20', '611100020', 'usuario', NULL, '2026-03-01 11:00:00', 1),
(21, 'Isabel', 'Crespo Lara', 'isabel@tabernapilar.com', '$2y$10$examplehash21', '976300021', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(22, 'Tomás', 'Aguirre Mena', 'tomas@asadorzgz.com', '$2y$10$examplehash22', '976300022', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(23, 'Beatriz', 'Salinas Ibáñez', 'beatriz@cafearagon.com', '$2y$10$examplehash23', '976300023', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(24, 'Fernando', 'Domínguez Vara', 'fernando@sidreriazgz.com', '$2y$10$examplehash24', '976300024', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(25, 'Rosa', 'Alcalde Tena', 'rosa@marisqueriaebro.com', '$2y$10$examplehash25', '976300025', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(26, 'Andrés', 'Fuentes Pla', 'andres@tacosmexzgz.com', '$2y$10$examplehash26', '976300026', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(27, 'Natalia', 'Bravo Espinosa', 'natalia@veganzgz.com', '$2y$10$examplehash27', '976300027', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(28, 'Héctor', 'Calvo Ríos', 'hector@yamizgz.com', '$2y$10$examplehash28', '976300028', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(29, 'Pilar', 'Montes Lago', 'pilar@burgeriaartesanal.com', '$2y$10$examplehash29', '976300029', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(30, 'Emilio', 'Cano Vergara', 'emilio@pinchoszgz.com', '$2y$10$examplehash30', '976300030', 'restaurante', NULL, '2026-01-03 09:00:00', 1),
(31, 'Juan', 'del Campo Munoz', 'juaniko@gmail.com', '$2y$10$kee5tWQ8aea9I9wEaKexJuuAH5iuJJX9SHuhKr6YEmv5hJUyrXJdK', '675432319', 'restaurante', NULL, '2026-04-20 21:10:41', 1),
(32, 'Pepe', 'Lopez', 'lopez@pepe.com', '$2y$10$KPEvAwPPSdASBGbt0.Bf9.PjOTlMV4CxCV0WEBleyna9ICe8ialL2', '65734523', 'usuario', NULL, '2026-04-29 16:25:21', 1),
(33, 'Pepe', 'Lopez', 'pepe@lopez.com', '$2y$10$lQktwIVI6c/kox9stNK/MOJ3ruUuhRr0kvRWegdhe7bl2/ZXD.dxy', '645738290', 'usuario', NULL, '2026-04-29 16:36:51', 1),
(34, 'prueba', 'prueba3', 'prueba@gmail.com', '$2y$10$jbeeIjLXZOtAKDdaR9xvKemt4Z6DFJ39TjxIzrjQdsagcwE9a9iq6', '32312121', 'usuario', NULL, '2026-04-30 21:56:40', 1),
(35, 'samuadmin', '', 'admin@reats.com', '$2y$10$JunpgTeAHAXPwb0S7ThaSuIIiCTzPf9vvX1CAhPs/THJWsX39m.2e', '', 'admin', NULL, '2026-04-30 22:58:51', 1),
(36, 'Pepe', 'Gil', 'gil@pepe.com', '$2y$10$ERC46Dw5OOnVWoPk8gMQt.vvvkcM7keaZN0SToLOq2/v0cUEt1Iyu', '345656565', 'restaurante', NULL, '2026-04-30 23:16:16', 1),
(37, 'lucas', 'l', 'l@l.com', '$2y$10$zGfHy2EpXIHyn.PkCfodVeqXM/XHKrnqmRj/aqyl2ENvQMGzB7Bn6', '', 'restaurante', NULL, '2026-04-30 23:32:21', 1),
(38, 'Samu', 'prueba', '25.dam.samuelmartinez@colegiomontessori.com', '$2y$10$69oqT3TlKSpxwzHpuO3NO.9MF/dmyEGU3P8iFRKqYzABviZKTE6W2', '232323232', 'usuario', NULL, '2026-05-04 18:41:25', 1),
(39, 'Juako', 'cano', '23.daw.juancano@fundacionmontessori.com', '$2y$10$UrX12nO.GUCqpANwDOnZNeE5ZKWyQkYhLHMkt1lzq5YsPHBt97.Du', '640026875', 'usuario', NULL, '2026-05-06 09:05:27', 1),
(40, 'Pablo', 'Lazaro', 'pablo@lazaro.com', '$2y$10$lniS48zs2H8c5uF5XKAVUe/EXF0jk6uWdjFia4bUNSPw/BVRNkwPG', '123455445', 'restaurante', NULL, '2026-05-06 09:10:21', 1);

-- --------------------------------------------------------

--
-- Table structure for table `valoraciones`
--

CREATE TABLE `valoraciones` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `restaurante_id` int(11) NOT NULL,
  `reserva_id` int(11) DEFAULT NULL,
  `puntuacion` tinyint(4) NOT NULL CHECK (`puntuacion` between 1 and 5),
  `comentario` text DEFAULT NULL,
  `fecha` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `valoraciones`
--

INSERT INTO `valoraciones` (`id`, `usuario_id`, `restaurante_id`, `reserva_id`, `puntuacion`, `comentario`, `fecha`) VALUES
(1, 7, 1, NULL, 3, 'Genial', '2026-04-16 22:29:03'),
(2, 7, 2, NULL, 4, 'Excelente trato del camarero, la comida normalilla', '2026-04-14 17:18:42'),
(3, 11, 7, 4, 5, 'Ambiente inmejorable y las migas están para chuparse los dedos.', '2026-02-15 12:00:00'),
(4, 12, 8, 5, 4, 'El chuletón de buey es una experiencia única. Un poco caro pero vale la pena.', '2026-02-21 13:00:00'),
(5, 13, 11, 6, 5, 'El mejor marisco que he comido en Zaragoza. El arroz de bogavante, espectacular.', '2026-03-01 14:00:00'),
(6, 14, 14, 7, 4, 'El ramen tonkotsu muy bueno aunque el caldo podría ser un poco más concentrado.', '2026-03-06 13:00:00'),
(7, 15, 16, 8, 5, 'Pinchos increíbles, el montadito de foie se va en un segundo. Imprescindible.', '2026-03-11 14:00:00'),
(8, 16, 12, 9, 4, 'Tacos muy ricos, el de carnitas el mejor. El mezcal también excelente.', '2026-03-16 13:00:00'),
(9, 17, 9, 10, 3, 'Desayuno correcto, nada especial. El café estaba algo flojo.', '2026-03-21 12:00:00'),
(10, 18, 13, 11, 5, 'Sorprendente para ser vegano. El curry de garbanzos y la burger vegana, tops.', '2026-03-26 13:00:00'),
(11, 19, 15, 12, 4, 'Buenas hamburguesas artesanales. La Baturra con el queso Tronchón, increíble.', '2026-04-02 14:00:00'),
(12, 20, 10, 13, 4, 'Sidra muy bien tirada y el cachopo grandísimo. Repetiremos.', '2026-04-06 13:00:00'),
(13, 5, 7, 14, 5, 'Las tapas aragonesas son fantásticas. El servicio muy atento y rápido.', '2026-04-09 12:00:00'),
(14, 6, 8, 15, 5, 'El cordero al sarmiento, una delicia. Sitio ideal para celebraciones.', '2026-04-11 13:00:00'),
(15, 11, 9, 31, 4, 'Brunch muy completo y bien presentado. Buenos precios.', '2026-01-21 13:00:00'),
(16, 11, 12, 32, 5, 'El guacamole hecho en el momento y los tacos de jackfruit para repetir.', '2026-01-29 14:00:00'),
(17, 12, 10, 33, 3, 'Ambiente muy animado pero el servicio tardó bastante. La fabada correcta.', '2026-02-06 13:00:00'),
(18, 12, 16, 34, 4, 'Buena calidad-precio en los pinchos. Me gustó el de salmón especialmente.', '2026-02-13 12:00:00'),
(19, 13, 7, 35, 5, 'El ternasco al horno deshacía en la boca. Vino de Somontano muy bueno.', '2026-02-19 13:00:00'),
(20, 14, 15, 36, 4, 'La Picante es para los valientes, muy bien ejecutada. Los aros de cebolla top.', '2026-03-02 14:00:00'),
(21, 15, 8, 37, 5, 'Sin duda el mejor asador de Zaragoza. El secreto ibérico a la brasa, perfecto.', '2026-03-13 13:00:00'),
(22, 16, 11, 38, 5, 'Las gambas al ajillo y la lubina a la plancha, frescura total. Lo recomiendo.', '2026-03-23 14:00:00'),
(23, 17, 13, 39, 4, 'Propuesta vegana muy cuidada. Me encantó el bowl de quinoa.', '2026-04-03 13:00:00'),
(24, 18, 16, 40, 3, 'Pinchos bien pero el servicio un poco desorganizado esa noche.', '2026-04-10 12:00:00'),
(25, 19, 7, 41, 5, 'Perfecto para una comida rápida y rica. Las bravas caseras, las mejores.', '2026-04-13 13:00:00'),
(26, 20, 9, 42, 4, 'Brunch del domingo muy agradable, buena música y ambiente relajado.', '2026-04-15 14:00:00'),
(27, 5, 12, 43, 4, 'Tacos de pescado muy originales. El guacamole imprescindible.', '2026-04-16 13:00:00'),
(28, 6, 14, 44, 5, 'La mejor cocina japonesa que he probado en Zaragoza. El ramen tonkotsu, brutal.', '2026-04-17 14:00:00'),
(29, 7, 7, 45, 4, 'Muy buena taberna con sabor aragonés de verdad.', '2026-04-18 13:00:00'),
(30, 7, 16, 46, 5, 'Los pinchos calientes son una maravilla. No te pierdas el champiñón relleno.', '2026-03-21 14:00:00'),
(31, 7, 8, 47, 5, 'Las verduras a la brasa con las carnes hacen un menú perfecto.', '2026-02-26 13:00:00'),
(32, 7, 13, 48, 4, 'Muy bien para comer sano sin renunciar al sabor. Las gyozas, sorprendentes.', '2026-01-31 14:00:00');

-- --------------------------------------------------------

--
-- Stand-in structure for view `vista_restaurantes`
-- (See below for the actual view)
--
CREATE TABLE `vista_restaurantes` (
`id` int(11)
,`nombre` varchar(150)
,`descripcion` text
,`direccion` varchar(255)
,`latitud` decimal(10,8)
,`longitud` decimal(11,8)
,`telefono` varchar(20)
,`email_contacto` varchar(200)
,`categoria` varchar(80)
,`precio_medio` decimal(6,2)
,`aforo_total` int(11)
,`activo` tinyint(1)
,`valoracion_media` decimal(5,1)
,`num_valoraciones` bigint(21)
,`reservas_hoy` bigint(21)
,`plazas_disponibles_hoy` bigint(22)
);

-- --------------------------------------------------------

--
-- Structure for view `vista_restaurantes`
--
DROP TABLE IF EXISTS `vista_restaurantes`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vista_restaurantes`  AS SELECT `r`.`id` AS `id`, `r`.`nombre` AS `nombre`, `r`.`descripcion` AS `descripcion`, `r`.`direccion` AS `direccion`, `r`.`latitud` AS `latitud`, `r`.`longitud` AS `longitud`, `r`.`telefono` AS `telefono`, `r`.`email_contacto` AS `email_contacto`, `r`.`categoria` AS `categoria`, `r`.`precio_medio` AS `precio_medio`, `r`.`aforo_total` AS `aforo_total`, `r`.`activo` AS `activo`, coalesce(round(avg(`v`.`puntuacion`),1),0) AS `valoracion_media`, count(distinct `v`.`id`) AS `num_valoraciones`, (select count(0) from `reservas` `res` where `res`.`restaurante_id` = `r`.`id` and `res`.`fecha` = curdate() and `res`.`estado` in ('pendiente','confirmada')) AS `reservas_hoy`, `r`.`aforo_total`- (select count(0) from `reservas` `res2` where `res2`.`restaurante_id` = `r`.`id` and `res2`.`fecha` = curdate() and `res2`.`estado` in ('pendiente','confirmada')) AS `plazas_disponibles_hoy` FROM (`restaurantes` `r` left join `valoraciones` `v` on(`v`.`restaurante_id` = `r`.`id`)) WHERE `r`.`activo` = 1 GROUP BY `r`.`id` ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `fotos_restaurante`
--
ALTER TABLE `fotos_restaurante`
  ADD PRIMARY KEY (`id`),
  ADD KEY `restaurante_id` (`restaurante_id`);

--
-- Indexes for table `horarios`
--
ALTER TABLE `horarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `restaurante_id` (`restaurante_id`);

--
-- Indexes for table `menu_categorias`
--
ALTER TABLE `menu_categorias`
  ADD PRIMARY KEY (`id`),
  ADD KEY `restaurante_id` (`restaurante_id`);

--
-- Indexes for table `menu_platos`
--
ALTER TABLE `menu_platos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `categoria_id` (`categoria_id`),
  ADD KEY `restaurante_id` (`restaurante_id`);

--
-- Indexes for table `propinas`
--
ALTER TABLE `propinas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reserva_id` (`reserva_id`),
  ADD KEY `usuario_id` (`usuario_id`),
  ADD KEY `restaurante_id` (`restaurante_id`);

--
-- Indexes for table `reservas`
--
ALTER TABLE `reservas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`),
  ADD KEY `restaurante_id` (`restaurante_id`);

--
-- Indexes for table `restaurantes`
--
ALTER TABLE `restaurantes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`),
  ADD KEY `fk_aprobado_por` (`aprobado_por`);

--
-- Indexes for table `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `valoraciones`
--
ALTER TABLE `valoraciones`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_usuario_restaurante` (`usuario_id`,`restaurante_id`),
  ADD KEY `restaurante_id` (`restaurante_id`),
  ADD KEY `reserva_id` (`reserva_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `fotos_restaurante`
--
ALTER TABLE `fotos_restaurante`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `horarios`
--
ALTER TABLE `horarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=127;

--
-- AUTO_INCREMENT for table `menu_categorias`
--
ALTER TABLE `menu_categorias`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=55;

--
-- AUTO_INCREMENT for table `menu_platos`
--
ALTER TABLE `menu_platos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=96;

--
-- AUTO_INCREMENT for table `propinas`
--
ALTER TABLE `propinas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `reservas`
--
ALTER TABLE `reservas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;

--
-- AUTO_INCREMENT for table `restaurantes`
--
ALTER TABLE `restaurantes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT for table `valoraciones`
--
ALTER TABLE `valoraciones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `fotos_restaurante`
--
ALTER TABLE `fotos_restaurante`
  ADD CONSTRAINT `fotos_restaurante_ibfk_1` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `horarios`
--
ALTER TABLE `horarios`
  ADD CONSTRAINT `horarios_ibfk_1` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `menu_categorias`
--
ALTER TABLE `menu_categorias`
  ADD CONSTRAINT `menu_categorias_ibfk_1` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `menu_platos`
--
ALTER TABLE `menu_platos`
  ADD CONSTRAINT `menu_platos_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `menu_categorias` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `menu_platos_ibfk_2` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `propinas`
--
ALTER TABLE `propinas`
  ADD CONSTRAINT `propinas_ibfk_1` FOREIGN KEY (`reserva_id`) REFERENCES `reservas` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `propinas_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `propinas_ibfk_3` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `reservas`
--
ALTER TABLE `reservas`
  ADD CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reservas_ibfk_2` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `restaurantes`
--
ALTER TABLE `restaurantes`
  ADD CONSTRAINT `fk_aprobado_por` FOREIGN KEY (`aprobado_por`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `restaurantes_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `valoraciones`
--
ALTER TABLE `valoraciones`
  ADD CONSTRAINT `valoraciones_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `valoraciones_ibfk_2` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `valoraciones_ibfk_3` FOREIGN KEY (`reserva_id`) REFERENCES `reservas` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
