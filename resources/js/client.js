$(function () {
	var roomId = (function() {
		var matches = /\/([0-9a-z\-]{36})/i.exec(window.location.pathname);
		return matches && matches[1];
	})();

	function updateVotes() {
		$.ajax({
			url: '/'+roomId+'/votes',
			type: 'GET',
			dataType: 'json',
			success: handleUpdateVotes
		});
	}

	function handleUpdateVotes(data) {
		$('#votes .vote').remove();

		$.each(data, function (clientId, data) {
			var points = data.points;
			var name = data.name;
			
			var vote = $('<div class="vote">');

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

			var header = $('<h3>').text(name);

			vote.append(card)
			vote.append(header);

			$('#votes').append(vote)
		});

		// Check if the votes were reset.
		if (Object.keys(data).length == 0) {
			$('#vote .card').removeClass('selected');
		}
	}

	$('#vote .card').click(function (e) {
		var card = $(e.currentTarget);
		var name = $('#your_name').val();
		var points = card.data('points') || Number(card.text());

		$('#vote .card').removeClass('selected');
		card.addClass('selected');

		$.ajax({
			url: '/'+roomId+'/vote',
			type: 'PUT',
			data: {
				'name': name,
				'points': points
			},
			dataType: 'json',
			success: handleUpdateVotes
		});
	});

	$('#toggle').click(function (e) {
		$.ajax({
			url: '/'+roomId+'/reveal',
			type: 'POST',
			dataType: 'json',
			success: handleUpdateVotes
		});
	});

	$('#reset').click(function (e) {
		$.ajax({
			url: '/'+roomId+'/reset',
			type: 'POST',
			dataType: 'json',
			success: handleUpdateVotes
		});
	});

	setInterval(updateVotes, 500);
});
