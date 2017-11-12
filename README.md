[![Build Status](http://jenkins.cyno.space/jenkins/buildStatus/icon?job=GitHub pvetracker)](http://jenkins.cyno.space/jenkins/job/GitHub%20pvetracker/)
[![SonarQube Coverage](https://img.shields.io/sonar/http/sonar.cyno.space/net.troja.eve:pvetracker/coverage.svg)](http://sonar.cyno.space/dashboard?id=net.troja.eve%3Apvetracker)
[![SonarQube Tech Debt](https://img.shields.io/sonar/http/sonar.cyno.space/net.troja.eve:pvetracker/tech_debt.svg?style=plastic)](http://sonar.cyno.space/dashboard?id=net.troja.eve%3Apvetracker)

# PvE Tracker
EVE Online Tracker for PvE activities - to log all your PvE activities and get an overview about the money you earn per hour, spawn probability of faction rat, every loot value per site...

## Test system and communication
There is a public test system where you can have a look at the latest stage of the development: http://pve.cyno.space/

If you have any questions or a problem, please join discord here: https://discord.gg/kFNUgwE

## Features
* Login via Eve SSO
* Track current system and ship (from ESI)
* Track
  * Name/Type of site
  * Duration of the site
  * Loot
  * If the site escalated
  * If a faction rat spawned
* Reports about
  * Money earned per hour (overall, last month...)
  * Spawn probability of faction rat
  * Average loot value (overall, per type)
  * Highes loot value (overall, per type)

## Usage
You need a mysql database as storage, create a database and user. The tables
will be created automatically.

Create your own application credentials at https://developers.eveonline.com/applications

Then checkout and build the project with
´´´Shell
mvn clean package
´´´

Create a file **application.yml** with the following content:
```
security:
  oauth2:
    client:
      clientId: overwrite-me
      clientSecret: overwrite-me
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pve?verifyServerCertificate=false&useSSL=false&requireSSL=false&characterEncoding=UTF-8
    username: pve
    password: pve
```
Change the clientId, ClientSecret and database settings accordingly.

Start it with
```Shell
java -jar target/pvetoday.jar
```

Open in your browser the url: http://localhost:8080

## Dependency
 * [autocomplete.js](https://github.com/autocompletejs/autocomplete.js)
  
