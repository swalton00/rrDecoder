
ALTER TABLE cvValues ALTER COLUMN cvNumber SET NOT NULL ;

ALTER TABLE cvValues ADD CONSTRAINT cvVal_uniq UNIQUE (decoderId, CvNumber);

;

CREATE TABLE cv_versions
(
    decoderId INT          NOT NULL,
    cvNumber  VARCHAR(255) NOT NULL,
    cvVersion INT          NOT NULL,
    changeTIme TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    CONSTRAINT cv_ver_uni UNIQUE (decoderId, cvNumber, cvVersion),
    CONSTRAINT cv_ver_pri_key PRIMARY KEY (decoderId, cvNumber, cvVersion),
    CONSTRAINT cv_ver_for_cvNumber FOREIGN KEY (decoderId, cvNumber) REFERENCES cvValues (decoderId, cvNumber) ON DELETE CASCADE
);

ALTER TABLE DECODER ADD COLUMN changeVersion INT NOT NULL DEFAULT 0;

CREATE TABLE savedCVvals
(
    decoderId INT   NOT NULL ,
    cvNumber  VARCHAR(255) NOT NULL ,
    version   INT   NOT NULL,
    savedVal  VARCHAR(255) ,
    CONSTRAINT svd_val_uni UNIQUE (decoderId, cvNumber, version),
    CONSTRAINT svd_val_for_parent FOREIGN KEY (decoderId, cvNumber) REFERENCES CVVALUES (decoderId, cvNumber)
);

ALTER TABLE roster ALTER COLUMN dateupdated
    DROP DEFAULT;

ALTER TABLE roster ALTER COLUMN dateupdated
    DROP NOT NULL;

ALTER TABLE roster ALTER COLUMN dateupdated
    DROP DEFAULT;

ALTER TABLE roster ALTER COLUMN dateupdated
    DROP NOT NULL;

ALTER TABLE
    FunctionLabels ADD column Label_Version INTEGER;

ALTER TABLE
    functionlabels ADD column locked BOOLEAN;
ALTER TABLE
    functionlabels ADD column visible BOOLEAN;
ALTER TABLE
    functionlabels ADD column shunt BOOLEAN;

ALTER TABLE
    functionlabels ADD CONSTRAINT lab_unique UNIQUE (decoderid, functionnumber);

CREATE TABLE
    label_versions
(
    decoderid      INTEGER NOT NULL,
    functionnumber CHARACTER VARYING(16),
    version        INTEGER,
    version_time   TIMESTAMP NOT NULL,
    CONSTRAINT label_ver_primary PRIMARY KEY (decoderid, functionnumber, version),
    CONSTRAINT label_ver_foreign FOREIGN KEY (decoderid, functionnumber) REFERENCES
        functionlabels (decoderid, functionnumber) ON
        DELETE
        CASCADE
);

CREATE TABLE
    saved_Labels
(
    decoderid      INTEGER NOT NULL,
    functionnumber CHARACTER VARYING(16) NOT NULL,
    version_number INTEGER NOT NULL,
    saved_label    CHARACTER VARYING(255),
    visible        BOOLEAN,
    locked         BOOLEAN,
    shunt          BOOLEAN,
    CONSTRAINT svd_labs_primary PRIMARY KEY (decoderid, functionnumber, version_number),
    CONSTRAINT svd_labs_foreign FOREIGN KEY (decoderid, functionnumber) REFERENCES
        functionlabels (decoderid, functionnumber) ON
        DELETE
        CASCADE
);

ALTER TABLE
    keyvalues ADD column key_version CHARACTER VARYING(25);
ALTER TABLE
    keyvalues ADD CONSTRAINT key_unique UNIQUE (decoderid, pair_key);

CREATE TABLE
    saved_keys
(
    decoderId      INTEGER NOT NULL,
    pair_key       CHARACTER VARYING(255) NOT NULL,
    version_number INTEGER NOT NULL,
    CONSTRAINT saved_keys_unique UNIQUE (decoderid, pair_key, version_number),
    CONSTRAINT saved_keys_primary PRIMARY KEY (decoderid, pair_key, version_number),
    CONSTRAINT saved_keys_foreign FOREIGN KEY (decoderId, pair_key) REFERENCES keyvalues
        (decoderid, pair_key) ON
        DELETE
        CASCADE
);

CREATE TABLE
    key_versions
(
    decoderid    INTEGER NOT NULL,
    pair_key     CHARACTER VARYING(255),
    version      INTEGER NOT NULL,
    version_time TIMESTAMP NOT NULL,
    CONSTRAINT key_ver_unique UNIQUE (decoderid, pair_key, version),
    CONSTRAINT key_ver_primary PRIMARY KEY (decoderid, pair_key, version),
    CONSTRAINT key_ver_foreign FOREIGN KEY (decoderid, pair_key) REFERENCES functionlabels
        (decoderid, pair_key) ON
        DELETE
        CASCADE
);

UPDATE DB_VERSION set  MINOR = 3 WHERE id = 1;