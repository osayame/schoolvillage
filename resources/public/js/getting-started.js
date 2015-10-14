$(document).ready(function() {

    //Tiny MCE
    tinymce.init({
        selector: ".tinymce",
        plugins: "link, paste",
        theme: "modern",
        height: 250,
        paste_text_use_dialog: false,
        paste_auto_cleanup_on_paste: true,
        removed_menuitems: "underline pastetext paste link newdocument visualaid strikethrough superscript subscript formats",
        toolbar1: "undo redo | bold italic | link"
    });

    function pickPhoto() {
        filepicker.setKey("AzbXXdVINS4ukFsTeCEAIz");
        return filepicker.pickAndStore({
            mimetype: "image/*",
            openTo: 'COMPUTER',
            service: 'COMPUTER'
        }, {
            location: "S3",
            path: "logos/"
        }, function(success) {
            var logo = success[0].key.split("/")[1];
            $('#org-logo').attr("src", "https://s3.amazonaws.com/doubledonation/logos/" + logo);
            $('#logo').val(logo);
        });
    };

    $(document.body).on("click", "#pick-photo", function(event){
        event.preventDefault();
        $(this).blur();
        pickPhoto();
    });

    $("input#org-color").minicolors({
        theme: "bootstrap"
    });

});
