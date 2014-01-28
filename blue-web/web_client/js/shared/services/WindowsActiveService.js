'use strict';

angular.module('bluelatex.Shared.Services.WindowActive', [])
  .factory("WindowActiveService", ['$scope', '$document', '$window',
    function ($scope, $document, $window) {
      var windowActive = false;
      var hidden = "hidden";
      // Standards:
      if (hidden in $document)
        $document.addEventListener("visibilitychange", onchange);
      else if ((hidden = "mozHidden") in $$document)
        $$document.addEventListener("mozvisibilitychange", onchange);
      else if ((hidden = "webkitHidden") in $$document)
        $document.addEventListener("webkitvisibilitychange", onchange);
      else if ((hidden = "msHidden") in $document)
        $document.addEventListener("msvisibilitychange", onchange);
      // IE 9 and lower:
      else if ('onfocusin' in $document)
        $document.onfocusin = $document.onfocusout = onchange;
      // All others:
      else
        $window.onpageshow = $window.onpagehide = $window.onfocus = $window.onblur = onchange;

      function onchange(evt) {
        console.log("window",evt);
        var v = 'visible',
          h = 'hidden',
          evtMap = {
            focus: v,
            focusin: v,
            pageshow: v,
            blur: h,
            focusout: h,
            pagehide: h
          };
          console.log(evt);

        evt = evt || $window.event;
        if (evt.type in evtMap) {
          var windowActive = evtMap[evt.type] == v;
          $scope.$broadcast('windowActive', windowActive);
        } else {
          var windowActive = this[hidden];
          $scope.$broadcast('windowActive', windowActive);
        }
      }
      return {
        isActiveWindow: function () {
          return windowActive;
        }
      };
    }
  ]);