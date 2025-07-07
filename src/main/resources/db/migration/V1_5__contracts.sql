CREATE TABLE contracts (
                       contract_id int NOT NULL,
                       type_id int NOT NULL,
                       price numeric NOT NULL,
                       date_expired timestamp NOT NULL,
                       PRIMARY KEY (contract_id)
);

CREATE INDEX index_contracts_type ON contracts(type_id);
