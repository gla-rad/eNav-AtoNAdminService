/**
 * Global variables
 */
var atonChartMap = undefined;
var atonClusterGroup = undefined;
var coverageLayer = undefined;
var legendControl = undefined;

/**
 * Every AtoN currently loaded from the service, along with the marker that
 * represents it. The filters below only ever hide and show entries of this
 * list, so switching a type on and off never costs a round trip.
 */
var loadedAtons = [];

/**
 * The AtoN type descriptions that are currently switched on. An empty set
 * means that nothing has been loaded yet.
 */
var enabledTypes = new Set();

/**
 * The maximum number of entries listed in the results rail. Beyond that the
 * chart itself remains the tool for exploring the data.
 */
const MAX_LISTED_RESULTS = 250;

/**
 * Loads the S-201 datasets into the dataset selector. The datatables endpoint
 * is used here because it is the one that also reports the cancellation state
 * of each dataset.
 */
function loadDatasets() {
    $.ajax({
        url: './api/dataset/dt',
        type: 'POST',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({
            draw: 1,
            start: 0,
            length: 500,
            columns: [],
            order: [],
            search: { value: '', regexp: 'false', includeCancelled: false }
        }),
        success: (response) => {
            const select = $('#datasetSelect');
            (response.data || []).forEach(dataset => {
                const title = (dataset.datasetIdentificationInformation || {}).datasetTitle || dataset.uuid;
                $('<option></option>')
                    .attr('value', dataset.uuid)
                    .text(title)
                    .data('dataset', dataset)
                    .appendTo(select);
            });
        },
        error: (response) => showErrorDialog(extractErrorMessage(response))
    });
}

/**
 * Loads the Aids to Navigation to be plotted. When a dataset is selected the
 * service narrows the search down to the coverage of that dataset.
 */
function loadAtons() {
    const datasetUuid = $('#datasetSelect').val();
    const url = datasetUuid
        ? `./api/atons/map?datasetUuid=${encodeURIComponent(datasetUuid)}`
        : './api/atons/map';

    setBusy(true);
    $.ajax({
        url: url,
        type: 'GET',
        contentType: 'application/json; charset=utf-8',
        success: (response) => {
            loadedAtons = (response || [])
                .map(aton => ({
                    aton: aton,
                    position: MapUtils.centreOf(aton.geometry),
                    name: MapUtils.atonName(aton),
                    type: aton.atonType || 'Unknown'
                }))
                .filter(entry => entry.position !== null);

            buildTypeFilters();
            refreshChart(true);
            setBusy(false);

            // A deep link may ask for one specific AtoN to be brought up
            focusRequestedAton();
        },
        error: (response) => {
            setBusy(false);
            showErrorDialog(extractErrorMessage(response));
        }
    });
}

/**
 * Toggles the busy indication of the chart toolbar.
 *
 * @param {Boolean} busy    Whether a load is in progress
 */
function setBusy(busy) {
    $('#reloadChart').prop('disabled', busy)
        .find('i')
        .toggleClass('fa-rotate', !busy)
        .toggleClass('fa-circle-notch fa-spin', busy);
}

/**
 * Rebuilds the AtoN type checkboxes out of the types actually present in the
 * loaded data, grouped the same way the chart colours them.
 */
