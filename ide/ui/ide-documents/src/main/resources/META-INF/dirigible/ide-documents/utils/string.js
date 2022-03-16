exports.replaceAll = function (string, find, replace) {
	return string.replace(new RegExp(find, 'g'), replace);
};

exports.unescapePath = function (path) {
	return path.replace(/\\/g, '');
};

exports.getNameFromPath = function (path) {
	let splittedFullName = path.split("/");
	let name = splittedFullName[splittedFullName.length - 1];
	return (!name || name.lenght === 0) ? "root" : name;
};

exports.formatPath = function (path) {
	path = exports.replaceAll(path, "//", "/");
	if (!path.startsWith("/")) {
		path = "/" + path;
	}
	if (path.endsWith("/")) {
		path = path.substr(0, path.length - 1);
	}
	return path;
}