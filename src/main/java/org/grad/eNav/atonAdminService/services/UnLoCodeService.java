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

package org.grad.eNav.atonAdminService.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.grad.eNav.atonAdminService.models.UnLoCodeMapEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

/**
 * The UN/LoCode Service Class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class UnLoCodeService {

    private static final String JSON_KEY_COUNTRY = "Country";
    private static final String JSON_KEY_LOCATION = "Location";
    private static final String JSON_KEY_COORDINATES = "Coordinates";
    private static final double INVALID_COORDINATE = -99999.0;

    /**
     * The JSON Object Mapper.
     */
    @Autowired
    ObjectMapper objectMapper;

    // Service Variables
    HashMap<String, UnLoCodeMapEntry> UnLoCodeMap = new HashMap<>();

    /**
     * Once the service has been initialised, it will read the full list of the
     * UN LoCodes from the JSON-encoded resource file.
     */
    @PostConstruct
    public void init() {
        InputStream s = this.getClass().getClassLoader().getResourceAsStream("UnLoCodeLists.json");
        try {
            this.loadUnLoCodeMapping(s);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Provides access to the UN/LOCODE entry for the provided UN/LOCODE string.
     *
     * @param unLoCode the provided UN/LOCODE string
     * @return the corresponding UN/LOCODE map entry
     */
    public UnLoCodeMapEntry getUnLoCodeMapEntry(String unLoCode) {
        return Optional.ofNullable(unLoCode)
                .filter(UnLoCodeMap::containsKey)
                .map(UnLoCodeMap::get)
                .orElse(null);
    }

    /**
     * Fetch lat/lon from json mapping file and populate coverage geometry with it as point
     *
     * @throws Exception if the unLoCode mapping file could not be found
     */
    private void loadUnLoCodeMapping(InputStream inStream) throws IOException {
        this.UnLoCodeMap.clear();
        final JsonNode unLoCodeJson = this.objectMapper.readTree(inStream);
        for (JsonNode entry : unLoCodeJson) {
            UnLoCodeMapEntry unLoCode = new UnLoCodeMapEntry();
            unLoCode.setLatitude(INVALID_COORDINATE);
            unLoCode.setLongitude(INVALID_COORDINATE);
            try {
                String country = entry.get(JSON_KEY_COUNTRY).textValue();
                String location = entry.get(JSON_KEY_LOCATION).textValue();
                String coordinatesCombined = entry.get(JSON_KEY_COORDINATES).textValue();
                //coordinates are given in the form of "DDMM[N/S] DDDMM[W/E]"
                if (StringUtils.isNotBlank(coordinatesCombined)) {
                    coordinatesCombined = coordinatesCombined.trim();
                    if (coordinatesCombined.length() > 0) {
                        String[] c = coordinatesCombined.split("\\s");
                        String latDegrees = c[0].substring(0, 2);
                        String latMinutes = c[0].substring(2, 4);
                        String latDirection = c[0].substring(4, 5);
                        unLoCode.setLatitude(Double.parseDouble(latDegrees + "." + latMinutes));
                        if (latDirection == "S") {
                            unLoCode.setLatitude(-unLoCode.getLatitude());
                        }
                        String lonDegrees = c[1].substring(0, 3);
                        String lonMinutes = c[1].substring(3, 5);
                        String lonDirection = c[1].substring(5, 6);
                        unLoCode.setLongitude(Double.parseDouble(lonDegrees + "." + lonMinutes));
                        if (lonDirection == "W") {
                            unLoCode.setLongitude(-unLoCode.getLongitude());
                        }
                    }
                }
                String status = entry.get("Status").textValue();

                unLoCode.setStatus(status);
                if (unLoCode.getLatitude() != INVALID_COORDINATE && unLoCode.getLongitude() != INVALID_COORDINATE) {
                    this.UnLoCodeMap.put(country + location, unLoCode);
                }
            } catch (Exception ex) {
                log.error("Error parsing UnLoCode mapping file: ", ex);
            }
        }
    }

}
