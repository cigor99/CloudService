$('#login-form').submit(function() {
    // get all the inputs into an array.
    var $inputs = $('#login-form :input');

    var values = {};
    $inputs.each(function() {
        values[this.name] = $(this).val();
        console.log(values[this.name])
    });

});
