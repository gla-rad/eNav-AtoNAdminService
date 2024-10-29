/**
 * Global variables
 */
var datasetTable = undefined;
var datasetContentLogMap = undefined;
var drawControl = undefined;
var drawnItems = undefined;

/**
 * The Dataset Content Log Table Column Definitions
 * @type {Array}
 */
var datasetContentLogColumnDefs = [
{
    data: "id",
    title: "ID",
    hoverMsg: "The Dataset Content Log ID",
    placeholder: "The Dataset Content Log ID",
    visible: false,
    searchable: false
}, {
    data: "uuid",
    title: "UUID",
    hoverMsg: "The Dataset UUID",
    placeholder: "The Dataset UUID",
    visible: true,
    searchable: false
}, {
     data: "datasetType",
     title: "DatasetTypeID",
     hoverMsg: "The Dataset Type",
     visible: true,
     searchable: false
}, {
    data: "operation",
    title: "Operation",
    hoverMsg: "Operation",
    required: true
 }, {
    data: "sequenceNo",
    title: "Sequence No",
    hoverMsg: "Sequence No",
    visible: true,
    searchable: false
 }, {
    data: "geometry",
    title: "Geometry",
    hoverMsg: "The Dataset Geometry",
    visible: false,
    searchable: false
 }, {
    data: "generatedAt",
    title: "Generated At",
    hoverMsg: "Generated At",
    visible: true,
    searchable: false
}];

// Run when the document is ready
$(() => {
    // And re-initialise it
    datasetContentLogTable = $('#dataset_content_logs_table').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            type: "POST",
            url: "./api/datasetcontentlog/dt",
            contentType: "application/json",
            data: (d) => {
                return JSON.stringify(d);
            },
            error: (response, status, more) => {
               error({"responseText" : response.getResponseHeader("X-atonService-error")}, status, more);
           }
        },
        columns: datasetContentLogColumnDefs,
        order: [[6, 'desc']],
        dom: '<"d-flex"<"flex-start"B><"flex-middle p-1"l><"flex-end flex-fill"f>><"d-flex mt-1 mb-1"t><"d-flex w-100"<"flex-fill"i><"flex-end"p>>',
        select: 'single',
        lengthMenu: [10, 25, 50, 75, 100],
        responsive: true,
        buttons: [{
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-map-location-dot"></i>',
            titleAttr: 'View Dataset Content Log Area',
            name: 'datasetContentLogGeometry', // do not change name
            className: 'dataset-geometry-toggle',
            action: (e, dt, node, config) => {
                loadDatasetContentLogGeometry(e, dt, node, config);
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-code"></i>',
            titleAttr: 'View Dataset Content Log Data',
            name: 'datasetContentLog', // do not change name
            className: 'dataset-content-log-toggle',
            action: (e, dt, node, config) => {
                loadDatasetContentLog(e, dt, node, config, 'Data');
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-code-compare"></i>',
            titleAttr: 'View Dataset Content Log Delta',
            name: 'datasetContentLog', // do not change name
            className: 'dataset-content-log-toggle',
            action: (e, dt, node, config) => {
                loadDatasetContentLog(e, dt, node, config, 'Delta');
            }
        }]
    });

    // We also need to link the aton geometry toggle button with the the modal
    // panel so that by clicking the button the panel pops up. It's easier done
    // with jQuery.
    datasetContentLogTable.buttons('.dataset-geometry-toggle')
        .nodes()
        .attr({ "data-bs-toggle": "modal", "data-bs-target": "#datasetContentLogGeometryPanel" });

    // We also need to link the aton content toggle button with the the modal
    // panel so that by clicking the button the panel pops up. It's easier done
    // with jQuery.
    datasetContentLogTable.buttons('.dataset-content-log-toggle')
        .nodes()
        .attr({ "data-bs-toggle": "modal", "data-bs-target": "#datasetContentLogPanel" });

    // Now also initialise the aton geometry map before we need it
    datasetContentLogMap = L.map('datasetContentLogGeometryMap').setView([54.910, -3.432], 5);
    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(datasetContentLogMap);

    // FeatureGroup is to store editable layers
    drawnItems = new L.FeatureGroup();
    datasetContentLogMap.addLayer(drawnItems);

    // Invalidate the map size on show to fix the presentation
    $('#datasetContentLogGeometryPanel').on('shown.bs.modal', () => {
        setTimeout(() => {
            datasetContentLogMap.invalidateSize();
        }, 10);
    });
});

/**
 * This function will load the dataset content log geometry onto the drawnItems
 * variable so that it is shown in the dataset content log maps layers.
 *
 * @param {Event}         event         The event that took place
 * @param {DataTable}     table         The dataset table
 * @param {Node}          button        The button node that was pressed
 * @param {Configuration} config        The table configuration
 */
function loadDatasetContentLogGeometry(event, table, button, config) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var geometry = data[0].geometry;

    // Recreate the drawn items feature group
    drawnItems.clearLayers();
    if(geometry) {
        var geomLayer = L.geoJson(geometry, {coordsToLatLng: (coords)=>coords});
        addNonGroupLayers(geomLayer, drawnItems);
        datasetContentLogMap.setView(geomLayer.getBounds().getCenter(), 5);
    }
}

/**
 * This function will load the dataset content log data/delta onto the dataset
 * content log dialog text area.
 *
 * @param {Event}         event         The event that took place
 * @param {DataTable}     table         The AtoN messages table
 * @param {Node}          button        The button node that was pressed
 * @param {Configuration} config        The table configuration
 * @param {String}        endpoint       The endpoint to be used (data/delta)
 */
function loadDatasetContentLog(event, table, button, config, endpoint) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var datasetId = data[0].id;

    // Initialise the popup and clear any previous output
    $('#datasetContentLogPanelHeader').html(`Dataset Content Log - ${endpoint}`)
    $('#datasetContentLogTextArea').val("Loading...");

    // And get the dataset content using the SECOM dataset endpoint
    $.ajax({
        url: `api/datasetcontentlog/${datasetId}/${endpoint.toLowerCase()}`,
        type: 'GET',
        contentType: 'text/plain; charset=utf-8',
        success: (response) => {
            // Show the content - we asked only for one dataset
            if(response) {
                // Format and display
                $('#datasetContentLogTextArea').val(formatXml(response));
            } else {
                $('#datasetContentLogTextArea').val("No data found");
            }
        },
        error: (response, status, more) => {
            showErrorDialog(response.getResponseHeader("X-atonService-error"));
        }
    });
}

// Would benefit from https://github.com/Leaflet/Leaflet/issues/4461
function addNonGroupLayers(sourceLayer, targetGroup) {
    if (sourceLayer instanceof L.LayerGroup) {
        sourceLayer.eachLayer((layer) => {
            addNonGroupLayers(layer, targetGroup);
        });
    } else {
        targetGroup.addLayer(sourceLayer);
    }
}


