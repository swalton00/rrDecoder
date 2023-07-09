//CREATE USER rrdec password 'rrdecpw';
//CREATE schema rrdec;
//GRANT ALTER ANY schema TO rrdec;
CREATE TABLE
    rrdec.roster
    (
        id      INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT primaryKey PRIMARY KEY,
        entered     TIMESTAMP DEFAULT CURRENT TIMESTAMP NOT NULL ,
        fullpath    VARCHAR(256) DEFAULT '' NOT NULL ,
        dateUpdated TIMESTAMP DEFAULT CURRENT TIMESTAMP NOT NULL ,
        systemName  VARCHAR(64) DEFAULT '' NOT NULL
    );
CREATE UNIQUE INDEX
    rrdec.roster_IDX1
ON
    rrdec.roster
    (
        systemName,
        fullpath
    );
CREATE TABLE
    rrdec.decoderType
    (
        id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT decoderType_primaryKey
        PRIMARY KEY,
        decoderFamily VARCHAR(256),
        decoderModel  VARCHAR(256),
        dateUpdated   TIMESTAMP DEFAULT CURRENT TIMESTAMP NOT NULL
    );
CREATE UNIQUE INDEX
    rrdec.Decoder_IDX1
ON
    rrdec.decoderType
    (
        decoderFamily,
        decoderModel
    );
CREATE TABLE
    rrdec.decoder
    (
        id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT decoder_primaryKey PRIMARY
        KEY,
        decoderID      VARCHAR(256) CONSTRAINT decoderID_null NOT NULL,
        fileName       VARCHAR(256) CONSTRAINT decoder_fileName NOT NULL,
        roadNumber     VARCHAR(256),
        roadName       VARCHAR(256),
        manufacturer   VARCHAR(256),
        owner          VARCHAR(256),
        model          VARCHAR(256),
        dccaddress     VARCHAR(256),
        manufacturerID VARCHAR(256),
        productid      VARCHAR(256),
        dateUpdated    TIMESTAMP,
        decoderTypeID  INTEGER NOT NULL CONSTRAINT loco_decoder REFERENCES rrdec.decoderType(id)
        ON
DELETE
    RESTRICT,
    rosterID INTEGER NOT NULL CONSTRAINT loco_roster REFERENCES rrdec.roster(id)
ON
DELETE
    CASCADE
    );
CREATE TABLE
    rrdec.functionLabels
    (
        id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT func_labl_primaryKey
        PRIMARY KEY,
        decoderID INTEGER NOT NULL CONSTRAINT Func_Labl_decoder REFERENCES rrdec.decoder(id) ON
DELETE
    CASCADE,
    functionNumber VARCHAR(16) NOT NULL,
    functionLabel  VARCHAR(255) NOT NULL
    );
CREATE TABLE
    rrdec.SpeedProfile
    (
        id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT speed_profile_primaryKey
        PRIMARY KEY,
        decoderID INTEGER NOT NULL CONSTRAINT speed_profile_decoder REFERENCES rrdec.decoder(id)
        ON
DELETE
    CASCADE,
    step INTEGER,
    forward DOUBLE,
    reverse DOUBLE
    );
CREATE TABLE
    rrdec.KeyValues
    (
        id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT key_value_primaryKey
        PRIMARY KEY,
        decoderID INTEGER NOT NULL CONSTRAINT key_value_decoder REFERENCES rrdec.decoder(id) ON
DELETE
    CASCADE,
    Pair_Key   VARCHAR(255),
    Pair_value VARCHAR(255)
    );
CREATE TABLE
    rrdec.DecoderDef
    (
        id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT dec_def_primaryKey PRIMARY 
        KEY,
        decoderID INTEGER NOT NULL CONSTRAINT dec_def_decoder REFERENCES rrdec.decoder(id) ON
DELETE
    CASCADE,
    varValue VARCHAR(255),
    item     VARCHAR(255)
    );
CREATE TABLE
    rrdec.CVValues
    (
        id   INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT cv_primaryKey PRIMARY KEY,
        decoderID INTEGER NOT NULL CONSTRAINT cv_decoder REFERENCES rrdec.decoder(id) ON
DELETE
    CASCADE,
    cvValue VARCHAR(255),
    value   VARCHAR(255) 
    );