function buildTypeFilters() {
    // Count how many AtoNs of each type came back, keeping them grouped
    const groups = new Map();
    loadedAtons.forEach(entry => {
        const style = MapUtils.atonStyle(entry.type);
        if (!groups.has(style.group)) {
            groups.set(style.group, new Map());
        }
        const types = groups.get(style.group);
        types.set(entry.type, (types.get(entry.type) || 0) + 1);
    });

    // Everything starts switched on
    enabledTypes = new Set(loadedAtons.map(entry => entry.type));

    const container = $('#atonFilters').empty();
    if (groups.size === 0) {
        container.append('<div class="empty-state py-3"><i class="fa-solid fa-circle-info"></i>'
            + '<span>No Aids to Navigation to filter</span></div>');
        return;
    }

    [...groups.keys()].sort().forEach(group => {
        const section = $('<div class="mb-2"></div>');
        section.append(`<div class="filter-group-title">${escapeHtml(group)}</div>`);
        [...groups.get(group).keys()].sort().forEach(atonType => {
            const style = MapUtils.atonStyle(atonType);
            const count = groups.get(group).get(atonType);
            const id = `type-${atonType.replace(/[^a-z0-9]/gi, '-')}`;
            const row = $(`
                <div class="form-check d-flex align-items-center gap-2">
                    <input class="form-check-input aton-type-filter" type="checkbox" checked id="${id}">
                    <label class="form-check-label d-flex align-items-center gap-2 small" for="${id}">
                        <span class="aton-result-swatch" style="background-color:${style.colour}"></span>
                        <span></span>
                        <span class="cell-muted">(${count})</span>
                    </label>
                </div>`);
            row.find('label > span:nth-child(2)').text(atonType);
            row.find('input').data('atonType', atonType);
            section.append(row);
        });
        container.append(section);
    });
}

/**
 * Applies the current filters and redraws the markers, the results rail and
 * the legend.
 *
 * @param {Boolean} fit     Whether the chart should zoom onto the results
 */
function refreshChart(fit) {
    const needle = ($('#atonSearch').val() || '').trim().toLowerCase();
    const matches = loadedAtons.filter(entry => {
        if (!enabledTypes.has(entry.type)) {
            return false;
        }
        if (needle.length === 0) {
            return true;
        }
        return entry.name.toLowerCase().includes(needle)
            || String(entry.aton.idCode || '').toLowerCase().includes(needle);
    });

    // Redraw the markers
    atonClusterGroup.clearLayers();
    matches.forEach(entry => {
        entry.marker = L.marker(entry.position, {
            icon: MapUtils.atonIcon(entry.type),
            title: entry.name
        }).bindPopup(MapUtils.atonPopup(entry.aton));
        atonClusterGroup.addLayer(entry.marker);
    });

    renderResults(matches);
    renderLegend(matches);
    $('#atonCount').text(matches.length);

    if (fit && matches.length > 0) {
        MapUtils.fitTo(atonChartMap, atonClusterGroup, 14);
    }
}

/**
 * Renders the list of matching Aids to Navigation next to the chart. Clicking
 * an entry flies the chart onto it and opens its popup.
 *
 * @param {Array}   matches     The AtoN entries matching the filters
 */
function renderResults(matches) {
    const list = $('#atonResults').empty();
    if (matches.length === 0) {
        list.append('<div class="empty-state"><i class="fa-solid fa-magnifying-glass"></i>'
            + '<span>No Aid to Navigation matches the current filters</span></div>');
        return;
    }

    matches.slice(0, MAX_LISTED_RESULTS).forEach(entry => {
        const style = MapUtils.atonStyle(entry.type);
        const item = $(`
            <button type="button" class="aton-result">
                <span class="aton-result-swatch" style="background-color:${style.colour}"></span>
                <span class="flex-fill overflow-hidden">
                    <span class="aton-result-name d-block"></span>
                    <span class="aton-result-meta d-block"></span>
                </span>
            </button>`);
        item.find('.aton-result-name').text(entry.name);
        item.find('.aton-result-meta').text(`${entry.aton.idCode || 'No ID code'} · ${entry.type}`);
        item.on('click', () => focusAton(entry));
        list.append(item);
    });

    if (matches.length > MAX_LISTED_RESULTS) {
        list.append(`<div class="cell-muted small text-center py-2">`
            + `and ${matches.length - MAX_LISTED_RESULTS} more on the chart…</div>`);
    }
}

/**
 * Brings one AtoN into view and opens its popup.
 *
 * @param {Object}  entry   The AtoN entry to be focused
 */
function focusAton(entry) {
    if (entry.marker) {
        // The marker may still be hidden inside a cluster, so let the cluster
        // group pick the zoom level that brings it out on its own
        atonClusterGroup.zoomToShowLayer(entry.marker, () => entry.marker.openPopup());
    } else {
        atonChartMap.flyTo(entry.position, Math.max(atonChartMap.getZoom(), 14), { duration: 0.6 });
    }
}

