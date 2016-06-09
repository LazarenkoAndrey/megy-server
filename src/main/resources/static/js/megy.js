$('.disableButtonOnClick').on('click', function () {
    console.log('s1');

    if($(this).hasClass('disabled')) {
        console.log('s2');
        return false;
    } else {
        $(this).addClass('disabled');
    }
});

