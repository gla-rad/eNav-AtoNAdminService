--
-- Name: radar_transponder_beacon_signal_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.radar_transponder_beacon_signal_sequences (
    radar_transponder_beacon_id numeric(38,0) NOT NULL,
    signal_duration numeric(38,2),
    signal_status character varying(255),
    CONSTRAINT radar_transponder_beacon_signal_sequences_signal_status_check CHECK (((signal_status)::text = ANY ((ARRAY['LIT_SOUND'::character varying, 'ECLIPSED_SILENT'::character varying])::text[])))
);

--
-- Name: radar_transponder_beacon_signal_sequences fk3nyf65n00vg2qwdefdhasjklt4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.radar_transponder_beacon_signal_sequences
    ADD CONSTRAINT fk3nyf65n00vg2qwdefdhasjklt4 FOREIGN KEY (radar_transponder_beacon_id) REFERENCES public.aids_to_navigation(id);