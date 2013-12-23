(function(self) {

  var Blue = function() {

    // get the paper identifier
    this.paperId  = s/\<papers\/()\/web\>/

    // initialize the editor
    this.editor = ace.edit("ace-editor");
    this.editor.setTheme("ace/theme/github");
    var session = this.editor.getSession();
    session.setMode("ace/mode/latex");
    session.setUseWrapMode(true);
    session.setUseSoftTabs(true);

  };

  self.blue = new Blue();

})(this)

