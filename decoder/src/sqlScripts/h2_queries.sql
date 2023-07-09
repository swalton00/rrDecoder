SELECT
    id,
    entered AS entryTime,
    fullpath,
    dateupdated,
    systemname
FROM
    rrdec.roster
ORDER BY
    systemname;
INSERT INTO
    rrdec.roster
    (
        entered,
        fullpath,
        systemName
    )
    VALUES
    (
        CURRENT TIMESTAMP,
        'test',
        'test1'
    );
INSERT INTO
    rrdec.decoderType
    (
        decoderfamily,
        decodermodel,
        dateupdated
    )
    VALUES
    (
        'ESU LokSound 5',
        'LokSound 5',
        CURRENT TIMESTAMP
    );
SELECT
    id,
    decoderFamily,
    decoderModel,
    dateUpdated AS updated
FROM
    rrdec.decoderType
ORDER BY
    decoderfamily,
    decodermodel;
UPDATE
    rrdec.roster
SET
    fullpath='test2',
    systemName = 'system',
    dateUpdated = CURRENT TIMESTAMP
WHERE
    id = 102;
SELECT
    id,
    entered AS entryTime,
    fullpath,
    dateupdated,
    systemname
FROM
    rrdec.roster
WHERE
    id = 102
ORDER BY
    systemname;
SELECT
    id,
    entered AS entryTime,
    fullpath,
    dateupdated,
    systemname
FROM
    rrdec.roster
WHERE
    systemName = 'Mike'
AND fullPath = 'this';
INSERT INTO
    rrdec.decoder
    (
        decoderId,
        filename,
        roadnumber,
        roadname,
        manufacturer,
        owner,
        dccaddress,
        productid,
        decoderTypeId,
        rosterid
    )
    VALUES
    (
        '123',
        '123.xml',
        '123',
        'Pennsy',
        '',
        'jsw',
        '123',
        '',
        1,
        1
    );
SELECT
    id,
    decoderFamily,
    decoderModel,
    dateUpdated AS updated
FROM
    rrdec.decoderType
WHERE
    id = 1;
SELECT
    id,
    decoderFamily,
    decoderModel,
    dateUpdated AS updated
FROM
    rrdec.decoderType
WHERE
    decoderFamily = 'ESU LokSound 5'
AND decoderModel = 'LokSound 5';
UPDATE
    rrdec.decoderType
SET
    decoderFamily = 'ESU Lok',
    decoderModel = 'Lok',
    dateUpdated = CURRENT TIMESTAMP
WHERE
    id = 1;
INSERT INTO
    rrdec.decoder
    (
        decoderId,
        filename,
        roadnumber,
        roadname,
        owner,
        model,
        dccaddress,
        manufacturerid,
        decoderTypeId,
        rosterid
    )
    VALUES
    (
        '2200',
        '2200.xml',
        '2200',
        'Pennsylvania',
        'jsw',
        'GP-30',
        '2200',
        'ScaleTrains',
        1,
        1
    );
UPDATE
    rrdec.decoder
SET
    decoderid = '123',
    filename = '123',
    roadnumber = '123',
    roadname = '123',
    manufacturer = 'ScaleTrains',
    owner = 'jsw',
    dccaddress = '123',
    decoderTypeid = 76,
    dateUpdated = CURRENT TIMESTAMP
WHERE
    id = 1;
DELETE
FROM
    rrdec.decoder
WHERE
    id = 1;
SELECT
    id,
    decoderid,
    filename,
    roadnumber,
    roadname,
    owner,
    model,
    dccaddress,
    manufacturerid,
    decodertypeid,
    rosterid,
    dateUpdated
FROM
    rrdec.decoder
WHERE
    rosterid = 15
ORDER BY
    id;
INSERT INTO
    rrdec.FunctionLabels
    (
        decoderID,
        FunctionNumber,
        FunctionLabel
    )
    VALUES
    (
        180,
        '0',
        'Headlight'
    );
INSERT    INTO
    rrdec.KeyValues
    (
        decoderId,
        Pair_key,
        Pair_value
    )
    VALUES
    (
        139,
        'DispatcherTrainType',
        'LOCAL PASSENGER'
    );
INSERT INTO
    rrdec.speedProfile
    (
        decoderId,
        STEP,
        forward,
        reverse
    )
    VALUES
    (
        139,
        159,
        52.735554,
        53.890057
    );
INSERT INTO
    rrdec.decoderDef
    (
        decoderID,
        varValue,
        item
    )
    VALUES
    (
        139,
        'Vstart',
        '15'
    );
INSERT INTO
    rrdec.CvValues
    ( 
        decoderId,
        cvvalue,
        value 
    )
    VALUES 
    (
        139,
        '1',
        '15'
    );
    