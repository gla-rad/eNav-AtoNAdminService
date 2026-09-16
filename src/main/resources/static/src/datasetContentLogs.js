/**
 * Global variables
 */
var datasetContentLogTable = undefined;
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
    searchable: false,
    className: "noVis"
}, {
    data: "uuid",
    title: "UUID",
    hoverMsg: "The Dataset UUID",
    placeholder: "The Dataset UUID",
    visible: true,
    searchable: false,
    render: (data, type) => type === 'display' ? renderIdentifier(data) : data
}, {
     data: "datasetType",
     title: "Dataset Type",
     hoverMsg: "The Dataset Type",
     visible: true,
     searchable: false,
     render: (data, type) => type === 'display' ? renderTag(data, 'accent') : data
}, {
    data: "operation",
    title: "Operation",
    hoverMsg: "Operation",
    required: true,
    render: (data, type) => type === 'display' ? renderOperation(data) : data
 }, {
    data: "sequenceNo",
    title: "Sequence No",
    hoverMsg: "Sequence No",
    visible: true,
    searchable: false,
    render: (data, type) => type === 'display' ? renderIdentifier(data) : data
 }, {
    data: "geometry",
    title: "Area",
    hoverMsg: "The Dataset Geometry",
    visible: true,
    sortable: false,
    searchable: false,
    render: (data, type) => type === 'display' ? renderLogArea(data) : data
 }, {
    data: "generatedAt",
    title: "Generated At",
    hoverMsg: "Generated At",
    visible: true,
    searchable: false,
    render: (data, type) => type === 'display' ? renderDateTime(data) : data
}];

/**
 * Renders the dataset operation as a tag whose colour reflects how disruptive
 * the operation is for the subscribers of the dataset.
 *
 * @param {String}  operation   The dataset operation
 * @return {String} The operation markup
 */
function renderOperation(operation) {
    const variants = {
        CREATED: 'success',
        UPDATED: 'accent',
        CANCELLED: 'danger',
        DELETED: 'danger',
        AUTO: 'warning',
        OTHER: undefined
    };
    return renderTag(operation, variants[String(operation).toUpperCase()]);
}

/**
 * Renders whether a dataset content log entry carries an area of coverage.
 *
 * @param {Object}  geometry    The dataset content log geometry
 * @return {String} The area markup
 */
function renderLogArea(geometry) {
    return MapUtils.positionsOf(geometry).length > 0
        ? renderTag('Defined', 'success')
        : renderTag('Not set');
}

// Run when the document is ready
$(() => {
    // And re-initialise it
    datasetContentLogTable = $('#dataset_content_logs_table').DataTable($.extend(commonDatatableOptions(), {
        ajax: {
            type: "POST",
            url: "./api/datasetcontentlog/dt",
            contentType: "application/json",
            data: (d) => {
                return JSON.stringify(d);
            },
            error: (response, status, more) => {
                showErrorDialog(extractErrorMessage(response));
            }
        },
        columns: datasetContentLogColumnDefs,
        order: [[6, 'desc']],
        buttons: [{
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-map-location-dot"></i><span class="dt-button-text">Area</span>',
            titleAttr: 'View the dataset content log area',
            name: 'datasetContentLogGeometry', // do not change name
            className: 'dataset-geometry-toggle',
            action: (e, dt, node, config) => {
                loadDatasetContentLogGeometry(e, dt, node, config);
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-code"></i><span class="dt-button-text">Data</span>',
            titleAttr: 'View the dataset content log data',
            name: 'datasetContentLog', // do not change name
            className: 'dataset-content-log-toggle',
            action: (e, dt, node, config) => {
                loadDatasetContentLog(e, dt, node, config, 'Data');
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-code-compare"></i><span class="dt-button-text">Delta</span>',
            titleAttr: 'View the dataset content log delta',
            name: 'datasetContentLogDelta', // do not change name
            className: 'dataset-content-log-toggle',
            action: (e, dt, node, config) => {
                loadDatasetContentLog(e, dt, node, config, 'Delta');
            }
        }].concat(commonDatatableButtons('S-201 Dataset Content Logs'))
    }));

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
    datasetContentLogMap = MapUtils.createMap('datasetContentLogGeometryMap');

    // FeatureGroup is to store editable layers
    drawnItems = new L.FeatureGroup();
    datasetContentLogMap.addLayer(drawnItems);

    // Invalidate the map size on show to fix the presentation
    MapUtils.refreshOnModalShow('#datasetContentLogGeometryPanel', datasetContentLogMap);
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
        var geomLayer = MapUtils.geoJsonLayer(geometry);
        MapUtils.addNonGroupLayers(geomLayer, drawnItems);
        MapUtils.fitTo(datasetContentLogMap, drawnItems, 10);
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
    $('#datasetContentLogPanelHeader').html(`<i class="fa-solid fa-code"></i>Dataset Content Log - ${endpoint}`);
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
            $('#datasetContentLogTextArea').val("");
            showErrorDialog(extractErrorMessage(response));
        }
    });
}
