drop table if exists DECODERDEF;
drop table if exists LABEL_VERSIONS;
drop table if exists SAVED_LABELS;
drop table if exists SAVED_KEYS;
drop table if exists SAVEDCVVALS;
drop table if exists KEY_VERSIONS;
drop table if exists CV_VERSIONS;
alter table FUNCTIONLABELS drop column if exists label_version;
alter table KEYVALUES drop column if exists KEY_VERSION;
