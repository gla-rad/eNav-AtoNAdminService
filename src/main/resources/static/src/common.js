/**
 * The key under which the selected colour theme is remembered.
 */
const THEME_STORAGE_KEY = 'aton-admin-theme';

/**
 * Returns the colour theme currently applied to the document.
 *
 * @return {String} Either "light" or "dark"
 */
function getTheme() {
    return document.documentElement.getAttribute('data-bs-theme') === 'dark' ? 'dark' : 'light';
}

/**
 * Applies the provided colour theme to the document and remembers the choice
 * for the next visit. Any listener registered through onThemeChange() is
 * notified so that components which cannot be styled with CSS alone - the
 * Leaflet base layers for instance - can react.
 *
 * @param {String}  theme   The theme to apply ("light" or "dark")
 */
function setTheme(theme) {
    const applied = theme === 'dark' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-bs-theme', applied);
    try {
        window.localStorage.setItem(THEME_STORAGE_KEY, applied);
    } catch (e) {
        // Storage may be unavailable (private mode), the theme still applies
    }
    $(document).trigger('aton:themechange', [applied]);
}

/**
 * Registers a callback to be invoked whenever the colour theme changes.
 *
 * @param {Function}    callback    Receives the newly applied theme name
 */
function onThemeChange(callback) {
    $(document).on('aton:themechange', (event, theme) => callback(theme));
}

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
    $('#errorDialog .modal-body').html(text || 'An unexpected error occurred.');

    // And show the dialog
    $('#errorDialog').modal('show');
}

/**
 * Pops up a short lived notification in the bottom right corner of the screen.
 * This is used for the operations that succeed quietly and therefore do not
 * deserve a modal dialog of their own.
 *
 * @param {String}  message     The message to be displayed
 * @param {String}  variant     One of "info", "success", "warning" or "danger"
 */
function showToast(message, variant) {
    const icons = {
        info: 'fa-circle-info text-primary',
        success: 'fa-circle-check text-success',
        warning: 'fa-triangle-exclamation text-warning',
        danger: 'fa-circle-exclamation text-danger'
    };
    const icon = icons[variant] || icons.info;

    // Make sure the stack that hosts the toasts exists
    let stack = $('#toastStack');
    if (stack.length === 0) {
        stack = $('<div id="toastStack" class="toast-stack"></div>').appendTo('body');
    }

    // Build, show and then dispose of the toast
    const toast = $(`
        <div class="toast align-items-center" role="alert" aria-live="polite" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center gap-2">
                    <i class="fa-solid ${icon}"></i><span></span>
                </div>
                <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>`);
    toast.find('.toast-body span').text(message);
    stack.append(toast);
    toast.on('hidden.bs.toast', () => toast.remove());
    new bootstrap.Toast(toast[0], { delay: 4000 }).show();
}

/**
 * Extracts the most meaningful message out of a failed jQuery AJAX response.
 * The service reports its errors through a dedicated header, so that is the
 * first place to look at.
 *
 * @param {Object}  response    The jQuery AJAX response object
 * @return {String} The error message to be presented to the user
 */
function extractErrorMessage(response) {
    return (response && response.getResponseHeader && response.getResponseHeader("X-atonService-error"))
        || (response && response.statusText)
        || 'An unexpected error occurred.';
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

/**
 * Copies the content of the given input or text area to the clipboard and
 * confirms the operation with a toast.
 *
 * @param {String}  selector    The selector of the element to be copied
 */
function copyToClipboard(selector) {
    const element = $(selector);
    if (element.length === 0) {
        return;
    }
    const text = element.val();

    // The asynchronous clipboard API is only available in secure contexts, so
    // fall back onto selecting the content and letting the browser copy it
    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text)
            .then(() => showToast('Copied to the clipboard', 'success'))
            .catch(() => showToast('The clipboard is not available', 'warning'));
    } else {
        element.trigger('select');
        document.execCommand('copy');
        showToast('Copied to the clipboard', 'success');
    }
}

/**
 * Escapes a value so that it can safely be dropped into an HTML string. Any
 * empty value is rendered as an em dash to keep the tables tidy.
 *
 * @param {*}   value   The value to be escaped
 * @return {String} The HTML-safe representation of the value
 */
function escapeHtml(value) {
    if (value === null || value === undefined || value === '') {
        return '&mdash;';
    }
    return $('<div>').text(value).html();
}

/**
 * Renders a value as one of the pill shaped tags used across the tables.
 *
 * @param {String}  value       The text of the tag
 * @param {String}  variant     An optional variant ("accent", "success", ...)
 * @return {String} The tag markup
 */
function renderTag(value, variant) {
    if (!value) {
        return '<span class="cell-muted">&mdash;</span>';
    }
    const suffix = variant ? ` tag-${variant}` : '';
    return `<span class="tag${suffix}">${escapeHtml(value)}</span>`;
}

