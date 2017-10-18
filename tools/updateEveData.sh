#!/bin/bash

PASSWORD="pve"
MP="-u pve -p$PASSWORD pve"

wget -q https://www.fuzzwork.co.uk/dump/latest/invTypes.sql.bz2
wget -q https://www.fuzzwork.co.uk/dump/latest/trnTranslations.sql.bz2

bunzip2 invTypes.sql.bz2
bunzip2 trnTranslations.sql.bz2

mysql $MP < invTypes.sql
mysql $MP < trnTranslations.sql

echo "TRUNCATE TABLE type_translation;" | mysql $MP

echo "INSERT INTO type_translation (type_id, language_id, text) SELECT i.typeID, t.languageID, t.text FROM invTypes i JOIN trnTranslations t ON i.typeID = t.keyID WHERE i.published = 1 AND t.languageID IN ('de' , 'en', 'fr', 'ja', 'ru') AND t.tcID = 8;" | mysql $MP

echo "DROP TABLE invTypes; DROP TABLE trnTranslations;" | mysql $MP

mysqldump $MP -t --compact type_translation > type_translation.sql

rm invTypes.sql trnTranslations.sql
