$(document).ready(function() {

    tinymce.init({
        selector: ".tinymce",
        theme: "modern",
        plugins: [
        "advlist autolink lists link image charmap print preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen",
        "insertdatetime media nonbreaking save table contextmenu directionality",
        "emoticons template paste textcolor colorpicker textpattern imagetools"
        ],
        toolbar1: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image",
        toolbar2: "print preview media | forecolor backcolor emoticons",
        image_advtab: true
    });

    function pickPhoto() {
        filepicker.setKey("AxfbfP1fRQbqAhk95fgu4z");
        return filepicker.pickAndStore({
            mimetype: "image/*",
            openTo: 'COMPUTER',
            service: 'COMPUTER'
        }, {
            location: "S3",
            path: "photos/"
        }, function(success) {
            var photo = success[0].key.split("/")[1];
            $('#user-photo').attr("src", "https://s3.amazonaws.com/schoolvillage/photos/" + photo);
            $('#photo').val(photo);
        });
    };

    function pickResume() {
        filepicker.setKey("AxfbfP1fRQbqAhk95fgu4z");
        return filepicker.pickAndStore({
            mimetype: "application/msword, application/pdf, application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            openTo: 'COMPUTER',
            service: 'COMPUTER'
        }, {
            location: "S3",
            path: "resumes/"
        }, function(success) {
            var resume = success[0].key.split("/")[1];
            $('#user-resume').attr("href", "https://s3.amazonaws.com/schoolvillage/resumes/" + resume);
            $('#resume').val(resume);
        });
    };

    $(document.body).on("click", "#pick-photo", function(event){
        event.preventDefault();
        $(this).blur();
        pickPhoto();
    });

    $(document.body).on("click", "#pick-resume", function(event){
        event.preventDefault();
        $(this).blur();
        pickResume();
    });

    $(function() {
        if (!document.getElementById('state') || !$('#user_id').length > 0){
            return;
        }

        else {
            var id = $('#user_id').val();
            var url = "/state/" + id;

            $.get(url, function( data ) {

            })
            .done (function(data) {
                var element = document.getElementById('state');
                element.value = data;
            })
            .error (function(xhr) {
              console.log(xhr.responseText);
          });
        }
    })

    $("form").submit(function(e) {

        var ref = $(this).find("[required]");

        $(ref).each(function(){
            if ( $(this).val() == '' )
            {
                alert("Required field should not be blank.");

                $(this).focus();

                e.preventDefault();
                return false;
            }
        });  return true;
    });

});