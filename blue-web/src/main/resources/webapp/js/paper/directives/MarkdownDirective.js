/*
* Create a markdown html preview
*/
angular.module('bluelatex.Paper.Directives', [])
.directive('markdown', function () {
    var converter = new Showdown.converter();
    return {
        restrict: 'A',
        scope: {
	      ngModel: '='
	    },
        link: function (scope, element, attrs) {
            var htmlText = converter.makeHtml(scope.ngModel);
            element.html(htmlText);

            scope.$watch("ngModel", function (val) {
            	var htmlText = converter.makeHtml(val);
            	element.html(htmlText);
            });
        }
    };

});