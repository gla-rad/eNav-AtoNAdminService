-- Currently the initial database considered simple timestamps without any
-- timezone information. To allow for that we should alter all the table
-- columns to a timestampz type.

--
-- Alter the aids_to_navigation table
--

ALTER TABLE aids_to_navigation
ALTER COLUMN last_modified_at
TYPE timestamptz
USING last_modified_at AT TIME ZONE current_setting('TIMEZONE');

--
-- Alter the dataset_content table
--

ALTER TABLE dataset_content
ALTER COLUMN generated_at
TYPE timestamptz
USING generated_at AT TIME ZONE current_setting('TIMEZONE');

--
-- Alter the dataset_content table
--

ALTER TABLE s125dataset
ALTER COLUMN created_at
TYPE timestamptz
USING created_at AT TIME ZONE current_setting('TIMEZONE');

ALTER TABLE s125dataset
ALTER COLUMN last_updated_at
TYPE timestamptz
USING last_updated_at AT TIME ZONE current_setting('TIMEZONE');

--
-- Alter the dataset_content table
--

ALTER TABLE dataset_content_log
ALTER COLUMN generated_at
TYPE timestamptz
USING generated_at AT TIME ZONE current_setting('TIMEZONE');

--
-- Alter the subscription_request table
--

ALTER TABLE subscription_request
ALTER COLUMN created_at
TYPE timestamptz
USING created_at AT TIME ZONE current_setting('TIMEZONE');

ALTER TABLE subscription_request
ALTER COLUMN updated_at
TYPE timestamptz
USING updated_at AT TIME ZONE current_setting('TIMEZONE');

ALTER TABLE subscription_request
ALTER COLUMN subscription_period_start
TYPE timestamptz
USING subscription_period_start AT TIME ZONE current_setting('TIMEZONE');

ALTER TABLE subscription_request
ALTER COLUMN subscription_period_end
TYPE timestamptz
USING subscription_period_end AT TIME ZONE current_setting('TIMEZONE');

