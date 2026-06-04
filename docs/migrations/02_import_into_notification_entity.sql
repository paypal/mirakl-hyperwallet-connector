-- =============================================================================
-- IMPORT: CSV  →  NOTIFICATION_ENTITY
-- =============================================================================
-- Run this script against the NEW H2 database (the one that has the
-- NOTIFICATION_ENTITY table but no NOTIFICATION_INFO_ENTITY table).
--
-- Prerequisites:
--   1. Stop the connector application before running this script so that
--      Hibernate does not interfere with the sequence or the table.
--   2. Copy the CSV produced by 01_export_notification_info_entity.sql to a
--      path accessible from the machine that hosts the NEW database file.
--      Update the path in the CSVREAD call below accordingly.
--
-- H2 Shell (command line):
--   java -cp h2-<version>.jar org.h2.tools.Shell \
--        -url "jdbc:h2:file:/path/to/new/hmc.data" \
--        -user sa -password password \
--        -sql "RUNSCRIPT FROM '/path/to/02_import_into_notification_entity.sql'"
--
-- H2 Console (browser UI):
--   Connect to the new database, open the SQL editor, and paste this script.
--
-- Column mapping:
--   NOTIFICATION_INFO_ENTITY          NOTIFICATION_ENTITY
--   ─────────────────────────────     ──────────────────────────────────────
--   NOTIFICATION_TOKEN             →  WEB_HOOK_TOKEN
--   TARGET                         →  OBJECT_TOKEN
--   TARGET prefix (USR/PMT/…)      →  NOTIFICATION_TYPE  (derived below)
--   PROGRAM                        →  PROGRAM
--   RETRY_COUNTER                  →  RETRY_COUNTER
--   CREATION_DATE                  →  CREATION_DATE
--   CREATION_DATE                  →  RECEPTION_DATE  (best available approx.)
--   (fixed)  'FAILED'              →  STATUS
--   (auto)   sequence              →  ID
-- =============================================================================

-- ── Step 1: load the CSV into a temporary table ──────────────────────────────

CREATE TEMP TABLE NOTIFICATION_INFO_IMPORT (
    NOTIFICATION_TOKEN VARCHAR(255),
    TARGET             VARCHAR(255),
    PROGRAM            VARCHAR(255),
    RETRY_COUNTER      INT,
    CREATION_DATE      TIMESTAMP
) AS
SELECT * FROM CSVREAD('/tmp/notification_info_export.csv');

-- ── Step 2: dry-run check ─────────────────────────────────────────────────────
-- Shows which tokens would be inserted and which already exist in the target
-- table. Review this result set before running Step 3.

SELECT
    i.NOTIFICATION_TOKEN,
    i.TARGET,
    i.PROGRAM,
    i.RETRY_COUNTER,
    i.CREATION_DATE,
    CASE
        WHEN e.WEB_HOOK_TOKEN IS NOT NULL THEN 'SKIP  (already exists)'
        ELSE                                   'INSERT'
    END AS ACTION
FROM NOTIFICATION_INFO_IMPORT i
LEFT JOIN NOTIFICATION_ENTITY e ON e.WEB_HOOK_TOKEN = i.NOTIFICATION_TOKEN
ORDER BY i.CREATION_DATE ASC;

-- ── Step 3: insert — skip tokens that already exist ──────────────────────────
--
-- NOTIFICATION_TYPE is derived from the prefix of the object token (TARGET)
-- before the first '-' character.  Values that do not match a known prefix
-- (USR, PMT, STK, TRM) are stored as 'UNK'.
--
-- The ID is obtained from the same sequence Hibernate uses so that the
-- auto-increment does not collide with future inserts made by the application.
-- Hibernate 6 (Spring Boot 3) names the sequence  NOTIFICATION_ENTITY_SEQ.

INSERT INTO NOTIFICATION_ENTITY (
    ID,
    WEB_HOOK_TOKEN,
    OBJECT_TOKEN,
    NOTIFICATION_TYPE,
    PROGRAM,
    RETRY_COUNTER,
    CREATION_DATE,
    RECEPTION_DATE,
    LAST_RETRY_DATE,
    NEXT_RETRY_DATE,
    STATUS
)
SELECT
    NEXT VALUE FOR NOTIFICATION_ENTITY_SEQ,
    i.NOTIFICATION_TOKEN,
    i.TARGET,
    CASE UPPER(SUBSTRING(i.TARGET, 1, POSITION('-' IN i.TARGET) - 1))
        WHEN 'USR' THEN 'USR'
        WHEN 'PMT' THEN 'PMT'
        WHEN 'STK' THEN 'STK'
        WHEN 'TRM' THEN 'TRM'
        ELSE            'UNK'
    END,
    i.PROGRAM,
    i.RETRY_COUNTER,
    i.CREATION_DATE,
    i.CREATION_DATE,   -- RECEPTION_DATE: original value unknown, use CREATION_DATE
    i.CREATION_DATE,   -- LAST_RETRY_DATE: best approximation
    NULL,              -- NEXT_RETRY_DATE: NULL = not eligible for further retries (FAILED)
    'FAILED'
FROM NOTIFICATION_INFO_IMPORT i
-- Skip tokens that are already tracked in the new table.
WHERE NOT EXISTS (
    SELECT 1
    FROM NOTIFICATION_ENTITY e
    WHERE e.WEB_HOOK_TOKEN = i.NOTIFICATION_TOKEN
);

-- ── Step 4: verify ────────────────────────────────────────────────────────────

SELECT
    COUNT(*)                                            AS total_in_csv,
    SUM(CASE WHEN e.WEB_HOOK_TOKEN IS NOT NULL THEN 1 ELSE 0 END) AS matched_in_target
FROM NOTIFICATION_INFO_IMPORT i
LEFT JOIN NOTIFICATION_ENTITY e ON e.WEB_HOOK_TOKEN = i.NOTIFICATION_TOKEN;

-- Show the imported rows.
SELECT
    WEB_HOOK_TOKEN,
    OBJECT_TOKEN,
    NOTIFICATION_TYPE,
    STATUS,
    RETRY_COUNTER,
    CREATION_DATE
FROM NOTIFICATION_ENTITY
WHERE STATUS = 'FAILED'
ORDER BY CREATION_DATE ASC;

-- ── Step 5: clean up temporary table ─────────────────────────────────────────

DROP TABLE NOTIFICATION_INFO_IMPORT;
