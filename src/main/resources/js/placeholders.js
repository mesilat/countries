define('com.mesilat/placeholders',
[
    'jquery',
    'ajs',
    'tinymce'
],
function($,AJS,tinymce){
    "use strict";

    return {
        init : function(ed){
            AJS.bind('editor.text-placeholder.activated', function(e, data) {
                if (data && data.placeholderType === "com-mesilat-date") {
                    ed.execCommand("mceConfInsertDateAutocomplete", false, {}, {skip_undo: true});
                } else if (data && data.placeholderType.startsWith('com-mesilat-')) {
                    ed.execCommand("MesilatAutocomplete", false, {
                        refKey: data.placeholderType.substring(12),
                        title: $(ed.parents[0]).text()
                    }, {
                        skip_undo: true
                    });
                }
            });
        },
        getInfo : function() {
            return {
                longname:  'Extra Placeholders',
                author:    'Mesilat Limited',
                authorurl: 'http://www.mesilat.com',
                version:   tinymce.majorVersion + '.' + tinymce.minorVersion
            };
        }
    };
});


require('confluence/module-exporter').safeRequire('com.mesilat/placeholders', function (MesilatPlaceholders) {
    var tinymce = require('tinymce');
    if (!('MesilatPlaceholders' in tinymce.plugins)){
        tinymce.create('tinymce.plugins.MesilatPlaceholders', MesilatPlaceholders);
        tinymce.PluginManager.add('mesilatplaceholders', tinymce.plugins.MesilatPlaceholders);
        AJS.Editor.Adapter.addTinyMcePluginInit(function (settings) {
            settings.plugins += ',mesilatplaceholders';
        });
    }
});