$( document ).ready(function() {
  initializeCategories();

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


  function initializeCategories() {
    if($('#allcategories').checked) {
      $('#allcategories').value = "true";
    }else{
      $('#allcategories').value = "false";
    }

    $('.category').each(function(event) {
      if(this.checked) {
        this.value = "true";
      }else{
        this.value = "false";
      }
    });
  };

  $('.category').click(function(event) {  //on click
    if(this.checked) { // check select status
      this.value = "true";
    }else{
      this.value = "false";
    }
  });

  $('#company-search').typeahead({
    minLength: 1,
    order: "asc",
    hint: true,
    group: true,
    template: '<span class="row"> {{display}} <small style="color:green">{{group}}</small></span>',
    backdrop: {
      "background-color": "#fff"
    },
    emptyTemplate: 'No result for "{{query}}"',
    source: {
      company: {
        url: {
          type: "GET",
          url: "/api/dbadmin/get-companies"
        }
      },
      subsidiary: {
        url: [
          {
            type: "GET",
            url: "/api/dbadmin/get-subsidiaries",
          }
        ]
      }
    },
    callback: {
      onClickAfter: function (node, a, item, event) {
        document.getElementById('searchForm').submit();
      }
    }

  });

  $("#add-subsidiary").on("click", function(event){
    event.preventDefault();
    var id = $('#company_id').val();
    var url = "/api/dbadmin/add-subsidiary/"  + id;
    var input = $(this).siblings("#subsidiary_input");
    if (!input.val().trim() || input.val().trim().length === 0) {
      return;
    }

    var newItem = "<li class='list-group-item subsidiary'>" + '<input name="subsidiaries[]" class="form-control" value="' + input.val().trim() + '" />'
        + "</li>";
    $(".list-group").append(newItem);
    input.val("");
  });

  $(document.body).on("keypress", ".edit", function(e){
    var key = e.which;
    if(key == 13)
    {
      $(this).parent().find(".update-subsidiary").click();
      return false;
    }
  });

  $(document.body).on("click", ".edit-subsidiary", function(event){
    var display = $(this).parent().parent().find(".display");
    display.hide();
    display.siblings(".edit").show().val(display.text()).focus();
    display.siblings(".btn-group").find(".edit-subsidiary").hide();
    display.siblings(".btn-group").find(".remove-subsidiary").hide();
    display.siblings(".btn-group").find(".update-subsidiary").show();
  });

  $(".navbar-brand").on("click", function(event){
    event.preventDefault();
    $(this).blur();
    location.replace('/dbadmin');
  });


  (function initializeFlagButtons(){
    var id = $('#company_id').val();
    if (!id) {
      return;
    }
    var url = "/api/dbadmin/get-status/" + id;
    $.get(url, function( data ) {

    })
        .done (function(data) {
      console.log("current company status: " + data);

      switch(data) {
        case 'Approved':
          $('.flag-company').show();
          break;
        case 'Pending':
          $('.btn-pending').show();
          break;
        case 'Flagged':
          $('.unflag-company').show();
          break;
      }
    });
  })();

  $(document.body).on("click", ".flag-company", function(event){
    $('#exampleModal').modal('show');
    $(this).blur();
    event.preventDefault();
  });

  $(document.body).on("click", ".add-flag", function(event){
    var id = $('#company_id').val();
    var url = "/api/dbadmin/add-flag/" + id + '?';
    var message = $('#flag-text');
    $.post(url, {comment: message.val()}, function( data ) {

    })
        .done (function() {
      $('#exampleModal').modal('hide');
      $(document.body).find("#flag-text").val('');
      $('.flag-company').hide();
      $('.unflag-company').show();
    });
  });

  $(document.body).on("click", ".unflag-company", function(event){
    $('#removeFlagModal').modal('show');
    $(this).blur();
    var id = $('#company_id').val();
    var url = "/api/dbadmin/get-flag/" + id;

    event.preventDefault();
    $.get(url, function( data ) {
    })
        .done (function(data) {
      $('#removeFlagModal').find("#flag-comment").text(data);
    });
  });

  $(document.body).on("click", ".remove-flag", function(event){
    var id = $('#company_id').val();
    var url = "/api/dbadmin/remove-flag/" + id + '?';

    $.post(url, function( data ) {

    })
        .done (function() {
      $('.btn-pending').show();
      tinymce.triggerSave();
      $("#editform").submit();
    })
        .error (function(xhr) {
      console.log(xhr);
    });
  });

  $(document.body).on("click", ".btn-approve-version", function(event){
    event.preventDefault();
    var company = $('#company_id').val();
    var version = $('#version_id').val();
    var url = "/api/dbadmin/approve-version/" + version + '?';

    $.post(url, function( data ) {

    })
        .done (function() {
      location.replace("/api/dbadmin/company/" + company + '?');
    })
        .error (function(xhr) {
      console.log(xhr.responseText);
    })
  });

  $(document.body).on("click", ".btn-pending", function(event){
    event.preventDefault();
    var company = $('#company_id').val();
    var url = "/api/dbadmin/approve/" + company + '?';
    location.replace(url);
  });


  $('.bs-callout-label').each(function() {
    $(this).parent().parent().addClass('bs-callout bs-callout-danger');
  });

  $(document.body).on("click", ".btn-update", function(event){
    event.preventDefault();
    $(this).blur();
    var id = $('#company_id').val();
    if (id) {
      var url = "/api/dbadmin/get-status/" + id;
      $.get(url, function (data) {
        $(".company-status").text(data.toLowerCase());
        if (data === 'Flagged') {
          $('#flaggedAlertModal').modal('show');
          return;
        }
        else if (data === 'Pending') {
          $('#pendingAlertModal').modal('show');
          return;
        }
        else {
          tinymce.triggerSave();
          $('#editform').submit();
        }
      })
          .done(function (data) {
      });
    } else {
      tinymce.triggerSave();
      $('#editform').submit();
    }
  });

  function getCompanyStatus() {
    var id = $('#company_id').val();
    var url = "/api/dbadmin/get-status/" + id;
    $.get(url, function( data ) {
      return data
    });
  };

  function changeCompanyStatus(status) {
    var id = $('#company_id').val();
    var url = "/api/dbadmin/change-status/" + id + '?'
    $.post(url, {status: status}, function( data ) {

    })
        .done (function() {
      location.replace("/api/dbadmin/edit/" + id + '?');
    });
  };

  $(document.body).on("click", ".approve-company", function(event){
    event.preventDefault();
    $(this).blur();
    var id = $('#company_id').val();
    var url = "/api/dbadmin/change-status/" + id + '?'
    $.post(url, {status: "Approved"}, function( data ) {

    })
        .done (function() {
      location.replace("/api/dbadmin/edit/" + id + '?');
    });
  });

  $(document.body).on("click", ".btn-reject-version", function(event){
    event.preventDefault();
    $(this).blur();
    $('#rejectProposedModal').modal('show');
  });

  $(document.body).on("click", ".update-pending", function(event){
    event.preventDefault();
    $(this).blur();
    tinymce.triggerSave();
    $('#editform').submit();
  });

  $(document.body).on("click", ".flag-rejected-submission", function(event){
    $(this).blur();
    changeCompanyStatus("Flagged");
  });


  $(document.body).on("click", ".unflag-rejected-submission", function(event){
    $(this).blur();
    changeCompanyStatus("Approved");
  });

});
