drop table if exists DECODERDEF;
drop table if exists LABEL_VERSIONS;
drop table if exists SAVED_LABELS;
drop table if exists SAVED_KEYS;
drop table if exists SAVEDCVVALS;
drop table if exists KEY_VERSIONS;
drop table if exists CV_VERSIONS;
alter table FUNCTIONLABELS drop column if exists label_version;
alter table KEYVALUES drop column if exists KEY_VERSION;
create table if not exists CV_VERSIONS (
        DECODERID       integer not null,
        VERSION_NUMBER  integer not null,
        CREATED_ON      timestamp not null,
        constraint cv_version_pri primary key (DECODERID, VERSION_NUMBER),
        constraint cv_version_ref1 foreign key(DECODERID) REFERENCES DECODER(ID) 
        ON DELETE restrict ON UPDATE restrict);
create table if not exists CV_DIFF (
        DECODERID       integer not null,
        VERSION_NUMBER  integer not null,
        CVNUMBER        VARCHAR(255) not null,
        OLD_VALUE       VARCHAR(255),
        NEW_VALUE       VARCHAR(255),
        constraint cv_diff_pri PRIMARY KEY (DECODERID, CVNUMBER, VERSION_NUMBER),
        constraint cv_diff_ref1 FOREIGN KEY (DECODERID, VERSION_NUMBER) references
                CV_VERSIONS(DECODERID, VERSION_NUMBER)
             on delete restrict on update restrict);
create table if not exists LABEL_VERSIONS (
        DECODERID       integer not null,
        VERSION_NUMBER  integer not null,
        CREATED_ON      timestamp not null,
        constraint label_version_pri primary key (DECODERID, VERSION_NUMBER),
        constraint label_version_ref1 FOREIGN KEY (DECODERID) references
                DECODER(ID) on delete restrict on update restrict);
create table if not exists LABEL_DIFF (
        DECODERID       integer not null,
        VERSION_NUMBER  integer not null,
        FUNCTION_NUMBER VARCHAR(16) not null,
        OLD_VALUE       VARCHAR(255),
        NEW_VALUE       VARCHAR(255),
        OLD_LOCKED     boolean,
        NEW_LOCKED      boolean,
        constraint label_diff_pri PRIMARY KEY (DECODERID, VERSION_NUMBER, FUNCTION_NUMBER),
        constraint label_diff_ref1 FOREIGN KEY (DECODERID, VERSION_NUMBER)
                references LABEL_VERSIONS(DECODERID, VERSION_NUMBER)  
                on delete restrict on update restrict);   
create table if not exists KEYVALUES_VERSIONS (
        DECODERID       integer not null,
        VERSION_NUMBER  integer not null,
        CREATED_ON      timestamp not null,
        constraint keyvalues_versions_pri PRIMARY KEY (DECODERID, VERSION_NUMBER),
        constraint keyvalues_versions_ref1 FOREIGN KEY (DECODERID) references DECODER(ID)
                on delete restrict on update restrict);
create table if not exists KEYVALUES_DIFF (
        DECODERID       integer not null,
        VERSION_NUMBER  integer not null,
        PAIR_KEY        VARCHAR(255) not null,
        OLD_VALUE       VARCHAR(255),
        NEW_VALUE       VARCHAR(255),
        constraint keyvalues_diff_pri PRIMARY KEY (DECODERID, VERSION_NUMBER, PAIR_KEY),
        constraint keyvalues_diff_ref1 FOREIGN KEY (DECODERID, VERSION_NUMBER) 
                references KEYVALUES_VERSIONS(DECODERID, VERSION_NUMBER)
                on delete restrict on update restrict);
        
