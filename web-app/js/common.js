$(document).ready(function () {

    $(".showbiz .thumbnail").hoverIntent(function () {

        $(this).find('img').first().stop().animate({
            'position': 'absolute',
            'right': '0',
            'margin-right': '0',
            'margin-left': '0',
            'border-radius': '2px',
            'padding': '2px',
            'width': '50px',
            'height': '50px'
        });

        $(this).find('.title').first().stop().animate({
            'top': '0',
            'margin-right': '60px',
            'margin-left': '0'
        });

        $(this).find('.attributes').first().stop().animate({
            'top': $(this).find('.title').first().height() - 10,
            'opacity': '1'
        });

        $(this).find('.buttons').first().stop().animate({
            'top': $(this).find('.title').first().height() - 5,
            'opacity': '1'
        });

    }, function () {

        $(this).find('img').first().stop().animate({
            'right': 'auto',
            'margin-right': $(this).width()/2 - 80,
            'margin-left': $(this).width()/2 - 80,
            'border-radius': '4px',
            'padding': '4px',
            'width': '150px',
            'height': '150px'
        });

        $(this).find('.title').first().stop().animate({
            'top': '168px',
            'margin-right': '30px',
            'margin-left': '30px'
        });

        $(this).find('.attributes').first().stop().animate({
            'top': '300px',
            'opacity': '0'
        });

        $(this).find('.buttons').first().stop().animate({
            'top': '255px',
            'opacity': '0'
        });
    });

    $(window).resize(function () {
        changeAddToBasketButtonLabel();
        resizeThumbnails();
    });

    changeAddToBasketButtonLabel();
    resizeThumbnails();

    $('.scrollable').rollbar({zIndex:80,wheelSpeed:10});

    $('.topNavigationItem a.dropdown-toggle').tipsy({live:true});

    $('.has-tipsy').each(function(){
        var tipContainer = $(this).parent().parent().find('.tips-container');
        tipContainer.append('<span class="manual-tip ' + $(this).attr('type') + '"><span>' + $(this).attr('original-title') + '</span></span>');

        $(this).hoverIntent(function () {
            $('.manual-tip').css('opacity', 0)
            $('.manual-tip').attr('opened', '0')
            var tip = $(this).parent().parent().find('.tips-container .manual-tip.' + $(this).attr('type'));
            tip.css('top', $(this).parent().parent().find('.title').first().height() + 30);
            tip.css('left', $(this).position().left + 15);
            tip.animate({
                'opacity': 0.7
            });
        }, function(){
            var tip = $(this).parent().parent().find('.tips-container .manual-tip.' + $(this).attr('type'));
            tip.css('opacity', 0)
        });


    });
});


function changeAddToBasketButtonLabel() {
      $('#basketCounter').fadeIn();
}

function resizeThumbnails() {
    var currentWidth = $('.span600').first().width();
    if (currentWidth > 700) {
        $('.thumbnails.productTypes .span3').css('width', '20%');
        $('.thumbnailGrid .span3').css('width', (currentWidth - 30) / 4);
    }
    else {
        $('.thumbnails.productTypes .span3').css('width', '25%');
        $('.thumbnailGrid .span3').css('width', (currentWidth - 25) / 3);
    }

    //show top 8 brand images
    var brandItems = $('.brand-carousel li a img');
    if(brandItems.length > 8)
        brandItems = brandItems.slice(0, 8);
    brandItems.each(function(){
        if($(this).attr('src') != $(this).attr('original-src'))
            $(this).attr('src', $(this).attr('original-src'));
    });

    $('.sb-navigation-right').hoverIntent(function(){
        lastHoveredCarouselButton = $(this);
        hoverCarouselButton();
    }, function(){
        lastHoveredCarouselButton = null;
    });

    $('.sb-navigation-left').hoverIntent(function(){
        lastHoveredCarouselButton = $(this);
        hoverCarouselButton();
    }, function(){
        lastHoveredCarouselButton = null;
    });
}

var lastHoveredCarouselButton;
function hoverCarouselButton(){
    if(lastHoveredCarouselButton){
        lastHoveredCarouselButton.click();
        setTimeout(hoverCarouselButton, 300);
    }
}

