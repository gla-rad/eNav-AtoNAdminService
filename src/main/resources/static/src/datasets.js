/**
 * Global variables
 */
var datasetTable = undefined;
var datasetMap = undefined;
var drawControl = undefined;
var drawnItems = undefined;

/**
 * The Dataset Table Column Definitions
 * @type {Array}
 */
var datasetColumnDefs = [
{
    data: "uuid",
    title: "UUID",
    hoverMsg: "The Dataset UUID",
    placeholder: "The Dataset UUID",
    type: "hidden",
    visible: false,
    searchable: false
}, {
     data: "datasetIdentificationInformation.id",
     title: "Information ID",
     hoverMsg: "The Dataset Identification Information ID",
     placeholder: "The Dataset Identification Information ID",
     type: "hidden",
     visible: false,
     searchable: false
}, {
    data: "datasetIdentificationInformation.datasetTitle",
    title: "Title",
    hoverMsg: "The Dataset Title",
    placeholder: "The Dataset Title",
    required: true,
    width: "20%"
 }, {
    data: "datasetIdentificationInformation.encodingSpecification",
    title: "Encoding",
    hoverMsg: "The Dataset Encoding",
    placeholder: "The Dataset Encoding",
    type: "select",
    options: {
        "S100 Part 10b":"S100 Part 10b"
    },
    required: true
 }, {
    data: "datasetIdentificationInformation.encodingSpecificationEdition",
    title: "Encoding Edition",
    hoverMsg: "The Dataset Encoding Edition",
    placeholder: "The Dataset Encoding Edition",
    type: "select",
    options: {
        "1.0.0":"1.0.0"
    },
    required: true
 }, {
    data: "datasetIdentificationInformation.productIdentifier",
    title: "Product Identifier",
    hoverMsg: "The Dataset Product Identifier",
    placeholder: "The Dataset Product Identifier",
    type: "select",
    options: {
        "S-201":"S-201"
    },
    required: true
}, {
    data: "datasetIdentificationInformation.productEdition",
    title: "Product Edition",
    hoverMsg: "The Dataset Product Edition",
    placeholder: "The Dataset Product Edition",
    type: "select",
    options: {
        "1.1.0":"1.1.0"
    },
    required: true
 }, {
    data: "datasetIdentificationInformation.applicationProfile",
    title: "Application Profile",
    hoverMsg: "The Dataset Application Profile",
    placeholder: "The Dataset Application Profile",
    required: true
}, {
    data: "datasetIdentificationInformation.datasetFileIdentifier",
    title: "File Identifier",
    hoverMsg: "The Dataset File Identifier",
    placeholder: "The Dataset File Identifier",
    required: true
 }, {
    data: "geometry",
    title: "Geometry",
    hoverMsg: "The Dataset Geometry",
    placeholder: "The Dataset Geometry",
    type: "hidden",
    visible: false,
    searchable: false
 },{
    data: "datasetIdentificationInformation.datasetAbstract",
    title: "Abstract",
    type: "textarea",
    hoverMsg: "The Dataset Abstract",
    placeholder: "The Dataset Abstract",
    visible: false,
    searchable: false
}, {
    data: "createdAt",
    title: "Created At",
    type: "hidden",
    hoverMsg: "Dataset Created At",
    placeholder: "Dataset Created At",
    visible: false,
    searchable: false
}, {
    data: "lastUpdatedAt",
    title: "Updated At",
    type: "hidden",
    hoverMsg: "Dataset Updated At",
    placeholder: "Dataset Updated At",
    searchable: false
}, {
     data: "datasetContent.generatedAt",
     title: "Content Updated At",
     type: "hidden",
     hoverMsg: "Dataset Content Updated At",
     placeholder: "Dataset Content Updated At",
     searchable: false
 }, {
    data: "cancelled",
    title: "Cancelled",
    type: "hidden",
    hoverMsg: "Cancelled",
    placeholder: "Cancelled",
    visible: false,
    searchable: false
}];

