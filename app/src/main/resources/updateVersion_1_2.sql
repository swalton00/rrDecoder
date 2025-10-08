SET SCHEMA TEST;
DROP TABLE IF EXISTS cv_versions;
;
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
    DROP DEFAULT ON NULL;

ALTER TABLE roster ALTER COLUMN dateupdated
    DROP NOT NULL;

UPDATE DB_VERSION set  MINOR = 3 WHERE id = 1;