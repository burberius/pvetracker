#!/bin/bash

wget -q https://www.fuzzwork.co.uk/dump/latest/invTypes.sql.bz2
wget -q https://www.fuzzwork.co.uk/dump/latest/trnTranslations.sql.bz2

bunzip2 invTypes.sql.bz2
bunzip2 trnTranslations.sql.bz2

./mysql2sqlite invTypes.sql | sqlite3 test.db
./mysql2sqlite trnTranslations.sql | sqlite3 test.db

echo "CREATE TABLE type_translation (id SERIAL,type_id int NOT NULL, language varchar(2) DEFAULT NULL, name varchar(150) DEFAULT NULL, PRIMARY KEY (id));" | sqlite3 test.db

echo "INSERT INTO type_translation (type_id, language, name) SELECT i.typeID, t.languageID, t.text FROM invTypes i JOIN trnTranslations t ON i.typeID = t.keyID WHERE i.published = 1 AND t.languageID IN ('de' , 'en', 'fr', 'ja', 'ru') AND t.tcID = 8;" | sqlite3 test.db

echo "DROP TABLE invTypes; DROP TABLE trnTranslations;" | sqlite3 test.db

sqlite3 test.db .dump | sed -e '/^INSERT/!d' > type_translation.sql

rm invTypes.sql trnTranslations.sql test.db
