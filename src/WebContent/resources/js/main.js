var timer = $("#timer-body").text();

setInterval(function() {
	timerText = timer / 10.0;
	if(timer % 10.0 == 0)
	{
		timerText += ".0";
	}

	$("#timer-body").text(timerText + "s");
	timer++;
}, 100);

$("#note").fadeOut();
window.setTimeout(function() {
	$(".flash").fadeTo(500, 0).slideUp(500, function() {
		$(this).remove();
	});
}, 5000);

var $star_rating = $('.star-rating .fa');

var SetRatingStar = function() {
  return $star_rating.each(function() {
    if (parseInt($star_rating.siblings('input.rating-value').val()) >= parseInt($(this).data('rating'))) {
      return $(this).removeClass('fa-star-o').addClass('fa-star');
    } else {
      return $(this).removeClass('fa-star').addClass('fa-star-o');
    }
  });
};

$star_rating.on('click', function() {
  $star_rating.siblings('input.rating-value').val($(this).data('rating'));
  return SetRatingStar();
});

SetRatingStar();