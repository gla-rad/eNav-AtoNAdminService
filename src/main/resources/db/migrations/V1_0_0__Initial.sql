--
-- PostgreSQL database dump
--

-- Dumped from database version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

-- *not* creating schema, since initdb creates it

--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

-- CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: -
--

-- COMMENT ON EXTENSION postgis IS 'PostGIS geometry and geography spatial types and functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: aggregation_join_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.aggregation_join_table (
    aggregation_id numeric(38,0) NOT NULL,
    aton_id numeric(38,0) NOT NULL
);


--
-- Name: aggregation_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.aggregation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: aids_to_navigation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.aids_to_navigation (
    based_on_fixed_marks boolean,
    bridle_holds_id numeric(38,0),
    buoy_part_id numeric(38,0),
    cable_holds_bridle_id numeric(38,0),
    cable_holds_swivel_id numeric(38,0),
    cable_length numeric(38,2),
    counter_weight_holds_id numeric(38,0),
    date_end date,
    date_start date,
    depth_range_minimum_value numeric(38,2),
    diameter numeric(38,2),
    effective_intensity numeric(38,2),
    elevation numeric(38,2),
    estimated_range_of_transmission numeric(38,2),
    flare_bearing numeric(38,0),
    height numeric(38,2),
    height_length_units smallint,
    horizontal_accuracy numeric(38,2),
    horizontal_length numeric(38,2),
    horizontal_width numeric(38,2),
    id numeric(38,0) NOT NULL,
    installation_date date,
    is_slatted boolean,
    major_light boolean,
    manned_structure boolean,
    maximum_permitted_draught numeric(38,2),
    multiplicity_known boolean,
    number_of_features numeric(38,0),
    orientation numeric(38,2),
    orientation_uncertainty numeric(38,2),
    orientation_value numeric(38,2),
    parent_id numeric(38,0),
    peak_intensity numeric(38,2),
    period_end date,
    period_start date,
    racon_sector_limit_one_bearing numeric(38,2),
    racon_sector_limit_one_line_length numeric(38,0),
    racon_sector_limit_two_bearing numeric(38,2),
    racon_sector_limit_two_line_length numeric(38,0),
    radar_conspicuous boolean,
    scale_minimum numeric(38,0),
    shackle_to_anchor_connected_id numeric(38,0),
    shackle_to_buoy_connected_to_id numeric(38,0),
    shackle_to_cable_connected_id numeric(38,0),
    shackle_to_swivel_connected_id numeric(38,0),
    signal_duration numeric(38,2),
    signal_frequency numeric(38,0),
    signal_output double precision,
    signal_period numeric(38,2),
    source_date date,
    swivel_holds_id numeric(38,0),
    uncertainty_fixed numeric(38,2),
    uncertainty_variable_factor numeric(38,2),
    value_of_geographic_range numeric(38,2),
    value_of_luminous_range numeric(38,2),
    value_of_maximum_range numeric(38,2),
    value_of_nominal_range numeric(38,2),
    vertical_accuracy numeric(38,2),
    vertical_length numeric(38,2),
    wave_length_value numeric(38,2),
    weight numeric(38,2),
    last_modified_at timestamp(6) with time zone,
    dtype character varying(31) NOT NULL,
    administrative_division character varying(255),
    aid_availability_category character varying(255),
    aton_maintenance_record character varying(255),
    aton_number character varying(255),
    beacon_shape character varying(255),
    bridle_link_type character varying(255),
    building_shape character varying(255),
    buoy_shape character varying(255),
    category_of_cable character varying(255),
    category_of_cardinal_mark character varying(255),
    category_of_fog_signal character varying(255),
    category_of_installation_buoy character varying(255),
    category_of_lateral_mark character varying(255),
    category_of_navigation_line character varying(255),
    category_of_physicalaisaid_to_navigation_type character varying(255),
    category_of_pile character varying(255),
    category_of_power_source character varying(255),
    category_of_radar_transponder_beacon_type character varying(255),
    category_of_radio_station character varying(255),
    category_of_silo_tank character varying(255),
    category_of_special_purpose_mark character varying(255),
    category_of_syntheticaisaidto_navigation character varying(255),
    city_name character varying(255),
    condition character varying(255),
    counter_weight_type character varying(255),
    country character varying(255),
    delivery_point character varying(255),
    exhibition_condition_of_light character varying(255),
    id_code character varying(255) NOT NULL,
    inspection_frequency character varying(255),
    inspection_requirements character varying(255),
    language character varying(255),
    light_characteristic character varying(255),
    light_visibility character varying(255),
    manufacturer character varying(255),
    marks_navigational_system_of character varying(255),
    mmsi_code character varying(255),
    nature_of_construction character varying(255),
    pictorial_representation character varying(255),
    postal_code character varying(255),
    radar_band character varying(255),
    shackle_type character varying(255),
    signal_generation character varying(255),
    signal_group character varying(255),
    signal_status character varying(255),
    sinker_type character varying(255),
    source character varying(255),
    status character varying(255),
    swivel_type character varying(255),
    text character varying(255),
    topmark_daymark_shape character varying(255),
    traffic_flow character varying(255),
    type_of_buoy character varying(255),
    vertical_datum character varying(255),
    virtualaisaid_to_navigation_type character varying(255),
    visual_prominence character varying(255),
    geometry public.geometry,
    signal_groups character varying(255)[],
    CONSTRAINT aids_to_navigation_aid_availability_category_check CHECK (((aid_availability_category)::text = ANY ((ARRAY['CATEGORY_1'::character varying, 'CATEGORY_2'::character varying, 'CATEGORY_3'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_beacon_shape_check CHECK (((beacon_shape)::text = ANY ((ARRAY['STAKE_POLE_PERCH_POST'::character varying, 'WITHY'::character varying, 'BEACON_TOWER'::character varying, 'LATTICE_BEACON'::character varying, 'PILE_BEACON'::character varying, 'CAIRN'::character varying, 'BUOYANT_BEACON'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_building_shape_check CHECK (((building_shape)::text = ANY ((ARRAY['HIGH_RISE_BUILDING'::character varying, 'PYRAMID'::character varying, 'CYLINDRICAL'::character varying, 'SPHERICAL'::character varying, 'CUBIC'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_buoy_shape_check CHECK (((buoy_shape)::text = ANY ((ARRAY['CONICAL'::character varying, 'CAN'::character varying, 'SPHERICAL'::character varying, 'PILLAR'::character varying, 'SPAR'::character varying, 'BARREL'::character varying, 'SUPERBUOY'::character varying, 'ICE_BUOY'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_cable_check CHECK (((category_of_cable)::text = ANY ((ARRAY['POWER_LINE'::character varying, 'TRANSMISSION_LINE'::character varying, 'TELEPHONE'::character varying, 'TELEGRAPH'::character varying, 'MOORING_CABLE'::character varying, 'FERRY'::character varying, 'FIBRE_OPTIC_CABLE'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_cardinal_mark_check CHECK (((category_of_cardinal_mark)::text = ANY ((ARRAY['NORTH_CARDINAL_MARK'::character varying, 'EAST_CARDINAL_MARK'::character varying, 'SOUTH_CARDINAL_MARK'::character varying, 'WEST_CARDINAL_MARK'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_fog_signal_check CHECK (((category_of_fog_signal)::text = ANY ((ARRAY['EXPLOSIVE'::character varying, 'DIAPHONE'::character varying, 'SIREN'::character varying, 'NAUTOPHONE'::character varying, 'REED'::character varying, 'TYFON'::character varying, 'BELL'::character varying, 'WHISTLE'::character varying, 'GONG'::character varying, 'HORN'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_installation_buoy_check CHECK (((category_of_installation_buoy)::text = ANY ((ARRAY['CATENARY_ANCHOR_LEG_MOORING'::character varying, 'SINGLE_BUOY_MOORING'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_lateral_mark_check CHECK (((category_of_lateral_mark)::text = ANY ((ARRAY['PORT_HAND_LATERAL_MARK'::character varying, 'STARBOARD_HAND_LATERAL_MARK'::character varying, 'PREFERRED_CHANNEL_TO_STARBOARD_LATERAL_MARK'::character varying, 'PREFERRED_CHANNEL_TO_PORT_LATERAL_MARK'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_navigation_line_check CHECK (((category_of_navigation_line)::text = ANY ((ARRAY['CLEARING_LINE'::character varying, 'TRANSIT_LINE'::character varying, 'LEADING_LINE_BEARING_A_RECOMMENDED_TRACK'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_physicalaisaid_to_navigati_check CHECK (((category_of_physicalaisaid_to_navigation_type)::text = ANY ((ARRAY['PHYSICAL_AIS_TYPE_1'::character varying, 'PHYSICAL_AIS_TYPE_2'::character varying, 'PHYSICAL_AIS_TYPE_3'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_pile_check CHECK (((category_of_pile)::text = ANY ((ARRAY['STAKE'::character varying, 'POST'::character varying, 'TRIPODAL'::character varying, 'PILING'::character varying, 'AREA_OF_PILES'::character varying, 'PIPE'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_power_source_check CHECK (((category_of_power_source)::text = ANY ((ARRAY['BATTERY'::character varying, 'GENERATOR'::character varying, 'SOLAR_PANEL'::character varying, 'ELECTRICAL_SERVICE'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_radar_transponder_beacon_t_check CHECK (((category_of_radar_transponder_beacon_type)::text = ANY ((ARRAY['RAMARK_RADAR_BEACON_TRANSMITTING_CONTINUOUSLY'::character varying, 'RACON_RADAR_TRANSPONDER_BEACON'::character varying, 'LEADING_RACON_RADAR_TRANSPONDER_BEACON'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_radio_station_check CHECK (((category_of_radio_station)::text = ANY ((ARRAY['CIRCULAR_NON_DIRECTIONAL_MARINE_OR_AERO_MARINE_RADIOBEACON'::character varying, 'DIRECTIONAL_RADIOBEACON'::character varying, 'ROTATING_PATTERN_RADIOBEACON'::character varying, 'CONSOL_BEACON'::character varying, 'RADIO_DIRECTION_FINDING_STATION'::character varying, 'COAST_RADIO_STATION_PROVIDING_QTG_SERVICE'::character varying, 'AERONAUTICAL_RADIOBEACON'::character varying, 'DECCA'::character varying, 'LORAN_C'::character varying, 'DIFFERENTIAL_GNSS'::character varying, 'TORAN'::character varying, 'OMEGA'::character varying, 'SYLEDIS'::character varying, 'CHAIKA'::character varying, 'RADIO_TELEPHONE_STATION'::character varying, 'AIS_BASE_STATION'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_silo_tank_check CHECK (((category_of_silo_tank)::text = ANY ((ARRAY['SILO_IN_GENERAL'::character varying, 'TANK_IN_GENERAL'::character varying, 'GRAIN_ELEVATOR'::character varying, 'WATER_TOWER'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_special_purpose_mark_check CHECK (((category_of_special_purpose_mark)::text = ANY ((ARRAY['FIRING_DANGER_MARK'::character varying, 'TARGET_MARK'::character varying, 'MARKER_SHIP_MARK'::character varying, 'DEGAUSSING_RANGE_MARK'::character varying, 'BARGE_MARK'::character varying, 'CABLE_MARK'::character varying, 'SPOIL_GROUND_MARK'::character varying, 'OUTFALL_MARK'::character varying, 'ODAS'::character varying, 'RECORDING_MARK'::character varying, 'SEAPLANE_ANCHORAGE_MARK'::character varying, 'RECREATION_ZONE_MARK'::character varying, 'PRIVATE_MARK'::character varying, 'MOORING_MARK'::character varying, 'LANBY'::character varying, 'LEADING_MARK'::character varying, 'MEASURED_DISTANCE_MARK'::character varying, 'NOTICE_MARK'::character varying, 'TSS_MARK'::character varying, 'ANCHORING_PROHIBITED_MARK'::character varying, 'BERTHING_PROHIBITED_MARK'::character varying, 'OVERTAKING_PROHIBITED_MARK'::character varying, 'TWO_WAY_TRAFFIC_PROHIBITED_MARK'::character varying, 'REDUCED_WAKE_MARK'::character varying, 'SPEED_LIMIT_MARK'::character varying, 'STOP_MARK'::character varying, 'GENERAL_WARNING_MARK'::character varying, 'SOUND_SHIP_S_SIREN_MARK'::character varying, 'RESTRICTED_VERTICAL_CLEARANCE_MARK'::character varying, 'MAXIMUM_VESSEL_S_DRAUGHT_MARK'::character varying, 'RESTRICTED_HORIZONTAL_CLEARANCE_MARK'::character varying, 'STRONG_CURRENT_WARNING_MARK'::character varying, 'BERTHING_PERMITTED_MARK'::character varying, 'OVERHEAD_POWER_CABLE_MARK'::character varying, 'CHANNEL_EDGE_GRADIENT_MARK'::character varying, 'TELEPHONE_MARK'::character varying, 'FERRY_CROSSING_MARK'::character varying, 'PIPELINE_MARK'::character varying, 'ANCHORAGE_MARK'::character varying, 'CLEARING_MARK'::character varying, 'CONTROL_MARK'::character varying, 'DIVING_MARK'::character varying, 'REFUGE_BEACON'::character varying, 'FOUL_GROUND_MARK'::character varying, 'YACHTING_MARK'::character varying, 'HELIPORT_MARK'::character varying, 'GNSS_MARK'::character varying, 'SEAPLANE_LANDING_MARK'::character varying, 'ENTRY_PROHIBITED_MARK'::character varying, 'WORK_IN_PROGRESS_MARK'::character varying, 'MARK_WITH_UNKNOWN_PURPOSE'::character varying, 'WELLHEAD_MARK'::character varying, 'CHANNEL_SEPARATION_MARK'::character varying, 'MARINE_FARM_MARK'::character varying, 'ARTIFICIAL_REEF_MARK'::character varying, 'ICE_MARK'::character varying, 'NATURE_RESERVE_MARK'::character varying, 'FISH_AGGREGATING_DEVICE'::character varying, 'WRECK_MARK'::character varying, 'CUSTOMS_MARK'::character varying, 'CAUSEWAY_MARK'::character varying, 'WAVE_RECORDER'::character varying, 'JETSKI_PROHIBITED'::character varying, 'FACILITY_PROTECTION_MARK'::character varying, 'OIL_PIPELINE_PROTECTION_MARK'::character varying, 'MARINE_CABLE_PROTECTION_MARK'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_category_of_syntheticaisaidto_navigati_check CHECK (((category_of_syntheticaisaidto_navigation)::text = ANY ((ARRAY['PREDICTED'::character varying, 'MONITORED'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_condition_check CHECK (((condition)::text = ANY ((ARRAY['UNDER_CONSTRUCTION'::character varying, 'RUINED'::character varying, 'UNDER_RECLAMATION'::character varying, 'WINGLESS'::character varying, 'PLANNED_CONSTRUCTION'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_exhibition_condition_of_light_check CHECK (((exhibition_condition_of_light)::text = ANY ((ARRAY['LIGHT_SHOWN_WITHOUT_CHANGE_OF_CHARACTER'::character varying, 'DAYTIME_LIGHT'::character varying, 'FOG_LIGHT'::character varying, 'NIGHT_LIGHT'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_height_length_units_check CHECK (((height_length_units >= 0) AND (height_length_units <= 5))),
    CONSTRAINT aids_to_navigation_light_characteristic_check CHECK (((light_characteristic)::text = ANY ((ARRAY['FIXED'::character varying, 'FLASHING'::character varying, 'LONG_FLASHING'::character varying, 'QUICK_FLASHING'::character varying, 'VERY_QUICK_FLASHING'::character varying, 'CONTINUOUS_ULTRA_QUICK_FLASHING'::character varying, 'ISOPHASED'::character varying, 'OCCULTING'::character varying, 'MORSE'::character varying, 'FIXED_AND_FLASH'::character varying, 'FLASH_AND_LONG_FLASH'::character varying, 'OCCULTING_AND_FLASH'::character varying, 'FIXED_AND_LONG_FLASH'::character varying, 'OCCULTING_ALTERNATING'::character varying, 'LONG_FLASH_ALTERNATING'::character varying, 'FLASH_ALTERNATING'::character varying, 'GROUP_ALTERNATING'::character varying, 'QUICK_FLASH_PLUS_LONG_FLASH'::character varying, 'VERY_QUICK_FLASH_PLUS_LONG_FLASH'::character varying, 'ULTRA_QUICK_FLASH_PLUS_LONG_FLASH'::character varying, 'ALTERNATING'::character varying, 'FIXED_AND_ALTERNATING_FLASHING'::character varying, 'GROUP_OCCULTING_LIGHT'::character varying, 'COMPOSITE_GROUP_OCCULTING_LIGHT'::character varying, 'GROUP_FLASHING_LIGHT'::character varying, 'COMPOSITE_GROUP_FLASHING_LIGHT'::character varying, 'GROUP_QUICK_LIGHT'::character varying, 'GROUP_VERY_QUICK_LIGHT'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_light_visibility_check CHECK (((light_visibility)::text = ANY ((ARRAY['HIGH_INTENSITY'::character varying, 'LOW_INTENSITY'::character varying, 'FAINT'::character varying, 'INTENSIFIED'::character varying, 'UNINTENSIFIED'::character varying, 'VISIBILITY_DELIBERATELY_RESTRICTED'::character varying, 'OBSCURED'::character varying, 'PARTIALLY_OBSCURED'::character varying, 'VISIBLE_IN_LINE_OF_RANGE'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_marks_navigational_system_of_check CHECK (((marks_navigational_system_of)::text = ANY ((ARRAY['IALA_A'::character varying, 'IALA_B'::character varying, 'NO_SYSTEM'::character varying, 'OTHER_SYSTEM'::character varying, 'CEVNI'::character varying, 'RUSSIAN_INLAND_WATERWAY_REGULATIONS'::character varying, 'BRAZILIAN_NATIONAL_INLAND_WATERWAY_REGULATIONS_TWO_SIDES'::character varying, 'BRAZILIAN_NATIONAL_INLAND_WATERWAY_REGULATIONS_SIDE_INDEPENDENT'::character varying, 'PARAGUAY_PARANA_WATERWAY_BRAZILIAN_COMPLEMENTARY_AIDS'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_nature_of_construction_check CHECK (((nature_of_construction)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_shackle_type_check CHECK (((shackle_type)::text = ANY ((ARRAY['FORELOCK_SHACKLES'::character varying, 'CLENCHING_SHACKLES'::character varying, 'BOLT_SHACKLES'::character varying, 'SCREW_PIN_SHACKLES'::character varying, 'KENTER_SHACKLE'::character varying, 'QUICK_RELEASE_LINK'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_signal_generation_check CHECK (((signal_generation)::text = ANY ((ARRAY['AUTOMATICALLY'::character varying, 'BY_WAVE_ACTION'::character varying, 'BY_HAND'::character varying, 'BY_WIND'::character varying, 'RADIO_ACTIVATED'::character varying, 'CALL_ACTIVATED'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_signal_status_check CHECK (((signal_status)::text = ANY ((ARRAY['LIT_SOUND'::character varying, 'ECLIPSED_SILENT'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_status_check CHECK (((status)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_traffic_flow_check CHECK (((traffic_flow)::text = ANY ((ARRAY['INBOUND'::character varying, 'OUTBOUND'::character varying, 'ONE_WAY'::character varying, 'TWO_WAY'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_vertical_datum_check CHECK (((vertical_datum)::text = ANY ((ARRAY['MEAN_LOW_WATER_SPRINGS'::character varying, 'MEAN_LOWER_LOW_WATER_SPRINGS'::character varying, 'MEAN_SEA_LEVEL'::character varying, 'LOWEST_LOW_WATER'::character varying, 'MEAN_LOW_WATER'::character varying, 'LOWEST_LOW_WATER_SPRINGS'::character varying, 'APPROXIMATE_MEAN_LOW_WATER_SPRINGS'::character varying, 'INDIAN_SPRING_LOW_WATER'::character varying, 'LOW_WATER_SPRINGS'::character varying, 'APPROXIMATE_LOWEST_ASTRONOMICAL_TIDE'::character varying, 'NEARLY_LOWEST_LOW_WATER'::character varying, 'MEAN_LOWER_LOW_WATER'::character varying, 'LOW_WATER'::character varying, 'APPROXIMATE_MEAN_LOW_WATER'::character varying, 'APPROXIMATE_MEAN_LOWER_LOW_WATER'::character varying, 'MEAN_HIGH_WATER'::character varying, 'MEAN_HIGH_WATER_SPRINGS'::character varying, 'HIGH_WATER'::character varying, 'APPROXIMATE_MEAN_SEA_LEVEL'::character varying, 'HIGH_WATER_SPRINGS'::character varying, 'MEAN_HIGHER_HIGH_WATER'::character varying, 'EQUINOCTIAL_SPRING_LOW_WATER'::character varying, 'LOWEST_ASTRONOMICAL_TIDE'::character varying, 'LOCAL_DATUM'::character varying, 'INTERNATIONAL_GREAT_LAKES_DATUM_1985'::character varying, 'MEAN_WATER_LEVEL'::character varying, 'LOWER_LOW_WATER_LARGE_TIDE'::character varying, 'HIGHER_HIGH_WATER_LARGE_TIDE'::character varying, 'NEARLY_HIGHEST_HIGH_WATER'::character varying, 'HIGHEST_ASTRONOMICAL_TIDE'::character varying, 'LOCAL_LOW_WATER_REFERENCE_LEVEL'::character varying, 'LOCAL_HIGH_WATER_REFERENCE_LEVEL'::character varying, 'LOCAL_MEAN_WATER_REFERENCE_LEVEL'::character varying, 'EQUIVALENT_HEIGHT_OF_WATER_GERMAN_GL_W'::character varying, 'HIGHEST_SHIPPING_HEIGHT_OF_WATER_GERMAN_HSW'::character varying, 'REFERENCE_LOW_WATER_LEVEL_ACCORDING_TO_DANUBE_COMMISSION'::character varying, 'HIGHEST_SHIPPING_HEIGHT_OF_WATER_ACCORDING_TO_DANUBE_COMMISSION'::character varying, 'DUTCH_RIVER_LOW_WATER_REFERENCE_LEVEL_OLR'::character varying, 'RUSSIAN_PROJECT_WATER_LEVEL'::character varying, 'RUSSIAN_NORMAL_BACKWATER_LEVEL'::character varying, 'OHIO_RIVER_DATUM'::character varying, 'DUTCH_HIGH_WATER_REFERENCE_LEVEL'::character varying, 'BALTIC_SEA_CHART_DATUM_2000'::character varying, 'DUTCH_ESTUARY_LOW_WATER_REFERENCE_LEVEL_OLW'::character varying, 'INTERNATIONAL_GREAT_LAKES_DATUM_2020'::character varying, 'SEA_FLOOR'::character varying, 'SEA_SURFACE'::character varying, 'HYDROGRAPHIC_ZERO'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_virtualaisaid_to_navigation_type_check CHECK (((virtualaisaid_to_navigation_type)::text = ANY ((ARRAY['NORTH_CARDINAL'::character varying, 'EAST_CARDINAL'::character varying, 'SOUTH_CARDINAL'::character varying, 'WEST_CARDINAL'::character varying, 'PORT_LATERAL'::character varying, 'STARBOARD_LATERAL'::character varying, 'PREFERRED_CHANNEL_TO_PORT'::character varying, 'PREFERRED_CHANNEL_TO_STARBOARD'::character varying, 'ISOLATED_DANGER'::character varying, 'SAFE_WATER'::character varying, 'SPECIAL_PURPOSE'::character varying, 'NEW_DANGER_MARKING'::character varying])::text[]))),
    CONSTRAINT aids_to_navigation_visual_prominence_check CHECK (((visual_prominence)::text = ANY ((ARRAY['VISUALLY_CONSPICUOUS'::character varying, 'NOT_VISUALLY_CONSPICUOUS'::character varying, 'PROMINENT'::character varying])::text[])))
);


--
-- Name: aids_to_navigation_feature_names; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.aids_to_navigation_feature_names (
    aids_to_navigation_id numeric(38,0) NOT NULL,
    display_name boolean,
    language character varying(255),
    name character varying(255)
);


--
-- Name: aids_to_navigation_seasonal_action_requireds; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.aids_to_navigation_seasonal_action_requireds (
    aids_to_navigation_id numeric(38,0) NOT NULL,
    seasonal_action_requireds character varying(255)
);


--
-- Name: aids_to_navigation_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.aids_to_navigation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: association_join_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.association_join_table (
    association_id numeric(38,0) NOT NULL,
    aton_id numeric(38,0) NOT NULL
);


--
-- Name: association_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.association_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: aton_aggregation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.aton_aggregation (
    id numeric(38,0) NOT NULL,
    category_of_aggregation_type character varying(255),
    CONSTRAINT aton_aggregation_category_of_aggregation_type_check CHECK (((category_of_aggregation_type)::text = ANY ((ARRAY['LEADING_LINE'::character varying, 'MEASURED_DISTANCE'::character varying, 'RANGE_SYSTEM'::character varying])::text[])))
);


--
-- Name: aton_association; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.aton_association (
    id numeric(38,0) NOT NULL,
    category_of_association character varying(255),
    CONSTRAINT aton_association_category_of_association_check CHECK (((category_of_association)::text = ANY ((ARRAY['CHANNEL_MARKINGS'::character varying, 'DANGER_MARKINGS'::character varying])::text[])))
);


--
-- Name: beacon_special_purpose_category_of_special_purpose_marks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.beacon_special_purpose_category_of_special_purpose_marks (
    beacon_special_purpose_id numeric(38,0) NOT NULL,
    category_of_special_purpose_marks character varying(255),
    CONSTRAINT beacon_special_purpose_categ_category_of_special_purpose__check CHECK (((category_of_special_purpose_marks)::text = ANY ((ARRAY['FIRING_DANGER_MARK'::character varying, 'TARGET_MARK'::character varying, 'MARKER_SHIP_MARK'::character varying, 'DEGAUSSING_RANGE_MARK'::character varying, 'BARGE_MARK'::character varying, 'CABLE_MARK'::character varying, 'SPOIL_GROUND_MARK'::character varying, 'OUTFALL_MARK'::character varying, 'ODAS'::character varying, 'RECORDING_MARK'::character varying, 'SEAPLANE_ANCHORAGE_MARK'::character varying, 'RECREATION_ZONE_MARK'::character varying, 'PRIVATE_MARK'::character varying, 'MOORING_MARK'::character varying, 'LANBY'::character varying, 'LEADING_MARK'::character varying, 'MEASURED_DISTANCE_MARK'::character varying, 'NOTICE_MARK'::character varying, 'TSS_MARK'::character varying, 'ANCHORING_PROHIBITED_MARK'::character varying, 'BERTHING_PROHIBITED_MARK'::character varying, 'OVERTAKING_PROHIBITED_MARK'::character varying, 'TWO_WAY_TRAFFIC_PROHIBITED_MARK'::character varying, 'REDUCED_WAKE_MARK'::character varying, 'SPEED_LIMIT_MARK'::character varying, 'STOP_MARK'::character varying, 'GENERAL_WARNING_MARK'::character varying, 'SOUND_SHIP_S_SIREN_MARK'::character varying, 'RESTRICTED_VERTICAL_CLEARANCE_MARK'::character varying, 'MAXIMUM_VESSEL_S_DRAUGHT_MARK'::character varying, 'RESTRICTED_HORIZONTAL_CLEARANCE_MARK'::character varying, 'STRONG_CURRENT_WARNING_MARK'::character varying, 'BERTHING_PERMITTED_MARK'::character varying, 'OVERHEAD_POWER_CABLE_MARK'::character varying, 'CHANNEL_EDGE_GRADIENT_MARK'::character varying, 'TELEPHONE_MARK'::character varying, 'FERRY_CROSSING_MARK'::character varying, 'PIPELINE_MARK'::character varying, 'ANCHORAGE_MARK'::character varying, 'CLEARING_MARK'::character varying, 'CONTROL_MARK'::character varying, 'DIVING_MARK'::character varying, 'REFUGE_BEACON'::character varying, 'FOUL_GROUND_MARK'::character varying, 'YACHTING_MARK'::character varying, 'HELIPORT_MARK'::character varying, 'GNSS_MARK'::character varying, 'SEAPLANE_LANDING_MARK'::character varying, 'ENTRY_PROHIBITED_MARK'::character varying, 'WORK_IN_PROGRESS_MARK'::character varying, 'MARK_WITH_UNKNOWN_PURPOSE'::character varying, 'WELLHEAD_MARK'::character varying, 'CHANNEL_SEPARATION_MARK'::character varying, 'MARINE_FARM_MARK'::character varying, 'ARTIFICIAL_REEF_MARK'::character varying, 'ICE_MARK'::character varying, 'NATURE_RESERVE_MARK'::character varying, 'FISH_AGGREGATING_DEVICE'::character varying, 'WRECK_MARK'::character varying, 'CUSTOMS_MARK'::character varying, 'CAUSEWAY_MARK'::character varying, 'WAVE_RECORDER'::character varying, 'JETSKI_PROHIBITED'::character varying, 'FACILITY_PROTECTION_MARK'::character varying, 'OIL_PIPELINE_PROTECTION_MARK'::character varying, 'MARINE_CABLE_PROTECTION_MARK'::character varying])::text[])))
);


--
-- Name: broadcast_by_join_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.broadcast_by_join_table (
    electronic_aton_id numeric(38,0) NOT NULL,
    radio_station_id numeric(38,0) NOT NULL
);


--
-- Name: buoy_special_purpose_category_of_special_purpose_marks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.buoy_special_purpose_category_of_special_purpose_marks (
    buoy_special_purpose_id numeric(38,0) NOT NULL,
    category_of_special_purpose_marks character varying(255),
    CONSTRAINT buoy_special_purpose_categor_category_of_special_purpose__check CHECK (((category_of_special_purpose_marks)::text = ANY ((ARRAY['FIRING_DANGER_MARK'::character varying, 'TARGET_MARK'::character varying, 'MARKER_SHIP_MARK'::character varying, 'DEGAUSSING_RANGE_MARK'::character varying, 'BARGE_MARK'::character varying, 'CABLE_MARK'::character varying, 'SPOIL_GROUND_MARK'::character varying, 'OUTFALL_MARK'::character varying, 'ODAS'::character varying, 'RECORDING_MARK'::character varying, 'SEAPLANE_ANCHORAGE_MARK'::character varying, 'RECREATION_ZONE_MARK'::character varying, 'PRIVATE_MARK'::character varying, 'MOORING_MARK'::character varying, 'LANBY'::character varying, 'LEADING_MARK'::character varying, 'MEASURED_DISTANCE_MARK'::character varying, 'NOTICE_MARK'::character varying, 'TSS_MARK'::character varying, 'ANCHORING_PROHIBITED_MARK'::character varying, 'BERTHING_PROHIBITED_MARK'::character varying, 'OVERTAKING_PROHIBITED_MARK'::character varying, 'TWO_WAY_TRAFFIC_PROHIBITED_MARK'::character varying, 'REDUCED_WAKE_MARK'::character varying, 'SPEED_LIMIT_MARK'::character varying, 'STOP_MARK'::character varying, 'GENERAL_WARNING_MARK'::character varying, 'SOUND_SHIP_S_SIREN_MARK'::character varying, 'RESTRICTED_VERTICAL_CLEARANCE_MARK'::character varying, 'MAXIMUM_VESSEL_S_DRAUGHT_MARK'::character varying, 'RESTRICTED_HORIZONTAL_CLEARANCE_MARK'::character varying, 'STRONG_CURRENT_WARNING_MARK'::character varying, 'BERTHING_PERMITTED_MARK'::character varying, 'OVERHEAD_POWER_CABLE_MARK'::character varying, 'CHANNEL_EDGE_GRADIENT_MARK'::character varying, 'TELEPHONE_MARK'::character varying, 'FERRY_CROSSING_MARK'::character varying, 'PIPELINE_MARK'::character varying, 'ANCHORAGE_MARK'::character varying, 'CLEARING_MARK'::character varying, 'CONTROL_MARK'::character varying, 'DIVING_MARK'::character varying, 'REFUGE_BEACON'::character varying, 'FOUL_GROUND_MARK'::character varying, 'YACHTING_MARK'::character varying, 'HELIPORT_MARK'::character varying, 'GNSS_MARK'::character varying, 'SEAPLANE_LANDING_MARK'::character varying, 'ENTRY_PROHIBITED_MARK'::character varying, 'WORK_IN_PROGRESS_MARK'::character varying, 'MARK_WITH_UNKNOWN_PURPOSE'::character varying, 'WELLHEAD_MARK'::character varying, 'CHANNEL_SEPARATION_MARK'::character varying, 'MARINE_FARM_MARK'::character varying, 'ARTIFICIAL_REEF_MARK'::character varying, 'ICE_MARK'::character varying, 'NATURE_RESERVE_MARK'::character varying, 'FISH_AGGREGATING_DEVICE'::character varying, 'WRECK_MARK'::character varying, 'CUSTOMS_MARK'::character varying, 'CAUSEWAY_MARK'::character varying, 'WAVE_RECORDER'::character varying, 'JETSKI_PROHIBITED'::character varying, 'FACILITY_PROTECTION_MARK'::character varying, 'OIL_PIPELINE_PROTECTION_MARK'::character varying, 'MARINE_CABLE_PROTECTION_MARK'::character varying])::text[])))
);


--
-- Name: cable_submarine_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cable_submarine_statuses (
    cable_submarine_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT cable_submarine_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: dangerous_feature; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dangerous_feature (
    id numeric(38,0) NOT NULL,
    interoperability_identifier character varying(255),
    geometry public.geometry
);


--
-- Name: dangerous_feature_association_join_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dangerous_feature_association_join_table (
    association_id numeric(38,0) NOT NULL,
    dangerous_feature_id numeric(38,0) NOT NULL
);


--
-- Name: dangerous_feature_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dangerous_feature_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dataset_content; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dataset_content (
    content_length numeric(38,0),
    delta_length numeric(38,0),
    id numeric(24,0) NOT NULL,
    sequence_no numeric(38,0),
    generated_at timestamp(6) with time zone,
    content oid,
    delta oid
);


--
-- Name: dataset_content_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dataset_content_log (
    content_length numeric(38,0),
    delta_length numeric(38,0),
    id numeric(24,0) NOT NULL,
    sequence_no numeric(38,0),
    generated_at timestamp(6) with time zone,
    uuid uuid NOT NULL,
    dataset_type character varying(255),
    operation character varying(255),
    content oid,
    delta oid,
    geometry public.geometry,
    CONSTRAINT dataset_content_log_dataset_type_check CHECK (((dataset_type)::text = 'S201'::text)),
    CONSTRAINT dataset_content_log_operation_check CHECK (((operation)::text = ANY ((ARRAY['CREATED'::character varying, 'UPDATED'::character varying, 'CANCELLED'::character varying, 'DELETED'::character varying, 'OTHER'::character varying, 'AUTO'::character varying])::text[])))
);


--
-- Name: dataset_content_log_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dataset_content_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dataset_content_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dataset_content_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dataset_identification_generator_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dataset_identification_generator_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: daymark_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.daymark_colour_patterns (
    daymark_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT daymark_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: daymark_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.daymark_colours (
    daymark_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT daymark_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: daymark_nature_of_constructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.daymark_nature_of_constructions (
    daymark_id numeric(38,0) NOT NULL,
    nature_of_constructions character varying(255),
    CONSTRAINT daymark_nature_of_constructions_nature_of_constructions_check CHECK (((nature_of_constructions)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: daymark_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.daymark_statuses (
    daymark_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT daymark_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: electronic_aton_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.electronic_aton_statuses (
    electronic_aton_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT electronic_aton_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: equipment_remote_monitoring_systems; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.equipment_remote_monitoring_systems (
    equipment_id numeric(38,0) NOT NULL,
    remote_monitoring_systems character varying(255)
);


--
-- Name: fog_signal_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fog_signal_statuses (
    fog_signal_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT fog_signal_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: generic_beacon_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_beacon_colour_patterns (
    generic_beacon_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT generic_beacon_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: generic_beacon_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_beacon_colours (
    generic_beacon_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT generic_beacon_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: generic_beacon_nature_of_constructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_beacon_nature_of_constructions (
    generic_beacon_id numeric(38,0) NOT NULL,
    nature_of_constructions character varying(255),
    CONSTRAINT generic_beacon_nature_of_construc_nature_of_constructions_check CHECK (((nature_of_constructions)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: generic_beacon_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_beacon_statuses (
    generic_beacon_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT generic_beacon_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: generic_buoy_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_buoy_colour_patterns (
    generic_buoy_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT generic_buoy_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: generic_buoy_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_buoy_colours (
    generic_buoy_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT generic_buoy_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: generic_buoy_nature_of_constructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_buoy_nature_of_constructions (
    generic_buoy_id numeric(38,0) NOT NULL,
    nature_of_constructions character varying(255),
    CONSTRAINT generic_buoy_nature_of_constructi_nature_of_constructions_check CHECK (((nature_of_constructions)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: generic_buoy_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_buoy_statuses (
    generic_buoy_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT generic_buoy_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: generic_light_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.generic_light_statuses (
    generic_light_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT generic_light_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: information; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.information (
    dangerous_feature_id numeric(38,0),
    feature_id numeric(38,0),
    fixes_id numeric(38,0),
    id numeric(38,0) NOT NULL,
    positions_id numeric(38,0),
    source_date date,
    dtype character varying(31) NOT NULL,
    aton_commissioning character varying(255),
    aton_removal character varying(255),
    aton_replacement character varying(255),
    audible_signal_aton_change character varying(255),
    change_types character varying(255),
    electronic_aton_change character varying(255),
    file_locator character varying(255),
    file_reference character varying(255),
    fixed_aton_change character varying(255),
    floating_aton_change character varying(255),
    headline character varying(255),
    horizontal_datum character varying(255),
    language character varying(255),
    lighted_aton_change character varying(255),
    nmea_string character varying(255),
    positioning_device character varying(255),
    positioning_equipment character varying(255),
    positioning_procedure character varying(255),
    reference_point character varying(255),
    text character varying(255),
    CONSTRAINT information_aton_commissioning_check CHECK (((aton_commissioning)::text = ANY ((ARRAY['BUOY_ESTABLISHMENT'::character varying, 'LIGHT_ESTABLISHMENT'::character varying, 'BEACON_ESTABLISHMENT'::character varying, 'AUDIBLE_SIGNAL_ESTABLISHMENT'::character varying, 'FOG_SIGNAL_ESTABLISHMENT'::character varying, 'AIS_TRANSMITTER_ESTABLISHMENT'::character varying, 'V_AIS_ESTABLISHMENT'::character varying, 'RACON_ESTABLISHMENT'::character varying, 'DGPS_STATION_ESTABLISHMENT'::character varying, 'E_LORAN_STATION_ESTABLISHMENT'::character varying, 'DGLONASS_STATION_ESTABLISHMENT'::character varying, 'E_CHAYKA_STATION_ESTABLISHMENT'::character varying, 'EGNOS_ESTABLISHMENT'::character varying])::text[]))),
    CONSTRAINT information_aton_removal_check CHECK (((aton_removal)::text = ANY ((ARRAY['BUOY_REMOVAL'::character varying, 'BUOY_TEMPORARY_REMOVAL'::character varying, 'LIGHT_REMOVAL'::character varying, 'LIGHT_TEMPORARY_REMOVAL'::character varying, 'BEACON_REMOVAL'::character varying, 'BEACON_TEMPORARY_REMOVAL'::character varying, 'FOG_SIGNAL_REMOVAL'::character varying, 'FOG_SIGNAL_TEMPORARY_REMOVAL'::character varying, 'AUDIBLE_SIGNAL_REMOVAL'::character varying, 'AUDIBLE_SIGNAL_TEMPORARY_REMOVAL'::character varying, 'V_AIS_REMOVAL'::character varying, 'V_AIS_TEMPORARY_REMOVAL'::character varying, 'RACON_SIGNAL_REMOVAL'::character varying, 'RACON_TEMPORARY_REMOVAL'::character varying, 'DGPS_REMOVAL'::character varying, 'DGPS_TEMPORARY_REMOVAL'::character varying, 'EGNOS_REMOVAL'::character varying, 'EGNOS_TEMPORARY_REMOVAL'::character varying, 'LORAN_C_STATION_REMOVAL'::character varying, 'LORAN_C_STATION_TEMPORARY_REMOVAL'::character varying, 'E_LORAN_REMOVAL'::character varying, 'E_LORAN_TEMPORARY_REMOVAL'::character varying, 'CHAYKA_STATION_REMOVAL'::character varying, 'CHAYKA_STATION_TEMPORARY_REMOVAL'::character varying, 'E_CHAYKA_STATION_REMOVAL'::character varying, 'E_CHAYKA_STATION_TEMPORARY_REMOVAL'::character varying])::text[]))),
    CONSTRAINT information_aton_replacement_check CHECK (((aton_replacement)::text = ANY ((ARRAY['BUOY_CHANGE'::character varying, 'BUOY_TEMPORARY_CHANGE'::character varying, 'LIGHT_CHANGE'::character varying, 'LIGHT_TEMPORARY_CHANGE'::character varying, 'SECTOR_LIGHT_CHANGE'::character varying, 'SECTOR_LIGHT_TEMPORARY_CHANGE'::character varying, 'BEACON_CHANGE'::character varying, 'BEACON_TEMPORARY_CHANGE'::character varying, 'FOG_SIGNAL_CHANGE'::character varying, 'FOG_SIGNAL_TEMPORARY_CHANGE'::character varying, 'AUDIBLE_SIGNAL_CHANGE'::character varying, 'AUDIBLE_SIGNAL_TEMPORARY_CHANGE'::character varying, 'V_AIS_CHANGE'::character varying, 'V_AIS_TEMPORARY_CHANGE'::character varying, 'RACON_SIGNAL_CHANGE'::character varying, 'RACON_TEMPORARY_CHANGE'::character varying])::text[]))),
    CONSTRAINT information_audible_signal_aton_change_check CHECK (((audible_signal_aton_change)::text = ANY ((ARRAY['AUDIBLE_SIGNAL_OUT_OF_SERVICE'::character varying, 'FOG_SIGNAL_OUT_OF_SERVICE'::character varying, 'AUDIBLE_SIGNAL_OPERATING_PROPERLY'::character varying, 'FOG_SIGNAL_OPERATING_PROPERLY'::character varying])::text[]))),
    CONSTRAINT information_change_types_check CHECK (((change_types)::text = ANY ((ARRAY['ADVANCED_NOTICE_OF_CHANGES'::character varying, 'DISCREPANCY'::character varying, 'PROPOSED_CHANGES'::character varying, 'TEMPORARY_CHANGES'::character varying])::text[]))),
    CONSTRAINT information_electronic_aton_change_check CHECK (((electronic_aton_change)::text = ANY ((ARRAY['AIS_TRANSMITTER_OUT_OF_SERVICE'::character varying, 'AIS_TRANSMITTER_UNRELIABLE'::character varying, 'AIS_TRANSMITTER_OPERATING_PROPERLY'::character varying, 'V_AIS_OUT_OF_SERVICE'::character varying, 'V_AIS_UNRELIABLE'::character varying, 'V_AIS_OPERATING_PROPERLY'::character varying, 'RACON_OUT_OF_SERVICE'::character varying, 'RACON_UNRELIABLE'::character varying, 'RACON_OPERATING_PROPERLY'::character varying, 'DGPS_OUT_OF_SERVICE'::character varying, 'DGPS_OPERATING_PROPERLY'::character varying, 'DGPS_UNRELIABLE'::character varying, 'LORAN_C_OPERATING_PROPERLY'::character varying, 'LORAN_C_UNRELIABLE'::character varying, 'LORAN_C_OUT_OF_SERVICE'::character varying, 'E_LORAN_OPERATING_PROPERLY'::character varying, 'E_LORAN_UNRELIABLE'::character varying, 'E_LORAN_OUT_OF_SERVICE'::character varying, 'DGLOANSS_OPERATING_PROPERLY'::character varying, 'DGLOANSS_UNRELIABLE'::character varying, 'DGLOANSS_OUT_OF_SERVICE'::character varying, 'CHAYKA_OPERATING_PROPERLY'::character varying, 'CHAYKA_UNRELIABLE'::character varying, 'CHAYKA_OUT_OF_SERVICE'::character varying, 'E_CHAYKA_OPERATING_PROPERLY'::character varying, 'E_CHAYKA_UNRELIABLE'::character varying, 'E_CHAYKA_OUT_OF_SERVICE'::character varying, 'EGNOS_OPERATING_PROPERLY'::character varying, 'EGNOS_UNRELIABLE'::character varying, 'EGNOS_OUT_OF_SERVICE'::character varying])::text[]))),
    CONSTRAINT information_fixed_aton_change_check CHECK (((fixed_aton_change)::text = ANY ((ARRAY['BEACON_MISSING'::character varying, 'BEACON_DAMAGED'::character varying, 'LIGHT_BEACON_UNLIT'::character varying, 'LIGHT_BEACON_UNRELIABLE'::character varying, 'LIGHT_BEACON_NOT_SYNCHRONIZED'::character varying, 'LIGHT_BEACON_DAMAGED'::character varying, 'BEACON_TOPMARK_MISSING'::character varying, 'BEACON_TOPMARK_DAMAGED'::character varying, 'BEACON_DAYMARK_UNRELIABLE'::character varying, 'FLOODLIT_BEACON_UNLIT'::character varying, 'BEACON_RESTORED_TO_NORMAL'::character varying])::text[]))),
    CONSTRAINT information_floating_aton_change_check CHECK (((floating_aton_change)::text = ANY ((ARRAY['BUOY_ADRIFT'::character varying, 'BUOY_DAMAGED'::character varying, 'BUOY_DAYMARK_UNRELIABLE'::character varying, 'BUOY_DESTROYED'::character varying, 'BUOY_MISSING'::character varying, 'BUOY_MOVE'::character varying, 'BUOY_OFF_POSITION'::character varying, 'BUOY_RE_ESTABLISHMENT'::character varying, 'BUOY_RESTORED_TO_NORMAL'::character varying, 'BUOY_TOPMARK_DAMAGED'::character varying, 'BUOY_TOPMARK_MISSING'::character varying, 'BUOY_WILL_BE_WITHDRAWN'::character varying, 'BUOY_WITHDRAWN'::character varying, 'DECOMMISSIONED_FOR_WINTER'::character varying, 'LIFTED_FOR_WINTER'::character varying, 'LIGHT_BUOY_LIGHT_DAMAGED'::character varying, 'LIGHT_BUOY_LIGHT_NOT_SYNCHRONIZED'::character varying, 'LIGHT_BUOY_LIGHT_UNLIT'::character varying, 'LIGHT_BUOY_LIGHT_UNRELIABLE'::character varying, 'MARINE_AIDS_TO_NAVIGATION_UNRELIABLE'::character varying, 'RECOMMISSIONED_FOR_NAVIGATION_SEASON'::character varying, 'REPLACED_BY_WINTER_SPAR'::character varying, 'SEASONAL_DECOMMISSIONING_COMPLETE'::character varying, 'SEASONAL_DECOMMISSIONING_IN_PROGRESS'::character varying, 'SEASONAL_RECOMMISSIONING_COMPLETE'::character varying, 'SEASONAL_RECOMMISSIONING_IN_PROGRESS'::character varying])::text[]))),
    CONSTRAINT information_horizontal_datum_check CHECK (((horizontal_datum)::text = ANY ((ARRAY['WGS_72'::character varying, 'WGS_84'::character varying, 'EUROPEAN_1950'::character varying, 'POTSDAM_DATUM'::character varying, 'ADINDAN'::character varying, 'AFGOOYE'::character varying, 'AIN_EL_ABD_1970'::character varying, 'ANNA_1_ASTRO_1965'::character varying, 'ANTIGUA_ISLAND_ASTRO_1943'::character varying, 'ARC_1950'::character varying, 'ARC_1960'::character varying, 'ASCENSION_ISLAND_1958'::character varying, 'ASTRO_BEACON_E_1945'::character varying, 'ASTRO_DOS_71_4'::character varying, 'ASTRO_TERN_ISLAND_FRIG_1961'::character varying, 'ASTRONOMICAL_STATION_1952'::character varying, 'AUSTRALIAN_GEODETIC_1966'::character varying, 'AUSTRALIAN_GEODETIC_1984'::character varying, 'AYABELLE_LIGHTHOUSE'::character varying, 'BELLEVUE_IGN'::character varying, 'BERMUDA_1957'::character varying, 'BISSAU'::character varying, 'BOGOTA_OBSERVATORY'::character varying, 'BUKIT_RIMPAH'::character varying, 'CAMP_AREA_ASTRO'::character varying, 'CAMPO_INCHAUSPE_1969'::character varying, 'CANTON_ASTRO_1966'::character varying, 'CAPE_DATUM'::character varying, 'CAPE_CANAVERAL'::character varying, 'CARTHAGE'::character varying, 'CHATAM_ISLAND_ASTRO_1971'::character varying, 'CHUA_ASTRO'::character varying, 'CORREGO_ALEGRE'::character varying, 'DABOLA'::character varying, 'DJAKARTA_BATAVIA'::character varying, 'DOS_1968'::character varying, 'EASTER_ISLAND_1967'::character varying, 'EUROPEAN_1979'::character varying, 'FORT_THOMAS_1955'::character varying, 'GAN_1970'::character varying, 'GEODETIC_DATUM_1949'::character varying, 'GRACIOSA_BASE_SW_1948'::character varying, 'GUAM_1963'::character varying, 'GUNUNG_SEGARA'::character varying, 'GUX_1_ASTRO'::character varying, 'HERAT_NORTH'::character varying, 'HJORSEY_1955'::character varying, 'HONG_KONG_1963'::character varying, 'HU_TZU_SHAN'::character varying, 'INDIAN'::character varying, 'INDIAN_1954'::character varying, 'INDIAN_1975'::character varying, 'IRELAND_1965'::character varying, 'ISTS_061_ASTRO_1968'::character varying, 'ISTS_073_ASTRO_1969'::character varying, 'JOHNSTON_ISLAND_1961'::character varying, 'KANDAWALA'::character varying, 'KERGUELEN_ISLAND_1949'::character varying, 'KERTAU_1968'::character varying, 'KUSAIE_ASTRO_1951'::character varying, 'L_C_5_ASTRO_1961'::character varying, 'LEIGON'::character varying, 'LIBERIA_1964'::character varying, 'LUZON'::character varying, 'MAHE_1971'::character varying, 'MASSAWA'::character varying, 'MERCHICH'::character varying, 'MIDWAY_ASTRO_1961'::character varying, 'MINNA'::character varying, 'MONTSERRAT_ISLAND_ASTRO_1958'::character varying, 'M_PORALOKO'::character varying, 'NAHRWAN'::character varying, 'NAPARIMA_BWI'::character varying, 'NORTH_AMERICAN_1927'::character varying, 'NORTH_AMERICAN_1983'::character varying, 'OBSERVATORIO_METEOROLOGICO_1939'::character varying, 'OLD_EGYPTIAN_1907'::character varying, 'OLD_HAWAIIAN'::character varying, 'OMAN'::character varying, 'ORDNANCE_SURVEY_OF_GREAT_BRITAIN_1936'::character varying, 'PICO_DE_LAS_NIEVES'::character varying, 'PITCAIRN_ASTRO_1967'::character varying, 'POINT_58'::character varying, 'POINTE_NOIRE_1948'::character varying, 'PORTO_SANTO_1936'::character varying, 'PROVISIONAL_SOUTH_AMERICAN_1956'::character varying, 'PROVISIONAL_SOUTH_CHILEAN_1963'::character varying, 'PUERTO_RICO'::character varying, 'QATAR_NATIONAL'::character varying, 'QORNOQ'::character varying, 'REUNION'::character varying, 'ROME_1940'::character varying, 'SANTO_DOS_1965'::character varying, 'SAO_BRAZ'::character varying, 'SAPPER_HILL_1943'::character varying, 'SCHWARZECK'::character varying, 'SELVAGEM_GRANDE_1938'::character varying, 'SOUTH_AMERICAN_1969'::character varying, 'SOUTH_ASIA'::character varying, 'TANANARIVE_OBSERVATORY_1925'::character varying, 'TIMBALAI_1948'::character varying, 'TOKYO'::character varying, 'TRISTAN_ASTRO_1968'::character varying, 'VITI_LEVU_1916'::character varying, 'WAKE_ENIWETOK_1960'::character varying, 'WAKE_ISLAND_ASTRO_1952'::character varying, 'YACARE'::character varying, 'ZANDERIJ'::character varying, 'AMERICAN_SAMOA_1962'::character varying, 'DECEPTION_ISLAND'::character varying, 'INDIAN_1960'::character varying, 'INDONESIAN_1974'::character varying, 'NORTH_SAHARA_1959'::character varying, 'PULKOVO_1942'::character varying, 'S_JTSK'::character varying, 'VOIROL_1950'::character varying, 'AVERAGE_TERRESTRIAL_SYSTEM_1977'::character varying, 'COMPENSATION_GEODESIQUE_DU_QUEBEC_1977'::character varying, 'FINNISH_KKJ'::character varying, 'ORDNANCE_SURVEY_OF_IRELAND'::character varying, 'REVISED_KERTAU'::character varying, 'REVISED_NAHRWAN'::character varying, 'GGRS_76_GREECE'::character varying, 'NOUVELLE_TRIANGULATION_DE_FRANCE'::character varying, 'RT_90_SWEDEN'::character varying, 'GEOCENTRIC_DATUM_OF_AUSTRALIA'::character varying, 'BJZ_54_A_954_BEIJING_COORDINATES'::character varying, 'MODIFIED_BJZ_54'::character varying, 'GDZ_80'::character varying, 'LOCAL_DATUM'::character varying])::text[]))),
    CONSTRAINT information_lighted_aton_change_check CHECK (((lighted_aton_change)::text = ANY ((ARRAY['LIGHT_UNLIT'::character varying, 'LIGHT_UNRELIABLE'::character varying, 'LIGHT_RE_ESTABLISHMENT'::character varying, 'LIGHT_RANGE_REDUCED'::character varying, 'LIGHT_WITHOUT_RHYTHM'::character varying, 'LIGHT_OUT_OF_SYNCHRONIZATION'::character varying, 'LIGHT_DAYMARK_UNRELIABLE'::character varying, 'LIGHT_OPERATING_PROPERLY'::character varying, 'SECTOR_LIGHT_SECTOR_OBSCURED'::character varying, 'FRONT_LEADING_RANGE_LIGHT_UNLIT'::character varying, 'REAR_LEADING_RANGE_LIGHT_UNLIT'::character varying, 'FRONT_LEADING_RANGE_LIGHT_UNRELIABLE'::character varying, 'REAR_LEADING_RANGE_LIGHT_UNRELIABLE'::character varying, 'FRONT_LEADING_RANGE_LIGHT_LIGHT_RANGE_REDUCED'::character varying, 'REAR_LEADING_RANGE_LIGHT_LIGHT_RANGE_REDUCED'::character varying, 'FRONT_LEADING_RANGE_LIGHT_WITHOUT_RHYTHM'::character varying, 'REAR_LEADING_RANGE_LIGHT_WITHOUT_RHYTHM'::character varying, 'LEADING_RANGE_LIGHTS_OUT_OF_SYNCHRONIZATION'::character varying, 'FRONT_LEADING_RANGE_BEACON_UNRELIABLE'::character varying, 'REAR_LEADING_RANGE_BEACON_UNRELIABLE'::character varying, 'FRONT_LEADING_RANGE_LIGHT_IS_OPERATING_PROPERLY'::character varying, 'REAR_LEADING_RANGE_LIGHT_IS_OPERATING_PROPERLY'::character varying, 'FRONT_LEADING_RANGE_BEACON_RESTORED_TO_NORMAL'::character varying, 'REAR_LEADING_RANGE_BEACON_RESTORED_TO_NORMAL'::character varying])::text[]))),
    CONSTRAINT information_positioning_equipment_check CHECK (((positioning_equipment)::text = ANY ((ARRAY['DGPS_RECEIVER'::character varying, 'GLONASS_RECEIVER'::character varying, 'GPS_RECEIVER'::character varying, 'GPS_WAAS_RECEIVER'::character varying])::text[])))
);


--
-- Name: information_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.information_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: landmark_category_of_landmarks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.landmark_category_of_landmarks (
    landmark_id numeric(38,0) NOT NULL,
    category_of_landmarks character varying(255),
    CONSTRAINT landmark_category_of_landmarks_category_of_landmarks_check CHECK (((category_of_landmarks)::text = ANY ((ARRAY['CAIRN'::character varying, 'CEMETERY'::character varying, 'CHIMNEY'::character varying, 'DISH_AERIAL'::character varying, 'FLAGSTAFF'::character varying, 'FLARE_STACK'::character varying, 'MAST'::character varying, 'WINDSOCK'::character varying, 'MONUMENT'::character varying, 'COLUMN_PILLAR'::character varying, 'MEMORIAL_PLAQUE'::character varying, 'OBELISK'::character varying, 'STATUE'::character varying, 'CROSS'::character varying, 'DOME'::character varying, 'RADAR_SCANNER'::character varying, 'TOWER'::character varying, 'WINDMILL'::character varying, 'WINDMOTOR'::character varying, 'SPIRE_MINARET'::character varying, 'LARGE_ROCK_OR_BOULDER_ON_LAND'::character varying, 'TRIANGULATION_MARK'::character varying, 'BOUNDARY_MARK'::character varying, 'OBSERVATION_WHEEL'::character varying, 'TORII'::character varying, 'BRIDGE'::character varying, 'DAM'::character varying])::text[])))
);


--
-- Name: landmark_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.landmark_colour_patterns (
    landmark_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT landmark_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: landmark_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.landmark_colours (
    landmark_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT landmark_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: landmark_functions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.landmark_functions (
    landmark_id numeric(38,0) NOT NULL,
    functions character varying(255),
    CONSTRAINT landmark_functions_functions_check CHECK (((functions)::text = ANY ((ARRAY['HARBOUR_MASTERS_OFFICE'::character varying, 'CUSTOMS_OFFICE'::character varying, 'HEALTH_OFFICE'::character varying, 'HOSPITAL'::character varying, 'POST_OFFICE'::character varying, 'HOTEL'::character varying, 'RAILWAY_STATION'::character varying, 'POLICE_STATION'::character varying, 'WATER_POLICE_STATION'::character varying, 'PILOT_OFFICE'::character varying, 'PILOT_LOOKOUT'::character varying, 'BANK_OFFICE'::character varying, 'HEADQUARTERS_FOR_DISTRICT_CONTROL'::character varying, 'TRANSIT_SHED_WAREHOUSE'::character varying, 'FACTORY'::character varying, 'POWER_STATION'::character varying, 'ADMINISTRATIVE'::character varying, 'EDUCATIONAL_FACILITY'::character varying, 'CHURCH'::character varying, 'CHAPEL'::character varying, 'TEMPLE'::character varying, 'PAGODA'::character varying, 'SHINTO_SHRINE'::character varying, 'BUDDHIST_TEMPLE'::character varying, 'MOSQUE'::character varying, 'MARABOUT'::character varying, 'LOOKOUT'::character varying, 'COMMUNICATION'::character varying, 'TELEVISION'::character varying, 'RADIO'::character varying, 'RADAR'::character varying, 'LIGHT_SUPPORT'::character varying, 'MICROWAVE'::character varying, 'COOLING'::character varying, 'OBSERVATION'::character varying, 'TIMEBALL'::character varying, 'CLOCK'::character varying, 'CONTROL'::character varying, 'AIRSHIP_MOORING'::character varying, 'STADIUM'::character varying, 'BUS_STATION'::character varying, 'PASSENGER_TERMINAL_BUILDING'::character varying, 'SEA_RESCUE_CONTROL'::character varying, 'OBSERVATORY'::character varying, 'ORE_CRUSHER'::character varying, 'BOATHOUSE'::character varying, 'PUMPING_STATION'::character varying])::text[])))
);


--
-- Name: landmark_nature_of_constructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.landmark_nature_of_constructions (
    landmark_id numeric(38,0) NOT NULL,
    nature_of_constructions character varying(255),
    CONSTRAINT landmark_nature_of_constructions_nature_of_constructions_check CHECK (((nature_of_constructions)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: landmark_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.landmark_statuses (
    landmark_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT landmark_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: light_air_obstruction_exhibition_condition_of_lights; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_air_obstruction_exhibition_condition_of_lights (
    light_air_obstruction_id numeric(38,0) NOT NULL,
    exhibition_condition_of_lights character varying(255),
    CONSTRAINT light_air_obstruction_exhibi_exhibition_condition_of_ligh_check CHECK (((exhibition_condition_of_lights)::text = ANY ((ARRAY['LIGHT_SHOWN_WITHOUT_CHANGE_OF_CHARACTER'::character varying, 'DAYTIME_LIGHT'::character varying, 'FOG_LIGHT'::character varying, 'NIGHT_LIGHT'::character varying])::text[])))
);


--
-- Name: light_air_obstruction_light_visibilities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_air_obstruction_light_visibilities (
    light_air_obstruction_id numeric(38,0) NOT NULL,
    light_visibilities character varying(255),
    CONSTRAINT light_air_obstruction_light_visibiliti_light_visibilities_check CHECK (((light_visibilities)::text = ANY ((ARRAY['HIGH_INTENSITY'::character varying, 'LOW_INTENSITY'::character varying, 'FAINT'::character varying, 'INTENSIFIED'::character varying, 'UNINTENSIFIED'::character varying, 'VISIBILITY_DELIBERATELY_RESTRICTED'::character varying, 'OBSCURED'::character varying, 'PARTIALLY_OBSCURED'::character varying, 'VISIBLE_IN_LINE_OF_RANGE'::character varying])::text[])))
);


--
-- Name: light_air_obstruction_signal_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_air_obstruction_signal_sequences (
    light_air_obstruction_id numeric(38,0) NOT NULL,
    signal_duration numeric(38,2),
    signal_status character varying(255),
    CONSTRAINT light_air_obstruction_signal_sequences_signal_status_check CHECK (((signal_status)::text = ANY ((ARRAY['LIT_SOUND'::character varying, 'ECLIPSED_SILENT'::character varying])::text[])))
);


--
-- Name: light_all_around_category_of_lights; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_all_around_category_of_lights (
    light_all_around_id numeric(38,0) NOT NULL,
    category_of_lights character varying(255),
    CONSTRAINT light_all_around_category_of_lights_category_of_lights_check CHECK (((category_of_lights)::text = ANY ((ARRAY['DIRECTIONAL_FUNCTION'::character varying, 'LEADING_LIGHT'::character varying, 'AERO_LIGHT'::character varying, 'AIR_OBSTRUCTION_LIGHT'::character varying, 'FLOOD_LIGHT'::character varying, 'STRIP_LIGHT'::character varying, 'SUBSIDIARY_LIGHT'::character varying, 'SPOTLIGHT'::character varying, 'FRONT'::character varying, 'REAR'::character varying, 'LOWER'::character varying, 'UPPER'::character varying, 'EMERGENCY'::character varying, 'BEARING_LIGHT'::character varying, 'HORIZONTALLY_DISPOSED'::character varying, 'VERTICALLY_DISPOSED'::character varying])::text[])))
);


--
-- Name: light_all_around_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_all_around_colours (
    light_all_around_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT light_all_around_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: light_all_around_signal_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_all_around_signal_sequences (
    light_all_around_id numeric(38,0) NOT NULL,
    signal_duration numeric(38,2),
    signal_status character varying(255),
    CONSTRAINT light_all_around_signal_sequences_signal_status_check CHECK (((signal_status)::text = ANY ((ARRAY['LIT_SOUND'::character varying, 'ECLIPSED_SILENT'::character varying])::text[])))
);


--
-- Name: light_float_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_float_colour_patterns (
    light_float_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT light_float_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: light_float_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_float_colours (
    light_float_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT light_float_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: light_float_nature_of_constructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_float_nature_of_constructions (
    light_float_id numeric(38,0) NOT NULL,
    nature_of_constructions character varying(255),
    CONSTRAINT light_float_nature_of_constructio_nature_of_constructions_check CHECK (((nature_of_constructions)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: light_float_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_float_statuses (
    light_float_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT light_float_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: light_fog_detector_signal_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_fog_detector_signal_sequences (
    light_fog_detector_id numeric(38,0) NOT NULL,
    signal_duration numeric(38,2),
    signal_status character varying(255),
    CONSTRAINT light_fog_detector_signal_sequences_signal_status_check CHECK (((signal_status)::text = ANY ((ARRAY['LIT_SOUND'::character varying, 'ECLIPSED_SILENT'::character varying])::text[])))
);


--
-- Name: light_sectored_category_of_lights; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_sectored_category_of_lights (
    light_sectored_id numeric(38,0) NOT NULL,
    category_of_lights character varying(255),
    CONSTRAINT light_sectored_category_of_lights_category_of_lights_check CHECK (((category_of_lights)::text = ANY ((ARRAY['DIRECTIONAL_FUNCTION'::character varying, 'LEADING_LIGHT'::character varying, 'AERO_LIGHT'::character varying, 'AIR_OBSTRUCTION_LIGHT'::character varying, 'FLOOD_LIGHT'::character varying, 'STRIP_LIGHT'::character varying, 'SUBSIDIARY_LIGHT'::character varying, 'SPOTLIGHT'::character varying, 'FRONT'::character varying, 'REAR'::character varying, 'LOWER'::character varying, 'UPPER'::character varying, 'EMERGENCY'::character varying, 'BEARING_LIGHT'::character varying, 'HORIZONTALLY_DISPOSED'::character varying, 'VERTICALLY_DISPOSED'::character varying])::text[])))
);


--
-- Name: light_sectored_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_sectored_colours (
    light_sectored_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT light_sectored_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: light_sectored_obscured_sectors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_sectored_obscured_sectors (
    light_sectored_id numeric(38,0) NOT NULL,
    sector_limit_one_bearing numeric(38,2),
    sector_limit_one_line_length numeric(38,0),
    sector_limit_two_bearing numeric(38,2),
    sector_limit_two_line_length numeric(38,0),
    language character varying(255),
    text character varying(255)
);


--
-- Name: light_vessel_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_vessel_colour_patterns (
    light_vessel_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT light_vessel_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: light_vessel_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_vessel_colours (
    light_vessel_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT light_vessel_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: light_vessel_nature_of_constructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_vessel_nature_of_constructions (
    light_vessel_id numeric(38,0) NOT NULL,
    nature_of_constructions character varying(255),
    CONSTRAINT light_vessel_nature_of_constructi_nature_of_constructions_check CHECK (((nature_of_constructions)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: light_vessel_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.light_vessel_statuses (
    light_vessel_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT light_vessel_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: navigation_line_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.navigation_line_statuses (
    navigation_line_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT navigation_line_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: offshore_platform_category_of_offshore_platforms; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.offshore_platform_category_of_offshore_platforms (
    offshore_platform_id numeric(38,0) NOT NULL,
    category_of_offshore_platforms character varying(255),
    CONSTRAINT offshore_platform_category_o_category_of_offshore_platfor_check CHECK (((category_of_offshore_platforms)::text = ANY ((ARRAY['OIL_RIG'::character varying, 'PRODUCTION_PLATFORM'::character varying, 'OBSERVATION_RESEARCH_PLATFORM'::character varying, 'ARTICULATED_LOADING_PLATFORM'::character varying, 'SINGLE_ANCHOR_LEG_MOORING'::character varying, 'MOORING_TOWER'::character varying, 'ARTIFICIAL_ISLAND'::character varying, 'FLOATING_PRODUCTION_STORAGE_AND_OFF_LOADING_VESSEL'::character varying, 'ACCOMMODATION_PLATFORM'::character varying, 'NAVIGATION_COMMUNICATION_AND_CONTROL_BUOY'::character varying, 'FLOATING_OIL_TANK'::character varying])::text[])))
);


--
-- Name: offshore_platform_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.offshore_platform_colour_patterns (
    offshore_platform_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT offshore_platform_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: offshore_platform_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.offshore_platform_colours (
    offshore_platform_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT offshore_platform_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: offshore_platform_nature_of_constructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.offshore_platform_nature_of_constructions (
    offshore_platform_id numeric(38,0) NOT NULL,
    nature_of_constructions character varying(255),
    CONSTRAINT offshore_platform_nature_of_const_nature_of_constructions_check CHECK (((nature_of_constructions)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: offshore_platform_products; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.offshore_platform_products (
    offshore_platform_id numeric(38,0) NOT NULL,
    products character varying(255),
    CONSTRAINT offshore_platform_products_products_check CHECK (((products)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: offshore_platform_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.offshore_platform_statuses (
    offshore_platform_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT offshore_platform_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: pile_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pile_colour_patterns (
    pile_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT pile_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: pile_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pile_colours (
    pile_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT pile_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: power_source_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.power_source_statuses (
    power_source_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT power_source_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: radar_reflector_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.radar_reflector_statuses (
    radar_reflector_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT radar_reflector_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: radar_transponder_beacon_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.radar_transponder_beacon_statuses (
    radar_transponder_beacon_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT radar_transponder_beacon_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: recommended_track_nav_lines; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.recommended_track_nav_lines (
    navigation_line_id numeric(38,0) NOT NULL,
    recommended_track_id numeric(38,0) NOT NULL
);


--
-- Name: recommended_track_quality_of_vertical_measurement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.recommended_track_quality_of_vertical_measurement (
    recommended_track_id numeric(38,0) NOT NULL,
    quality_of_vertical_measurement character varying(255),
    CONSTRAINT recommended_track_quality_of_quality_of_vertical_measurem_check CHECK (((quality_of_vertical_measurement)::text = ANY ((ARRAY['DEPTH_KNOWN'::character varying, 'DEPTH_OR_LEAST_DEPTH_UNKNOWN'::character varying, 'DOUBTFUL_SOUNDING'::character varying, 'UNRELIABLE_SOUNDING'::character varying, 'NO_BOTTOM_FOUND_AT_VALUE_SHOWN'::character varying, 'LEAST_DEPTH_KNOWN'::character varying, 'LEAST_DEPTH_UNKNOWN_SAFE_CLEARANCE_AT_VALUE_SHOWN'::character varying, 'VALUE_REPORTED_NOT_SURVEYED'::character varying, 'VALUE_REPORTED_NOT_CONFIRMED'::character varying, 'MAINTAINED_DEPTH'::character varying, 'NOT_REGULARLY_MAINTAINED'::character varying])::text[])))
);


--
-- Name: recommended_track_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.recommended_track_statuses (
    recommended_track_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT recommended_track_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: recommended_track_technique_of_vertical_measurements; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.recommended_track_technique_of_vertical_measurements (
    recommended_track_id numeric(38,0) NOT NULL,
    technique_of_vertical_measurements character varying(255),
    CONSTRAINT recommended_track_technique__technique_of_vertical_measur_check CHECK (((technique_of_vertical_measurements)::text = ANY ((ARRAY['FOUND_BY_ECHO_SOUNDER'::character varying, 'FOUND_BY_SIDE_SCAN_SONAR'::character varying, 'FOUND_BY_MULTI_BEAM'::character varying, 'FOUND_BY_DIVER'::character varying, 'FOUND_BY_LEAD_LINE'::character varying, 'SWEPT_BY_WIRE_DRAG'::character varying, 'FOUND_BY_LASER'::character varying, 'SWEPT_BY_VERTICAL_ACOUSTIC_SYSTEM'::character varying, 'FOUND_BY_ELECTROMAGNETIC_SENSOR'::character varying, 'PHOTOGRAMMETRY'::character varying, 'SATELLITE_IMAGERY'::character varying, 'FOUND_BY_LEVELLING'::character varying, 'SWEPT_BY_SIDE_SCAN_SONAR'::character varying, 'COMPUTER_GENERATED'::character varying, 'FOUND_BY_LIDAR'::character varying, 'SYNTHETIC_APERTURE_RADAR'::character varying, 'HYPERSPECTRAL_IMAGERY'::character varying])::text[])))
);


--
-- Name: retro_reflector_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.retro_reflector_colour_patterns (
    retro_reflector_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT retro_reflector_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: retro_reflector_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.retro_reflector_colours (
    retro_reflector_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT retro_reflector_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: retro_reflector_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.retro_reflector_statuses (
    retro_reflector_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT retro_reflector_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: s201_dataset_content_xref; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.s201_dataset_content_xref (
    dataset_content_id numeric(24,0),
    dataset_uuid uuid NOT NULL
);


--
-- Name: s201dataset; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.s201dataset (
    cancelled boolean,
    dataset_identification_id numeric(38,0),
    created_at timestamp(6) with time zone,
    last_updated_at timestamp(6) with time zone,
    replaces uuid,
    uuid uuid NOT NULL,
    geometry public.geometry
);


--
-- Name: s201dataset_identification; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.s201dataset_identification (
    dataset_reference_date date,
    id numeric(38,0) NOT NULL,
    application_profile character varying(255),
    dataset_abstract character varying(255),
    dataset_file_identifier character varying(255),
    dataset_language character varying(255),
    dataset_title character varying(255),
    encoding_specification character varying(255),
    encoding_specification_edition character varying(255),
    product_edition character varying(255),
    product_identifier character varying(255)
);


--
-- Name: s201dataset_identification_dataset_topic_categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.s201dataset_identification_dataset_topic_categories (
    s201dataset_identification_id numeric(38,0) NOT NULL,
    dataset_topic_categories character varying(255),
    CONSTRAINT s201dataset_identification_datas_dataset_topic_categories_check CHECK (((dataset_topic_categories)::text = ANY ((ARRAY['FARMING'::character varying, 'BIOTA'::character varying, 'BOUNDARIES'::character varying, 'CLIMATOLOGY_METEOROLOGY_ATMOSPHERE'::character varying, 'ECONOMY'::character varying, 'ELEVATION'::character varying, 'ENVIRONMENT'::character varying, 'GEOSCIENTIFIC_INFORMATION'::character varying, 'HEALTH'::character varying, 'IMAGERY_BASE_MAPS_EARTH_COVER'::character varying, 'INTELLIGENCE_MILITARY'::character varying, 'INLAND_WATERS'::character varying, 'LOCATION'::character varying, 'OCEANS'::character varying, 'PLANNING_CADASTRE'::character varying, 'SOCIETY'::character varying, 'STRUCTURE'::character varying, 'TRANSPORTATION'::character varying, 'UTILITIES_COMMUNICATION'::character varying])::text[])))
);


--
-- Name: sector_characteristics; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sector_characteristics (
    id numeric(38,0) NOT NULL,
    moire_effect boolean,
    orientation_uncertainty numeric(38,2),
    orientation_value numeric(38,2),
    sector_arc_extension boolean,
    sector_limit_one_bearing numeric(38,2),
    sector_limit_one_line_length numeric(38,0),
    sector_limit_two_bearing numeric(38,2),
    sector_limit_two_line_length numeric(38,0),
    sector_of_id numeric(38,0),
    signal_period numeric(38,2),
    value_of_nominal_range numeric(38,2),
    light_characteristic character varying(255),
    signal_groups character varying(255)[],
    CONSTRAINT sector_characteristics_light_characteristic_check CHECK (((light_characteristic)::text = ANY ((ARRAY['FIXED'::character varying, 'FLASHING'::character varying, 'LONG_FLASHING'::character varying, 'QUICK_FLASHING'::character varying, 'VERY_QUICK_FLASHING'::character varying, 'CONTINUOUS_ULTRA_QUICK_FLASHING'::character varying, 'ISOPHASED'::character varying, 'OCCULTING'::character varying, 'MORSE'::character varying, 'FIXED_AND_FLASH'::character varying, 'FLASH_AND_LONG_FLASH'::character varying, 'OCCULTING_AND_FLASH'::character varying, 'FIXED_AND_LONG_FLASH'::character varying, 'OCCULTING_ALTERNATING'::character varying, 'LONG_FLASH_ALTERNATING'::character varying, 'FLASH_ALTERNATING'::character varying, 'GROUP_ALTERNATING'::character varying, 'QUICK_FLASH_PLUS_LONG_FLASH'::character varying, 'VERY_QUICK_FLASH_PLUS_LONG_FLASH'::character varying, 'ULTRA_QUICK_FLASH_PLUS_LONG_FLASH'::character varying, 'ALTERNATING'::character varying, 'FIXED_AND_ALTERNATING_FLASHING'::character varying, 'GROUP_OCCULTING_LIGHT'::character varying, 'COMPOSITE_GROUP_OCCULTING_LIGHT'::character varying, 'GROUP_FLASHING_LIGHT'::character varying, 'COMPOSITE_GROUP_FLASHING_LIGHT'::character varying, 'GROUP_QUICK_LIGHT'::character varying, 'GROUP_VERY_QUICK_LIGHT'::character varying])::text[])))
);


--
-- Name: sector_characteristics_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sector_characteristics_colours (
    sector_characteristics_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT sector_characteristics_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: sector_characteristics_light_visibilities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sector_characteristics_light_visibilities (
    sector_characteristics_id numeric(38,0) NOT NULL,
    light_visibilities character varying(255),
    CONSTRAINT sector_characteristics_light_visibilit_light_visibilities_check CHECK (((light_visibilities)::text = ANY ((ARRAY['HIGH_INTENSITY'::character varying, 'LOW_INTENSITY'::character varying, 'FAINT'::character varying, 'INTENSIFIED'::character varying, 'UNINTENSIFIED'::character varying, 'VISIBILITY_DELIBERATELY_RESTRICTED'::character varying, 'OBSCURED'::character varying, 'PARTIALLY_OBSCURED'::character varying, 'VISIBLE_IN_LINE_OF_RANGE'::character varying])::text[])))
);


--
-- Name: sector_characteristics_sector_informations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sector_characteristics_sector_informations (
    sector_characteristics_id numeric(38,0) NOT NULL,
    language character varying(255),
    text character varying(255)
);


--
-- Name: sector_characteristics_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.sector_characteristics_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sector_characteristics_signal_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sector_characteristics_signal_sequences (
    sector_characteristics_id numeric(38,0) NOT NULL,
    signal_duration numeric(38,2),
    signal_status character varying(255),
    CONSTRAINT sector_characteristics_signal_sequences_signal_status_check CHECK (((signal_status)::text = ANY ((ARRAY['LIT_SOUND'::character varying, 'ECLIPSED_SILENT'::character varying])::text[])))
);


--
-- Name: silo_tank_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.silo_tank_colour_patterns (
    silo_tank_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT silo_tank_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: silo_tank_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.silo_tank_colours (
    silo_tank_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT silo_tank_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: silo_tank_nature_of_constructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.silo_tank_nature_of_constructions (
    silo_tank_id numeric(38,0) NOT NULL,
    nature_of_constructions character varying(255),
    CONSTRAINT silo_tank_nature_of_constructions_nature_of_constructions_check CHECK (((nature_of_constructions)::text = ANY ((ARRAY['MASONRY'::character varying, 'CONCRETED'::character varying, 'LOOSE_BOULDERS'::character varying, 'HARD_SURFACED'::character varying, 'UNSURFACED'::character varying, 'WOODEN'::character varying, 'METAL'::character varying, 'GLASS_REINFORCED_PLASTIC'::character varying, 'PAINTED'::character varying, 'FRAMEWORK'::character varying, 'LATTICED'::character varying, 'GLASS'::character varying, 'FIBERGLASS'::character varying, 'PLASTIC'::character varying])::text[])))
);


--
-- Name: silo_tank_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.silo_tank_statuses (
    silo_tank_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT silo_tank_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: subscription_request; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subscription_request (
    push_all boolean,
    created_at timestamp(6) with time zone,
    subscription_period_end timestamp(6) with time zone,
    subscription_period_start timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    data_reference uuid,
    uuid uuid NOT NULL,
    callback_endpoint character varying(255),
    client_mrn character varying(255),
    container_type character varying(255),
    data_product_type character varying(255),
    product_version character varying(255),
    unlocode character varying(255),
    geometry public.geometry,
    subscription_geometry public.geometry,
    CONSTRAINT subscription_request_container_type_check CHECK (((container_type)::text = ANY ((ARRAY['S100_DataSet'::character varying, 'S100_ExchangeSet'::character varying, 'NONE'::character varying])::text[]))),
    CONSTRAINT subscription_request_data_product_type_check CHECK (((data_product_type)::text = ANY ((ARRAY['OTHER'::character varying, 'S57'::character varying, 'S101'::character varying, 'S102'::character varying, 'S104'::character varying, 'S111'::character varying, 'S122'::character varying, 'S123'::character varying, 'S124'::character varying, 'S125'::character varying, 'S126'::character varying, 'S127'::character varying, 'S128'::character varying, 'S129'::character varying, 'S131'::character varying, 'S201'::character varying, 'S210'::character varying, 'S211'::character varying, 'S212'::character varying, 'S401'::character varying, 'S402'::character varying, 'S411'::character varying, 'S412'::character varying, 'S413'::character varying, 'S414'::character varying, 'S421'::character varying, 'RTZ'::character varying, 'EPC'::character varying])::text[])))
);


--
-- Name: swivel_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.swivel_statuses (
    swivel_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT swivel_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: topmark_colour_patterns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.topmark_colour_patterns (
    topmark_id numeric(38,0) NOT NULL,
    colour_patterns character varying(255),
    CONSTRAINT topmark_colour_patterns_colour_patterns_check CHECK (((colour_patterns)::text = ANY ((ARRAY['HORIZONTAL_STRIPES'::character varying, 'VERTICAL_STRIPES'::character varying, 'DIAGONAL_STRIPES'::character varying, 'SQUARED'::character varying, 'STRIPES_DIRECTION_UNKNOWN'::character varying, 'BORDER_STRIPE'::character varying, 'SINGLE_COLOUR'::character varying, 'RECTANGLE'::character varying, 'TRIANGLE'::character varying])::text[])))
);


--
-- Name: topmark_colours; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.topmark_colours (
    topmark_id numeric(38,0) NOT NULL,
    colours character varying(255),
    CONSTRAINT topmark_colours_colours_check CHECK (((colours)::text = ANY ((ARRAY['WHITE'::character varying, 'BLACK'::character varying, 'RED'::character varying, 'GREEN'::character varying, 'BLUE'::character varying, 'YELLOW'::character varying, 'GREY'::character varying, 'BROWN'::character varying, 'AMBER'::character varying, 'VIOLET'::character varying, 'ORANGE'::character varying, 'MAGENTA'::character varying, 'PINK'::character varying, 'GREEN_A'::character varying, 'GREEN_B'::character varying, 'WHITE_TEMPORARY'::character varying, 'RED_TEMPORARY'::character varying, 'YELLOW_TEMPORARY'::character varying, 'GREEN_PREFERRED'::character varying, 'GREEN_TEMPORARY'::character varying])::text[])))
);


--
-- Name: topmark_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.topmark_statuses (
    topmark_id numeric(38,0) NOT NULL,
    statuses character varying(255),
    CONSTRAINT topmark_statuses_statuses_check CHECK (((statuses)::text = ANY ((ARRAY['PERMANENT'::character varying, 'OCCASIONAL'::character varying, 'RECOMMENDED'::character varying, 'NOT_IN_USE'::character varying, 'PERIODIC_INTERMITTENT'::character varying, 'RESERVED'::character varying, 'TEMPORARY'::character varying, 'PRIVATE'::character varying, 'MANDATORY'::character varying, 'EXTINGUISHED'::character varying, 'ILLUMINATED'::character varying, 'HISTORIC'::character varying, 'PUBLIC'::character varying, 'SYNCHRONIZED'::character varying, 'WATCHED'::character varying, 'UNWATCHED'::character varying, 'EXISTENCE_DOUBTFUL'::character varying, 'ON_REQUEST'::character varying, 'DROP_AWAY'::character varying, 'RISING'::character varying, 'INCREASING'::character varying, 'DECREASING'::character varying, 'STRONG'::character varying, 'GOOD'::character varying, 'MODERATELY'::character varying, 'POOR'::character varying, 'BUOYED'::character varying, 'FULLY_OPERATIONAL'::character varying, 'PARTIALLY_OPERATIONAL'::character varying, 'DRIFTING'::character varying, 'BROKEN'::character varying, 'OFFLINE'::character varying, 'DISCONTINUED'::character varying, 'MANUAL_OBSERVATION'::character varying, 'UNKNOWN_STATUS'::character varying, 'CONFIRMED'::character varying, 'CANDIDATE'::character varying, 'UNDER_MODIFICATION'::character varying, 'EXPERIMENTAL'::character varying, 'UNDER_REMOVAL_DELETION'::character varying, 'REMOVED_DELETED'::character varying, 'CANDIDATE_FOR_MODIFICATION'::character varying])::text[])))
);


--
-- Name: aggregation_join_table aggregation_join_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aggregation_join_table
    ADD CONSTRAINT aggregation_join_table_pkey PRIMARY KEY (aggregation_id, aton_id);


--
-- Name: aids_to_navigation aids_to_navigation_bridle_holds_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT aids_to_navigation_bridle_holds_id_key UNIQUE (bridle_holds_id);


--
-- Name: aids_to_navigation aids_to_navigation_counter_weight_holds_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT aids_to_navigation_counter_weight_holds_id_key UNIQUE (counter_weight_holds_id);


--
-- Name: aids_to_navigation aids_to_navigation_id_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT aids_to_navigation_id_code_key UNIQUE (id_code);


--
-- Name: aids_to_navigation aids_to_navigation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT aids_to_navigation_pkey PRIMARY KEY (id);


--
-- Name: aids_to_navigation aids_to_navigation_shackle_to_anchor_connected_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT aids_to_navigation_shackle_to_anchor_connected_id_key UNIQUE (shackle_to_anchor_connected_id);


--
-- Name: aids_to_navigation aids_to_navigation_shackle_to_buoy_connected_to_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT aids_to_navigation_shackle_to_buoy_connected_to_id_key UNIQUE (shackle_to_buoy_connected_to_id);


--
-- Name: aids_to_navigation aids_to_navigation_swivel_holds_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT aids_to_navigation_swivel_holds_id_key UNIQUE (swivel_holds_id);


--
-- Name: association_join_table association_join_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.association_join_table
    ADD CONSTRAINT association_join_table_pkey PRIMARY KEY (association_id, aton_id);


--
-- Name: aton_aggregation aton_aggregation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aton_aggregation
    ADD CONSTRAINT aton_aggregation_pkey PRIMARY KEY (id);


--
-- Name: aton_association aton_association_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aton_association
    ADD CONSTRAINT aton_association_pkey PRIMARY KEY (id);


--
-- Name: broadcast_by_join_table broadcast_by_join_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.broadcast_by_join_table
    ADD CONSTRAINT broadcast_by_join_table_pkey PRIMARY KEY (electronic_aton_id, radio_station_id);


--
-- Name: dangerous_feature_association_join_table dangerous_feature_association_join_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dangerous_feature_association_join_table
    ADD CONSTRAINT dangerous_feature_association_join_table_pkey PRIMARY KEY (association_id, dangerous_feature_id);


--
-- Name: dangerous_feature dangerous_feature_interoperability_identifier_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dangerous_feature
    ADD CONSTRAINT dangerous_feature_interoperability_identifier_key UNIQUE (interoperability_identifier);


--
-- Name: dangerous_feature dangerous_feature_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dangerous_feature
    ADD CONSTRAINT dangerous_feature_pkey PRIMARY KEY (id);


--
-- Name: dataset_content_log dataset_content_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dataset_content_log
    ADD CONSTRAINT dataset_content_log_pkey PRIMARY KEY (id);


--
-- Name: dataset_content dataset_content_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dataset_content
    ADD CONSTRAINT dataset_content_pkey PRIMARY KEY (id);


--
-- Name: information information_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.information
    ADD CONSTRAINT information_pkey PRIMARY KEY (id);


--
-- Name: recommended_track_nav_lines recommended_track_nav_lines_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recommended_track_nav_lines
    ADD CONSTRAINT recommended_track_nav_lines_pkey PRIMARY KEY (navigation_line_id, recommended_track_id);


--
-- Name: s201_dataset_content_xref s201_dataset_content_xref_dataset_content_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201_dataset_content_xref
    ADD CONSTRAINT s201_dataset_content_xref_dataset_content_id_key UNIQUE (dataset_content_id);


--
-- Name: s201_dataset_content_xref s201_dataset_content_xref_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201_dataset_content_xref
    ADD CONSTRAINT s201_dataset_content_xref_pkey PRIMARY KEY (dataset_uuid);


--
-- Name: s201dataset s201dataset_dataset_identification_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201dataset
    ADD CONSTRAINT s201dataset_dataset_identification_id_key UNIQUE (dataset_identification_id);


--
-- Name: s201dataset_identification s201dataset_identification_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201dataset_identification
    ADD CONSTRAINT s201dataset_identification_pkey PRIMARY KEY (id);


--
-- Name: s201dataset s201dataset_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201dataset
    ADD CONSTRAINT s201dataset_pkey PRIMARY KEY (uuid);


--
-- Name: sector_characteristics sector_characteristics_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector_characteristics
    ADD CONSTRAINT sector_characteristics_pkey PRIMARY KEY (id);


--
-- Name: subscription_request subscription_request_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscription_request
    ADD CONSTRAINT subscription_request_pkey PRIMARY KEY (uuid);


--
-- Name: idx4mcuo154o08owisji6m02jnef; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx4mcuo154o08owisji6m02jnef ON public.dataset_content_log USING btree (dataset_type, uuid, operation, sequence_no, generated_at);


--
-- Name: sector_characteristics_signal_sequences fk125ia9gyqihknmpofmg7gkgg6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector_characteristics_signal_sequences
    ADD CONSTRAINT fk125ia9gyqihknmpofmg7gkgg6 FOREIGN KEY (sector_characteristics_id) REFERENCES public.sector_characteristics(id);


--
-- Name: dangerous_feature_association_join_table fk13arsohtig3e7eao48xc9i5sg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dangerous_feature_association_join_table
    ADD CONSTRAINT fk13arsohtig3e7eao48xc9i5sg FOREIGN KEY (association_id) REFERENCES public.aton_association(id);


--
-- Name: aids_to_navigation fk198y14mlyw3feqnuyargwokjw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fk198y14mlyw3feqnuyargwokjw FOREIGN KEY (shackle_to_buoy_connected_to_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: association_join_table fk1crfhe99bfgl9pkx1u2el9t7e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.association_join_table
    ADD CONSTRAINT fk1crfhe99bfgl9pkx1u2el9t7e FOREIGN KEY (association_id) REFERENCES public.aton_association(id);


--
-- Name: dangerous_feature_association_join_table fk1qunx6iguhbt5k45kl1us3y8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dangerous_feature_association_join_table
    ADD CONSTRAINT fk1qunx6iguhbt5k45kl1us3y8 FOREIGN KEY (dangerous_feature_id) REFERENCES public.dangerous_feature(id);


--
-- Name: information fk2aomer28cjm92xcitwvk0ushk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.information
    ADD CONSTRAINT fk2aomer28cjm92xcitwvk0ushk FOREIGN KEY (feature_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_float_nature_of_constructions fk2xvmp44u583s5j7mach3yp98u; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_float_nature_of_constructions
    ADD CONSTRAINT fk2xvmp44u583s5j7mach3yp98u FOREIGN KEY (light_float_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fk339vlwt00mcle6vo0idbgfabf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fk339vlwt00mcle6vo0idbgfabf FOREIGN KEY (swivel_holds_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_air_obstruction_signal_sequences fk3nyf65n00vg2qwdefdhasjjj4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_air_obstruction_signal_sequences
    ADD CONSTRAINT fk3nyf65n00vg2qwdefdhasjjj4 FOREIGN KEY (light_air_obstruction_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: offshore_platform_colours fk3omovbpo3ejssis657eqkh3ft; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.offshore_platform_colours
    ADD CONSTRAINT fk3omovbpo3ejssis657eqkh3ft FOREIGN KEY (offshore_platform_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: offshore_platform_products fk3rmgps2uex35irostwp8o0h8q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.offshore_platform_products
    ADD CONSTRAINT fk3rmgps2uex35irostwp8o0h8q FOREIGN KEY (offshore_platform_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: recommended_track_quality_of_vertical_measurement fk3tlbagu7lnm6hq7yqo261amen; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recommended_track_quality_of_vertical_measurement
    ADD CONSTRAINT fk3tlbagu7lnm6hq7yqo261amen FOREIGN KEY (recommended_track_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: beacon_special_purpose_category_of_special_purpose_marks fk3wbq3tb33ytp70vsjxkeuevn4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.beacon_special_purpose_category_of_special_purpose_marks
    ADD CONSTRAINT fk3wbq3tb33ytp70vsjxkeuevn4 FOREIGN KEY (beacon_special_purpose_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: offshore_platform_statuses fk4a92ructtenbqc6dln1l64qa0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.offshore_platform_statuses
    ADD CONSTRAINT fk4a92ructtenbqc6dln1l64qa0 FOREIGN KEY (offshore_platform_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fk4bmod4aisryaj8ntvphuky0rl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fk4bmod4aisryaj8ntvphuky0rl FOREIGN KEY (buoy_part_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation_feature_names fk4vi0j8yyshp2sxoye2e1m1hqo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation_feature_names
    ADD CONSTRAINT fk4vi0j8yyshp2sxoye2e1m1hqo FOREIGN KEY (aids_to_navigation_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: landmark_nature_of_constructions fk5fsuxp110jqqksdsb6sqhbxy1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.landmark_nature_of_constructions
    ADD CONSTRAINT fk5fsuxp110jqqksdsb6sqhbxy1 FOREIGN KEY (landmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: silo_tank_statuses fk5oflmf4ojxpwcvsjbgv0lrddu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.silo_tank_statuses
    ADD CONSTRAINT fk5oflmf4ojxpwcvsjbgv0lrddu FOREIGN KEY (silo_tank_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_buoy_colour_patterns fk5txne3qukaebfjb2vow00hskp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_buoy_colour_patterns
    ADD CONSTRAINT fk5txne3qukaebfjb2vow00hskp FOREIGN KEY (generic_buoy_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: retro_reflector_colour_patterns fk5uwkfqryybf53twhaexhawx9f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retro_reflector_colour_patterns
    ADD CONSTRAINT fk5uwkfqryybf53twhaexhawx9f FOREIGN KEY (retro_reflector_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_light_statuses fk60svjru8bu9yq5rack6qnlxg5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_light_statuses
    ADD CONSTRAINT fk60svjru8bu9yq5rack6qnlxg5 FOREIGN KEY (generic_light_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_sectored_obscured_sectors fk61xqu8gwdy38lte6uerb9qoc2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_sectored_obscured_sectors
    ADD CONSTRAINT fk61xqu8gwdy38lte6uerb9qoc2 FOREIGN KEY (light_sectored_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: daymark_colour_patterns fk672e8amd17ud2myo2542v68rj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daymark_colour_patterns
    ADD CONSTRAINT fk672e8amd17ud2myo2542v68rj FOREIGN KEY (daymark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_vessel_colour_patterns fk6b35oy77hapi82aqex767k33k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_vessel_colour_patterns
    ADD CONSTRAINT fk6b35oy77hapi82aqex767k33k FOREIGN KEY (light_vessel_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: information fk776erqglr8qg0q7i4b3mvgb34; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.information
    ADD CONSTRAINT fk776erqglr8qg0q7i4b3mvgb34 FOREIGN KEY (fixes_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_buoy_statuses fk83oowdnwv9o8gj32dhkvs97t2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_buoy_statuses
    ADD CONSTRAINT fk83oowdnwv9o8gj32dhkvs97t2 FOREIGN KEY (generic_buoy_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_beacon_colour_patterns fk8c9q44e199mu3yyd2xo8e3tke; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_beacon_colour_patterns
    ADD CONSTRAINT fk8c9q44e199mu3yyd2xo8e3tke FOREIGN KEY (generic_beacon_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: information fk8iqtunielc1wa7mq650mjooyp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.information
    ADD CONSTRAINT fk8iqtunielc1wa7mq650mjooyp FOREIGN KEY (dangerous_feature_id) REFERENCES public.dangerous_feature(id);


--
-- Name: landmark_category_of_landmarks fk8n27xce5ev8842t2sja4lx4sd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.landmark_category_of_landmarks
    ADD CONSTRAINT fk8n27xce5ev8842t2sja4lx4sd FOREIGN KEY (landmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_vessel_statuses fk8x7eiobw1vjaaokxoekrow8db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_vessel_statuses
    ADD CONSTRAINT fk8x7eiobw1vjaaokxoekrow8db FOREIGN KEY (light_vessel_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: sector_characteristics_colours fk8yv7p5rdo7jjue5sdt5861wjx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector_characteristics_colours
    ADD CONSTRAINT fk8yv7p5rdo7jjue5sdt5861wjx FOREIGN KEY (sector_characteristics_id) REFERENCES public.sector_characteristics(id);


--
-- Name: s201_dataset_content_xref fk9j9b0r5frutdrps6f9f96sqwp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201_dataset_content_xref
    ADD CONSTRAINT fk9j9b0r5frutdrps6f9f96sqwp FOREIGN KEY (dataset_uuid) REFERENCES public.s201dataset(uuid);


--
-- Name: buoy_special_purpose_category_of_special_purpose_marks fk9jw2xf1miah3fwe0kychek7pm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.buoy_special_purpose_category_of_special_purpose_marks
    ADD CONSTRAINT fk9jw2xf1miah3fwe0kychek7pm FOREIGN KEY (buoy_special_purpose_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fk9sw77tic14q593lxy5ixikr0i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fk9sw77tic14q593lxy5ixikr0i FOREIGN KEY (shackle_to_swivel_connected_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: daymark_statuses fk9uj205brx918jmmvva7dv1qwe; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daymark_statuses
    ADD CONSTRAINT fk9uj205brx918jmmvva7dv1qwe FOREIGN KEY (daymark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: silo_tank_colour_patterns fkamr0dmcs7cnq6sipg7wj9b8rm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.silo_tank_colour_patterns
    ADD CONSTRAINT fkamr0dmcs7cnq6sipg7wj9b8rm FOREIGN KEY (silo_tank_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_float_colours fkba022nc7q3lj8gymm48xp7v12; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_float_colours
    ADD CONSTRAINT fkba022nc7q3lj8gymm48xp7v12 FOREIGN KEY (light_float_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: radar_transponder_beacon_statuses fkbddvdidj0cosfjt8nau1sik47; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.radar_transponder_beacon_statuses
    ADD CONSTRAINT fkbddvdidj0cosfjt8nau1sik47 FOREIGN KEY (radar_transponder_beacon_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: silo_tank_colours fkbenumyd918txgdetfa4ehdlfj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.silo_tank_colours
    ADD CONSTRAINT fkbenumyd918txgdetfa4ehdlfj FOREIGN KEY (silo_tank_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: navigation_line_statuses fkbyxrjlfbms9c8whwph5engt82; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.navigation_line_statuses
    ADD CONSTRAINT fkbyxrjlfbms9c8whwph5engt82 FOREIGN KEY (navigation_line_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: pile_colour_patterns fkc0kbdg6qr78unrd9fm61g1e2i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pile_colour_patterns
    ADD CONSTRAINT fkc0kbdg6qr78unrd9fm61g1e2i FOREIGN KEY (pile_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: swivel_statuses fkc4ykqbws5uxlhdlcthd2pj228; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.swivel_statuses
    ADD CONSTRAINT fkc4ykqbws5uxlhdlcthd2pj228 FOREIGN KEY (swivel_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_beacon_colours fkcid9qtjlrhjb39v7tcpwh6tfu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_beacon_colours
    ADD CONSTRAINT fkcid9qtjlrhjb39v7tcpwh6tfu FOREIGN KEY (generic_beacon_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: fog_signal_statuses fkd1fov6iuwkqj2rue5aph9lset; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fog_signal_statuses
    ADD CONSTRAINT fkd1fov6iuwkqj2rue5aph9lset FOREIGN KEY (fog_signal_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: broadcast_by_join_table fkd4rfi17wmgtxabphk6qpdwc9o; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.broadcast_by_join_table
    ADD CONSTRAINT fkd4rfi17wmgtxabphk6qpdwc9o FOREIGN KEY (radio_station_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: landmark_colour_patterns fkdlymx2s2ovrw9qgqkf4yr9wio; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.landmark_colour_patterns
    ADD CONSTRAINT fkdlymx2s2ovrw9qgqkf4yr9wio FOREIGN KEY (landmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: landmark_statuses fkdnhsghr8jgyf2ot43vagnyswm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.landmark_statuses
    ADD CONSTRAINT fkdnhsghr8jgyf2ot43vagnyswm FOREIGN KEY (landmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: offshore_platform_nature_of_constructions fkdyg1o4yv2qnokwsanryipmq09; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.offshore_platform_nature_of_constructions
    ADD CONSTRAINT fkdyg1o4yv2qnokwsanryipmq09 FOREIGN KEY (offshore_platform_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: landmark_functions fkdysttmq6d6t9appu8v9t5viin; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.landmark_functions
    ADD CONSTRAINT fkdysttmq6d6t9appu8v9t5viin FOREIGN KEY (landmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: broadcast_by_join_table fkeh2mux2myfjp99fdhj6f0ssma; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.broadcast_by_join_table
    ADD CONSTRAINT fkeh2mux2myfjp99fdhj6f0ssma FOREIGN KEY (electronic_aton_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: sector_characteristics_light_visibilities fkejejdpxpxsmm9o7t0dq80hhqt; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector_characteristics_light_visibilities
    ADD CONSTRAINT fkejejdpxpxsmm9o7t0dq80hhqt FOREIGN KEY (sector_characteristics_id) REFERENCES public.sector_characteristics(id);


--
-- Name: recommended_track_nav_lines fkeni1a0te0u215bn43ltt0cu1r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recommended_track_nav_lines
    ADD CONSTRAINT fkeni1a0te0u215bn43ltt0cu1r FOREIGN KEY (navigation_line_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_air_obstruction_light_visibilities fkfa5fmfsbocisekt0i0ea73555; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_air_obstruction_light_visibilities
    ADD CONSTRAINT fkfa5fmfsbocisekt0i0ea73555 FOREIGN KEY (light_air_obstruction_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: sector_characteristics fkfhy4r7jr448c77cml7ttbpvlx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector_characteristics
    ADD CONSTRAINT fkfhy4r7jr448c77cml7ttbpvlx FOREIGN KEY (sector_of_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: topmark_statuses fkfrgb3vgon7cwt2avbak7p7cfl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topmark_statuses
    ADD CONSTRAINT fkfrgb3vgon7cwt2avbak7p7cfl FOREIGN KEY (topmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aggregation_join_table fkgi1orxdge89o874tr8uqv3g2m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aggregation_join_table
    ADD CONSTRAINT fkgi1orxdge89o874tr8uqv3g2m FOREIGN KEY (aton_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_buoy_nature_of_constructions fkglgghue4fi0uxce8vj6mrwsne; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_buoy_nature_of_constructions
    ADD CONSTRAINT fkglgghue4fi0uxce8vj6mrwsne FOREIGN KEY (generic_buoy_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fkgrxkxhkg6aq4buli06e88otx3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fkgrxkxhkg6aq4buli06e88otx3 FOREIGN KEY (cable_holds_swivel_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: topmark_colour_patterns fkh78djj7ilv86539msxim0jkjl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topmark_colour_patterns
    ADD CONSTRAINT fkh78djj7ilv86539msxim0jkjl FOREIGN KEY (topmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fkheeijso9l549n9ww5jdo1mqgg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fkheeijso9l549n9ww5jdo1mqgg FOREIGN KEY (cable_holds_bridle_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_beacon_nature_of_constructions fkhtwo4tjm2umvxk5g35odb1w5b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_beacon_nature_of_constructions
    ADD CONSTRAINT fkhtwo4tjm2umvxk5g35odb1w5b FOREIGN KEY (generic_beacon_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: power_source_statuses fki2x0jiu2e09bu4el1x4x8djr9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_source_statuses
    ADD CONSTRAINT fki2x0jiu2e09bu4el1x4x8djr9 FOREIGN KEY (power_source_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: electronic_aton_statuses fkihkpr0gq8u5n1v2riwh9it6ho; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.electronic_aton_statuses
    ADD CONSTRAINT fkihkpr0gq8u5n1v2riwh9it6ho FOREIGN KEY (electronic_aton_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fkiyocj77royw712uohyecc3ta2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fkiyocj77royw712uohyecc3ta2 FOREIGN KEY (counter_weight_holds_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: pile_colours fkj16km8aaau28qdgw8gnipkfvw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pile_colours
    ADD CONSTRAINT fkj16km8aaau28qdgw8gnipkfvw FOREIGN KEY (pile_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aggregation_join_table fkjsxslhtqlw1q3nl0tjwph6dfd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aggregation_join_table
    ADD CONSTRAINT fkjsxslhtqlw1q3nl0tjwph6dfd FOREIGN KEY (aggregation_id) REFERENCES public.aton_aggregation(id);


--
-- Name: light_all_around_category_of_lights fkjujv4co0fs6qwojgsbfayhuci; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_all_around_category_of_lights
    ADD CONSTRAINT fkjujv4co0fs6qwojgsbfayhuci FOREIGN KEY (light_all_around_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_float_statuses fkk9q8q6seqe0drxjth225y9t0s; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_float_statuses
    ADD CONSTRAINT fkk9q8q6seqe0drxjth225y9t0s FOREIGN KEY (light_float_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fkker0dbq866yiek6hfe7g6ck8x; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fkker0dbq866yiek6hfe7g6ck8x FOREIGN KEY (bridle_holds_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation_seasonal_action_requireds fkkspjecmrl39rkqcr4phh8vbeu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation_seasonal_action_requireds
    ADD CONSTRAINT fkkspjecmrl39rkqcr4phh8vbeu FOREIGN KEY (aids_to_navigation_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fkl4aphybohji5bxrkpu2evte79; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fkl4aphybohji5bxrkpu2evte79 FOREIGN KEY (parent_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: daymark_nature_of_constructions fklexo016yqlakrcdxj5eh3xpuu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daymark_nature_of_constructions
    ADD CONSTRAINT fklexo016yqlakrcdxj5eh3xpuu FOREIGN KEY (daymark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: s201dataset_identification_dataset_topic_categories fklo1rb7bjutii5rm3rg68klfso; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201dataset_identification_dataset_topic_categories
    ADD CONSTRAINT fklo1rb7bjutii5rm3rg68klfso FOREIGN KEY (s201dataset_identification_id) REFERENCES public.s201dataset_identification(id);


--
-- Name: retro_reflector_colours fklsj15gkc4u2mdwms7utewcv9c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retro_reflector_colours
    ADD CONSTRAINT fklsj15gkc4u2mdwms7utewcv9c FOREIGN KEY (retro_reflector_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: recommended_track_nav_lines fklu033whce7cmk4enhusmkuign; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recommended_track_nav_lines
    ADD CONSTRAINT fklu033whce7cmk4enhusmkuign FOREIGN KEY (recommended_track_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_sectored_colours fkm1g7hk4g8mrcd9fae3lp2seew; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_sectored_colours
    ADD CONSTRAINT fkm1g7hk4g8mrcd9fae3lp2seew FOREIGN KEY (light_sectored_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_float_colour_patterns fkm6yh2wjl4sx9v1bf17o90a5x0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_float_colour_patterns
    ADD CONSTRAINT fkm6yh2wjl4sx9v1bf17o90a5x0 FOREIGN KEY (light_float_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: s201_dataset_content_xref fkmn60fyowkvc248qp9yvowwu49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201_dataset_content_xref
    ADD CONSTRAINT fkmn60fyowkvc248qp9yvowwu49 FOREIGN KEY (dataset_content_id) REFERENCES public.dataset_content(id);


--
-- Name: offshore_platform_colour_patterns fkmpg445gugc7hk2l3vcrj6v22p; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.offshore_platform_colour_patterns
    ADD CONSTRAINT fkmpg445gugc7hk2l3vcrj6v22p FOREIGN KEY (offshore_platform_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_air_obstruction_exhibition_condition_of_lights fkmsr9gxxdp383o5m0o2lrsex26; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_air_obstruction_exhibition_condition_of_lights
    ADD CONSTRAINT fkmsr9gxxdp383o5m0o2lrsex26 FOREIGN KEY (light_air_obstruction_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: recommended_track_statuses fkn0lmw8trccihu8avhljnditfy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recommended_track_statuses
    ADD CONSTRAINT fkn0lmw8trccihu8avhljnditfy FOREIGN KEY (recommended_track_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: s201dataset fknaigvoqgxkei9dsw4tffp5iwk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.s201dataset
    ADD CONSTRAINT fknaigvoqgxkei9dsw4tffp5iwk FOREIGN KEY (dataset_identification_id) REFERENCES public.s201dataset_identification(id);


--
-- Name: aids_to_navigation fkntgajymfjuc4nlwvopiwofsrl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fkntgajymfjuc4nlwvopiwofsrl FOREIGN KEY (shackle_to_cable_connected_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: topmark_colours fko38bvwbecuavymunepg8b83ca; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topmark_colours
    ADD CONSTRAINT fko38bvwbecuavymunepg8b83ca FOREIGN KEY (topmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: equipment_remote_monitoring_systems fkof6rshd86e3cld5lr1bpay0iu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.equipment_remote_monitoring_systems
    ADD CONSTRAINT fkof6rshd86e3cld5lr1bpay0iu FOREIGN KEY (equipment_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: daymark_colours fkolv2h9xncd5r5wi34iduf5bmg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daymark_colours
    ADD CONSTRAINT fkolv2h9xncd5r5wi34iduf5bmg FOREIGN KEY (daymark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: landmark_colours fkp5xq9ykqfhqelsd6vqybexxi8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.landmark_colours
    ADD CONSTRAINT fkp5xq9ykqfhqelsd6vqybexxi8 FOREIGN KEY (landmark_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: offshore_platform_category_of_offshore_platforms fkpbhlf959xo5lg7xsaftlxykm4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.offshore_platform_category_of_offshore_platforms
    ADD CONSTRAINT fkpbhlf959xo5lg7xsaftlxykm4 FOREIGN KEY (offshore_platform_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_sectored_category_of_lights fkpf34ssy51opbpbp9gen7mwhyb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_sectored_category_of_lights
    ADD CONSTRAINT fkpf34ssy51opbpbp9gen7mwhyb FOREIGN KEY (light_sectored_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: association_join_table fkptw72of63ump5specjkwfoigi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.association_join_table
    ADD CONSTRAINT fkptw72of63ump5specjkwfoigi FOREIGN KEY (aton_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: cable_submarine_statuses fkpvrq9etfrpsbla6bi9vr4nf06; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cable_submarine_statuses
    ADD CONSTRAINT fkpvrq9etfrpsbla6bi9vr4nf06 FOREIGN KEY (cable_submarine_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_all_around_signal_sequences fkq6n69tgsq5r2sld8pj6jcmxs9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_all_around_signal_sequences
    ADD CONSTRAINT fkq6n69tgsq5r2sld8pj6jcmxs9 FOREIGN KEY (light_all_around_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_buoy_colours fkqlayplmfv7p4rubc3r7vx6geg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_buoy_colours
    ADD CONSTRAINT fkqlayplmfv7p4rubc3r7vx6geg FOREIGN KEY (generic_buoy_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_all_around_colours fkqm25fnmovjvc5rkl9aonm6fn8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_all_around_colours
    ADD CONSTRAINT fkqm25fnmovjvc5rkl9aonm6fn8 FOREIGN KEY (light_all_around_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: recommended_track_technique_of_vertical_measurements fkqvh9qmt43ht3na3e8df02fs5m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recommended_track_technique_of_vertical_measurements
    ADD CONSTRAINT fkqvh9qmt43ht3na3e8df02fs5m FOREIGN KEY (recommended_track_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: generic_beacon_statuses fkrs59lewrie8cuivokrykq1sxm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.generic_beacon_statuses
    ADD CONSTRAINT fkrs59lewrie8cuivokrykq1sxm FOREIGN KEY (generic_beacon_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: silo_tank_nature_of_constructions fks8beivg3tnqciji0y1fqwp77i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.silo_tank_nature_of_constructions
    ADD CONSTRAINT fks8beivg3tnqciji0y1fqwp77i FOREIGN KEY (silo_tank_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_vessel_colours fksb65mo973oqic4bk110898ktd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_vessel_colours
    ADD CONSTRAINT fksb65mo973oqic4bk110898ktd FOREIGN KEY (light_vessel_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: sector_characteristics_sector_informations fkshasq7x39luvdbi6tx6bj0077; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector_characteristics_sector_informations
    ADD CONSTRAINT fkshasq7x39luvdbi6tx6bj0077 FOREIGN KEY (sector_characteristics_id) REFERENCES public.sector_characteristics(id);


--
-- Name: light_fog_detector_signal_sequences fkshxsafm4h0mydj0vs3sfb1iu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_fog_detector_signal_sequences
    ADD CONSTRAINT fkshxsafm4h0mydj0vs3sfb1iu FOREIGN KEY (light_fog_detector_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: radar_reflector_statuses fksnim7v1rd2333g21ybcpt7chv; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.radar_reflector_statuses
    ADD CONSTRAINT fksnim7v1rd2333g21ybcpt7chv FOREIGN KEY (radar_reflector_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: information fksspdlpkta0dxmnmq4wpiu97ht; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.information
    ADD CONSTRAINT fksspdlpkta0dxmnmq4wpiu97ht FOREIGN KEY (positions_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: light_vessel_nature_of_constructions fksw1yugjjvc3121lw576nlgin6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.light_vessel_nature_of_constructions
    ADD CONSTRAINT fksw1yugjjvc3121lw576nlgin6 FOREIGN KEY (light_vessel_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: aids_to_navigation fkt9u5svpcyu5xj3gn1v6fehtdk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aids_to_navigation
    ADD CONSTRAINT fkt9u5svpcyu5xj3gn1v6fehtdk FOREIGN KEY (shackle_to_anchor_connected_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: retro_reflector_statuses fkteix1uk4u4ybcq2q1uhxdpa1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retro_reflector_statuses
    ADD CONSTRAINT fkteix1uk4u4ybcq2q1uhxdpa1 FOREIGN KEY (retro_reflector_id) REFERENCES public.aids_to_navigation(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: -
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--