// Run when the document is ready
$(() => {
    // And re-initialise it
    datasetTable = $('#dataset_table').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            type: "POST",
            url: `./api/dataset/dt`,
            contentType: "application/json",
            data: (d) => {
                // Add the include cancelled datasets option
                d.search.includeCancelled = $('#showCancelledDatasetsSwitch').is(":checked") ? true : false;
                // And build the JSON object
                return JSON.stringify(d);
            },
            error: (response, status, more) => {
                error({"responseText" : response.getResponseHeader("X-atonService-error")}, status, more);
            }
        },
        columns: datasetColumnDefs,
        dom: '<"d-flex"<"flex-start"B><"flex-middle p-1"l><"flex-end flex-fill"f>><"d-flex mt-1 mb-1"t><"d-flex w-100"<"flex-fill"i><"flex-end"p>>',
        select: 'single',
        lengthMenu: [10, 25, 50, 75, 100],
        responsive: true,
        altEditor: true, // Enable altEditor
        buttons: [{
            text: '<i class="fa-solid fa-circle-plus"></i>',
            titleAttr: 'Add Dataset',
            name: 'add' // do not change name
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-pen-to-square"></i>',
            titleAttr: 'Edit Dataset',
            name: 'edit' // do not change name
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-ban"></i>',
            titleAttr: 'Cancel Dataset',
            name: 'cancel',
            action: (e, dt, node, config) => {
                cancelDataset(e, dt, node, config);
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-copy"></i>',
            titleAttr: 'Replace Dataset',
            name: 'replace',
            action: (e, dt, node, config) => {
                replaceDataset(e, dt, node, config);
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-trash-can"></i>',
            titleAttr: 'Delete Dataset',
            name: 'delete' // do not change name
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-map-location-dot"></i>',
            titleAttr: 'View Dataset Area',
            name: 'datasetGeometry', // do not change name
            className: 'dataset-geometry-toggle',
            action: (e, dt, node, config) => {
                loadDatasetGeometry(e, dt, node, config);
            }
        }, {
            extend: 'selected', // Bind to Selected row
            text: '<i class="fa-solid fa-code"></i>',
            titleAttr: 'View S-201 Dataset Content',
            name: 'datasetContent', // do not change name
            className: 'dataset-content-toggle',
            action: (e, dt, node, config) => {
                loadDatasetContent(e, dt, node, config);
            }
        }],
        onAddRow: (datatable, rowdata, success, error) => {
            $.ajax({
                url: './api/dataset',
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                crossDomain: true,
                dataType: 'json',
                data: JSON.stringify({
                    uuid: rowdata["uuid"],
                    datasetIdentificationInformation: {
                        datasetTitle: rowdata["datasetIdentificationInformation.datasetTitle"],
                        encodingSpecification: rowdata["datasetIdentificationInformation.encodingSpecification"],
                        encodingSpecificationEdition: rowdata["datasetIdentificationInformation.encodingSpecificationEdition"],
                        productIdentifier: rowdata["datasetIdentificationInformation.productIdentifier"],
                        productEdition: rowdata["datasetIdentificationInformation.productEdition"],
                        applicationProfile: rowdata["datasetIdentificationInformation.applicationProfile"],
                        datasetFileIdentifier: rowdata["datasetIdentificationInformation.datasetFileIdentifier"],
                        datasetAbstract: rowdata["datasetIdentificationInformation.datasetAbstract"],
                    },
                    geometry: null,
                    createdAt: null,
                    lastUpdatedAt: null,
                    datasetContentGeneratedAt: null,
                    cancelled: null
                }),
                success: success,
                error: (response, status, more) => {
                    error({"responseText" : response.getResponseHeader("X-atonService-error")}, status, more);
                }
            });
        },
        onEditRow: (datatable, rowdata, success, error) => {
            // The geometry is not read correctly so we need to access it in-direclty
            var idx = datasetTable.cell('.selected', 0).index();
            var data = datasetTable.rows(idx.row).data();
            var geometry = data[0].geometry;
            $.ajax({
                url: `./api/dataset/${rowdata["uuid"]}`,
                type: 'PUT',
                contentType: 'application/json; charset=utf-8',
                crossDomain: true,
                dataType: 'json',
                data: JSON.stringify({
                    uuid: rowdata["uuid"],
                    datasetIdentificationInformation: {
                        id: rowdata["datasetIdentificationInformation.id"],
                        datasetTitle: rowdata["datasetIdentificationInformation.datasetTitle"],
                        encodingSpecification: rowdata["datasetIdentificationInformation.encodingSpecification"],
                        encodingSpecificationEdition: rowdata["datasetIdentificationInformation.encodingSpecificationEdition"],
                        productIdentifier: rowdata["datasetIdentificationInformation.productIdentifier"],
                        productEdition: rowdata["datasetIdentificationInformation.productEdition"],
                        applicationProfile: rowdata["datasetIdentificationInformation.applicationProfile"],
                        datasetFileIdentifier: rowdata["datasetIdentificationInformation.datasetFileIdentifier"],
                        datasetAbstract: rowdata["datasetIdentificationInformation.datasetAbstract"],
                    },
                    geometry: geometry,
                    createdAt: rowdata["createdAt"],
                    lastUpdatedAt: rowdata["lastUpdatedAt"],
                    datasetContentGeneratedAt: rowdata["datasetContentGeneratedAt"],
                    cancelled: rowdata["cancelled"]
                }),
                success: success,
                error: (response, status, more) => {
                     error({"responseText" : response.getResponseHeader("X-atonService-error")}, status, more);
                }
            });
        },
        onDeleteRow: (datatable, selectedRows, success, error) => {
            selectedRows.every(function(rowIdx, tableLoop, rowLoop) {
                $.ajax({
                    type: 'DELETE',
                    url: `./api/dataset/${this.data()["uuid"]}`,
                    crossDomain: true,
                    success: success,
                    error: (response, status, more) => {
                        error({"responseText" : response.getResponseHeader("X-atonService-error")}, status, more);
                    }
                });
            });
        },
        // Indicate the dataset rows that have been cancelled
        createdRow: (row, data, dataIndex) => {
            if(data["cancelled"] == true) {
                $(row).addClass('cancelled');
            }
        }
    });

    // On changes in the cancelled dataset inclusion option, we need to
    // reload the whole table
    $('#showCancelledDatasetsSwitch').change(() => datasetTable.ajax.reload());

    // We also need to link the aton geometry toggle button with the the modal
    // panel so that by clicking the button the panel pops up. It's easier done
    // with jQuery.
    datasetTable.buttons('.dataset-geometry-toggle')
        .nodes()
        .attr({ "data-bs-toggle": "modal", "data-bs-target": "#datasetGeometryPanel" });

    // We also need to link the aton content toggle button with the the modal
    // panel so that by clicking the button the panel pops up. It's easier done
    // with jQuery.
    datasetTable.buttons('.dataset-content-toggle')
        .nodes()
        .attr({ "data-bs-toggle": "modal", "data-bs-target": "#datasetContentPanel" });

    // Now also initialise the aton geometry map before we need it
    datasetMap = L.map('datasetGeometryMap').setView([54.910, -3.432], 5);
    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(datasetMap);

    // FeatureGroup is to store editable layers
    drawnItems = new L.FeatureGroup();
    datasetMap.addLayer(drawnItems);

    // Add the draw toolbar
    drawControl = new L.Control.Draw({
        draw: {
            marker: false,
            polyline: false,
            polygon: true,
            rectangle: true,
            circle: true,
            circlemarker: false,
        },
        edit: {
            featureGroup: drawnItems,
            remove: true
        }
    });

    datasetMap.on('draw:created', (e) => {
        var type = e.layerType;
        var layer = e.layer;

        // Do whatever else you need to. (save to db, add to map etc)
        drawnItems.addLayer(layer);
    });

    // Invalidate the map size on show to fix the presentation
    $('#datasetGeometryPanel').on('shown.bs.modal', () => {
        setTimeout(() => {
            datasetMap.invalidateSize();
        }, 10);
    });
});

