exports.isInert = function() {
	var helium = new JavaImporter(Packages.io.dirigible.helium);
	with (helium) {
		var output = HeliumFacade.isInert();
		return output;
	}
};