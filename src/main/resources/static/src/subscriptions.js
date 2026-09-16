/**
 * Global variables
 */
var subscriptionTable = undefined;
var subscriptionMap = undefined;
var drawControl = undefined;
var drawnItems = undefined;

/**
 * The Subscriptions Table Column Definitions
 * @type {Array}
 */
var subscriptionColumnDefs = [
{
    data: "uuid",
    title: "UUID",
    hoverMsg: "The Subscription UUID",
    placeholder: "The Subscription UUID",
    visible: true,
    searchable: true,
    render: (data, type) => type === 'display' ? renderIdentifier(data) : data
}, {
     data: "containerType",
     title: "Container",
     hoverMsg: "The Subscription Container Type",
     visible: true,
     searchable: true,
     render: (data, type) => type === 'display' ? renderTag(data) : data
}, {
    data: "dataProductType",
    title: "Data Product",
    hoverMsg: "The Subscription Data Product Type",
    visible: true,
    searchable: true,
    render: (data, type) => type === 'display' ? renderTag(data, 'accent') : data
 }, {
    data: "dataReference",
    title: "Data Ref",
    hoverMsg: "The Subscription Data Reference",
    visible: true,
    searchable: true,
    render: (data, type) => type === 'display' ? renderIdentifier(data) : data
 }, {
    data: "subscriptionGeometry",
    title: "Area",
    hoverMsg: "The Subscription Geometry",
    visible: true,
    sortable: false,
    searchable: false,
    render: (data, type) => type === 'display' ? renderSubscriptionArea(data) : data
 }, {
    data: "createdAt",
    title: "Created At",
    hoverMsg: "Created At",
    visible: true,
    searchable: false,
    render: (data, type) => type === 'display' ? renderDateTime(data) : data
}, {
    data: "updatedAt",
    title: "Updated At",
    hoverMsg: "Updated At",
    visible: true,
    searchable: false,
    render: (data, type) => type === 'display' ? renderDateTime(data) : data
}, {
     data: "clientMrn",
     title: "Client MRN",
     hoverMsg: "The Subscription Client MRN",
     visible: true,
     searchable: true,
     render: (data, type) => type === 'display' ? renderIdentifier(data) : data
 }];

/**
 * Renders whether a subscription is restricted to a geographical area.
 *
 * @param {Object}  geometry    The subscription geometry
 * @return {String} The area markup
 */
function renderSubscriptionArea(geometry) {
    return MapUtils.positionsOf(geometry).length > 0
        ? renderTag('Restricted', 'accent')
        : renderTag('Unrestricted');
}

// Run when the document is ready
$(() => {
    // And re-initialise it
    subscriptionTable = $('#subscriptions_table').DataTable($.extend(commonDatatableOptions(), {
        ajax: {
            type: "POST",
            url: "./api/subscriptions/dt",
            contentType: "application/json",
            data: (d) => {
                return JSON.stringify(d);
            },
            error: (response, status, more) => {
                showErrorDialog(extractErrorMessage(response));
            }
        },
        columns: subscriptionColumnDefs,
        order: [[6, 'desc']],
        buttons: [{
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-map-location-dot"></i><span class="dt-button-text">Area</span>',
            titleAttr: 'View the subscription area',
            name: 'subscriptionGeometry', // do not change name
            className: 'subscription-geometry-toggle',
            action: (e, dt, node, config) => {
                loadSubscriptionGeometry(e, dt, node, config);
            }
        }].concat(commonDatatableButtons('SECOM Subscriptions'))
    }));

    // We also need to link the aton geometry toggle button with the the modal
    // panel so that by clicking the button the panel pops up. It's easier done
    // with jQuery.
    subscriptionTable.buttons('.subscription-geometry-toggle')
        .nodes()
        .attr({ "data-bs-toggle": "modal", "data-bs-target": "#subscriptionGeometryPanel" });

    // Now also initialise the subscription geometry map before we need it
    subscriptionMap = MapUtils.createMap('subscriptionGeometryMap');

    // FeatureGroup is to store editable layers
    drawnItems = new L.FeatureGroup();
    subscriptionMap.addLayer(drawnItems);

    // Invalidate the map size on show to fix the presentation
    MapUtils.refreshOnModalShow('#subscriptionGeometryPanel', subscriptionMap);
});

/**
 * This function will load the subscription geometry onto the drawnItems
 * variable so that it is shown in the subscription map layers.
 *
 * @param {Event}         event         The event that took place
 * @param {DataTable}     table         The dataset table
 * @param {Node}          button        The button node that was pressed
 * @param {Configuration} config        The table configuration
 */
function loadSubscriptionGeometry(event, table, button, config) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var geometry = data[0].subscriptionGeometry;

    // Recreate the drawn items feature group
    drawnItems.clearLayers();
    if(geometry) {
        var geomLayer = MapUtils.geoJsonLayer(geometry);
        MapUtils.addNonGroupLayers(geomLayer, drawnItems);
        MapUtils.fitTo(subscriptionMap, drawnItems, 10);
    }
}
