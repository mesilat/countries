require([
    'ajs',
    'jquery',
    'confluence/legacy',
    'tinymce',
    'com.mesilat/autocomplete',
    'com.mesilat/base64'
], function(AJS,$,Confluence,tinymce,autocomplete,base64){
    function getUrl(val) {
        if (val) {
            return AJS.contextPath() + '/rest/countries/1.0/find';
        } else {
            return null;
        }
    };
    function getParams(autoCompleteControl, val){
        var params = {
            'title': autoCompleteControl.settings.title,
            'max-results': 10
        };
        if (val) {
            params.filter = Confluence.unescapeEntities(val);
        }
        return params;
    };
    function getTitle(){
        return 'Select country...';
    };
    function getHostAddress(){
        var url = window.location.href;
        var arr = url.split("/");
        return arr[0] + "//" + arr[2];
    }
    function updateCountry(autoCompleteControl, link){
        var code = link.restObj.id; // Country Code
        var def = base64.encode('{country:code=' + code + '}').replace('=','');
        var locale = $('meta[name="ajs-user-locale"]').attr('content');
        var href = AJS.contextPath() + '/plugins/servlet/confluence/placeholder/macro?definition=' + def + '&locale=' + locale + '&version=2';

        var ed = AJS.Rte.getEditor();

        var $span = $(ed.dom.create('span'), ed.getDoc());
        var $img = $('<img class="editor-inline-macro">');
        $img.attr('src', href);
        $img.attr('data-macro-name', 'country');
        $img.attr('data-macro-parameters', 'code=' + code);
        $img.attr('data-macro-schema-version', '1');
        $img.attr('data-mce-src', getHostAddress() + href);
        var $div = $('<div>');
        $img.appendTo($div);

        //console.log('com.mesilat/countries',$div.html());

        $span.html($div.html());
        tinymce.confluence.NodeUtils.replaceSelection($span);
    };
    function updateSymbol(autoCompleteControl, link){
        var code = link.restObj.id; // Country Code
        var def = base64.encode('{country-symbol:code=' + code + '}').replace('=','');
        var locale = $('meta[name="ajs-user-locale"]').attr('content');
        var href = AJS.contextPath() + '/plugins/servlet/confluence/placeholder/macro?definition=' + def + '&locale=' + locale + '&version=2';

        var ed = AJS.Rte.getEditor();

        var $span = $(ed.dom.create('span'), ed.getDoc());
        var $img = $('<img class="editor-inline-macro">');
        $img.attr('src', href);
        $img.attr('data-macro-name', 'country-symbol');
        $img.attr('data-macro-parameters', 'code=' + code);
        $img.attr('data-macro-schema-version', '1');
        $img.attr('data-mce-src', getHostAddress() + href);
        var $div = $('<div>');
        $img.appendTo($div);

        $span.html($div.html());
        tinymce.confluence.NodeUtils.replaceSelection($span);
    };

    autocomplete.refKeys.country = {
        getUrl: getUrl,
        getParams: getParams,
        getTitle: getTitle,
        update: updateCountry
    };
    autocomplete.refKeys.countrySymbol = {
        getUrl: getUrl,
        getParams: getParams,
        getTitle: getTitle,
        update: updateSymbol
    };
});

define('com.mesilat/country',
[
    'jquery',
    'ajs',
    'tinymce'
],
function($,AJS,tinymce){
    "use strict";

    return {
        init : function(ed){
            if(AJS.Rte.Placeholder && AJS.Rte.Placeholder.addPlaceholderType) {
                AJS.Rte.Placeholder.addPlaceholderType({
                    type: 'com-mesilat-country',
                    label: 'Country',
                    tooltip: 'Add country flag and name to your Confluence template'
                });
            }
        },
        getInfo : function() {
            return {
                longname:  'Country Placeholder',
                author:    'Mesilat Limited',
                authorurl: 'http://www.mesilat.com',
                version:   tinymce.majorVersion + '.' + tinymce.minorVersion
            };
        }
    };
});


require('confluence/module-exporter').safeRequire('com.mesilat/country', function (CountryPlaceholder) {
    var tinymce = require('tinymce');
    tinymce.create('tinymce.plugins.CountryPlaceholder', CountryPlaceholder);
    tinymce.PluginManager.add('countryplaceholder', tinymce.plugins.CountryPlaceholder);
    AJS.Editor.Adapter.addTinyMcePluginInit(function (settings) {
        settings.plugins += ',countryplaceholder';
    });
});