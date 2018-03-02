DROP TABLE MOUVEMENT;
DROP TABLE PRODUIT;

CREATE TABLE PRODUIT(
    nProduit INT NOT NULL generated ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(20),
    stockMin INT,
    stockMax INT,
	CONSTRAINT UK_PRODUIT UNIQUE (nom)
);

CREATE TABLE MOUVEMENT(
    nMouvement INT NOT NULL generated ALWAYS AS IDENTITY PRIMARY KEY,
    nProduit INT NOT NULL,
    quantite INT NOT NULL,
    dateMouvement DATE NOT NULL,
    CONSTRAINT FK_MOUVEMENT_PRODUIT FOREIGN KEY (nProduit) REFERENCES PRODUIT(nProduit)
);


INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('T-shirt Bleu', 15, 155);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('T-shirt Rouge', 25, 75);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('T-shirt Vert', 5, 100);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Jeans Court', 10, 90);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Chemise Pois', 7, 80);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Chemise Rose', 12, 110);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Chemise Marron', 5, 30);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Doudoune Bleu', 7, 42);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Doudoune Noir', 15, 75);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Doudoune Rose', 6, 55);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Blouson Noir', 8, 55);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Blouson Court', 12, 45);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Blouson Rouge', 10, 48);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Manteau', 8, 25);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Cinture Noir', 15, 45);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Cinture Marron', 8, 35);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Cinture Orange', 15, 50);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Cravatte Orange', 8, 28);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Cravatte Pois', 22, 38);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Cravatte Noir', 16, 55);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Cravatte Bleu', 20, 100);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Papillon Bleu', 15, 115);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Papillon Rouge', 25, 110);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Papillon Vert', 15, 75);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Papillon Marron', 10, 28);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Papillon Rose', 7, 65);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Papillon Gris', 15, 60);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Gants Simple', 15, 40);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Gants Noir', 25, 140);
INSERT INTO PRODUIT (nom, stockMin, stockMax)
	VALUES('Gants Bleu', 10, 80);