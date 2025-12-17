# 🏙️ Plateforme de Gestion d'Incidents - Ville Intelligente

Application web de gestion des incidents urbains développée avec **Spring Boot** et **Thymeleaf**.

## 📋 Table des matières
- [Description](#description)
- [Technologies utilisées](#technologies-utilisées)
- [Fonctionnalités principales](#fonctionnalités-principales)
- [Architecture de sécurité](#architecture-de-sécurité)
- [Installation et configuration](#installation-et-configuration)
- [Structure du projet](#structure-du-projet)
- [Auteur](#auteur)

---

## 📖 Description

Cette application permet aux citoyens de signaler des incidents urbains (nids-de-poule, lampadaires défectueux, déchets, etc.) et aux services municipaux de les gérer efficacement.

### Rôles utilisateurs :
- **👤 Citoyen** : Déclarer et suivre ses incidents
- **🔧 Agent** : Traiter les incidents assignés
- **👔 Admin** : Gérer son département et assigner les incidents aux agents
- **⚡ Super Admin** : Gestion globale (utilisateurs, départements, statistiques)

---

## 🛠️ Technologies utilisées

### Backend
- **Spring Boot 3.x**
- **Spring Security** (authentification, autorisation)
- **Spring Data JPA** (accès aux données)
- **Hibernate** (ORM)
- **Spring Mail** (notifications email)
- **Spring AOP** (journalisation des tentatives suspectes)

### Frontend
- **Thymeleaf** (moteur de templates)
- **HTML5 / CSS3**
- **JavaScript**
- **Chart.js** (graphiques statistiques)
- **Leaflet.js** (cartes interactives)

### Base de données
- **MySQL** (production)

### Outils
- **Maven** (gestion des dépendances)
- **Lombok** (réduction du code boilerplate)
- **Git** (versionnement)

---

## ⚙️ Fonctionnalités principales

### 🚨 Déclaration d'incidents
- Formulaire intuitif avec géolocalisation GPS (Leaflet)
- Upload de photos (validation taille max 5MB, formats JPG/PNG)
- Catégorisation (Infrastructure, Propreté, Sécurité, Éclairage, Transport, Environnement)
- Sélection du quartier avec code postal

### 📊 Tableaux de bord personnalisés par rôle

#### Super Admin
- Vue globale de tous les incidents
- Statistiques avec graphiques (Chart.js) :
  - Répartition par statut (diagramme circulaire)
  - Répartition par catégorie (graphique en barres)
  - Top 5 quartiers (graphique horizontal)
- Délai moyen de résolution
- Gestion des utilisateurs et départements

#### Admin
- Vue département uniquement
- Liste des agents du département
- Liste des incidents du département
- Assignation des incidents aux agents avec définition de priorité
- Statistiques  par département

#### Agent
- Liste des incidents assignés uniquement
- Gestion du statut des incidents (workflow complet)
- Statistiques personnelles
- Informations sur le département d'affectation

#### Citoyen
- Historique de ses incidents déclarés
- Suivi du statut en temps réel
- Formulaire de feedback pour incidents résolus
- Statistiques personnelles

### 🔄 Workflow complet des incidents
```
SIGNALE → PRIS_EN_CHARGE → EN_RESOLUTION → RESOLU → CLOTURE
```

**Transitions :**
- Admin assigne → PRIS_EN_CHARGE
- Agent commence → EN_RESOLUTION
- Agent termine → RESOLU
- Citoyen valide → CLOTURE (avec feedback)

### 📧 Notifications automatiques par email
- Email à l'agent lors de l'assignation d'un incident
- Email au citoyen lors de la prise en charge
- Email au citoyen lors de la résolution (avec demande de feedback)

### 🔍 Recherche et filtrage avancés
- Filtrage multi-critères :
  - Par statut (Signalé, Pris en charge, En résolution, Résolu, Clôturé)
  - Par catégorie
  - Par quartier
  - Par date de déclaration
- **Pagination** : 10, 20 ou 50 résultats par page
- **Tri dynamique** : Clic sur colonnes (Titre, Date, Priorité)


### 📥 Export de données
- **Export CSV** : Tous les incidents avec détails complets
- **Export PDF** : Rapport avec id , titre et statuts incidents

### 💬 Système de feedback
- Le citoyen peut donner son avis après résolution
- Clôture automatique de l'incident après feedback
- Historique des feedbacks conservé

### 📍 Géolocalisation
- Carte interactive Leaflet (OpenStreetMap)
- Clic sur carte pour positionner l'incident
- Bouton "Utiliser ma position actuelle" (GPS du navigateur)

---

## 🔒 Architecture de sécurité

### 1. **Protection CSRF (Cross-Site Request Forgery)**
- Tokens CSRF automatiques sur tous les formulaires
- Validation côté serveur par Spring Security
- Protection contre les attaques cross-site

**Comment ça fonctionne :**
```html
<form method="post">
    <input type="hidden" name="_csrf" value="token-unique"/>
</form>
```

### 2. **Validation des uploads de fichiers**
- **Taille maximale** : 5 MB (configuré dans `application.properties`)
- **Formats autorisés** : JPG, JPEG, PNG uniquement
- **Validation en 3 couches** :
  1. HTML (`accept` attribute) → UX
  2. Spring (`multipart.max-file-size`) → Limite serveur
  3. Controller (vérification MIME type) → Sécurité métier

**Protection contre :**
- Saturation du serveur (fichiers trop gros)
- Injection de malware (fichiers exécutables)

### 3. **Rate Limiting (Limitation de requêtes)**
- **Limite** : 100 requêtes par minute par adresse IP
- **Implémentation** : Interceptor Spring MVC personnalisé
 

**Protection contre :**
- Attaques par force brute (tentatives de login)
- Déni de service (DOS)
- Abus de ressources

**Technologie :** `HandlerInterceptor` + `ConcurrentHashMap`

### 4. **Contrôle d'accès basé sur les rôles (@PreAuthorize)**
- Séparation stricte des privilèges par rôle
- Annotations `@PreAuthorize` sur chaque endpoint

 

### 5. **Journalisation des tentatives suspectes (Spring AOP)**
- Aspect `@AfterThrowing` sur tous les controllers
- Log automatique des `AccessDeniedException`
- Informations tracées : IP, URL, utilisateur, méthode

**Exemple de log :**
```
[WARN] TENTATIVE D'ACCÈS NON AUTORISÉ : 
IP=192.168.1.50, URL=/admin/dashboard, 
User=citoyen@mail.com, Method=afficherDashboard
```

### 6. **Authentification **
- Mots de passe hachés avec **BCrypt**  
- Vérification d'email obligatoire
 
---

## 🚀 Installation et configuration

### Prérequis
- **Java 17+**
- **Maven 3.8+**
- **MySQL 8.0+**
- **Compte Gmail** (pour l'envoi d'emails)

### Étapes d'installation

#### 1. Cloner le projet
```bash
git clone https://github.com/filali6/gestion-incidents.git
cd gestion-incidents
```

#### 2. Configurer la base de données

Créez une base de données MySQL :
```sql
CREATE DATABASE gestion_incidents_db;
```

Modifiez `src/main/resources/application.properties` :
```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/gestion_incidents
spring.datasource.username=votre_username
spring.datasource.password=votre_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuration email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-application
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Upload de fichiers
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

**Note :** Pour Gmail, utilisez un [mot de passe d'application](https://support.google.com/accounts/answer/185833).

#### 3. Compiler et lancer
```bash
mvn clean install
mvn spring-boot:run
```

#### 4. Accéder à l'application
Ouvrez votre navigateur : **http://localhost:8080**

#### 5. Créer le Super Admin initial
Au premier lancement, créez manuellement le Super Admin dans la base :
```sql
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, actif, email_verifie, date_inscription) VALUES ('Super', 'Admin', 'superadmin@example.com', '$2a$10$wC7tfvNkXUnjzWtu83tPHuhp/VQusWXYJtaSZ65QB1Y2wI76CAkdW', 'ROLE_SUPER_ADMIN', true, true, NOW());
```

---

 ## 📂 Structure du projet
```
GESTION-INCIDENTS/
├── .vscode/                          # Configuration VS Code
├── src/
│   ├── main/
│   │   ├── java/com/ville/intelligente/gestionincidents/
│   │   │   ├── config/               #  Configuration Spring
│   │   │   │   ├── FileUploadConfig.java        # Config upload fichiers
│   │   │   │   ├── MailConfig.java              # Config email
│   │   │   │   ├── PasswordConfig.java          # Config encodeur mot de passe
│   │   │   │   ├── RateLimitInterceptor.java    # Limitation de requêtes (100/min)
│   │   │   │   ├── SecurityAuditAspect.java     # Journalisation AOP
│   │   │   │   ├── SecurityConfig.java          # Configuration Spring Security
│   │   │   │   ├── TestPasswordEncoder.java     # Test BCrypt
│   │   │   │   └── WebConfig.java               # Configuration MVC + Interceptors
│   │   │   │
│   │   │   ├── controller/           # Controllers Spring MVC
│   │   │   │   ├── AdminController.java         # Dashboard + assignation incidents
│   │   │   │   ├── AgentController.java         # Gestion incidents assignés + statuts
│   │   │   │   ├── AuthController.java          # Login, register, logout
│   │   │   │   ├── CitoyenController.java       # Dashboard citoyen
│   │   │   │   ├── DashboardController.java     # Redirection selon rôle
│   │   │   │   ├── ExportController.java        # Export CSV/PDF
│   │   │   │   ├── IncidentController.java      # Déclaration + détail + feedback
│   │   │   │   ├── IncidentListController.java  # Liste + filtres + pagination + tri
│   │   │   │   ├── ProfileController.java       # Gestion profil utilisateur
│   │   │   │   └── SuperAdminController.java    # Gestion utilisateurs + départements
│   │   │   │
│   │   │   ├── dao/                  #  Repositories Spring Data JPA
│   │   │   │   ├── CategorieIncidentDAO.java    # CRUD catégories
│   │   │   │   ├── IncidentDAO.java             # Requêtes incidents (filtres, stats)
│   │   │   │   ├── PhotoDAO.java                # Gestion photos
│   │   │   │   └── QuartierDAO.java             # CRUD quartiers
│   │   │   │
│   │   │   ├── dto/                  #  Data Transfer Objects
│   │   │   │   ├── CreateAdminAgentRequest.java # Création admin/agent
│   │   │   │   ├── CreateDepartementRequest.java# Création département
│   │   │   │   ├── RegisterRequest.java         # Inscription citoyen
│   │   │   │   └── UpdateProfileRequest.java    # Mise à jour profil
│   │   │   │
│   │   │   ├── metier/               #  Services métier  
│   │   │   │   ├── IncidentService.java         # Interface
│   │   │   │   ├── IncidentServiceImpl.java     # Logique incidents + workflow
│   │   │   │   ├── StatistiqueService.java      # Interface
│   │   │   │   └── StatistiqueServiceImpl.java  # Calcul statistiques + graphiques
│   │   │   │
│   │   │   ├── model/                #  Entités JPA
│   │   │   │   ├── enums/
│   │   │   │   │   ├── NotificationType.java    # Types notifications
│   │   │   │   │   ├── Role.java                # SUPER_ADMIN, ADMIN, AGENT, CITIZEN
│   │   │   │   │   └── StatutIncident.java      # Workflow (5 statuts)
│   │   │   │   ├── CategorieIncident.java       # Département (Infrastructure, etc.)
│   │   │   │   ├── Incident.java                # Incident principal
│   │   │   │   ├── Notification.java            # Notifications utilisateurs
│   │   │   │   ├── Photo.java                   # Photos incidents
│   │   │   │   ├── Quartier.java                # Quartiers ville
│   │   │   │   ├── Rapport.java                 # Rapports agents
│   │   │   │   └── Utilisateur.java             # Utilisateurs système
│   │   │   │
│   │   │   ├── repository/           
│   │   │   │   └── UtilisateurRepository.java   # Requêtes utilisateurs custom
│   │   │   │
│   │   │   ├── security/              
│   │   │   │   ├── CustomUserDetails.java              # Détails utilisateur Spring
│   │   │   │   ├── CustomUserDetailsService.java       # Chargement utilisateur DB
│   │   │   │   └── RoleBasedAuthenticationSuccessHandler.java  # Redirection post-login
│   │   │   │
│   │   │   ├── service/               
│   │   │   │   ├── DepartementService.java      # Gestion départements
│   │   │   │   ├── EmailService.java            # Envoi emails (assignation, résolution)
│   │   │   │   └── UtilisateurService.java      # CRUD utilisateurs + validation
│   │   │   │
│   │   │   ├── util/                  
│   │   │   │   └── EncryptionUtil.java          # Chiffrement AES (présent mais non activé)
│   │   │   │
│   │   │   └── GestionIncidentsVilleIntelligenteApplication.java  Boot
│   │   │
│   │   └── resources/
│   │       ├── static/              
│   │       │   └── uploads/          # Photos incidents uploadées
│   │       │
│   │       ├── templates/            #  Vues Thymeleaf
│   │       │   ├── super-admin/
│   │       │   │   ├── create-departement.html  # Formulaire département
│   │       │   │   ├── create-user.html         # Formulaire admin/agent
│   │       │   │   ├── departements.html        # Liste départements
│   │       │   │   └── utilisateurs.html        # Liste utilisateurs + filtres
│   │       │   ├── dashboard.html               # Dashboard multi-rôles unifié
│   │       │   ├── incident-detail.html         # Détail incident + actions
│   │       │   ├── incident-form.html           # Déclaration avec géolocalisation
│   │       │   ├── incident-list.html           # Liste + filtres + pagination
│   │       │   ├── login.html                   # Connexion
│   │       │   └── register.html                # Inscription citoyen
│   │       │
│   │       └── application.properties           #  Configuration Spring Boot
│   │
│   └── test/                         
│       └── java/com/ville/intelligente/gestionincidents/
│
├── target/                          
├── .gitignore                        # Fichiers ignorés par Git
├── mvnw / mvnw.cmd                   # Maven Wrapper
├── pom.xml                           # Configuration Maven + dépendances
└── README.md                         # Documentation (ce fichier)

 
 
---
 

 

## 🎓 Auteur

**[Chaima Filali & Mariem Ltifi]**  
Étudiantes en 3ème année INLOG  
Institut Supérieur des Arts et Multimédia à Manouba  

**Projet du module :** Développement Web Avancé  
**Année universitaire :** 2025-2026

📧 Email 1: chaimafilali6@gmail.com 
📧 Email 2: ltifimariem98@gmail.com
 

---

## 📄 Licence

Ce projet est développé dans un cadre académique à l'ISAMM.  
© 2025 - Tous droits réservés

---

 

 

**✨ Merci d'avoir consulté ce projet ! ✨**
