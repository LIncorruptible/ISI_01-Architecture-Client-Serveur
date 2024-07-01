# Gestion de Stock et Facturation - Système Client/Serveur

⚠️ _Ce projet a été réalisé dans le cadre du mini-projet de développement client/serveur pour le Master 1 MIAGE en 2024. Il a pour objectif pédagogique de mettre en pratique les connaissances acquises en développement logiciel, en gestion de projet, et en architecture client-serveur._ ⚠️

🚧 **Version 1** - Ensemble restreint de fonctionnalités implémentées.

## Description du projet 📁

### Sujet de projet

L'objectif est de développer un système informatique pour la gestion du stock de marchandises d'un vendeur et la préparation des factures des ventes effectuées. Le système doit être architecturé selon le modèle client-serveur et utiliser Java RMI comme middleware et MySQL comme SGBD.

### Fonctionnalités principales

- **Gestion du stock** : Suivi des articles avec leur référence, famille, prix unitaire, et quantité en stock.
- **Facturation** : Gestion des factures des clients avec détails des articles achetés, mode de paiement, et date de facturation.
- **Consultation et recherche d'articles** : Consultation du stock et recherche par famille d'articles.
- **Achat et paiement** : Enregistrement des achats et paiement des factures.
- **Calcul du chiffre d'affaires** : Calcul du chiffre d'affaires à une date donnée.
- **Mise à jour des prix** : Mise à jour des prix tous les matins par le serveur central.
- **Sauvegarde des factures** : Sauvegarde des factures tous les soirs sur le serveur central.

## Architecture du système

Le système est composé de :
- **Serveur central** : Situé au siège de l'entreprise, gère les données de stock et de facturation.
- **Serveurs des magasins** : Un serveur par magasin pour traiter les requêtes locales.
- **Postes clients (caisses)** : Interfaces utilisateur dans les magasins pour réaliser les opérations prévues.

### Fonctionnement général

1. Saisie de l'utilisateur sur le client (caisse).
2. Envoi de la requête au serveur local.
3. Traitement de la requête par le serveur.
4. Envoi du résultat au client.
5. Réception et affichage du résultat sur le client.

## Installation 📥

### Prérequis 🚨

Avant de commencer, assurez-vous d'avoir installé les éléments suivants :
- [Docker Desktop](https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe?utm_source=docker&utm_medium=webreferral&utm_campaign=dd-smartbutton&utm_location=module)
- [Java Development Kit (JDK) 22](https://www.oracle.com/java/technologies/javase-jdk22-downloads.html)
- [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/)
- [Git](https://git-scm.com/downloads)

### Instructions d'installation 📋

1. **Cloner le projet depuis GitHub**

    ```bash
    git clone https://github.com/your-repository.git
    cd your-repository
    ```
    ou bien télécharger le zip du repository et extrayez le ou bon vous semble sur votre machine locale.

2. **Configurer Docker Desktop**

    - Ouvrez Docker Desktop et connectez-vous ou créez un compte Docker si nécessaire.
    - Assurez-vous que Docker est en cours d'exécution.

3. **Ouvrir le projet dans IntelliJ IDEA**

    - Lancez IntelliJ IDEA Community Edition.
    - Ouvrez le projet cloné (`File > Open` et sélectionnez le dossier du projet).

### Lancement du projet 📤

1. **Démarrer les conteneurs Docker**

    Dans un terminal, placez-vous à la racine du dossier `Heptathlon` et exécutez :

    ```bash
    docker-compose up -d
    ```

2. **Compiler les projets**

    Dans IntelliJ IDEA, compilez les trois projets, en les ouvrants au préalable dans 3 fenêtres Intellij :
    - Sélectionnez le projet `Siege` et compilez (`Build > Build Project`).
    - Sélectionnez le projet `Serveur` et compilez (`Build > Build Project`).
    - Sélectionnez le projet `Client` et compilez (`Build > Build Project`).

3. **Exécuter les classes Main**

    - **Exécuter la classe Main du projet Siege**
      - Dans IntelliJ IDEA, naviguez jusqu'à la classe `Main` du projet `Siege`.
      - Faites un clic droit et sélectionnez `Run 'Main'`.

    - **Exécuter la classe Main du projet Serveur**
      - Répétez l'opération pour la classe `Main` du projet `Serveur`.

    - **Exécuter la classe Main du projet Client**
      - Répétez l'opération pour la classe `Main` du projet `Client`.

### Arrêt du projet 🚫

Pour arrêter le projet, utilisez la commande **CTRL + C** dans le terminal où Docker est en cours d'exécution, puis arrêtez les conteneurs Docker actifs en utilisant Docker Desktop. Et stoppez l'exécution des différents projets ouverts.

## Utilisation 🌐

Une fois le projet lancé :
- **Consulter le stock** : La table principale vous donne les articles et leur stock. Le volet de gauche les regroupent par famille.
- **Rechercher un article** : Saisissez la famille d'articles pour obtenir les références disponibles.
- **Acheter un article** : Enregistrez un achat en saisissant les détails nécessaires : la quantité et les articles sélectionnés.
- **Payer une facture** : Procédez au paiement en sélectionnant un mode de paiement.
- **Consulter une facture** : Affichez les détails d'une facture, disponible dans un fichier `.txt` enregistrer sur votre bureau dans un dossier `factures`
- **Calculer le chiffre d'affaires** : Obtenez le chiffre d'affaires pour une plage de dates données.

---

_Ce Readme a été rédigé par Maël RHUIN & Reda ES SALHI._