/**
 * Honours the "aton" query parameter, which the AtoN table uses to deep link
 * into this chart for one specific Aid to Navigation.
 */
function focusRequestedAton() {
    const requested = new URLSearchParams(window.location.search).get('aton');
    if (!requested) {
        return;
    }
    const entry = loadedAtons.find(candidate => candidate.aton.idCode === requested);
    if (entry) {
        $('#atonSearch').val(requested);
        refreshChart(false);
        focusAton(entry);
    } else {
        showToast(`The AtoN "${requested}" is not part of the current selection`, 'warning');
    }
}

/**
 * Renders the legend control, listing only the AtoN families actually plotted
 * on the chart at the moment.
 *
 * @param {Array}   matches     The AtoN entries currently plotted
 */
function renderLegend(matches) {
    const seen = new Map();
    matches.forEach(entry => {
        const style = MapUtils.atonStyle(entry.type);
        if (!seen.has(style.group)) {
            seen.set(style.group, style.colour);
        }
    });

    const content = [...seen.keys()].sort()
        .map(group => `<div class="map-legend-item">`
            + `<span class="map-legend-swatch" style="background-color:${seen.get(group)}"></span>`
            + `<span>${escapeHtml(group)}</span></div>`)
        .join('');

    $(legendControl.getContainer())
        .toggle(seen.size > 0)
        .html(`<h6>Legend</h6>${content}`);
}

/**
 * Draws - or clears - the outline of the area covered by the selected
 * dataset.
 */
function refreshCoverage() {
    coverageLayer.clearLayers();

    const option = $('#datasetSelect option:selected');
    const dataset = option.data('dataset');
    $('#chartTitle').text(option.val() ? option.text() : 'All Aids to Navigation');

    if (!dataset || !dataset.geometry || !$('#showCoverageSwitch').is(':checked')) {
        return;
    }

    const layer = MapUtils.geoJsonLayer(dataset.geometry, {
        style: () => ({
            color: '#e8a317',
            weight: 2,
            dashArray: '6 4',
            fillColor: '#e8a317',
            fillOpacity: 0.06
        })
    });
    MapUtils.addNonGroupLayers(layer, coverageLayer);
}

// Run when the document is ready
$(() => {
    // Build the chart and the layers it hosts
    atonChartMap = MapUtils.createMap('atonChartMap');
    coverageLayer = L.featureGroup().addTo(atonChartMap);
    atonClusterGroup = MapUtils.createClusterGroup().addTo(atonChartMap);

    // The legend lives as a Leaflet control in the bottom right corner
    legendControl = L.control({ position: 'bottomright' });
    legendControl.onAdd = () => L.DomUtil.create('div', 'map-legend');
    legendControl.addTo(atonChartMap);
    L.DomEvent.disableClickPropagation(legendControl.getContainer());

    // Populate the dataset selector and load the initial set of AtoNs
    loadDatasets();
    loadAtons();

    // Changing the dataset means asking the service for a different set
    $('#datasetSelect').on('change', () => {
        refreshCoverage();
        loadAtons();
    });
    $('#showCoverageSwitch').on('change', refreshCoverage);

    // Everything else is filtered on the data already at hand
    $('#atonSearch').on('input', () => refreshChart(false));
    $('#atonFilters').on('change', '.aton-type-filter', function () {
        const atonType = $(this).data('atonType');
        if ($(this).is(':checked')) {
            enabledTypes.add(atonType);
        } else {
            enabledTypes.delete(atonType);
        }
        refreshChart(false);
    });
    $('#selectAllTypes').on('click', () => {
        $('.aton-type-filter').prop('checked', true);
        enabledTypes = new Set(loadedAtons.map(entry => entry.type));
        refreshChart(false);
    });
    $('#clearAllTypes').on('click', () => {
        $('.aton-type-filter').prop('checked', false);
        enabledTypes = new Set();
        refreshChart(false);
    });

    $('#fitResults').on('click', () => MapUtils.fitTo(atonChartMap, atonClusterGroup, 14));
    $('#reloadChart').on('click', () => loadAtons());
});
