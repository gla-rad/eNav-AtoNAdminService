/**
 * Global variables
 */
var atonMessagesTable = undefined;
var atonMessagesMap = undefined;
var drawnItems = undefined;

/**
 * The AtoN Messages Table Column Definitions
 * @type {Array}
 */
var nodesColumnDefs = [
{
    data: "id",
    title: "ID",
    hoverMsg: "The AtoN ID",
    placeholder: "The AtoN ID",
    visible: false,
    searchable: false,
    className: "noVis"
}, {
     data: "idCode",
     title: "ID Code",
     hoverMsg: "The AtoN ID Code",
     placeholder: "The AtoN ID Code",
     render: (data, type) => type === 'display' ? renderIdentifier(data) : data
 }, {
     data: "atonType",
     title: "Type",
     hoverMsg: "The AtoN Type",
     placeholder: "The AtoN Type",
     sortable: false,
     render: (data, type) => type === 'display' ? renderAtonType(data) : data
 }, {
    data: ( row, type, val, meta ) => {
        if(row.featureNames && row.featureNames.length > 0) {
            displayEntry = row.featureNames.find(fn => fn.displayName);
            if(displayEntry) {
                return displayEntry.name;
            }
            return row.featureNames[0].name;
        }
        return '';
      },
    title: "Name",
    hoverMsg: "The AtoN Name",
    placeholder: "The AtoN Name",
    sortable: false
 }, {
    data: "geometry",
    title: "Position",
    hoverMsg: "The AtoN Position",
    placeholder: "The AtoN Position",
    sortable: false,
    searchable: false,
    render: (data, type) => type === 'display' ? renderPosition(data) : data
 }, {
    data: "dateStart",
    title: "Start Date",
    type: "date",
    hoverMsg: "The AtoN Start Date",
    placeholder: "The AtoN Start Date",
    render: (data, type) => type === 'display' ? renderDateTime(data) : data
}, {
    data: "dateEnd",
    title: "End Date",
    type: "date",
    hoverMsg: "The AtoN End Date",
    placeholder: "The AtoN End Date",
    render: (data, type) => type === 'display' ? renderDateTime(data) : data
 }, {
    data: "mmsiCode",
    title: "MMSI",
    hoverMsg: "The AtoN MMSI Code",
    placeholder: "The AtoN MMSI Code",
    sortable: false,
    visible: false,
    render: (data, type) => type === 'display' ? renderIdentifier(data) : data
 }, {
    data: "content",
    title: "Content",
    type: "textarea",
    hoverMsg: "The AtoN S-201 Content",
    placeholder: "The AtoN S-201 Content",
    visible: false,
    searchable: false,
    className: "noVis"
}];

/**
 * Renders the AtoN type as a tag carrying the same colour the chart uses for
 * that family of Aids to Navigation.
 *
 * @param {String}  atonType    The AtoN type description
 * @return {String} The tag markup
 */
function renderAtonType(atonType) {
    if(!atonType) {
        return '<span class="cell-muted">&mdash;</span>';
    }
    const style = MapUtils.atonStyle(atonType);
    return `<span class="tag"><span class="aton-result-swatch" style="background-color:${style.colour}"></span>`
        + `${escapeHtml(atonType)}</span>`;
}

/**
 * Renders the representative position of a geometry in decimal degrees.
 *
 * @param {Object}  geometry    The AtoN geometry
 * @return {String} The position markup
 */
function renderPosition(geometry) {
    const centre = MapUtils.centreOf(geometry);
    if(!centre) {
        return '<span class="cell-muted">&mdash;</span>';
    }
    return `<span class="cell-mono">${centre.lat.toFixed(4)}, ${centre.lng.toFixed(4)}</span>`;
}

