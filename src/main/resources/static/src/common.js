/**
 * A helper function to handle confirmation UI operations.
 *
 * @param {String}      text    The confirmation text to be displayed
 * @param {Function}    action  The action to be performed after confirmation
 */
function showConfirmationDialog(text, action) {
    // Initialise the confirmation dialog
    $('#confirmationDialog .modal-body').html(text);

    // Link the button (remove any previous links)
    $('#confirmationDialog button.btn-primary')
        .off('click')
        .click((e) => action());

    // And show the dialog
    $('#confirmationDialog').modal('show');
}

/**
 * A helper function to handle error UI operations.
 *
 * @param {String}      text    The error text to be displayed
 */
function showErrorDialog(text, action) {
    // Initialise the confirmation dialog
    $('#errorDialog .modal-body').html(text);

    // And show the dialog
    $('#errorDialog').modal('show');
}

/**
 * A helper function to prettify the XML string provided.
 *
 * @param {String}  xml     The XML input to be prettified
 */
function formatXml(xml) {
    var formatted = '';
    var reg = new RegExp("(>)(<)(\/*)", "g");
    xml = xml != undefined ? xml.replace(reg, '$1\r\n$2$3') : '';
    var pad = 0;
    var xmlArray = xml.split('\r\n');
    jQuery.each(xmlArray, (index, node) => {
        var last = index === xmlArray.length - 1;
        var indent = 0;
        if (node.match( /.+<\/\w[^>]*>$/ )) {
            indent = 0;
        } else if (node.match( /^<\/\w/ )) {
            if (pad != 0) {
                pad -= 1;
            }
        } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
            indent = 1;
        } else {
            indent = 0;
        }

        var padding = '';
        for (var i = 0; i < pad; i++) {
            padding += '  ';
        }

        formatted += padding + node + (last ? '' : '\r\n');
        pad += indent;
    });

    return formatted;
}