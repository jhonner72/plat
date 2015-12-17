CREATE TABLE IF NOT EXISTS JOB(
  JOB_ID NVARCHAR(41) NOT NULL PRIMARY KEY,
  JOB_OBJECT NVARCHAR(65536),
  MODIFIED_DATE DATETIME DEFAULT(CURRENT_DATE())
);

CREATE UNIQUE INDEX IF NOT EXISTS IDX_JOB_ID ON JOB (JOB_ID);