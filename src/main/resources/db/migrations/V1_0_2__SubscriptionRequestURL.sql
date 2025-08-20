
--
-- Alter the subscription_request table to add callback endpoints
--

ALTER TABLE subscription_request
ADD COLUMN IF NOT EXISTS callback_endpoint VARCHAR(2048)
DEFAULT NULL;

ALTER TABLE subscription_request
ADD COLUMN IF NOT EXISTS push_all BOOLEAN;
