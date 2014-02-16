angular.module('bluelatex.Latex.Controllers.Vignettes', [])
  .controller('VignettesController', ['$scope',
    function ($scope) {
      $scope.scale = "auto";

      $scope.changePage = function (page) {

      };

      $scope.changeScale = function (reviewer) {

      };

      $scope.refreshAll = function () {

      };

      $scope.refreshPage = function (page) {

      };
      $scope.onScroll = function (event) {
        console.log(event);
      };
    }
  ]).directive("scroll", function ($window) {
    return function(scope, element, attrs) {
        angular.element(element).bind("scroll", function(event) {
          scope.onScroll({
            clientHeight: element[0].clientHeight,
            clientWidth: element[0].clientWidth,
            scrollHeight: element[0].scrollHeight,
            scrollWidth: element[0].scrollWidth,
            scrollTop: element[0].scrollTop,
            scrollLeft: element[0].scrollLeft
          });
        });
    };
});