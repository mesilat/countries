define('com.mesilat/autocomplete:countries',[
    'jquery',
    'ajs',
    'tinymce',
    'com.mesilat/base64'
],function($,AJS,tinymce,base64){
    function getHostAddress(){
        var url = window.location.href;
        var arr = url.split("/");
        return arr[0] + "//" + arr[2];
    };
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

    return {
        updateCountry: updateCountry,
        updateSymbol: updateSymbol
    };
});