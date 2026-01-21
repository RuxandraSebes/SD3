# Smart Energy Management Platform

Un proiect bazat pe o arhitectura **microservicii**, care gestioneaza **utilizatorii, dispozitivele si autentificarea** intr-un sistem distribuit.
Proiectul foloseste:

* **Spring Boot** pentru microservicii
* **PostgreSQL** pentru baze de date separate
* **Traefik** ca reverse proxy si load balancer
* **React** pentru interfata frontend
* **Docker Compose** pentru orchestrarea completa

---

## Microservicii

### Auth Service

* Gestionarea utilizatorilor si autentificarea prin JWT.
* Endpointuri:

  * `POST /auth/login`
  * `POST /auth/register`
  * `GET /auth/validate`

---

### People Service

* Gestioneaza entitatile **Person**: id, nume, varsta, adresa.
* Endpointuri:

  * `GET /people`
  * `POST /people`
  * `PUT /people/{id}`
  * `DELETE /people/{id}`

---

### Devices Service

* Gestioneaza **Device**: id, nume, locatie, consum maxim.
* Contine relatia many-to-many `UserDevice (idRelatie, idUser, idDevice)`
* Permite atribuirea unuia sau mai multor utilizatori unui dispozitiv.
* Endpointuri:

  * `GET /devices`
  * `POST /devices`
  * `PUT /devices/{id}`
  * `DELETE /devices/{id}`

---

### Frontend (React)

* Interfata pentru administrarea **People** si **Devices**
* Dropdown multi-select pentru asocierea userilor la un device
* Ruteaza API-urile din microservicii prin Traefik
* Rulat pe portul `3000`

---

## Docker si Traefik

Arhitectura foloseste Traefik ca reverse proxy.
Toate serviciile sunt definite in `docker-compose.yml` si comunicate prin reteaua `demo_net`.

---

## Cum se ruleaza

### 1. Build + run containere

```bash
docker-compose up --build
```

### 2. Asteapta pana cand toate serviciile pornesc:

* `auth-service` → port 8081
* `people-service` → port 8082
* `devices-service` → port 8083
* `frontend` → port 3000

Traefik va rula pe portul `80` si va redirectiona automat.

---

## Comenzi utile

Creare imagini:

```bash
docker build -t backend-image-people .
docker build -t backend-image-devices .
docker build -t backend-image-auth .
```

Oprire containere:

```bash
docker-compose down
```

Stergere completa + rebuild:

```bash
docker-compose down
docker-compose up --build
```

## Ce mi s-a parut interesant/greu:

A fost prima data cand am lucrat cu Traefik si mi s-a parut complicat. 
Chit ca am mai lucrat cu Docker pana acum, a fost prima data cand am avut asa multe containere si am avut cateva probleme la configurarea docker-compose.yml si Dockerfiles.
