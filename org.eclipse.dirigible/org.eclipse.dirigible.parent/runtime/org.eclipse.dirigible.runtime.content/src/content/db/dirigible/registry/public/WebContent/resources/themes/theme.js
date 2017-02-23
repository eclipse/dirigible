	/** Themes from
  		http://getbootstrap.com/
  		http://bootswatch.com/
  	*/

	var themes = {
	    "default": "/services/web/resources/themes/default/bootstrap.min.css",
	    "cosmo" : "/services/web/resources/themes/cosmo/bootstrap.min.css",
	    "cyborg" : "/services/web/resources/themes/cyborg/bootstrap.min.css",
	    "flatly" : "/services/web/resources/themes/flatly/bootstrap.min.css",
	    "journal" : "/services/web/resources/themes/journal/bootstrap.min.css",
	    "readable" : "/services/web/resources/themes/readable/bootstrap.min.css",
	    "simplex" : "/services/web/resources/themes/simplex/bootstrap.min.css",
	    "yeti" : "/services/web/resources/themes/yeti/bootstrap.min.css"
	};

	$(function() {
		$.get( "/services/theme", function( data ) {
			var themesheet = $('<link href="'+themes[data.trim()]+'" rel="stylesheet" />');
			themesheet.appendTo('head');
		    $('.theme-link').click(function(){
		    	var themeName = $(this).attr('data-theme');
		        var themeUrl = themes[$(this).attr('data-theme')];
		        themesheet.attr('href',themeUrl);
		        $.get( "/services/theme?name=" + themeName);
	    	});
		});
	});