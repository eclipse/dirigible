const perspectiveData = {
    id: "portal-launchpad",
    name: "Dashboard",
    link: "../portal/dashboard.html",
    order: "0",
    icon: "../portal/images/navigation.svg",
};

if (typeof exports !== 'undefined') {
    exports.getPerspective = function () {
        return perspectiveData;
    }
}
