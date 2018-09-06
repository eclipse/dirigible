	/** Themes from
  		http://getbootstrap.com/
  		http://bootswatch.com/
  	*/

	var themes = {
	    "default": "../../../../services/v3/web/resources/themes/default/bootstrap.min.css",
	    "wendy" : "../../../../services/v3/web/resources/themes/wendy/bootstrap.min.css",
	    "baroness" : "../../../../services/v3/web/resources/themes/baroness/bootstrap.min.css",
	    "simone" : "../../../../services/v3/web/resources/themes/simone/bootstrap.min.css",
	    "alice" : "../../../../services/v3/web/resources/themes/alice/bootstrap.min.css",
	    "florence" : "../../../../services/v3/web/resources/themes/florence/bootstrap.min.css"
	};

	$(function() {
		$.get( "../../../../services/v3/core/theme", function( data ) {
			var themesheet = $('<link href="'+themes[data.trim()]+'" rel="stylesheet" />');
			themesheet.appendTo('head');
		    $('.theme-link').click(function() {
		    	var themeName = $(this).attr('data-theme');
		        var themeUrl = themes[$(this).attr('data-theme')];
		        themesheet.attr('href',themeUrl);
		        $.get( "../../../../services/v3/core/theme?name=" + themeName);
		        location.reload();
	    	});
		});
	});
