/*
 * This file is part of the \BlueLaTeX project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

angular.module('bluelatex.Paper.Controllers.Papers', ['ngStorage','bluelatex.Paper.Services.Paper'])
  .controller('PapersController', ['$rootScope', '$scope', 'PaperService','$log','MessagesService','$localStorage','localize',
    function ($rootScope, $scope, PaperService,$log,MessagesService,$localStorage,localize) {
      // list of peper
      $scope.papers = [];

      // load settings from localStorage
      $scope.reverse = $localStorage.userPaperReverse;
      if($scope.reverse == null)
        $scope.reverse = false;

      $scope.$watch("reverse", function(value) {
        $localStorage.reverse = value;
      });

      $scope.predicate = $localStorage.userPaperPredicate;
      if($scope.predicate == null)
        $scope.predicate = 'name';

      $scope.$watch("predicate", function(value) {
        $localStorage.userPaperPredicate = value;
      });

      $scope.orderBy = function(predicate, reverse) {
        $scope.predicate = predicate;
        $scope.reverse = reverse;
      };

      $scope.display_style = $localStorage.userPaperStyle;
      if($scope.display_style == null)
        $scope.display_style = 'list';

      $scope.$watch("display_style", function(value) {
        $localStorage.userPaperStyle = value;
      });

      $scope.date_filter = 'all';
      $scope.role_filter = 'all';
      $scope.tag_filter = 'all';

      // get paper of a user
      PaperService.getUserPapers($rootScope.loggedUser).then(function (data) {
        $scope.papers = data;
      }, function (err) {
        MessagesService.clear();
        switch (err.status) {
        case 401:
          MessagesService.error('_List_Papers_Not_connected_',err);
          break;
        case 500:
          MessagesService.error('_List_Papers_Something_wrong_happened_',err);
          break;
        default:
          MessagesService.error('_List_Papers_Something_wrong_happened_',err);
        }
      });

      /***************/
      /* Date Filter */
      /***************/
      var dateFilterToday = function (paper) {
        var now = new Date();
        return paper.date.getDate() == now.getDate() &&
          paper.date.getMonth() == now.getMonth() &&
          paper.date.getFullYear() == now.getFullYear();
      };
      $scope.dateFilterToday = dateFilterToday;

      var dateFilterYesterday = function (paper) {
        var yesterday = new Date();
        yesterday.setDate(yesterday.getDate() - 1);
        return paper.date.getDate() == yesterday.getDate() &&
          paper.date.getMonth() == yesterday.getMonth() &&
          paper.date.getFullYear() == yesterday.getFullYear();
      };
      $scope.dateFilterYesterday = dateFilterYesterday;

      var dateFilterLWeek = function (paper) {
        var now = new Date();
        return paper.date.getWeek() == now.getWeek() - 1;
      };
      $scope.dateFilterLWeek = dateFilterLWeek;

      var dateFilterTWeek = function (paper) {
        var now = new Date();
        return paper.date.getWeek() == now.getWeek() &&
          paper.date.getFullYear() == now.getFullYear();
      };
      $scope.dateFilterTWeek = dateFilterTWeek;

      var dateFilterMonth = function (paper) {
        var now = new Date();
        return paper.date.getMonth() == now.getMonth() &&
          paper.date.getFullYear() == now.getFullYear();
      };
      $scope.dateFilterMonth = dateFilterMonth;
      var dateFilterYear = function (paper) {
        var now = new Date();
        return paper.date.getFullYear() == now.getFullYear();
      };
      $scope.dateFilterYear = dateFilterYear;

      $scope.dateFilter = function (paper) {
        switch ($scope.date_filter) {
        case 'all':
          return true;
        case 'today':
          return dateFilterToday(paper);
        case 'yesterday':
          return dateFilterYesterday(paper);
        case 'lweek':
          return dateFilterLWeek(paper);
        case 'tweek':
          return dateFilterTWeek(paper);
        case 'month':
          return dateFilterMonth(paper);
        case 'year':
          return dateFilterYear(paper);
        }
      };

      /***************/
      /* Role filter */
      /***************/
      var roleFilterAuthor = function (paperRoles) {
        return paperRoles.role == 'author';
      };

      $scope.roleFilterAuthor = roleFilterAuthor;
      var roleFilterReviewer = function (paperRoles) {
        return paperRoles.role == 'reviewer';
      };

      $scope.roleFilterReviewer = roleFilterReviewer;
      $scope.roleFilter = function (paperRoles) {
        switch ($scope.role_filter) {
        case 'all':
          return true;
        case 'author':
          return roleFilterAuthor(paperRoles);
        case 'reviewer':
          return roleFilterReviewer(paperRoles);
        }
      };

      /**
      * Delete a paper
      */
      $scope.delete = function (paper) {
        if(!confirm(localize.getLocalizedString('_Delete_paper_confirm_', paper.name))) return;
        PaperService.delete(paper.id).then(function (data) {
          if (data.response == true) {
            $scope.papers.splice($scope.papers.indexOf(paper), 1);
          }
        }, function (err) {
          MessagesService.clear();
          switch (err.status) {
          case 401:
            MessagesService.error('_Delete_paper_User_must_be_authentified_',err);
            $rootScope.loggedUser = {};
            break;
          case 403:
            MessagesService.error('_Delete_paper_Authenticated_user_has_no_sufficient_rights_to_delete_the_paper_',err);
            break;
          case 500:
            MessagesService.error('_Delete_paper_Something_wrong_happened_',err);
            break;
          default:
            MessagesService.error('_Delete_paper_Something_wrong_happened_',err);
          }
        });
      };

      //action listener: action in the menu
      $rootScope.$on('handleMenuAction', function (event, data) {
        if ($scope[data]) {
          $scope[data]();
        }
      });
    }
  ]);
