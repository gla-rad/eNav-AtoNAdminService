--
-- Add an index column to the signal sequence tables so that the order of the
-- sequences is preserved. Existing rows are numbered in their physical order.
--

ALTER TABLE public.light_air_obstruction_signal_sequences
    ADD COLUMN signal_sequence_index integer;

UPDATE public.light_air_obstruction_signal_sequences s
    SET signal_sequence_index = o.idx
    FROM (SELECT ctid, row_number() OVER (PARTITION BY light_air_obstruction_id ORDER BY ctid) - 1 AS idx
          FROM public.light_air_obstruction_signal_sequences) o
    WHERE s.ctid = o.ctid;

ALTER TABLE public.light_air_obstruction_signal_sequences
    ALTER COLUMN signal_sequence_index SET NOT NULL,
    ADD PRIMARY KEY (light_air_obstruction_id, signal_sequence_index);

ALTER TABLE public.light_all_around_signal_sequences
    ADD COLUMN signal_sequence_index integer;

UPDATE public.light_all_around_signal_sequences s
    SET signal_sequence_index = o.idx
    FROM (SELECT ctid, row_number() OVER (PARTITION BY light_all_around_id ORDER BY ctid) - 1 AS idx
          FROM public.light_all_around_signal_sequences) o
    WHERE s.ctid = o.ctid;

ALTER TABLE public.light_all_around_signal_sequences
    ALTER COLUMN signal_sequence_index SET NOT NULL,
    ADD PRIMARY KEY (light_all_around_id, signal_sequence_index);

ALTER TABLE public.light_fog_detector_signal_sequences
    ADD COLUMN signal_sequence_index integer;

UPDATE public.light_fog_detector_signal_sequences s
    SET signal_sequence_index = o.idx
    FROM (SELECT ctid, row_number() OVER (PARTITION BY light_fog_detector_id ORDER BY ctid) - 1 AS idx
          FROM public.light_fog_detector_signal_sequences) o
    WHERE s.ctid = o.ctid;

ALTER TABLE public.light_fog_detector_signal_sequences
    ALTER COLUMN signal_sequence_index SET NOT NULL,
    ADD PRIMARY KEY (light_fog_detector_id, signal_sequence_index);

ALTER TABLE public.sector_characteristics_signal_sequences
    ADD COLUMN signal_sequence_index integer;

UPDATE public.sector_characteristics_signal_sequences s
    SET signal_sequence_index = o.idx
    FROM (SELECT ctid, row_number() OVER (PARTITION BY sector_characteristics_id ORDER BY ctid) - 1 AS idx
          FROM public.sector_characteristics_signal_sequences) o
    WHERE s.ctid = o.ctid;

ALTER TABLE public.sector_characteristics_signal_sequences
    ALTER COLUMN signal_sequence_index SET NOT NULL,
    ADD PRIMARY KEY (sector_characteristics_id, signal_sequence_index);

ALTER TABLE public.fog_signal_signal_sequences
    ADD COLUMN signal_sequence_index integer;

UPDATE public.fog_signal_signal_sequences s
    SET signal_sequence_index = o.idx
    FROM (SELECT ctid, row_number() OVER (PARTITION BY fog_signal_id ORDER BY ctid) - 1 AS idx
          FROM public.fog_signal_signal_sequences) o
    WHERE s.ctid = o.ctid;

ALTER TABLE public.fog_signal_signal_sequences
    ALTER COLUMN signal_sequence_index SET NOT NULL,
    ADD PRIMARY KEY (fog_signal_id, signal_sequence_index);

ALTER TABLE public.radar_transponder_beacon_signal_sequences
    ADD COLUMN signal_sequence_index integer;

UPDATE public.radar_transponder_beacon_signal_sequences s
    SET signal_sequence_index = o.idx
    FROM (SELECT ctid, row_number() OVER (PARTITION BY radar_transponder_beacon_id ORDER BY ctid) - 1 AS idx
          FROM public.radar_transponder_beacon_signal_sequences) o
    WHERE s.ctid = o.ctid;

ALTER TABLE public.radar_transponder_beacon_signal_sequences
    ALTER COLUMN signal_sequence_index SET NOT NULL,
    ADD PRIMARY KEY (radar_transponder_beacon_id, signal_sequence_index);
