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
    searchable: true
}, {
     data: "containerType",
     title: "Container",
     hoverMsg: "The Subscription Container Type",
     visible: true,
     searchable: true
}, {
    data: "dataProductType",
    title: "Data Product",
    hoverMsg: "The Subscription Data Product Type",
    visible: true,
    searchable: true
 }, {
    data: "dataReference",
    title: "Data Ref",
    hoverMsg: "The Subscription Data Reference",
    visible: true,
    searchable: true
 }, {
    data: "subscriptionGeometry",
    title: "Geometry",
    hoverMsg: "The Subscription Geometry",
    visible: false,
    searchable: false
 }, {
    data: "createdAt",
    title: "Created At",
    hoverMsg: "Created At",
    visible: true,
    searchable: false
}, {
    data: "updatedAt",
    title: "Updated At",
    hoverMsg: "Updated At",
    visible: true,
    searchable: false
}, {
     data: "clientMrn",
     title: "Client MRN",
     hoverMsg: "The Subscription Client MRN",
     visible: true,
     searchable: true
 }];

// Run when the document is ready
$(() => {
    // And re-initialise it
    subscriptionTable = $('#subscriptions_table').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            type: "POST",
            url: "./api/subscriptions/dt",
            contentType: "application/json",
            data: (d) => {
                return JSON.stringify(d);
            },
            error: (response, status, more) => {
               error({"responseText" : response.getResponseHeader("X-atonService-error")}, status, more);
           }
        },
        columns: subscriptionColumnDefs,
        order: [[6, 'desc']],
        dom: "<'row'<'col-lg-1 col-md-1'B><'col-lg-2 col-md-2'l><'col'f>><'row'<'col-md-12'rt>><'row'<'col-md-6'i><'col-md-6'p>>",
        select: 'single',
        lengthMenu: [10, 25, 50, 75, 100],
        responsive: true,
        buttons: [{
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-map-location-dot"></i>',
            titleAttr: 'View Subscription Area',
            name: 'subscriptionGeometry', // do not change name
            className: 'subscription-geometry-toggle',
            action: (e, dt, node, config) => {
                loadSubscriptionGeometry(e, dt, node, config);
            }
        }]
    });

    // We also need to link the aton geometry toggle button with the the modal
    // panel so that by clicking the button the panel pops up. It's easier done
    // with jQuery.
    subscriptionTable.buttons('.subscription-geometry-toggle')
        .nodes()
        .attr({ "data-bs-toggle": "modal", "data-bs-target": "#subscriptionGeometryPanel" });

    // Now also initialise the subscription geometry map before we need it
    subscriptionMap = L.map('subscriptionGeometryMap').setView([54.910, -3.432], 5);
    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(subscriptionMap);

    // FeatureGroup is to store editable layers
    drawnItems = new L.FeatureGroup();
    subscriptionMap.addLayer(drawnItems);

    // Invalidate the map size on show to fix the presentation
    $('#subscriptionGeometryPanel').on('shown.bs.modal', () => {
        setTimeout(() => {
            subscriptionMap.invalidateSize();
        }, 10);
    });
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
        var geomLayer = L.geoJson(geometry, {coordsToLatLng: (coords)=>coords});
        addNonGroupLayers(geomLayer, drawnItems);
        subscriptionMap.setView(geomLayer.getBounds().getCenter(), 5);
    }
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


