const viewData = {
	id: "bpm-process-context-view",
	label: "Process Context",
	factory: "frame",
	region: "bottom",
	link: "../ide-bpm-workspace/bpm-process-context.html",
};
if (typeof exports !== 'undefined') {
	exports.getView = function () {
		return viewData;
	}
}