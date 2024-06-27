USE `heptathlon`;

DROP TABLE IF EXISTS `famille`;
CREATE TABLE `famille` (
  `id_famille` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_famille`)
);

DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
  `id_stock` int NOT NULL AUTO_INCREMENT,
  `quantite` int DEFAULT NULL,
  PRIMARY KEY (`id_stock`)
);

DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `reference` int NOT NULL AUTO_INCREMENT,
  `prix` float DEFAULT NULL,
  `id_stock` int NOT NULL,
  `id_famille` int NOT NULL,
  PRIMARY KEY (`reference`),
  UNIQUE KEY `id_stock` (`id_stock`),
  KEY `id_famille` (`id_famille`),
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`id_stock`) REFERENCES `stock` (`id_stock`),
  CONSTRAINT `article_ibfk_2` FOREIGN KEY (`id_famille`) REFERENCES `famille` (`id_famille`)
);

DROP TABLE IF EXISTS `caisse`;
CREATE TABLE `caisse` (
  `id_caisse` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_caisse`)
);

DROP TABLE IF EXISTS `facture`;
CREATE TABLE `facture` (
  `id_facture` int NOT NULL AUTO_INCREMENT,
  `date_facturation` datetime DEFAULT NULL,
  `mode_paiement` varchar(50) DEFAULT NULL,
  `chemin_fichier` varchar(250) DEFAULT NULL,
  `prix_totale_TTC` double DEFAULT NULL,
  `id_caisse` int NOT NULL,
  PRIMARY KEY (`id_facture`),
  KEY `id_caisse` (`id_caisse`),
  CONSTRAINT `facture_ibfk_1` FOREIGN KEY (`id_caisse`) REFERENCES `caisse` (`id_caisse`)
);

DROP TABLE IF EXISTS `facturer`;
CREATE TABLE `facturer` (
  `reference` int NOT NULL,
  `id_facture` int NOT NULL,
  `quantite` int DEFAULT NULL,
  PRIMARY KEY (`reference`,`id_facture`),
  KEY `id_facture` (`id_facture`),
  CONSTRAINT `facturer_ibfk_1` FOREIGN KEY (`reference`) REFERENCES `article` (`reference`),
  CONSTRAINT `facturer_ibfk_2` FOREIGN KEY (`id_facture`) REFERENCES `facture` (`id_facture`)
);

USE `siege`;

DROP TABLE IF EXISTS `facture`;
CREATE TABLE `facture` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `id_caisse` int DEFAULT NULL,
  `id_facture` int DEFAULT NULL,
  `date_facturation` datetime DEFAULT NULL,
  `prix_totale_TTC` double DEFAULT NULL,
  `path` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`ID`)
)