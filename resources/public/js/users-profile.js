$(document).ready(function() {

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

    $(document.body).on("click", "#pick-photo", function(event){
        event.preventDefault();
        $(this).blur();
        pickPhoto();
    });

});