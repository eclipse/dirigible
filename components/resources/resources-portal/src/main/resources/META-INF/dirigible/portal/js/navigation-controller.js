const navigation = angular.module("launchpad", ["ngResource", "ideLayout", "ideUI"]);
navigation.controller("LaunchpadViewController", ["$scope", "messageHub", "$http", function ($scope, messageHub, $http) {
    $scope.currentViewId = 'dashboard';

    $scope.extraExtensionPoints = ['app', "dashboard-navigations", "dashboard-widgets"];
    $scope.groups = [];
    $scope.groupItems = [];

    function loadNavigationGroups() {
        return $http.get("/services/js/portal/api/NavigationGroupsExtension/NavigationGroupsService.js")
            .then(function (response) {
                $scope.groups = response.data;

                response.data.forEach(elem => {
                    $scope.groupItems[elem.label.toLowerCase()] = [];
                });
            })
            .catch(function (error) {
                console.error('Error fetching navigation groups:', error);
                $scope.state = { error: true, errorMessage: 'Failed to load navigation groups' };
                return async () => { };
            });
    }

    function loadNavigationItems() {
        return $http.get("/services/js/portal/api/NavigationExtension/NavigationService.js")
            .then(function (response) {
                $scope.navigationList = response.data;

                $scope.navigationList.forEach(e => addNavigationItem(e));

                Object.values($scope.groupItems).forEach(items => {
                    items.sort((a, b) => a.order - b.order);
                });
            })
            .catch(function (error) {
                console.error('Error fetching navigation items:', error);
                $scope.state = { error: true, errorMessage: 'Failed to load navigation items' };
            });
    }

    function addNavigationItem(itemData) {
        if (!itemData || !itemData.label || !itemData.group || !itemData.order || !itemData.link) {
            console.error('Invalid item data:', itemData);
            return;
        }

        const groupKey = itemData.group.toLowerCase();
        if (!$scope.groupItems[groupKey]) {
            console.error('Group key not found:', groupKey);
            return;
        }

        $scope.groupItems[groupKey].push({
            id: itemData.id,
            label: itemData.label,
            link: itemData.link
        });
    }

    loadNavigationGroups()
        .then(loadNavigationItems)
        .catch(function (error) {
            console.error('Error during initialization:', error);
        });

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
}]);