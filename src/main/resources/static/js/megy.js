$('.disableButtonOnClick').on('click', function () {
    if($(this).hasClass('disabled')) {
        return false;
    } else {
        $(this).addClass('disabled');
    }
});

