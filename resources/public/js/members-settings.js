$(document).ready(function() {

    (function setupFontSelector() {
        var id = $('#organization_id').val();
        var url = "/api/" + id + "/plugin-font";

        $.get(url, function( data ) {
        })
        .done (function(data) {
            var font = data;

            $('#plugin-font').fontSelector({
                'hide_fallbacks' : true,
                'initial' : font,
                'fonts' : [
                'Arial,Arial,Helvetica,sans-serif',
                'Arial Black,Arial Black,Gadget,sans-serif',
                'Comic Sans MS,Comic Sans MS,cursive',
                'Courier New,Courier New,Courier,monospace',
                'Georgia,Georgia,serif',
                'Impact,Charcoal,sans-serif',
                'Lucida Console,Monaco,monospace',
                'Lucida Sans Unicode,Lucida Grande,sans-serif',
                'Palatino Linotype,Book Antiqua,Palatino,serif',
                'Tahoma,Geneva,sans-serif',
                'Times New Roman,Times,serif',
                'Trebuchet MS,Helvetica,sans-serif',
                'Verdana,Geneva,sans-serif',
                'Gill Sans,Geneva,sans-serif'
                ]
            });
        });

})();

$(document.body).on("click", "#update-plugin", function(event){
    var font = $('#plugin-font').css("font-family");
    var bg_color = $('#plugin-bg-color').val();
    var font_color = $('#plugin-font-color').val();
    var header_size = $('#plugin-header-size').val();

    $.post("/api/plugincss", { font : font, background: bg_color, 
        header_size: header_size, color: font_color}, function( data ) 
        {  

        }).done(function() {
            location.reload(true);
        });
    });
});
