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

package org.grad.eNav.atonAdminService.utils;

import _int.iho.s_201.gml.cs0._2.ColourType;
import _int.iho.s_201.gml.cs0._2.LightCharacteristicType;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.models.dtos.s201.AtonAttributeGroupDto;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The AtoN Attribute Utilities Class.
 * <p>
 * The S-201 feature catalogue describes every type of Aid to Navigation with
 * its own set of attributes - a buoy has a shape and a colour pattern, a light
 * has a characteristic and a range, a racon has a Morse signal group - so a
 * single fixed list of fields can never describe them all. This class walks
 * the type hierarchy of an Aid to Navigation and collects whatever is worth
 * telling a mariner about it, grouped and already formatted for display.
 * <p>
 * Note that in S-201 the signal of a structure is modelled as a separate
 * equipment feature hanging off it, so the lights, fog signals and topmarks
 * carried by a lighthouse or a buoy are picked up as groups of their own.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class AtonAttributeUtils {

    /**
     * The customary abbreviations of the light characteristics, as they appear
     * on a nautical chart. Anything not listed here falls back onto the full
     * description of the characteristic.
     */
    private static final Map<LightCharacteristicType, String> LIGHT_CHARACTERS = Map.ofEntries(
            Map.entry(LightCharacteristicType.FIXED, "F"),
            Map.entry(LightCharacteristicType.FLASHING, "Fl"),
            Map.entry(LightCharacteristicType.LONG_FLASHING, "LFl"),
            Map.entry(LightCharacteristicType.QUICK_FLASHING, "Q"),
            Map.entry(LightCharacteristicType.VERY_QUICK_FLASHING, "VQ"),
            Map.entry(LightCharacteristicType.CONTINUOUS_ULTRA_QUICK_FLASHING, "UQ"),
            Map.entry(LightCharacteristicType.ISOPHASED, "Iso"),
            Map.entry(LightCharacteristicType.OCCULTING, "Oc"),
            Map.entry(LightCharacteristicType.MORSE, "Mo"),
            Map.entry(LightCharacteristicType.FIXED_AND_FLASH, "F Fl"),
            Map.entry(LightCharacteristicType.FLASH_AND_LONG_FLASH, "Fl+LFl"),
            Map.entry(LightCharacteristicType.OCCULTING_AND_FLASH, "Oc+Fl"),
            Map.entry(LightCharacteristicType.FIXED_AND_LONG_FLASH, "F+LFl"),
            Map.entry(LightCharacteristicType.OCCULTING_ALTERNATING, "Al Oc"),
            Map.entry(LightCharacteristicType.LONG_FLASH_ALTERNATING, "Al LFl"),
            Map.entry(LightCharacteristicType.FLASH_ALTERNATING, "Al Fl"),
            Map.entry(LightCharacteristicType.GROUP_ALTERNATING, "Al Gr"),
            Map.entry(LightCharacteristicType.QUICK_FLASH_PLUS_LONG_FLASH, "Q+LFl"),
            Map.entry(LightCharacteristicType.VERY_QUICK_FLASH_PLUS_LONG_FLASH, "VQ+LFl"),
            Map.entry(LightCharacteristicType.ULTRA_QUICK_FLASH_PLUS_LONG_FLASH, "UQ+LFl"),
            Map.entry(LightCharacteristicType.ALTERNATING, "Al"),
            Map.entry(LightCharacteristicType.FIXED_AND_ALTERNATING_FLASHING, "F Al Fl"),
            Map.entry(LightCharacteristicType.GROUP_OCCULTING_LIGHT, "Oc"),
            Map.entry(LightCharacteristicType.COMPOSITE_GROUP_OCCULTING_LIGHT, "Oc"),
            Map.entry(LightCharacteristicType.GROUP_FLASHING_LIGHT, "Fl"),
            Map.entry(LightCharacteristicType.COMPOSITE_GROUP_FLASHING_LIGHT, "Fl"),
            Map.entry(LightCharacteristicType.GROUP_QUICK_LIGHT, "Q"),
            Map.entry(LightCharacteristicType.GROUP_VERY_QUICK_LIGHT, "VQ")
    );

    /**
     * The customary abbreviations of the light colours. Only the colours a
     * light can actually show are listed, the rest are spelled out in full.
     */
    private static final Map<ColourType, String> LIGHT_COLOURS = Map.ofEntries(
            Map.entry(ColourType.WHITE, "W"),
            Map.entry(ColourType.RED, "R"),
            Map.entry(ColourType.GREEN, "G"),
            Map.entry(ColourType.BLUE, "Bu"),
            Map.entry(ColourType.YELLOW, "Y"),
            Map.entry(ColourType.AMBER, "Am"),
            Map.entry(ColourType.VIOLET, "Vi"),
            Map.entry(ColourType.ORANGE, "Or"),
            Map.entry(ColourType.MAGENTA, "Mag")
    );

    /**
     * The S-201 enumerations are generated out of the feature catalogue and
     * carry their human readable label in a "value" accessor. The lookup of
     * that accessor is cached per enumeration class, since the same handful of
     * enumerations is hit over and over again.
     */
    private static final Map<Class<?>, Optional<Method>> ENUM_LABELS = new ConcurrentHashMap<>();

    /**
     * The degree sign, spelled out so that the encoding of this file never
     * becomes part of the equation.
     */
    private static final String DEGREES = "\u00b0";

    /**
     * Describes an Aid to Navigation through the attributes that are specific
     * to its own type, along with the equipment it carries and the structure
     * that carries it.
     *
     * @param aidsToNavigation the Aid to Navigation to be described
     * @return the groups of attributes describing it, in presentation order
     */
    public static List<AtonAttributeGroupDto> attributesOf(AidsToNavigation aidsToNavigation) {
        final List<AtonAttributeGroupDto> groups = new ArrayList<>();
        if (aidsToNavigation == null) {
            return groups;
        }
        addTypeAttributes(groups, aidsToNavigation);
        addStructureAttributes(groups, aidsToNavigation);
        addEquipmentAttributes(groups, aidsToNavigation);
        addCarriedEquipment(groups, aidsToNavigation);
        return groups;
    }

    /**
     * Collects the attributes that only exist on the specific type of the
     * provided Aid to Navigation.
     *
     * @param groups the groups collected so far
     * @param aidsToNavigation the Aid to Navigation to be described
     */
    private static void addTypeAttributes(List<AtonAttributeGroupDto> groups, AidsToNavigation aidsToNavigation) {
        switch (aidsToNavigation) {
            case GenericBuoy buoy -> group(groups, "Buoy", attributes -> {
                put(attributes, "Category", markCategory(buoy));
                put(attributes, "Shape", describe(buoy.getBuoyShape()));
                put(attributes, "Colour", describeAll(buoy.getColours()));
                put(attributes, "Colour pattern", describeAll(buoy.getColourPatterns()));
                put(attributes, "Type of buoy", buoy.getTypeOfBuoy());
                put(attributes, "Construction", describeAll(buoy.getNatureOfConstructions()));
                put(attributes, "Marks system", describe(buoy.getMarksNavigationalSystemOf()));
                put(attributes, "Vertical length", metres(buoy.getVerticalLength()));
                put(attributes, "Radar conspicuous", flag(buoy.getRadarConspicuous()));
                put(attributes, "Status", describeAll(buoy.getStatuses()));
            });
            case GenericBeacon beacon -> group(groups, "Beacon", attributes -> {
                put(attributes, "Category", markCategory(beacon));
                put(attributes, "Shape", describe(beacon.getBeaconShape()));
                put(attributes, "Colour", describeAll(beacon.getColours()));
                put(attributes, "Colour pattern", describeAll(beacon.getColourPatterns()));
                put(attributes, "Construction", describeAll(beacon.getNatureOfConstructions()));
                put(attributes, "Marks system", describe(beacon.getMarksNavigationalSystemOf()));
                put(attributes, "Height", metres(beacon.getHeight()));
                put(attributes, "Elevation", metres(beacon.getElevation()));
                put(attributes, "Visual prominence", describe(beacon.getVisualProminence()));
                put(attributes, "Radar conspicuous", flag(beacon.getRadarConspicuous()));
                put(attributes, "Status", describeAll(beacon.getStatuses()));
            });
            case Landmark landmark -> group(groups, landmark instanceof Lighthouse ? "Lighthouse" : "Landmark", attributes -> {
                put(attributes, "Category", describeAll(landmark.getCategoryOfLandmarks()));
                put(attributes, "Function", describeAll(landmark.getFunctions()));
                put(attributes, "Colour", describeAll(landmark.getColours()));
                put(attributes, "Colour pattern", describeAll(landmark.getColourPatterns()));
                put(attributes, "Construction", describeAll(landmark.getNatureOfConstructions()));
                put(attributes, "Height", metres(landmark.getHeight()));
                put(attributes, "Elevation", metres(landmark.getElevation()));
                put(attributes, "Visual prominence", describe(landmark.getVisualProminence()));
                put(attributes, "Radar conspicuous", flag(landmark.getRadarConspicuous()));
                put(attributes, "Manned", flag(landmark.getMannedStructure()));
                put(attributes, "Status", describeAll(landmark.getStatuses()));
            });
            case LightFloat lightFloat -> group(groups, "Light float", attributes -> {
                put(attributes, "Colour", describeAll(lightFloat.getColours()));
                put(attributes, "Colour pattern", describeAll(lightFloat.getColourPatterns()));
                put(attributes, "Construction", describeAll(lightFloat.getNatureOfConstructions()));
                put(attributes, "Size", dimensions(lightFloat.getHorizontalLength(), lightFloat.getHorizontalWidth()));
                put(attributes, "Vertical length", metres(lightFloat.getVerticalLength()));
                put(attributes, "Visual prominence", describe(lightFloat.getVisualProminence()));
                put(attributes, "Radar conspicuous", flag(lightFloat.getRadarConspicuous()));
                put(attributes, "Manned", flag(lightFloat.getMannedStructure()));
                put(attributes, "Status", describeAll(lightFloat.getStatuses()));
            });
            case LightVessel lightVessel -> group(groups, "Light vessel", attributes -> {
                put(attributes, "Colour", describeAll(lightVessel.getColours()));
                put(attributes, "Colour pattern", describeAll(lightVessel.getColourPatterns()));
                put(attributes, "Construction", describeAll(lightVessel.getNatureOfConstructions()));
                put(attributes, "Size", dimensions(lightVessel.getHorizontalLength(), lightVessel.getHorizontalWidth()));
                put(attributes, "Vertical length", metres(lightVessel.getVerticalLength()));
                put(attributes, "Visual prominence", describe(lightVessel.getVisualProminence()));
                put(attributes, "Radar conspicuous", flag(lightVessel.getRadarConspicuous()));
                put(attributes, "Manned", flag(lightVessel.getMannedStructure()));
                put(attributes, "Status", describeAll(lightVessel.getStatuses()));
            });
            case OffshorePlatform platform -> group(groups, "Platform", attributes -> {
                put(attributes, "Category", describeAll(platform.getCategoryOfOffshorePlatforms()));
                put(attributes, "Product", describeAll(platform.getProducts()));
                put(attributes, "Colour", describeAll(platform.getColours()));
                put(attributes, "Colour pattern", describeAll(platform.getColourPatterns()));
                put(attributes, "Construction", describeAll(platform.getNatureOfConstructions()));
                put(attributes, "Height", metres(platform.getHeight()));
                put(attributes, "Visual prominence", describe(platform.getVisualProminence()));
                put(attributes, "Radar conspicuous", flag(platform.getRadarConspicuous()));
                put(attributes, "Manned", flag(platform.getMannedStructure()));
                put(attributes, "Status", describeAll(platform.getStatuses()));
            });
            case SiloTank siloTank -> group(groups, "Silo or tank", attributes -> {
                put(attributes, "Category", describe(siloTank.getCategoryOfSiloTank()));
                put(attributes, "Shape", describe(siloTank.getBuildingShape()));
                put(attributes, "Colour", describeAll(siloTank.getColours()));
                put(attributes, "Colour pattern", describeAll(siloTank.getColourPatterns()));
                put(attributes, "Construction", describeAll(siloTank.getNatureOfConstructions()));
                put(attributes, "Height", metres(siloTank.getHeight()));
                put(attributes, "Elevation", metres(siloTank.getElevation()));
                put(attributes, "Visual prominence", describe(siloTank.getVisualProminence()));
                put(attributes, "Radar conspicuous", flag(siloTank.getRadarConspicuous()));
                put(attributes, "Status", describeAll(siloTank.getStatuses()));
            });
            case Pile pile -> group(groups, "Pile", attributes -> {
                put(attributes, "Category", describe(pile.getCategoryOfPile()));
                put(attributes, "Colour", describeAll(pile.getColours()));
                put(attributes, "Colour pattern", describeAll(pile.getColourPatterns()));
                put(attributes, "Height", metres(pile.getHeight()));
                put(attributes, "Vertical length", metres(pile.getVerticalLength()));
                put(attributes, "Visual prominence", describe(pile.getVisualProminence()));
            });
            case GenericLight light -> addLightAttributes(groups, light);
            case FogSignal fogSignal -> group(groups, "Fog signal", attributes -> {
                put(attributes, "Category", describe(fogSignal.getCategoryOfFogSignal()));
                put(attributes, "Group", fogSignal.getSignalGroup());
                put(attributes, "Period", seconds(fogSignal.getSignalPeriod()));
                put(attributes, "Sequence", sequence(fogSignal.getSignalSequences()));
                put(attributes, "Frequency", hertz(fogSignal.getSignalFrequency()));
                put(attributes, "Range", nauticalMiles(fogSignal.getValueOfMaximumRange()));
                put(attributes, "Generation", describe(fogSignal.getSignalGeneration()));
                put(attributes, "Status", describeAll(fogSignal.getStatuses()));
            });
            case Daymark daymark -> group(groups, "Daymark", attributes -> {
                put(attributes, "Shape", daymark.getTopmarkDaymarkShape());
                put(attributes, "Category", describe(daymark.getCategoryOfSpecialPurposeMark()));
                put(attributes, "Colour", describeAll(daymark.getColours()));
                put(attributes, "Colour pattern", describeAll(daymark.getColourPatterns()));
                put(attributes, "Construction", describeAll(daymark.getNatureOfConstructions()));
                put(attributes, "Height", metres(daymark.getHeight()));
                put(attributes, "Elevation", metres(daymark.getElevation()));
                put(attributes, "Orientation", degrees(daymark.getOrientationValue()));
                put(attributes, "Status", describeAll(daymark.getStatuses()));
            });
            case Topmark topmark -> group(groups, "Topmark", attributes -> {
                put(attributes, "Shape", topmark.getTopmarkDaymarkShape());
                put(attributes, "Colour", describeAll(topmark.getColours()));
                put(attributes, "Colour pattern", describeAll(topmark.getColourPatterns()));
                put(attributes, "Vertical length", metres(topmark.getVerticalLength()));
                put(attributes, "Status", describeAll(topmark.getStatuses()));
            });
            case RadarTransponderBeacon racon -> group(groups, "Racon", attributes -> {
                put(attributes, "Category", describe(racon.getCategoryOfRadarTransponderBeaconType()));
                put(attributes, "Morse group", racon.getSignalGroup());
                put(attributes, "Wave length", waveLength(racon.getRadarWaveLength()));
                put(attributes, "Range", nauticalMiles(racon.getValueOfNominalRange()));
                put(attributes, "Sector", sectorLimits(racon.getSectorLimitOne(), racon.getSectorLimitTwo()));
                put(attributes, "Sequence", sequence(racon.getSignalSequences()));
                put(attributes, "Manufacturer", racon.getManufacturer());
                put(attributes, "Status", describeAll(racon.getStatuses()));
            });
            case RadarReflector reflector -> group(groups, "Radar reflector", attributes -> {
                put(attributes, "Height", metres(reflector.getHeight()));
                put(attributes, "Vertical datum", describe(reflector.getVerticalDatum()));
                put(attributes, "Status", describeAll(reflector.getStatuses()));
            });
            case RetroReflector reflector -> group(groups, "Retro reflector", attributes -> {
                put(attributes, "Colour", describeAll(reflector.getColours()));
                put(attributes, "Colour pattern", describeAll(reflector.getColourPatterns()));
                put(attributes, "Marks system", describe(reflector.getMarksNavigationalSystemOf()));
                put(attributes, "Height", metres(reflector.getHeight()));
                put(attributes, "Status", describeAll(reflector.getStatuses()));
            });
            case RadioStation station -> group(groups, "Radio station", attributes -> {
                put(attributes, "Category", describe(station.getCategoryOfRadioStation()));
                put(attributes, "Range", nauticalMiles(station.getEstimatedRangeOfTransmission()));
                put(attributes, "Status", describe(station.getStatus()));
            });
            case EnvironmentObservationEquipment equipment -> group(groups, "Observation equipment", attributes -> {
                put(attributes, "Type", join(equipment.getTypeOfEnvironmentObservationEquipments()));
                put(attributes, "Height", metres(equipment.getHeight()));
                put(attributes, "Status", describeAll(equipment.getStatuses()));
            });
            case PowerSource powerSource -> group(groups, "Power source", attributes -> {
                put(attributes, "Category", describe(powerSource.getCategoryOfPowerSource()));
                put(attributes, "Manufacturer", powerSource.getManufacturer());
                put(attributes, "Status", describeAll(powerSource.getStatuses()));
            });
            case VirtualAISAidToNavigation virtualAton -> group(groups, "AIS transmission", attributes -> {
                put(attributes, "Virtual type", describe(virtualAton.getVirtualAISAidToNavigationType()));
                putElectronic(attributes, virtualAton);
            });
            case SyntheticAISAidToNavigation syntheticAton -> group(groups, "AIS transmission", attributes -> {
                put(attributes, "Category", describe(syntheticAton.getCategoryOfSyntheticAISAidtoNavigation()));
                put(attributes, "Virtual type", describe(syntheticAton.getVirtualAISAidToNavigationType()));
                putElectronic(attributes, syntheticAton);
            });
            case PhysicalAISAidToNavigation physicalAton -> group(groups, "AIS transmission", attributes -> {
                put(attributes, "Category", describe(physicalAton.getCategoryOfPhysicalAISAidToNavigationType()));
                putElectronic(attributes, physicalAton);
            });
            case NavigationLine line -> group(groups, "Navigation line", attributes -> {
                put(attributes, "Category", describe(line.getCategoryOfNavigationLine()));
                put(attributes, "Orientation", degrees(line.getOrientation()));
                put(attributes, "Status", describeAll(line.getStatuses()));
            });
            case RecommendedTrack track -> group(groups, "Recommended track", attributes -> {
                put(attributes, "Orientation", degrees(Optional.ofNullable(track.getOrientation())
                        .map(Orientation::getOrientationValue)
                        .orElse(null)));
                put(attributes, "Traffic flow", describe(track.getTrafficFlow()));
                put(attributes, "Minimum depth", metres(track.getDepthRangeMinimumValue()));
                put(attributes, "Maximum draught", metres(track.getMaximumPermittedDraught()));
                put(attributes, "Vertical datum", describe(track.getVerticalDatum()));
                put(attributes, "Based on fixed marks", flag(track.getBasedOnFixedMarks()));
                put(attributes, "Status", describeAll(track.getStatuses()));
            });
            case CableSubmarine cable -> group(groups, "Submarine cable", attributes -> {
                put(attributes, "Category", describe(cable.getCategoryOfCable()));
                Optional.ofNullable(cable.getCableDimensions()).ifPresent(dimensions -> {
                    put(attributes, "Length", metres(dimensions.getCableLength()));
                    put(attributes, "Diameter", metres(dimensions.getDiameter()));
                });
                put(attributes, "Status", describeAll(cable.getStatuses()));
            });
            default -> {
                // Nothing beyond the attributes that every Aid to Navigation shares
            }
        }
    }

    /**
     * Collects the light attributes, which is where the S-201 model gets
     * interesting. An all round light carries a single rhythm, while a
     * sectored one describes a rhythm, a colour and a range per sector, so the
     * latter gets one additional group for every sector it shows.
     *
     * @param groups the groups collected so far
     * @param light the light to be described
     */
    private static void addLightAttributes(List<AtonAttributeGroupDto> groups, GenericLight light) {
        final RhythmOfLight rhythm = rhythmOf(light);
        final Set<ColourType> colours = lightColours(light);
        final BigDecimal range = nominalRangeOf(light);

        group(groups, lightTitle(light), attributes -> {
            put(attributes, "Character", Optional.ofNullable(rhythm)
                    .map(value -> character(value.getLightCharacteristic(), value.getSignalGroups(), colours, value.getSignalPeriod(), range))
                    .orElse(null));
            put(attributes, "Rhythm", Optional.ofNullable(rhythm)
                    .map(RhythmOfLight::getLightCharacteristic)
                    .map(AtonAttributeUtils::describe)
                    .orElse(null));
            put(attributes, "Colour", describeAll(colours));
            put(attributes, "Period", Optional.ofNullable(rhythm)
                    .map(RhythmOfLight::getSignalPeriod)
                    .map(AtonAttributeUtils::seconds)
                    .orElse(null));
            put(attributes, "Sequence", Optional.ofNullable(rhythm)
                    .map(RhythmOfLight::getSignalSequences)
                    .map(AtonAttributeUtils::sequence)
                    .orElse(null));
            put(attributes, "Range", nauticalMiles(range));
            put(attributes, "Category", describeAll(lightCategories(light)));
            put(attributes, "Height", metres(light.getHeight()));
            put(attributes, "Vertical datum", describe(light.getVerticalDatum()));
            put(attributes, "Peak intensity", candela(light.getPeakIntensity()));
            if (light instanceof LightAllAround allAround) {
                put(attributes, "Geographic range", nauticalMiles(allAround.getValueOfGeographicRange()));
                put(attributes, "Luminous range", nauticalMiles(allAround.getValueOfLuminousRange()));
                put(attributes, "Exhibition", describe(allAround.getExhibitionConditionOfLight()));
                put(attributes, "Visibility", describe(allAround.getLightVisibility()));
                put(attributes, "Major light", flag(allAround.getMajorLight()));
                put(attributes, "Generation", describe(allAround.getSignalGeneration()));
            }
            if (light instanceof LightAirObstruction airObstruction) {
                put(attributes, "Exhibition", describeAll(airObstruction.getExhibitionConditionOfLights()));
                put(attributes, "Visibility", describeAll(airObstruction.getLightVisibilities()));
            }
            if (light instanceof LightSectored sectored) {
                put(attributes, "Exhibition", describe(sectored.getExhibitionConditionOfLight()));
                put(attributes, "Generation", describe(sectored.getSignalGeneration()));
                put(attributes, "Sectors", count(sectored.getSectorCharacteristics()));
            }
            put(attributes, "Status", describeAll(light.getStatuses()));
        });

        if (light instanceof LightSectored sectored) {
            addSectorAttributes(groups, sectored);
        }
    }

    /**
     * Collects one group per sector of a sectored light, describing what a
     * mariner would see while sailing through it.
     *
     * @param groups the groups collected so far
     * @param light the sectored light to be described
     */
    private static void addSectorAttributes(List<AtonAttributeGroupDto> groups, LightSectored light) {
        final List<SectorCharacteristics> sectors = new ArrayList<>(light.getSectorCharacteristics());
        for (int index = 0; index < sectors.size(); index++) {
            final SectorCharacteristics sector = sectors.get(index);
            final LightSector lightSector = sector.getLightSector();
            final Set<ColourType> colours = Optional.ofNullable(lightSector)
                    .map(LightSector::getColours)
                    .orElse(Set.of());
            final BigDecimal range = Optional.ofNullable(lightSector)
                    .map(LightSector::getValueOfNominalRange)
                    .orElse(null);
            group(groups, String.format("Sector %d", index + 1), attributes -> {
                put(attributes, "Character", character(sector.getLightCharacteristic(), sector.getSignalGroups(),
                        colours, sector.getSignalPeriod(), range));
                put(attributes, "Colour", describeAll(colours));
                put(attributes, "Limits", Optional.ofNullable(lightSector)
                        .map(LightSector::getSectorLimit)
                        .map(limit -> sectorLimits(limit.getSectorLimitOne(), limit.getSectorLimitTwo()))
                        .orElse(null));
                put(attributes, "Range", nauticalMiles(range));
                put(attributes, "Period", seconds(sector.getSignalPeriod()));
                put(attributes, "Visibility", Optional.ofNullable(lightSector)
                        .map(LightSector::getLightVisibilities)
                        .map(AtonAttributeUtils::describeAll)
                        .orElse(null));
                put(attributes, "Information", Optional.ofNullable(lightSector)
                        .map(LightSector::getSectorInformations)
                        .map(informations -> informations.stream()
                                .map(SectorInformation::getText)
                                .filter(Objects::nonNull)
                                .collect(Collectors.joining(", ")))
                        .orElse(null));
            });
        }
    }

    /**
     * Collects the attributes shared by every structure, which describe how
     * the Aid to Navigation is administered rather than how it looks.
     *
     * @param groups the groups collected so far
     * @param aidsToNavigation the Aid to Navigation to be described
     */
    private static void addStructureAttributes(List<AtonAttributeGroupDto> groups, AidsToNavigation aidsToNavigation) {
        if (!(aidsToNavigation instanceof StructureObject structure)) {
            return;
        }
        group(groups, "Structure", attributes -> {
            put(attributes, "AtoN number", structure.getAtonNumber());
            put(attributes, "Availability", describe(structure.getAidAvailabilityCategory()));
            put(attributes, "Condition", describe(structure.getCondition()));
        });
    }

    /**
     * Collects the attributes shared by every piece of equipment, pointing at
     * the structure it is mounted on.
     *
     * @param groups the groups collected so far
     * @param aidsToNavigation the Aid to Navigation to be described
     */
    private static void addEquipmentAttributes(List<AtonAttributeGroupDto> groups, AidsToNavigation aidsToNavigation) {
        if (!(aidsToNavigation instanceof Equipment equipment)) {
            return;
        }
        group(groups, "Equipment", attributes -> {
            put(attributes, "Carried by", Optional.ofNullable(equipment.getParent())
                    .map(StructureObject::getIdCode)
                    .orElse(null));
            put(attributes, "Remote monitoring", join(equipment.getRemoteMonitoringSystems()));
        });
    }

    /**
     * Describes the equipment carried by a structure. This is what turns the
     * popup of a lighthouse or a buoy from a description of a shape into the
     * signal a mariner is actually looking for.
     *
     * @param groups the groups collected so far
     * @param aidsToNavigation the Aid to Navigation to be described
     */
    private static void addCarriedEquipment(List<AtonAttributeGroupDto> groups, AidsToNavigation aidsToNavigation) {
        if (!(aidsToNavigation instanceof StructureObject structure)) {
            return;
        }

        // A topmark can also be linked to a buoy through an association of its
        // own, so the two sources are merged and de-duplicated by identity
        final Map<BigInteger, Equipment> carried = new LinkedHashMap<>();
        structure.getChildren().forEach(child -> carried.putIfAbsent(child.getId(), child));
        if (structure instanceof GenericBuoy buoy) {
            buoy.getTopmarkParts().forEach(topmark -> carried.putIfAbsent(topmark.getId(), topmark));
        }

        carried.values()
                .stream()
                .filter(Objects::nonNull)
                .forEach(equipment -> addTypeAttributes(groups, equipment));
    }

    /**
     * Spells out the category of a mark, which every buoy and beacon type
     * names after its own family.
     *
     * @param aidsToNavigation the mark to be described
     * @return the category of the mark, or null when it has none
     */
    private static String markCategory(AidsToNavigation aidsToNavigation) {
        return switch (aidsToNavigation) {
            case BuoyCardinal buoy -> describe(buoy.getCategoryOfCardinalMark());
            case BuoyLateral buoy -> describe(buoy.getCategoryOfLateralMark());
            case BuoyInstallation buoy -> describe(buoy.getCategoryOfInstallationBuoy());
            case BuoySpecialPurpose buoy -> describeAll(buoy.getCategoryOfSpecialPurposeMarks());
            case BeaconCardinal beacon -> describe(beacon.getCategoryOfCardinalMark());
            case BeaconLateral beacon -> describe(beacon.getCategoryOfLateralMark());
            case BeaconSpecialPurpose beacon -> describeAll(beacon.getCategoryOfSpecialPurposeMarks());
            default -> null;
        };
    }

    /**
     * Adds the attributes shared by the AIS flavours of an Aid to Navigation.
     *
     * @param attributes the attributes collected so far
     * @param electronicAton the electronic Aid to Navigation to be described
     */
    private static void putElectronic(Map<String, String> attributes, ElectronicAton electronicAton) {
        put(attributes, "MMSI", electronicAton.getMmsiCode());
        put(attributes, "AtoN number", electronicAton.getAtonNumber());
        put(attributes, "Status", describeAll(electronicAton.getStatuses()));
    }

    /**
     * Names the group of a light after the kind of light it is.
     *
     * @param light the light to be titled
     * @return the title of its group
     */
    private static String lightTitle(GenericLight light) {
        return switch (light) {
            case LightSectored ignored -> "Sector light";
            case LightAirObstruction ignored -> "Air obstruction light";
            case LightFogDetector ignored -> "Fog detector light";
            default -> "Light";
        };
    }

    /**
     * Picks the rhythm of a light, which the sectored lights do not carry
     * since they describe one rhythm per sector instead.
     *
     * @param light the light to be inspected
     * @return the rhythm of the light, or null when it has none
     */
    private static RhythmOfLight rhythmOf(GenericLight light) {
        return switch (light) {
            case LightAllAround allAround -> allAround.getRhythmOfLight();
            case LightAirObstruction airObstruction -> airObstruction.getRhythmOfLight();
            case LightFogDetector fogDetector -> fogDetector.getRhythmOfLight();
            default -> null;
        };
    }

    /**
     * Picks the colours shown by a light.
     *
     * @param light the light to be inspected
     * @return the colours of the light
     */
    private static Set<ColourType> lightColours(GenericLight light) {
        return switch (light) {
            case LightAllAround allAround -> Optional.ofNullable(allAround.getColours()).orElse(Set.of());
            case LightSectored sectored -> Optional.ofNullable(sectored.getColours()).orElse(Set.of());
            default -> Set.of();
        };
    }

    /**
     * Picks the categories of a light.
     *
     * @param light the light to be inspected
     * @return the categories of the light, or null when it has none
     */
    private static Set<? extends Enum<?>> lightCategories(GenericLight light) {
        return switch (light) {
            case LightAllAround allAround -> allAround.getCategoryOfLights();
            case LightSectored sectored -> sectored.getCategoryOfLights();
            default -> null;
        };
    }

    /**
     * Picks the nominal range of a light.
     *
     * @param light the light to be inspected
     * @return the nominal range of the light, or null when it has none
     */
    private static BigDecimal nominalRangeOf(GenericLight light) {
        return switch (light) {
            case LightAllAround allAround -> allAround.getValueOfNominalRange();
            case LightAirObstruction airObstruction -> airObstruction.getValueOfNominalRange();
            default -> null;
        };
    }

    /**
     * Assembles the chart style characteristic of a light, such as
     * "Fl(2) R 10s 15M".
     *
     * @param characteristic the characteristic of the light
     * @param signalGroups the groups of the signal
     * @param colours the colours shown by the light
     * @param period the period of the signal
     * @param range the nominal range of the light
     * @return the assembled characteristic, which may be empty
     */
    private static String character(LightCharacteristicType characteristic,
                                    Set<String> signalGroups,
                                    Set<ColourType> colours,
                                    BigDecimal period,
                                    BigDecimal range) {
        final StringBuilder builder = new StringBuilder();
        Optional.ofNullable(characteristic)
                .ifPresent(value -> builder.append(LIGHT_CHARACTERS.getOrDefault(value, describe(value))));
        Optional.ofNullable(join(signalGroups, "+"))
                .ifPresent(groups -> builder.append("(").append(groups).append(")"));
        Optional.ofNullable(colours)
                .map(values -> values.stream()
                        .map(colour -> LIGHT_COLOURS.getOrDefault(colour, describe(colour)))
                        .collect(Collectors.joining()))
                .filter(abbreviation -> !abbreviation.isBlank())
                .ifPresent(abbreviation -> builder.append(" ").append(abbreviation));
        Optional.ofNullable(period)
                .ifPresent(value -> builder.append(" ").append(plain(value)).append("s"));
        Optional.ofNullable(range)
                .ifPresent(value -> builder.append(" ").append(plain(value)).append("M"));
        return builder.toString().trim();
    }

    /**
     * Describes the arc covered between two sector limits.
     *
     * @param first the first limit of the sector
     * @param second the second limit of the sector
     * @return the described arc, or null when neither limit is known
     */
    private static String sectorLimits(SectorLimitDetails first, SectorLimitDetails second) {
        final String from = Optional.ofNullable(first)
                .map(SectorLimitDetails::getSectorBearing)
                .map(AtonAttributeUtils::degrees)
                .orElse(null);
        final String to = Optional.ofNullable(second)
                .map(SectorLimitDetails::getSectorBearing)
                .map(AtonAttributeUtils::degrees)
                .orElse(null);
        if (from == null || to == null) {
            return Optional.ofNullable(from).orElse(to);
        }
        return String.format("%s - %s", from, to);
    }

    /**
     * Describes the on and off phases of a signal sequence.
     *
     * @param signalSequences the sequence of the signal
     * @return the described sequence, or null when there is none
     */
    private static String sequence(Collection<SignalSequence> signalSequences) {
        return Optional.ofNullable(signalSequences)
                .map(sequences -> sequences.stream()
                        .map(entry -> String.format("%s %s",
                                Optional.ofNullable(entry.getSignalDuration()).map(AtonAttributeUtils::seconds).orElse(""),
                                Optional.ofNullable(describe(entry.getSignalStatus())).orElse(""))
                                .trim())
                        .filter(entry -> !entry.isBlank())
                        .collect(Collectors.joining(", ")))
                .filter(text -> !text.isBlank())
                .orElse(null);
    }

    /**
     * Describes the wave length a racon responds on.
     *
     * @param radarWaveLength the wave length of the racon
     * @return the described wave length, or null when there is none
     */
    private static String waveLength(RadarWaveLength radarWaveLength) {
        return Optional.ofNullable(radarWaveLength)
                .map(value -> String.format("%s %s",
                        Optional.ofNullable(value.getRadarBand()).orElse(""),
                        Optional.ofNullable(value.getWaveLengthValue()).map(length -> plain(length) + " cm").orElse(""))
                        .trim())
                .filter(text -> !text.isBlank())
                .orElse(null);
    }

    /**
     * Describes the footprint of a floating structure.
     *
     * @param length the length of the structure
     * @param width the width of the structure
     * @return the described footprint, or null when neither side is known
     */
    private static String dimensions(BigDecimal length, BigDecimal width) {
        if (length == null || width == null) {
            return metres(Optional.ofNullable(length).orElse(width));
        }
        return String.format("%s x %s m", plain(length), plain(width));
    }

    /**
     * Collects a group of attributes, unless the Aid to Navigation turned out
     * to carry none of them at all.
     *
     * @param groups the groups collected so far
     * @param title the title of the group
     * @param attributes the collector of the attributes of the group
     */
    private static void group(List<AtonAttributeGroupDto> groups, String title, Consumer<Map<String, String>> attributes) {
        final Map<String, String> collected = new LinkedHashMap<>();
        attributes.accept(collected);
        if (collected.isEmpty()) {
            return;
        }
        final AtonAttributeGroupDto group = new AtonAttributeGroupDto();
        group.setTitle(title);
        group.setAttributes(collected);
        groups.add(group);
    }

    /**
     * Records an attribute, as long as it actually carries a value. The S-201
     * attributes are overwhelmingly optional, so listing the empty ones would
     * bury the handful that are populated.
     *
     * @param attributes the attributes collected so far
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    private static void put(Map<String, String> attributes, String name, String value) {
        if (value != null && !value.isBlank()) {
            attributes.put(name, value.trim());
        }
    }

    /**
     * Spells out one of the S-201 enumeration values.
     *
     * @param value the enumeration value
     * @return the label of the value, or null when there is none
     */
    private static String describe(Enum<?> value) {
        if (value == null) {
            return null;
        }
        return ENUM_LABELS
                .computeIfAbsent(value.getClass(), enumClass -> {
                    try {
                        return Optional.of(enumClass.getMethod("value"));
                    } catch (NoSuchMethodException ex) {
                        return Optional.empty();
                    }
                })
                .map(method -> {
                    try {
                        return (String) method.invoke(value);
                    } catch (ReflectiveOperationException ex) {
                        return null;
                    }
                })
                .orElseGet(() -> prettify(value.name()));
    }

    /**
     * Spells out a collection of S-201 enumeration values.
     *
     * @param values the enumeration values
     * @return the labels of the values, or null when there are none
     */
    private static String describeAll(Collection<? extends Enum<?>> values) {
        return Optional.ofNullable(values)
                .map(collection -> collection.stream()
                        .filter(Objects::nonNull)
                        .map(AtonAttributeUtils::describe)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", ")))
                .filter(text -> !text.isBlank())
                .orElse(null);
    }

    /**
     * Turns the name of an enumeration value into something readable, for the
     * rare case where it does not carry a label of its own.
     *
     * @param name the name of the enumeration value
     * @return the readable form of the name
     */
    private static String prettify(String name) {
        final String text = name.replace('_', ' ').toLowerCase(Locale.ROOT);
        return text.isEmpty() ? text : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /**
     * Joins a collection of values with commas.
     *
     * @param values the values to be joined
     * @return the joined values, or null when there are none
     */
    private static String join(Collection<String> values) {
        return join(values, ", ");
    }

    /**
     * Joins a collection of values with the provided separator.
     *
     * @param values the values to be joined
     * @param separator the separator between the values
     * @return the joined values, or null when there are none
     */
    private static String join(Collection<String> values, String separator) {
        return Optional.ofNullable(values)
                .map(collection -> collection.stream()
                        .filter(Objects::nonNull)
                        .filter(value -> !value.isBlank())
                        .collect(Collectors.joining(separator)))
                .filter(text -> !text.isBlank())
                .orElse(null);
    }

    /**
     * Counts the entries of a collection.
     *
     * @param values the collection to be counted
     * @return the size of the collection, or null when it is empty
     */
    private static String count(Collection<?> values) {
        return Optional.ofNullable(values)
                .filter(collection -> !collection.isEmpty())
                .map(collection -> String.valueOf(collection.size()))
                .orElse(null);
    }

    /**
     * Renders a yes or no answer.
     *
     * @param value the value to be rendered
     * @return the rendered answer, or null when nothing is known
     */
    private static String flag(Boolean value) {
        return value == null ? null : (value ? "Yes" : "No");
    }

    /**
     * Renders a measurement in metres.
     *
     * @param value the value to be rendered
     * @return the rendered measurement, or null when there is none
     */
    private static String metres(BigDecimal value) {
        return value == null ? null : plain(value) + " m";
    }

    /**
     * Renders a range in nautical miles.
     *
     * @param value the value to be rendered
     * @return the rendered range, or null when there is none
     */
    private static String nauticalMiles(BigDecimal value) {
        return value == null ? null : plain(value) + " M";
    }

    /**
     * Renders a duration in seconds.
     *
     * @param value the value to be rendered
     * @return the rendered duration, or null when there is none
     */
    private static String seconds(BigDecimal value) {
        return value == null ? null : plain(value) + "s";
    }

    /**
     * Renders a bearing in degrees.
     *
     * @param value the value to be rendered
     * @return the rendered bearing, or null when there is none
     */
    private static String degrees(BigDecimal value) {
        return value == null ? null : plain(value) + DEGREES;
    }

    /**
     * Renders a luminous intensity in candela.
     *
     * @param value the value to be rendered
     * @return the rendered intensity, or null when there is none
     */
    private static String candela(BigDecimal value) {
        return value == null ? null : plain(value) + " cd";
    }

    /**
     * Renders a frequency in hertz.
     *
     * @param value the value to be rendered
     * @return the rendered frequency, or null when there is none
     */
    private static String hertz(BigInteger value) {
        return value == null ? null : value + " Hz";
    }

    /**
     * Renders a decimal without the trailing zeros and without ever falling
     * back onto the scientific notation.
     *
     * @param value the value to be rendered
     * @return the rendered value
     */
    private static String plain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

}
