/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.*;
import _int.iho.s_201.gml.cs0._2.impl.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

/**
 * The S-201 Mapping enum.
 *
 * This enum is used as a map between the S-201 Aids to Navigation objects
 * and the local classes used for persistence. By mapping the classes in this
 * way, we can limit the amount of use required to manipulate the parsed XML
 * instance entries.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public enum S201AtonTypes {
    CARDINAL_BEACON("Cardinal Beacon", CardinalBeaconImpl.class,  BeaconCardinal.class),
    LATERAL_BEACON("Lateral Beacon", LateralBeaconImpl.class, BeaconLateral.class),
    ISOLATED_DANGER_BEACON("Isolated Danger Beacon", IsolatedDangerBeaconImpl.class, BeaconIsolatedDanger.class),
    SAFE_WATER_BEACON("Safe Water Beacon", SafeWaterBeaconImpl.class, BeaconSafeWater.class),
    SPECIAL_PURPOSE_BEACON("Special Purpose Beacon", SpecialPurposeGeneralBeaconImpl.class, BeaconSpecialPurpose.class),
    CARDINAL_BUOY("Cardinal Buoy", CardinalBuoyImpl.class, BuoyCardinal.class),
    LATERAL_BUOY("Lateral Buoy", LateralBuoyImpl.class, BuoyLateral.class),
    INSTALLATION_BUOY("Installation Buoy", InstallationBuoyImpl.class, BuoyInstallation.class),
    ISOLATED_DANGER_BUOY("Isolated Danger Buoy", IsolatedDangerBuoyImpl.class, BuoyIsolatedDanger.class),
    SAFE_WATER_BUOY("Safe Water Buoy", SafeWaterBuoyImpl.class, BuoySafeWater.class),
    SPECIAL_PURPOSE_BUOY("Special Purpose Buoy", SpecialPurposeGeneralBuoyImpl.class, BuoySpecialPurpose.class),
    EMERGENCY_WRECK_MARKING_BUOY("Emergency Wreck Marking Buoy", EmergencyWreckMarkingBuoyImpl.class, BuoyEmergencyWreckMarking.class),
    DAYMARK("Daymark", DaymarkImpl.class, Daymark.class),
    BUILDING("Building", BuildingImpl.class, Building.class),
    BRIDGE("Bridge", BridgeImpl.class, Bridge.class),
    ENVIRONMENT_OBSERVATION_EQUIPMENT("Environment Observation Equipment", EnvironmentObservationEquipmentImpl.class, EnvironmentObservationEquipment.class),
    FOG_SIGNAL("Fog Signal", FogSignalImpl.class, FogSignal.class),
    LIGHT_ALL_AROUND("Light All Around", LightAllAroundImpl.class, LightAllAround.class),
    LIGHT_AIR_OBSTRUCTION("Light All Around", LightAirObstructionImpl.class, LightAirObstruction.class),
    LIGHT_FOG_DETECTOR("Light Fog Detector", LightFogDetectorImpl.class, LightFogDetector.class),
    LIGHT_SECTORED("Light Sectored", LightSectoredImpl.class, LightSectored.class),
    LIGHT_FLOAT("Light Float", LightFloatImpl.class, LightFloat.class),
    LANDMARK("Cardinal Beacon", LandmarkTypeImpl.class, Landmark.class),
    LIGHTHOUSE("Lighthouse", LighthouseImpl.class, Lighthouse.class),
    LIGHT_VESSEL("Light Vessel", LightVesselImpl.class, LightVessel.class),
    NAVIGATION_LINE("Navigation Line", NavigationLineImpl.class, NavigationLine.class),
    OFFSHORE_PLATFORM("Offshore Platform", OffshorePlatformImpl.class, OffshorePlatform.class),
    PHYSICAL_AIS_ATON("Physical AIS AtoN", PhysicalAISAidToNavigationImpl.class, PhysicalAISAidToNavigation.class),
    PILE("Pile", PileImpl.class, Pile.class),
    POWER_SOURCE("Power Source", PowerSourceImpl.class, PowerSource.class),
    RADAR_REFLECTOR("Radar Reflector", RadarReflectorImpl.class, RadarReflector.class),
    RADAR_TRANSPONDER_BEACON("Radar Transponder Beacon", RadarTransponderBeaconImpl.class, RadarTransponderBeacon.class),
    RADIO_STATION("Radio Station", RadioStationImpl.class, RadioStation.class),
    RECOMMENDED_TRACK("Recommended Track", RecommendedTrackImpl.class, RecommendedTrack.class),
    RETRO_REFLECTOR("Retro Reflector", RetroreflectorImpl.class, RetroReflector.class),
    SILO_TANK("Silo Tank", SiloTankImpl.class, SiloTank.class),
    SYNTHETIC_AIS_ATON("Virtual AtoN", SyntheticAISAidToNavigationImpl.class, SyntheticAISAidToNavigation.class),
    TOPMARK("Topmark", TopmarkImpl.class, Topmark.class),
    VIRTUAL_AIS_ATON("Virtual AtoN", VirtualAISAidToNavigationImpl.class, VirtualAISAidToNavigation.class),
    MOORING_SHACKLE("Mooring Shackle", MooringShackleImpl.class, MooringShackle.class),
    BRIDLE("Bridle", BridleImpl.class, Bridle.class),
    SWIVEL("Swivel", SwivelImpl.class, Swivel.class),
    SINKER_ANCHOR("Sinker Anchor", SinkerAnchorImpl.class, SinkerAnchor.class),
    CABLE_SUBMARINE("Cable Submarine", CableSubmarineImpl.class, CableSubmarine.class),
    UNKNOWN("Unknown", AidsToNavigationTypeImpl.class, AidsToNavigation.class);

    // Enum Variables
    final Class<? extends AidsToNavigationTypeImpl> s201Class;
    final Class<? extends AidsToNavigation> localClass;
    final String description;

    /**
     * The S-201 AtoN Types Enum Constructor.
     *
     * @param description   The description of the AtoN type
     * @param s201Class     The S-201 Class to be mapped
     * @param localClass    The respective local persistence class
     */
    S201AtonTypes(String description, Class<? extends AidsToNavigationTypeImpl> s201Class, Class<? extends AidsToNavigation> localClass) {
        this.description = description;
        this.s201Class = s201Class;
        this.localClass = localClass;
    }

    /**
     * Gets s 201 class.
     *
     * @return the s 201 class
     */
    public Class<? extends AidsToNavigationTypeImpl> getS201Class() {
        return s201Class;
    }

    /**
     * Gets local equipment class.
     *
     * @return the local equipment class
     */
    @SuppressWarnings("unchecked")
    public Class<? extends EquipmentTypeImpl> getS201EquipmentClass() {
        return (Class<? extends EquipmentTypeImpl>) Optional.of(this)
                .filter(S201AtonTypes::isEquipment)
                .map(S201AtonTypes::getS201Class)
                .orElse(null);
    }

    /**
     * Gets local structure class.
     *
     * @return the local structure class
     */
    @SuppressWarnings("unchecked")
    public Class<? extends StructureObjectTypeImpl> getS201StructureClass() {
        return (Class<? extends StructureObjectTypeImpl>) Optional.of(this)
                .filter(S201AtonTypes::isStructure)
                .map(S201AtonTypes::getS201Class)
                .orElse(null);
    }

    /**
     * Gets local class.
     *
     * @return the local class
     */
    public Class<? extends AidsToNavigation> getLocalClass() {
        return localClass;
    }

    /**
     * Gets local equipment class.
     *
     * @return the local equipment class
     */
    @SuppressWarnings("unchecked")
    public Class<? extends Equipment> getLocalEquipmentClass() {
        return (Class<? extends Equipment>) Optional.of(this)
                .filter(S201AtonTypes::isEquipment)
                .map(S201AtonTypes::getLocalClass)
                .orElse(null);
    }

    /**
     * Gets local structure class.
     *
     * @return the local structure class
     */
    @SuppressWarnings("unchecked")
    public Class<? extends StructureObject> getLocalStructureClass() {
        return (Class<? extends StructureObject>) Optional.of(this)
                .filter(S201AtonTypes::isStructure)
                .map(S201AtonTypes::getLocalClass)
                .orElse(null);
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Is structure boolean.
     *
     * @return the boolean
     */
    public boolean isStructure() {
        return StructureObjectType.class.isAssignableFrom(this.s201Class);
    }

    /**
     * Is equipment boolean.
     *
     * @return the boolean
     */
    public boolean isEquipment() {
        return EquipmentType.class.isAssignableFrom(this.s201Class);
    }

    /**
     * Find the enum entry that corresponds to the provided S-201 class type.
     *
     * @param s201Class     The S-201 class type
     * @return The respective S-201 AtoN Type enum entry
     */
    public static <T extends AidsToNavigationType> S201AtonTypes fromS201Class(Class<T> s201Class) {
        return Arrays.stream(S201AtonTypes.values())
                .filter(t -> t.getS201Class().equals(s201Class))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /**
     * Find the enum entry that corresponds to the provided local persistence
     * class type.
     *
     * @param localClass    The local persistence class type
     * @return The respective S-201 AtoN Type enum entry
     */
    public static <T extends AidsToNavigation> S201AtonTypes fromLocalClass(Class<T> localClass) {
        return Arrays.stream(S201AtonTypes.values())
                .filter(t -> t.getLocalClass().equals(localClass))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /**
     * Returns the type of the geometry that the S-201 type supports.
     *
     * @return the type of the geometry that the S-201 type supports
     * @throws NoSuchFieldException
     */
    public Field getS201GeometryField() throws NoSuchFieldException {
        Class current = this.getS201Class();
        // Look for the geometry field and iterate to the super class if necessary
        while(!Arrays.stream(current.getDeclaredFields()).map(Field::getName).anyMatch("geometry"::equals) && current.getSuperclass() != null) {
            current = current.getSuperclass();
        }
        // Once found (or not) return the type of the geometry
        return current.getDeclaredField("geometry");
    }
}
