var request = require('request');
var util = require('util');
var htmlParser = require('htmlparser');

var scrapePage = function(error,body){
  if(error){
    console.log('error scraping:');
    console.log(error);
  }
  else
  {
    // look for all urls on the page
    var handler = new htmlParser.DefaultHandler(function(err,dom){
      if(error)
        console.error(error);
      else{
       var linkElements =  htmlParser.DomUtils.getElements({ class: 'card-recipe-info' }, dom);
       linkElements.forEach(function(linkElement){
        console.log(linkElement.attribs.href);
       });
      }
    },{'verbose':false});
    var parser = new htmlParser.Parser(handler);
    parser.parseComplete(body);
  }
};


for(var i=process.argv[2];i<=process.argv[3];i++){
  request('http://cooking.nytimes.com/partial/latest_recipes?latest-type=all&page='+i,function(error,response,body){
    scrapePage(error,body);
  });
}