// Run when the document is ready
$(() => {
    // And re-initialise it
    atonMessagesTable = $('#atons_table').DataTable($.extend(commonDatatableOptions(), {
        ajax: {
            type: "POST",
            url: "./api/atons/dt",
            contentType: "application/json",
            data: (d) => {
                return JSON.stringify(d);
            },
            error: (response, status, more) => {
                showErrorDialog(extractErrorMessage(response));
            }
        },
        columns: nodesColumnDefs,
        altEditor: true, // Enable altEditor
        buttons: [{
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-map-location-dot"></i><span class="dt-button-text">Location</span>',
            titleAttr: 'View the AtoN location',
            name: 'messageGeometry', // do not change name
            className: 'aton-geometry-toggle',
            action: (e, dt, node, config) => {
                loadAtonGeometry(e, dt, node, config);
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-code"></i><span class="dt-button-text">S-201</span>',
            titleAttr: 'View the AtoN S-201 content',
            name: 'atonContent', // do not change name
            className: 'aton-content-toggle',
            action: (e, dt, node, config) => {
                loadAtonContent(e, dt, node, config);
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-trash-can"></i><span class="dt-button-text">Delete</span>',
            titleAttr: 'Delete the selected AtoN',
            className: 'btn-danger-soft',
            name: 'delete' // do not change name
        }].concat(commonDatatableButtons('Aids to Navigation')),
        onDeleteRow: (datatable, selectedRows, success, error) => {
            selectedRows.every(function(rowIdx, tableLoop, rowLoop) {
                $.ajax({
                    type: 'DELETE',
                    url: `./api/atons/${this.data()["id"]}`,
                    crossDomain: true,
                    success: success,
                    error: (response, status, more) => {
                        error({"responseText" : extractErrorMessage(response)}, status, more);
                    }
                });
            });
        }
    }));

    // We also need to link the aton geometry toggle button with the the modal
    // panel so that by clicking the button the panel pops up. It's easier done
    // with jQuery.
    atonMessagesTable.buttons('.aton-geometry-toggle')
        .nodes()
        .attr({ "data-bs-toggle": "modal", "data-bs-target": "#atonGeometryPanel" });

    // We also need to link the aton content toggle button with the the modal
    // panel so that by clicking the button the panel pops up. It's easier done
    // with jQuery.
    atonMessagesTable.buttons('.aton-content-toggle')
        .nodes()
        .attr({ "data-bs-toggle": "modal", "data-bs-target": "#atonContentPanel" });

    // Now also initialise the aton geometry map before we need it
    atonMessagesMap = MapUtils.createMap('atonGeometryMap');

    // FeatureGroup is to store editable layers
    drawnItems = new L.FeatureGroup();
    atonMessagesMap.addLayer(drawnItems);

    atonMessagesMap.on('draw:created', (e) => {
        var type = e.layerType;
        var layer = e.layer;

        // Do whatever else you need to. (save to db, add to map etc)
        drawnItems.addLayer(layer);
    });

    // Invalidate the map size on show to fix the presentation
    MapUtils.refreshOnModalShow('#atonGeometryPanel', atonMessagesMap);
});

/**
 * This function will load the AtoN geometry onto the drawnItems variable
 * so that it is shown in the AtoN message maps layers.
 *
 * @param {Event}         event         The event that took place
 * @param {DataTable}     table         The AtoN messages table
 * @param {Node}          button        The button node that was pressed
 * @param {Configuration} config        The table configuration
 */
function loadAtonGeometry(event, table, button, config) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var aton = data[0];
    var geometry = aton.geometry;

    // Describe the selected AtoN above the map
    var centre = MapUtils.centreOf(geometry);
    $('#atonGeometryName').html(escapeHtml(MapUtils.atonName(aton)));
    $('#atonGeometryType').html(escapeHtml(aton.atonType || 'AtoN'));
    $('#atonGeometryPosition').html(centre
        ? `${centre.lat.toFixed(5)}, ${centre.lng.toFixed(5)}`
        : '&mdash;');
    $('#atonGeometryChartLink').attr('href', `map?aton=${encodeURIComponent(aton.idCode || '')}`);

    // Recreate the drawn items feature group
    drawnItems.clearLayers();
    if(geometry) {
        var geomLayer = MapUtils.geoJsonLayer(geometry, {
            pointToLayer: (feature, latlng) => L.marker(latlng, { icon: MapUtils.atonIcon(aton.atonType) })
        });
        geomLayer.bindPopup(MapUtils.atonPopup(aton));
        MapUtils.addNonGroupLayers(geomLayer, drawnItems);
        MapUtils.fitTo(atonMessagesMap, drawnItems, 14);
    }
}

/**
 * This function will load the AtoN content onto the AtoN content dialog text
 * area.
 *
 * @param {Event}         event         The event that took place
 * @param {DataTable}     table         The AtoN messages table
 * @param {Node}          button        The button node that was pressed
 * @param {Configuration} config        The table configuration
 */
function loadAtonContent(event, table, button, config) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var content = data[0].content;

    // Show the content
    $('#atonContentTextArea').val(content);
}
