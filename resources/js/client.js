$(function () {
	var id = (function() {
		function s4() {
			return Math.floor((1 + Math.random()) * 0x10000)
				.toString(16)
				.substring(1);
		}
		
		return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
		       s4() + '-' + s4() + s4() + s4();
	})();

	function updateVotes() {
		$.ajax({
			url: '/votes',
			type: 'GET',
			dataType: 'json',
			success: handleUpdateVotes
		});
	}

	function handleUpdateVotes(data) {
		$('#votes .card').remove();

		$.each(data, function (clientId, points) {
			var card = $('<a class="card">');

			switch(points) {
				case 'question':
					card.append($('<i>').addClass('fa fa-question'));
					break;
				case 'coffee':
					card.append($('<i>').addClass('fa fa-coffee'));
					break;
				default:
					card.text(points);
			}

			card.data('client-id', clientId);

			$('#votes').append(card)
		});
	}

	$('#vote .card').click(function (e) {
		var card = $(e.currentTarget);
		var points = card.data('points') || Number(card.text());
		$('#vote .card').removeClass('selected');
		card.addClass('selected');

		$.ajax({
			url: '/vote',
			type: 'PUT',
			data: {
				'client-id': id,
				'points': points
			},
			dataType: 'json',
			success: handleUpdateVotes
		});
	});

	$('#toggle').click(function (e) {
		$.ajax({
			url: '/reveal',
			type: 'POST',
			dataType: 'json',
			success: handleUpdateVotes
		});
	});

	$('#reset').click(function (e) {
		$.ajax({
			url: '/reset',
			type: 'POST',
			dataType: 'json',
			success: handleUpdateVotes
		});
	});

	setInterval(updateVotes, 500);
});