/**
 * This function will cancel the selected dataset if possible based on its
 * UUID.
 *
 * @param {Event}         event         The event that took place
 * @param {DataTable}     table         The dataset table
 * @param {Node}          button        The button node that was pressed
 * @param {Configuration} config        The table configuration
 */
function cancelDataset(event, table, button, config) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var datasetId = data[0].uuid;

    // Show the confirmation dialog
    showConfirmationDialog(`
           <p>The selected dataset with UUID:<p>
           <p class="fw-bold">${datasetId}</p>
           <p>will be cancelled. This action cannot be undone.<p>
           <p class="text-danger">Are  you sure you want to proceed?</p>
       `,
       () => {
          $.ajax({
                url: `./api/dataset/${datasetId}/cancel`,
                type: 'PUT',
                contentType: 'application/json; charset=utf-8',
                success: (response) => datasetTable.ajax.reload(),
                error: (response, status, more) => {
                    showErrorDialog(response.getResponseHeader("X-atonService-error"));
                }
          });
       }
    );
}

/**
 * This function will replace the selected dataset if possible based on its
 * UUID.
 *
 * @param {Event}         event         The event that took place
 * @param {DataTable}     table         The dataset table
 * @param {Node}          button        The button node that was pressed
 * @param {Configuration} config        The table configuration
 */