/**
 * Renders an identifier - a UUID or an ID code - in a monospaced font so that
 * long opaque strings stay scannable.
 *
 * @param {String}  value   The identifier
 * @return {String} The identifier markup
 */
function renderIdentifier(value) {
    if (!value) {
        return '<span class="cell-muted">&mdash;</span>';
    }
    return `<span class="cell-mono">${escapeHtml(value)}</span>`;
}

/**
 * Renders a date or a date-time in a compact and sortable representation.
 *
 * @param {String}  value   The ISO date or date-time
 * @return {String} The date markup
 */
function renderDateTime(value) {
    if (!value) {
        return '<span class="cell-muted">&mdash;</span>';
    }
    const text = String(value);

    // A plain date needs no reformatting, and parsing it would risk shifting
    // it by a day in the timezones west of Greenwich
    if (text.indexOf('T') < 0) {
        return `<span class="cell-mono">${escapeHtml(text.substring(0, 10))}</span>`;
    }

    const date = new Date(text);
    if (isNaN(date.getTime())) {
        return escapeHtml(text);
    }
    const pad = (number) => String(number).padStart(2, '0');
    const shown = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
        + ` ${pad(date.getHours())}:${pad(date.getMinutes())}`;
    return `<span class="cell-mono">${escapeHtml(shown)}</span>`;
}

/**
 * The default Datatables options shared by every table of the application.
 * Individual tables merge their own columns, buttons and callbacks on top of
 * these using $.extend().
 * <p>
 * The state is saved so that the page length, the ordering and the chosen
 * columns survive a reload. Datatables keys that state by table and page on
 * its own, and discards it whenever the column set of a table changes.
 *
 * @return {Object} The common Datatables configuration
 */
function commonDatatableOptions() {
    return {
        processing: true,
        serverSide: true,
        responsive: true,
        select: 'single',
        autoWidth: false,
        stateSave: true,
        stateDuration: 60 * 60 * 24 * 30,
        pageLength: 25,
        lengthMenu: [10, 25, 50, 75, 100],
        language: {
            search: '',
            searchPlaceholder: 'Search…',
            lengthMenu: 'Show _MENU_',
            info: '_START_–_END_ of _TOTAL_',
            infoEmpty: 'No entries',
            infoFiltered: '(filtered from _MAX_)',
            emptyTable: 'Nothing to show here yet',
            zeroRecords: 'No entry matches the current search',
            processing: '<i class="fa-solid fa-circle-notch fa-spin"></i>&nbsp;Loading…'
        },
        layout: {
            topStart: 'buttons',
            topEnd: 'search',
            bottomStart: ['pageLength', 'info'],
            bottomEnd: 'paging'
        }
    };
}

/**
 * Builds the two utility buttons every table gets for free - a reload and a
 * column visibility picker - plus the export collection.
 *
 * @param {String}  title   The title used for the exported files
 * @return {Array} The list of Datatables button definitions
 */
function commonDatatableButtons(title) {
    return [{
        extend: 'collection',
        text: '<i class="fa-solid fa-file-export"></i><span class="dt-button-text">Export</span>',
        titleAttr: 'Export the current view',
        autoClose: true,
        buttons: [{
            extend: 'copyHtml5',
            text: '<i class="fa-solid fa-copy"></i>&nbsp;Copy to clipboard',
            title: title,
            exportOptions: { columns: ':visible' }
        }, {
            extend: 'csvHtml5',
            text: '<i class="fa-solid fa-file-csv"></i>&nbsp;Download as CSV',
            title: title,
            exportOptions: { columns: ':visible' }
        }, {
            extend: 'print',
            text: '<i class="fa-solid fa-print"></i>&nbsp;Print',
            title: title,
            exportOptions: { columns: ':visible' }
        }]
    }, {
        extend: 'colvis',
        text: '<i class="fa-solid fa-table-columns"></i><span class="dt-button-text">Columns</span>',
        titleAttr: 'Choose the visible columns',
        columns: ':not(.noVis)'
    }, {
        text: '<i class="fa-solid fa-rotate"></i><span class="dt-button-text">Reload</span>',
        titleAttr: 'Reload the table data',
        action: (e, dt) => dt.ajax.reload(null, false)
    }];
}

/**
 * Wires up the elements that are present on every page - currently just the
 * colour theme toggle sitting in the navigation bar.
 */
$(() => {
    $('[data-theme-toggle]').on('click', () => setTheme(getTheme() === 'dark' ? 'light' : 'dark'));

    // Bootstrap's tooltips are opt-in, and the toolbars rely on them
    $('[data-bs-toggle="tooltip"]').each((index, element) => new bootstrap.Tooltip(element));
});
