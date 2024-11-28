const navigation = angular.module("launchpad", ["ngResource", "ideLayout", "ideUI"]);
navigation.controller("LaunchpadViewController", ["$scope", "messageHub", "$http", function ($scope, messageHub, $http) {
    $scope.currentViewId = 'dashboard';

    $scope.extraExtensionPoints = ['app', "dashboard-navigations", "dashboard-widgets"];
    $scope.groups = [];

    $scope.switchView = function (id, event) {
        if (event) event.stopPropagation();
        $scope.currentViewId = id;
    };

    $scope.isGroupVisible = function (group) {
        const items = $scope.groupItems[group.label.toLowerCase()];
        return items.some(function (item) {
            return $scope.currentViewId === item.id;
        });
    };

    messageHub.onDidReceiveMessage('launchpad.switch.perspective', function (msg) {
        $scope.$apply(function () {
            $scope.switchView(msg.data.viewId);
        });
    }, true)

    $scope.groupItems = [];
    $scope.groupItems['assets'] = [];
    $scope.groupItems["purchasing"] = [];
    $scope.groupItems["sales"] = [];
    $scope.groupItems["inventory"] = [];
    $scope.groupItems["reports"] = [];
    $scope.groupItems["products"] = [];
    $scope.groupItems["employees"] = [];
    $scope.groupItems["partners"] = [];
    $scope.groupItems["configurations"] = [];


    $scope.groups = [
        { "label": "Assets", "icon": "it-host" },
        { "label": "Purchasing", "icon": "credit-card" },
        { "label": "Sales", "icon": "currency" },
        { "label": "Inventory", "icon": "retail-store" },
        { "label": "Reports", "icon": "area-chart" },
        { "label": "Products", "icon": "product" },
        { "label": "Employees", "icon": "company-view" },
        { "label": "Partners", "icon": "customer-and-contacts" },
        { "label": "Configurations", "icon": "wrench" }
    ]

    $http.get("/services/js/portal/api/NavigationExtension/NavigationService.js")
        .then(function (response) {
            $scope.navigationList = response.data;

            $scope.navigationList.forEach(e => addNavigationItem(e));

            $scope.groupItems.forEach(e => e.sort((a, b) => a.order - b.order));

        })
        .catch(function (error) {
            console.error('Error fetching navigation list:', error);
            $scope.state.error = true;
            $scope.errorMessage = 'Failed to load navigation list';
        });

    function addNavigationItem(itemData) {
        if (!itemData || !itemData.label || !itemData.group || !itemData.order || !itemData.link) {
            console.error('Invalid item data:', itemData);
            return;
        }

        itemData.group = itemData.group.toLowerCase();
        if (!$scope.groupItems[itemData.group]) {
            console.error('Group key not found:', itemData.group);
            return;
        }

        $scope.groupItems[itemData.group].push({
            "id": itemData.id,
            "label": itemData.label,
            "link": itemData.link
        });
    }
}]);