function replaceDataset(event, table, button, config) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var datasetId = data[0].uuid;

    // Show the confirmation dialog
    showConfirmationDialog(`
           <p>The selected dataset with UUID:<p>
           <p class="fw-bold">${datasetId}</p>
           <p>will be replaces. This action involves cancelling the existing dataset and cannot be undone.<p>
           <p class="text-danger">Are  you sure you want to proceed?</p>
       `,
       () => {
          $.ajax({
                url: `./api/dataset/${datasetId}/replace`,
                type: 'PUT',
                contentType: 'application/json; charset=utf-8',
                success: (response) => datasetTable.ajax.reload(),
                error: (response, status, more) => {
                    showErrorDialog(response.getResponseHeader("X-atonService-error"));
                }
          });
       }
    );
}

/**
 * This function will load the dataset geometry onto the drawnItems variable
 * so that it is shown in the dataset message maps layers.
 *
 * @param {Event}         event         The event that took place
 * @param {DataTable}     table         The dataset table
 * @param {Node}          button        The button node that was pressed
 * @param {Configuration} config        The table configuration
 */
function loadDatasetGeometry(event, table, button, config) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var geometry = data[0].geometry;

    // Refresh the stations map control
    datasetMap.removeControl(drawControl);
    datasetMap.addControl(drawControl);

    // Recreate the drawn items feature group
    drawnItems.clearLayers();
    if(geometry) {
        var geomLayer = L.geoJson(geometry, {coordsToLatLng: (coords)=>coords});
        addNonGroupLayers(geomLayer, drawnItems);
        datasetMap.setView(geomLayer.getBounds().getCenter(), 5);
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
function loadDatasetContent(event, table, button, config) {
    var idx = table.cell('.selected', 0).index();
    var data = table.rows(idx.row).data();
    var datasetId = data[0].uuid;

    // First clear any previous output
    $('#datasetContentTextArea').val("Loading...");

    // And get the dataset content using the SECOM dataset endpoint
    $.ajax({
        url: `api/secom/v1/object?dataReference=${datasetId}`,
        type: 'GET',
        contentType: 'application/json; charset=utf-8',
        success: (response) => {
            // Show the content - we asked only for one dataset
            if(response.dataResponseObject && response.dataResponseObject.length == 1) {
                var raw = response.dataResponseObject[0].data;
                // Decode as required
                var processed = atob(raw);
                processed = processed.split('').map(x => x.charCodeAt(0));
                // Decompress if required
                if(response.dataResponseObject[0].exchangeMetadata.compressionFlag) {
                    processed = pako.ungzip(new Uint8Array(decoded), { to: 'string' });
                    processed = processed.split('').map(x => x.charCodeAt(0));
                }
                // Decrypt if required
                if(response.dataResponseObject[0].exchangeMetadata.dataProtection) {
                    log.warn("Decryption not supported yet!!!");
                    processed = processed; // Not supported yet
                }
                // To XML string - For large files, split the process into chunks
                var xml = "";
                while(processed.length > 0) {
                    xml += formatXml(String.fromCharCode.apply(null, processed.splice(0, 100000)));
                }
                // And finally display
                $('#datasetContentTextArea').val(xml);
            } else {
                $('#datasetContentTextArea').val("No data found");
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

/**
 * Saves the dataset geometry into the selected station entry from the dataset
 * table. The current geometry selection is found in the global drawnItems
 * variable. The Leaflet Draw geometry object should first be translated into
 * GeoJSON and then be set as the selected station's geometry.
 */
function saveGeometry() {
    // Get the selected station
    var dataset = datasetTable.row({selected : true}).data();

    // If a selection has been made
    if(dataset) {
        // Convert the feature collection to a geometry collection
        dataset.geometry = {
            type: "GeometryCollection",
            geometries: []
        };
        L.geoJson(drawnItems.toGeoJSON(), {coordsToLatLng: (coords)=>coords})
            .toGeoJSON()
            .features
            .forEach(feature => {
               dataset.geometry.geometries.push(feature.geometry);
            });

        $.ajax({
            url: `./api/dataset/${dataset.uuid}`,
            type: 'PUT',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            data: JSON.stringify(dataset),
            success: () => {console.log("success"); datasetTable.ajax.reload();},
            error: (response, status, more) => {
               showErrorDialog(response.getResponseHeader("X-atonService-error"));
            }
        });
    }
}

