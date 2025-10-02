--
-- Alter the subscription_request table to add the SECOM version
--

ALTER TABLE subscription_request
ADD COLUMN secom_version VARCHAR(2);