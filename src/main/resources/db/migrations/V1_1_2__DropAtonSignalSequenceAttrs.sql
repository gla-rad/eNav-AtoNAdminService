--
-- Drop signal duration and status attributes from radar_transponder_beacon_signal_sequences
--

ALTER TABLE aids_to_navigation
    DROP COLUMN signal_duration;

ALTER TABLE aids_to_navigation
    DROP COLUMN signal_status;