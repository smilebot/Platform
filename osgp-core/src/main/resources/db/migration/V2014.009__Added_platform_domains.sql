--
-- Add platform domains column to organisation table
--

ALTER TABLE organisation ADD COLUMN domains character varying(255);

--
-- Set a default domain.
--

UPDATE organisation SET domains = 'COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING;';

--
-- Create NOT NULL constraint for this added column.
--

ALTER TABLE organisation ALTER COLUMN domains SET NOT NULL;