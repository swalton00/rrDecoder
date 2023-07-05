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
    rrdec.locomotive
    (
        locoid,
        filename,
        roadnumber,
        roadname,
        manufacturer,
        owner,
        dccaddress,
        productid,
        decoderId,
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
    rrdec.decoder
ORDER BY
    family,
    model;
INSERT INTO
    rrdec.decoder
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
    rrdec.decoder
WHERE
    id = 1;
SELECT
    id,
    decoderFamily,
    decoderModel,
    dateUpdated AS updated
FROM
    rrdec.decoder
WHERE
    decoderFamily = 'ESU LokSound 5'
AND decoderModel = 'LokSound 5';
UPDATE
    rrdec.decoder
SET
    decoderFamily = 'ESU Lok',
    decoderModel = 'Lok',
    dateUpdated = CURRENT TIMESTAMP
WHERE
    id = 1;
INSERT INTO
    rrdec.locomotive
    (
        locoid,
        filename,
        roadnumber,
        roadname,
        owner,
        model,
        dccaddress,
        manufacturerid,
        decoderid,
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
    rrdec.locomotive
SET
    locoid = '123',
    filename = '123',
    roadnumber = '123',
    roadname = '123',
    manufacturer = 'ScaleTrains',
    owner = 'jsw',
    dccaddress = '123',
    decoderid = 76,
    dateUpdated = CURRENT TIMESTAMP
WHERE
    id = 1;
DELETE
FROM
    rrdec.locomotive
WHERE
    id = 1;
SELECT
    id,
    locoid,
    filename,
    roadnumber,
    roadname,
    owner,
    model,
    dccaddress,
    manufacturerid,
    decoderid,
    rosterid,
    dateUpdated
FROM
    rrdec.locomotive
WHERE
    rosterid = 15
ORDER BY 
    id;
    