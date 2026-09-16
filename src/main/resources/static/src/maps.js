/**
 * The shared mapping helpers.
 *
 * Every map of the application - the little geometry previews inside the
 * modal dialogs as well as the full blown AtoN chart - is built through this
 * module so that they all share the same base layers, controls and styling.
 *
 * Note that the geometries served by this application encode their positions
 * as [latitude, longitude] pairs rather than the [longitude, latitude] order
 * mandated by the GeoJSON specification. This is a consequence of the S-201
 * GML position lists being latitude first, so all the conversions below keep
 * that convention.
 */
const MapUtils = (() => {

    /**
     * The default view, centred on the British Isles.
     */
    const DEFAULT_CENTRE = [54.910, -3.432];
    const DEFAULT_ZOOM = 5;

    const OSM_ATTRIBUTION = '&copy; <a href="https://osm.org/copyright">OpenStreetMap</a> contributors';
    const ESRI_CANVAS_ATTRIBUTION = 'Tiles &copy; Esri, HERE, Garmin, ' + OSM_ATTRIBUTION;

    /**
     * Builds one of the Esri gray canvas base maps, which are free to use
     * without an API key. The canvas comes in two tile services, the muted
     * base and a transparent reference layer carrying the place labels, so
     * both are stacked into a single group that acts as one base layer.
     *
     * @param {String}  name    The canvas service prefix, e.g. World_Dark_Gray
     * @return {Object} The Leaflet layer group
     */
    function createEsriCanvasLayer(name) {
        const url = (service) => `https://server.arcgisonline.com/ArcGIS/rest/services/Canvas/${service}/MapServer/tile/{z}/{y}/{x}`;
        const options = { maxZoom: 19, maxNativeZoom: 16 };
        return L.layerGroup([
            L.tileLayer(url(`${name}_Base`), $.extend({ attribution: ESRI_CANVAS_ATTRIBUTION }, options)),
            L.tileLayer(url(`${name}_Reference`), options)
        ]);
    }

    /**
     * Builds the set of base layers offered by the layer control. The first
     * two entries are the light and the dark default, picked automatically
     * based on the colour theme of the application.
     *
     * @return {Object} The named base layers
     */
    function createBaseLayers() {
        return {
            'Nautical': L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: OSM_ATTRIBUTION
            }),
            'Light': createEsriCanvasLayer('World_Light_Gray'),
            'Dark': createEsriCanvasLayer('World_Dark_Gray'),
            'Satellite': L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
                maxZoom: 19,
                attribution: 'Imagery &copy; Esri, Maxar, Earthstar Geographics'
            })
        };
    }

    /**
     * Builds the optional overlays. The seamark overlay from OpenSeaMap is
     * the one that turns a generic web map into something a mariner can
     * actually read.
     *
     * @return {Object} The named overlay layers
     */
    function createOverlays() {
        return {
            'Seamarks': L.tileLayer('https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png', {
                maxZoom: 18,
                opacity: 0.9,
                attribution: '&copy; <a href="https://www.openseamap.org">OpenSeaMap</a> contributors'
            })
        };
    }

    /**
     * Creates a Leaflet map on the given element, wired up with the base
     * layers, the seamark overlay, a scale bar and a layer control. The base
     * layer follows the colour theme of the application until the user picks
     * one explicitly through the layer control.
     *
     * @param {String}  elementId   The ID of the element hosting the map
     * @param {Object}  options     Optional overrides for centre and zoom
     * @return {Object} The Leaflet map instance
     */
    function createMap(elementId, options) {
        const settings = $.extend({
            centre: DEFAULT_CENTRE,
            zoom: DEFAULT_ZOOM,
            seamarks: true
        }, options || {});

        const baseLayers = createBaseLayers();
        const overlays = createOverlays();
        const themedBase = () => (getTheme() === 'dark' ? baseLayers['Dark'] : baseLayers['Nautical']);

        const map = L.map(elementId, {
            center: settings.centre,
            zoom: settings.zoom,
            layers: settings.seamarks ? [themedBase(), overlays['Seamarks']] : [themedBase()],
            zoomControl: true,
            worldCopyJump: true
        });

        L.control.scale({ imperial: false, maxWidth: 140 }).addTo(map);
        L.control.layers(baseLayers, overlays, { position: 'topright' }).addTo(map);

        // Follow the colour theme, unless the user has chosen a base layer
        let baseLayerPinned = false;
        map.on('baselayerchange', () => { baseLayerPinned = true; });
        onThemeChange(() => {
            if (baseLayerPinned) {
                return;
            }
            Object.values(baseLayers)
                .filter(layer => map.hasLayer(layer))
                .forEach(layer => map.removeLayer(layer));
            themedBase().addTo(map);
        });

        return map;
    }

    /**
     * Turns one of the geometries served by this application into a Leaflet
     * layer, honouring the latitude-first coordinate order.
     *
     * @param {Object}  geometry    The GeoJSON geometry
     * @param {Object}  options     Additional L.geoJSON options
     * @return {Object} The Leaflet GeoJSON layer
     */
    function geoJsonLayer(geometry, options) {
        return L.geoJson(geometry, $.extend({
            coordsToLatLng: (coords) => coords,
            style: () => ({
                color: '#0f8fa5',
                weight: 2,
                opacity: 0.9,
                fillColor: '#0f8fa5',
                fillOpacity: 0.12
            })
        }, options || {}));
    }

    /**
     * Flattens a layer group into the target feature group. Leaflet Draw can
     * only edit plain layers, so any nesting introduced by a geometry
     * collection has to be unwrapped first.
     *
     * @see https://github.com/Leaflet/Leaflet/issues/4461
     * @param {Object}  sourceLayer     The layer to be flattened
     * @param {Object}  targetGroup     The group receiving the plain layers
     */
    function addNonGroupLayers(sourceLayer, targetGroup) {
        if (sourceLayer instanceof L.LayerGroup) {
            sourceLayer.eachLayer((layer) => addNonGroupLayers(layer, targetGroup));
        } else {
            targetGroup.addLayer(sourceLayer);
        }
    }

    /**
     * Zooms the map onto the provided layer. Single points cannot produce
     * usable bounds, so those are centred at a fixed zoom level instead.
     *
     * @param {Object}  map         The Leaflet map
     * @param {Object}  layer       The layer to zoom onto
     * @param {Number}  pointZoom   The zoom level used for single points
     */
    function fitTo(map, layer, pointZoom) {
        if (!layer) {
            return;
        }
        const bounds = layer.getBounds ? layer.getBounds() : null;
        if (!bounds || !bounds.isValid()) {
            return;
        }
        if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
            map.setView(bounds.getCenter(), pointZoom || 13);
        } else {
            map.fitBounds(bounds, { padding: [32, 32], maxZoom: pointZoom || 13 });
        }
    }

    /**
     * Collects every position of a geometry as a flat list of [lat, lng]
     * pairs, whatever its nesting depth.
     *
     * @param {Object}  geometry    The GeoJSON geometry
     * @return {Array} The list of positions
     */
    function positionsOf(geometry) {
        if (!geometry) {
            return [];
        }
        if (geometry.type === 'GeometryCollection') {
            return (geometry.geometries || []).flatMap(positionsOf);
        }
        const flatten = (coords) => (
            Array.isArray(coords[0]) ? coords.flatMap(flatten) : [coords]
        );
        return Array.isArray(geometry.coordinates) && geometry.coordinates.length > 0
            ? flatten(geometry.coordinates)
            : [];
    }

    /**
     * Computes the representative position of a geometry - the position
     * itself for a point, the centre of its extent for anything else.
     *
     * @param {Object}  geometry    The GeoJSON geometry
     * @return {Object} The Leaflet LatLng, or null when the geometry is empty
     */
    function centreOf(geometry) {
        const positions = positionsOf(geometry);
        if (positions.length === 0) {
            return null;
        }
        const sum = positions.reduce((acc, pos) => [acc[0] + pos[0], acc[1] + pos[1]], [0, 0]);
        return L.latLng(sum[0] / positions.length, sum[1] / positions.length);
    }

    /**
     * Invalidates the size of a map once the modal dialog hosting it becomes
     * visible. Leaflet cannot measure a hidden container, so without this the
     * tiles only cover a fraction of the panel.
     *
     * @param {String}  modalSelector   The selector of the hosting modal
     * @param {Object}  map             The Leaflet map
     */
    function refreshOnModalShow(modalSelector, map) {
        $(modalSelector).on('shown.bs.modal', () => {
            setTimeout(() => map.invalidateSize(), 10);
        });
    }

    /*****************************
     *   The AtoN presentation   *
     *****************************/

    /**
     * How each family of Aids to Navigation is drawn on the chart. The first
     * matching entry wins, so the more specific patterns come first. Matching
     * happens on the AtoN type description reported by the service, which
     * keeps this table working even when new types are introduced.
     */
    const ATON_STYLES = [
        { match: /emergency wreck/i,        group: 'Buoys',         colour: '#2471a3', icon: 'fa-triangle-exclamation' },
        { match: /cardinal buoy/i,          group: 'Buoys',         colour: '#e8a317', icon: 'fa-life-ring' },
        { match: /lateral buoy/i,           group: 'Buoys',         colour: '#c0392b', icon: 'fa-life-ring' },
        { match: /isolated danger buoy/i,   group: 'Buoys',         colour: '#212f3d', icon: 'fa-life-ring' },
        { match: /safe water buoy/i,        group: 'Buoys',         colour: '#a12b3c', icon: 'fa-life-ring' },
        { match: /special purpose buoy/i,   group: 'Buoys',         colour: '#d4ac0d', icon: 'fa-life-ring' },
        { match: /installation buoy/i,      group: 'Buoys',         colour: '#ca6f1e', icon: 'fa-life-ring' },
        { match: /buoy/i,                   group: 'Buoys',         colour: '#7d8b99', icon: 'fa-life-ring' },
        { match: /cardinal beacon/i,        group: 'Beacons',       colour: '#e8a317', icon: 'fa-tower-observation', fixed: true },
        { match: /lateral beacon/i,         group: 'Beacons',       colour: '#c0392b', icon: 'fa-tower-observation', fixed: true },
        { match: /isolated danger beacon/i, group: 'Beacons',       colour: '#212f3d', icon: 'fa-tower-observation', fixed: true },
        { match: /safe water beacon/i,      group: 'Beacons',       colour: '#a12b3c', icon: 'fa-tower-observation', fixed: true },
        { match: /special purpose beacon/i, group: 'Beacons',       colour: '#d4ac0d', icon: 'fa-tower-observation', fixed: true },
        { match: /virtual|synthetic/i,      group: 'AIS and Radio', colour: '#8e44ad', icon: 'fa-wifi', virtual: true },
        { match: /ais/i,                    group: 'AIS and Radio', colour: '#6c3fb5', icon: 'fa-tower-broadcast' },
        { match: /radar|radio/i,            group: 'AIS and Radio', colour: '#5b6ec4', icon: 'fa-satellite-dish' },
        { match: /beacon|pile/i,            group: 'Beacons',       colour: '#7d8b99', icon: 'fa-tower-observation', fixed: true },
        { match: /lighthouse/i,             group: 'Lights',        colour: '#e67e22', icon: 'fa-tower-observation', fixed: true },
        { match: /light vessel|light float/i, group: 'Lights',      colour: '#f39c12', icon: 'fa-ship' },
        { match: /light/i,                  group: 'Lights',        colour: '#f39c12', icon: 'fa-lightbulb' },
        { match: /fog signal/i,             group: 'Lights',        colour: '#7f8c8d', icon: 'fa-volume-high' },
        { match: /daymark|topmark|retro/i,  group: 'Marks',         colour: '#16a085', icon: 'fa-shapes', fixed: true },
        { match: /bridge/i,                 group: 'Structures',    colour: '#34495e', icon: 'fa-bridge', fixed: true },
        { match: /building|silo|landmark/i, group: 'Structures',    colour: '#34495e', icon: 'fa-building', fixed: true },
        { match: /platform/i,               group: 'Structures',    colour: '#34495e', icon: 'fa-industry', fixed: true },
        { match: /environment|power/i,      group: 'Equipment',     colour: '#0b7285', icon: 'fa-gauge-high', fixed: true },
        { match: /cable|shackle|bridle|swivel|sinker|counter weight/i, group: 'Equipment', colour: '#5d6d7e', icon: 'fa-link', fixed: true },
        { match: /track|navigation line/i,  group: 'Routes',        colour: '#2e86c1', icon: 'fa-route' }
    ];

    const ATON_DEFAULT_STYLE = { group: 'Other', colour: '#4a5d73', icon: 'fa-location-dot' };

    /**
     * Resolves the presentation of an AtoN type description.
     *
     * @param {String}  atonType    The AtoN type description
     * @return {Object} The colour, icon and shape to be used
     */
    function atonStyle(atonType) {
        const found = ATON_STYLES.find(entry => entry.match.test(atonType || ''));
        return found || ATON_DEFAULT_STYLE;
    }

    /**
     * Builds the Leaflet icon used to plot an AtoN. The marker is drawn with
     * CSS alone, so it picks up the palette without any image assets.
     *
     * @param {String}  atonType    The AtoN type description
     * @return {Object} The Leaflet DivIcon
     */
    function atonIcon(atonType) {
        const style = atonStyle(atonType);
        const modifier = (style.fixed ? ' aton-marker-fixed' : '') + (style.virtual ? ' aton-marker-virtual' : '');
        return L.divIcon({
            className: '',
            html: `<div class="aton-marker${modifier}" style="background-color:${style.colour}">`
                + `<i class="fa-solid ${style.icon}"></i></div>`,
            iconSize: [26, 26],
            iconAnchor: [13, 13],
            popupAnchor: [0, -14]
        });
    }

    /**
     * Picks the name to be shown for an AtoN, preferring the one flagged as
     * the display name by the S-201 feature name collection.
     *
     * @param {Object}  aton    The AtoN entry
     * @return {String} The name, or the ID code when no name is available
     */
    function atonName(aton) {
        const names = aton.featureNames || [];
        const display = names.find(featureName => featureName.displayName) || names[0];
        return (display && display.name) || aton.idCode || 'Unnamed AtoN';
    }

    /**
     * Builds the popup shown when an AtoN marker is clicked.
     *
     * @param {Object}  aton    The AtoN entry
     * @return {String} The popup markup
     */
    function atonPopup(aton) {
        const centre = centreOf(aton.geometry);
        const position = centre
            ? `${centre.lat.toFixed(5)}, ${centre.lng.toFixed(5)}`
            : 'Unknown';
        return `
            <div class="aton-popup-title">${escapeHtml(atonName(aton))}</div>
            <div class="aton-popup-type">${escapeHtml(aton.atonType || 'Unknown type')}</div>
            <dl class="aton-popup-grid">
                <dt>ID code</dt><dd>${escapeHtml(aton.idCode)}</dd>
                <dt>Position</dt><dd>${escapeHtml(position)}</dd>
                <dt>Valid from</dt><dd>${escapeHtml(aton.dateStart)}</dd>
                <dt>Valid to</dt><dd>${escapeHtml(aton.dateEnd)}</dd>
            </dl>`;
    }

    /**
     * Creates the marker cluster group used to plot large numbers of AtoNs
     * without drowning the chart in overlapping pins.
     *
     * @return {Object} The Leaflet marker cluster group
     */
    function createClusterGroup() {
        return L.markerClusterGroup({
            showCoverageOnHover: false,
            spiderfyOnMaxZoom: true,
            disableClusteringAtZoom: 15,
            maxClusterRadius: 50,
            iconCreateFunction: (cluster) => L.divIcon({
                html: `<div>${cluster.getChildCount()}</div>`,
                className: 'marker-cluster-aton',
                iconSize: L.point(40, 40)
            })
        });
    }

    // The public surface of the module
    return {
        DEFAULT_CENTRE: DEFAULT_CENTRE,
        DEFAULT_ZOOM: DEFAULT_ZOOM,
        createMap: createMap,
        geoJsonLayer: geoJsonLayer,
        addNonGroupLayers: addNonGroupLayers,
        fitTo: fitTo,
        positionsOf: positionsOf,
        centreOf: centreOf,
        refreshOnModalShow: refreshOnModalShow,
        atonStyle: atonStyle,
        atonIcon: atonIcon,
        atonName: atonName,
        atonPopup: atonPopup,
        createClusterGroup: createClusterGroup
    };
})();
