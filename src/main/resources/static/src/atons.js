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
    searchable: false
}, {
     data: "idCode",
     title: "ID Code",
     hoverMsg: "The AtoN ID Code",
     placeholder: "The AtoN ID Code",
 }, {
     data: "atonType",
     title: "Type",
     hoverMsg: "The AtoN Type",
     placeholder: "The AtoN Type",
 }, {
    data: ( row, type, val, meta ) => row.featureNames && row.featureNames.length > 0 ? row.featureNames.find(fn => fn.displayName).name : '',
    title: "Name",
    hoverMsg: "The AtoN Name",
    placeholder: "The AtoN Name",
 }, {
    data: "dateStart",
    title: "Start Date",
    type: "date",
    hoverMsg: "The AtoN Start Date",
    placeholder: "The AtoN Start Date"
}, {
    data: "dateEnd",
    title: "End Date",
    type: "date",
    hoverMsg: "The AtoN End Date",
    placeholder: "The AtoN End Date"
 }, {
    data: "geometry",
    title: "Geometry",
    hoverMsg: "The AtoN Geometry",
    placeholder: "The AtoN Geometry",
    visible: false,
    searchable: false
 },{
    data: "content",
    title: "Content",
    type: "textarea",
    hoverMsg: "The AtoN S-201 Content",
    placeholder: "The AtoN S-201 Content",
    visible: false,
    searchable: false
}];

// Run when the document is ready
$(() => {
    // And re-initialise it
    atonMessagesTable = $('#atons_table').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            type: "POST",
            url: "./api/atons/dt",
            contentType: "application/json",
            data: (d) => {
                return JSON.stringify(d);
            },
            error: (response, status, more) => {
                error({"responseText" : response.getResponseHeader("X-atonService-error")}, status, more);
            }
        },
        columns: nodesColumnDefs,
        dom: "<'row'<'col-lg-2 col-md-2'B><'col-lg-2 col-md-2'l><'col'f>><'row'<'col-md-12'rt>><'row'<'col-md-6'i><'col-md-6'p>>",
        select: 'single',
        lengthMenu: [10, 25, 50, 75, 100],
        responsive: true,
        altEditor: true, // Enable altEditor
        buttons: [{
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-trash-can"></i>',
            titleAttr: 'Delete Node',
            name: 'delete' // do not change name
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-map-location-dot"></i>',
            titleAttr: 'View Message Geometry',
            name: 'messageGeometry', // do not change name
            className: 'aton-geometry-toggle',
            action: (e, dt, node, config) => {
                loadAtonGeometry(e, dt, node, config);
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-code"></i>',
            titleAttr: 'View AtoN S-201 Content',
            name: 'atonContent', // do not change name
            className: 'aton-content-toggle',
            action: (e, dt, node, config) => {
                loadAtonContent(e, dt, node, config);
            }
        }],
        onDeleteRow: (datatable, selectedRows, success, error) => {
            selectedRows.every(function(rowIdx, tableLoop, rowLoop) {
                $.ajax({
                    type: 'DELETE',
                    url: `./api/atons/${this.data()["id"]}`,
                    crossDomain: true,
                    success: success,
                    error: (response, status, more) => {
                        error({"responseText" : response.getResponseHeader("X-atonService-error")}, status, more);
                    }
                });
            });
        }
    });

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
    atonMessagesMap = L.map('atonGeometryMap').setView([54.910, -3.432], 5);
    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(atonMessagesMap);

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
    $('#atonGeometryPanel').on('shown.bs.modal', () => {
        setTimeout(() => {
            atonMessagesMap.invalidateSize();
        }, 10);
    });
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
    var geometry = data[0].geometry;

    // Recreate the drawn items feature group
    drawnItems.clearLayers();
    if(geometry) {
        var geomLayer = L.geoJson(geometry);
        addNonGroupLayers(geomLayer, drawnItems);
        atonMessagesMap.setView(geomLayer.getBounds().getCenter(), 5);
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


