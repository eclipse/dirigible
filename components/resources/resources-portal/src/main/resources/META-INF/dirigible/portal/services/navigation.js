const perspectiveData = {
    id: "portal-launchpad",
    name: "Dashboard",
    link: "../dashboard.html",
    order: "0",
    icon: "../images/navigation.svg",
};

if (typeof exports !== 'undefined') {
    exports.getPerspective = function () {
        return perspectiveData;
    }
}
