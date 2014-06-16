angular.module('bluelatex.Paper.Services.Markdown', ['ngStorage'])
  .factory("MarkdownService", ['$log','$q','$http',
    function ($log,$q,$http) {

      /**
      * Create the table of content from Markdown
      */
      var parseTOC = function (content) {
        var toc = [];
        // search different section
        var regex = '(^(#+) ([^\n]+)|([^\n]+)\n((-|=){2,}))';
        var reg = new RegExp(regex, "gi");
        var astring = content.split('\n');
        var countToLine = regex.match(/\n(?!\])/g).length;

        for (var i = 0; i < astring.length; i++) {
          if(astring[i] == "") continue;
          var number = i + 1;
          var line = astring.slice(i,i+1+countToLine).join("\n");

          var result;
          if ((result = line.match(regex)) !== null) {
            var title =  result[3];
            var type = result[2];
            var level = 0;
            if(title == null) {
              title = result[4];
              type = result[6];
              level = type == '-'? 2: 1;
            } else {
              level = type.length
            }
            toc.push({
              type: type,
              level: level,
              restart: false,
              title: title,
              line: number
            });
          }
        }
        return toc;
      };

      return {
        parseTOC: parseTOC
      };
    }
  ]);