define('com.mesilat/autocomplete', [], function(){

    return {
        key: String.fromCharCode(1,2),
        refKeys: {}
    };
});


define('com.mesilat/autocomplete-settings',
[
    'jquery',
    'ajs',
    'confluence/legacy',
    'tinymce',
    'com.mesilat/autocomplete'
],
function($,AJS,Confluence,tinymce,autocomplete){
    "use strict";

    function createMatrixFromArray(arr){
        var matrix = [];
        arr.forEach(function(text){
            matrix.push({
                className: 'content-type-text',
                href: '#',
                name: text,
                restObj: {}
            });
        });
        var result = [];
        result[0] = matrix;
        return result;
    }

    function MesilatAutocompleteSettings() {
        Confluence.Editor.Autocompleter.Settings[autocomplete.key] = {
            ch: autocomplete.key,
            cache: false,
            endChars: [],
            dropDownClassName: 'autocomplete-links',
            selectFirstItem: true,
            headerText: 'unknown',

            getHeaderText: function () {
                return this.headerText;
            },
            getAdditionalLinks: function () {
                return [];
            },
            getDataAndRunCallback: function (autoCompleteControl, val, callback) {
                if (autoCompleteControl.settings.refKey in autocomplete.refKeys){
                    var reference = autocomplete.refKeys[autoCompleteControl.settings.refKey];
                    if ('values' in reference){
                        callback(createMatrixFromArray(reference.values), val, function(){});
                    } else {
                        Confluence.Editor.Autocompleter.Util.getRestData(
                            autoCompleteControl,
                            reference.getUrl,
                            reference.getParams,
                            val,
                            callback,
                            'content'
                        );
                    }
                } else {
                    console.log('com.mesilat/autocomplete','Ref key is not registered: ' + autoCompleteControl.settings.refKey);
                }                
            },
            update: function (autoCompleteControl, link){
                var reference = autocomplete.refKeys[autoCompleteControl.settings.refKey];
                if (typeof reference.update === 'function'){
                    reference.update(autoCompleteControl, link);
                } else {
                    var ed = AJS.Rte.getEditor();
                    var $span = $(ed.dom.create('span'), ed.getDoc());
                    $span.text(link.name);
                    tinymce.confluence.NodeUtils.replaceSelection($span);
                }
            }
        };
    }

    return MesilatAutocompleteSettings;
});


define('com.mesilat/autocomplete-plugin',
[
    'confluence/legacy',
    'tinymce',
    'com.mesilat/autocomplete',
    'com.mesilat/autocomplete-settings'
],
function(Confluence,tinymce,autocomplete,settings) {
    "use strict";

    return {
        init: function (ed) {
            ed.addCommand('MesilatAutocomplete', function (fs, params){
                Confluence.Editor.Autocompleter.Settings[autocomplete.key].refKey = params.refKey;
                Confluence.Editor.Autocompleter.Settings[autocomplete.key].headerText =
                    ('title' in params && params.title !== '')? params.title: autocomplete.refKeys[params.refKey].getTitle();
                Confluence.Editor.Autocompleter.Manager.shortcutFired(autocomplete.key);
            });
            settings();
        },
        getInfo: function () {
            return {
                longname:  'Extra Autocomplete',
                author:    'Mesilat Limited',
                authorurl: 'http://www.mesilat.com',
                version :  tinymce.majorVersion + '.' + tinymce.minorVersion
            };
        }
    };
});


require('confluence/module-exporter').safeRequire('com.mesilat/autocomplete-plugin', function(MesilatAutocomplete) {
    var tinymce = require('tinymce');
    if (!('MesilatAutocomplete' in tinymce.plugins)){
        tinymce.create('tinymce.plugins.MesilatAutocomplete', MesilatAutocomplete);
        tinymce.PluginManager.add('mesilatautocomplete', tinymce.plugins.MesilatAutocomplete);
        AJS.Editor.Adapter.addTinyMcePluginInit(function (settings) {
            settings.plugins += ',mesilatautocomplete';
        });
    }
});