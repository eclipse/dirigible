const viewData = {
    id: "portal-dashboard",
    label: "Dashboard",
    lazyLoad: true,
    link: "/services/web/portal/subviews/dashboard.html"
};
if (typeof exports !== 'undefined') {
    exports.getView = function () {
        return viewData;
    }
}