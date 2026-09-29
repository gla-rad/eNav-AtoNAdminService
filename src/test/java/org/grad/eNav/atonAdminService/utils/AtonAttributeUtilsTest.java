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

import _int.iho.s_201.gml.cs0._2.BuoyShapeType;
import _int.iho.s_201.gml.cs0._2.CategoryOfFogSignalType;
import _int.iho.s_201.gml.cs0._2.CategoryOfLandmarkType;
import _int.iho.s_201.gml.cs0._2.CategoryOfLateralMarkType;
import _int.iho.s_201.gml.cs0._2.ColourPatternType;
import _int.iho.s_201.gml.cs0._2.ColourType;
import _int.iho.s_201.gml.cs0._2.LightCharacteristicType;
import _int.iho.s_201.gml.cs0._2.MarksNavigationalSystemOfType;
import _int.iho.s_201.gml.cs0._2.NatureOfConstructionType;
import _int.iho.s_201.gml.cs0._2.StatusType;
import _int.iho.s_201.gml.cs0._2.VirtualAISAidToNavigationTypeType;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.models.dtos.s201.AtonAttributeGroupDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AtonAttributeUtilsTest {

    /**
     * Test that a lighthouse is described by its own structural attributes,
     * and that the light it carries contributes a group of its own with the
     * characteristic spelled out the way a chart would.
     */
    @Test
    void testAttributesOfLighthouse() {
        final Lighthouse lighthouse = new Lighthouse();
        lighthouse.setId(BigInteger.ONE);
        lighthouse.setIdCode("LH-001");
        lighthouse.setAtonNumber("A1234");
        lighthouse.setCategoryOfLandmarks(Set.of(CategoryOfLandmarkType.TOWER));
        lighthouse.setColours(Set.of(ColourType.WHITE));
        lighthouse.setColourPatterns(Set.of(ColourPatternType.HORIZONTAL_STRIPES));
        lighthouse.setNatureOfConstructions(Set.of(NatureOfConstructionType.MASONRY));
        lighthouse.setHeight(new BigDecimal("34.50"));

        final LightAllAround light = new LightAllAround();
        light.setId(BigInteger.TWO);
        light.setColours(Set.of(ColourType.RED));
        light.setValueOfNominalRange(new BigDecimal("15"));
        final RhythmOfLight rhythm = new RhythmOfLight();
        rhythm.setLightCharacteristic(LightCharacteristicType.GROUP_FLASHING_LIGHT);
        rhythm.setSignalGroups(Set.of("2"));
        rhythm.setSignalPeriod(new BigDecimal("10.0"));
        light.setRhythmOfLight(rhythm);
        light.setParent(lighthouse);
        lighthouse.getChildren().add(light);

        final List<AtonAttributeGroupDto> groups = AtonAttributeUtils.attributesOf(lighthouse);

        final Map<String, String> structure = attributesOf(groups, "Lighthouse");
        assertEquals("Tower", structure.get("Category"));
        assertEquals("White", structure.get("Colour"));
        assertEquals("Horizontal Stripes", structure.get("Colour pattern"));
        assertEquals("Masonry", structure.get("Construction"));
        assertEquals("34.5 m", structure.get("Height"));
        assertEquals("A1234", attributesOf(groups, "Structure").get("AtoN number"));

        // The signal of the lighthouse comes from the light it carries
        final Map<String, String> signal = attributesOf(groups, "Light");
        assertEquals("Fl(2) R 10s 15M", signal.get("Character"));
        assertEquals("Red", signal.get("Colour"));
        assertEquals("10s", signal.get("Period"));
        assertEquals("15 M", signal.get("Range"));
    }

    /**
     * Test that a buoy is described by the attributes of its own family, and
     * that the topmark associated with it is described alongside.
     */
    @Test
    void testAttributesOfBuoy() {
        final BuoyLateral buoy = new BuoyLateral();
        buoy.setId(BigInteger.TEN);
        buoy.setCategoryOfLateralMark(CategoryOfLateralMarkType.PORT_HAND_LATERAL_MARK);
        buoy.setBuoyShape(BuoyShapeType.CAN);
        buoy.setColours(Set.of(ColourType.RED));
        buoy.setColourPatterns(Set.of(ColourPatternType.SINGLE_COLOUR));
        buoy.setMarksNavigationalSystemOf(MarksNavigationalSystemOfType.IALA_A);
        buoy.setRadarConspicuous(Boolean.TRUE);
        buoy.setStatuses(Set.of(StatusType.PERMANENT));

        final Topmark topmark = new Topmark();
        topmark.setId(BigInteger.valueOf(11));
        topmark.setTopmarkDaymarkShape("Cylinder");
        topmark.setColours(Set.of(ColourType.RED));
        buoy.getTopmarkParts().add(topmark);

        final List<AtonAttributeGroupDto> groups = AtonAttributeUtils.attributesOf(buoy);

        final Map<String, String> attributes = attributesOf(groups, "Buoy");
        assertEquals("Port-Hand Lateral Mark", attributes.get("Category"));
        assertEquals("Can", attributes.get("Shape"));
        assertEquals("Red", attributes.get("Colour"));
        assertEquals("Single Colour", attributes.get("Colour pattern"));
        assertEquals("IALA A", attributes.get("Marks system"));
        assertEquals("Yes", attributes.get("Radar conspicuous"));
        assertEquals("Permanent", attributes.get("Status"));

        assertEquals("Cylinder", attributesOf(groups, "Topmark").get("Shape"));
    }

    /**
     * Test that a sectored light describes every one of its sectors on its
     * own, since the characteristic changes as the sectors are crossed.
     */
    @Test
    void testAttributesOfSectoredLight() {
        final LightSectored light = new LightSectored();
        light.setId(BigInteger.valueOf(20));
        light.setColours(Set.of(ColourType.WHITE));

        final SectorCharacteristics sector = new SectorCharacteristics();
        sector.setLightCharacteristic(LightCharacteristicType.OCCULTING);
        sector.setSignalPeriod(new BigDecimal("8"));
        final LightSector lightSector = new LightSector();
        lightSector.setColours(Set.of(ColourType.GREEN));
        lightSector.setValueOfNominalRange(new BigDecimal("12"));
        final SectorLimit sectorLimit = new SectorLimit();
        final SectorLimitDetails first = new SectorLimitDetails();
        first.setSectorBearing(new BigDecimal("45.5"));
        final SectorLimitDetails second = new SectorLimitDetails();
        second.setSectorBearing(new BigDecimal("90"));
        sectorLimit.setSectorLimitOne(first);
        sectorLimit.setSectorLimitTwo(second);
        lightSector.setSectorLimit(sectorLimit);
        sector.setLightSector(lightSector);
        light.getSectorCharacteristics().add(sector);

        final List<AtonAttributeGroupDto> groups = AtonAttributeUtils.attributesOf(light);

        assertEquals("1", attributesOf(groups, "Sector light").get("Sectors"));

        final Map<String, String> attributes = attributesOf(groups, "Sector 1");
        assertEquals("Oc G 8s 12M", attributes.get("Character"));
        assertEquals("Green", attributes.get("Colour"));
        assertEquals("45.5° - 90°", attributes.get("Limits"));
        assertEquals("12 M", attributes.get("Range"));
    }

    /**
     * Test that the electronic Aids to Navigation, which carry no shape or
     * colour at all, are described through their transmission instead.
     */
    @Test
    void testAttributesOfVirtualAton() {
        final VirtualAISAidToNavigation aton = new VirtualAISAidToNavigation();
        aton.setId(BigInteger.valueOf(30));
        aton.setMmsiCode("992351000");
        aton.setVirtualAISAidToNavigationType(VirtualAISAidToNavigationTypeType.NORTH_CARDINAL);
        aton.setStatuses(Set.of(StatusType.PERMANENT));

        final Map<String, String> attributes = attributesOf(AtonAttributeUtils.attributesOf(aton), "AIS transmission");
        assertEquals("North Cardinal", attributes.get("Virtual type"));
        assertEquals("992351000", attributes.get("MMSI"));
        assertEquals("Permanent", attributes.get("Status"));
    }

    /**
     * Test that a piece of equipment points back at the structure carrying it.
     */
    @Test
    void testAttributesOfEquipment() {
        final Lighthouse lighthouse = new Lighthouse();
        lighthouse.setId(BigInteger.ONE);
        lighthouse.setIdCode("LH-001");

        final FogSignal fogSignal = new FogSignal();
        fogSignal.setId(BigInteger.valueOf(40));
        fogSignal.setCategoryOfFogSignal(CategoryOfFogSignalType.HORN);
        fogSignal.setSignalPeriod(new BigDecimal("30"));
        fogSignal.setParent(lighthouse);

        final List<AtonAttributeGroupDto> groups = AtonAttributeUtils.attributesOf(fogSignal);
        assertEquals("Horn", attributesOf(groups, "Fog signal").get("Category"));
        assertEquals("30s", attributesOf(groups, "Fog signal").get("Period"));
        assertEquals("LH-001", attributesOf(groups, "Equipment").get("Carried by"));
    }

    /**
     * Test that the attributes an Aid to Navigation does not carry are left
     * out altogether, rather than being listed as empty.
     */
    @Test
    void testAttributesOfEmptyAton() {
        final BuoyLateral buoy = new BuoyLateral();
        buoy.setId(BigInteger.ONE);

        assertTrue(AtonAttributeUtils.attributesOf(buoy).isEmpty());
        assertTrue(AtonAttributeUtils.attributesOf(null).isEmpty());
    }

    /**
     * Picks the attributes of one of the collected groups.
     *
     * @param groups the collected groups
     * @param title the title of the group to be picked
     * @return the attributes of that group
     */
    private Map<String, String> attributesOf(List<AtonAttributeGroupDto> groups, String title) {
        final Optional<AtonAttributeGroupDto> group = groups.stream()
                .filter(entry -> title.equals(entry.getTitle()))
                .findFirst();
        assertTrue(group.isPresent(), String.format("No \"%s\" group was collected", title));
        return group.get().getAttributes();
    }

}
