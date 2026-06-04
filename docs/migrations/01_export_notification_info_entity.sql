-- =============================================================================
-- EXPORT: NOTIFICATION_INFO_ENTITY  →  CSV
-- =============================================================================
-- Run this script against the OLD H2 database (the one that still has the
-- NOTIFICATION_INFO_ENTITY table) using the H2 Shell or H2 Console.
--
-- The output file path below is an example; change it to any writable path on
-- the machine that hosts the old database file.
--
-- H2 Shell (command line):
--   java -cp h2-<version>.jar org.h2.tools.Shell \
--        -url "jdbc:h2:file:/path/to/old/hmc.data" \
--        -user sa -password password \
--        -sql "RUNSCRIPT FROM '/path/to/01_export_notification_info_entity.sql'"
--
-- H2 Console (browser UI):
--   Connect to the old database, open the SQL editor, and paste this script.
-- =============================================================================

-- Export only the FAILED notifications (RETRY_COUNTER > 0 indicates at least
-- one attempt was made; adjust the WHERE clause to taste).
CALL CSVWRITE(
    '/tmp/notification_info_export.csv',
    'SELECT
         NOTIFICATION_TOKEN,
         TARGET,
         PROGRAM,
         RETRY_COUNTER,
         CREATION_DATE
     FROM NOTIFICATION_INFO_ENTITY
     WHERE RETRY_COUNTER > 0
     ORDER BY CREATION_DATE ASC'
);

-- Verify: check how many rows were exported.
SELECT COUNT(*) AS exported_rows FROM NOTIFICATION_INFO_ENTITY WHERE RETRY_COUNTER > 0;
