
var pageloaded = function() {
  $("#date").append(new Date().toString());
  
  var chartsDiv = $(".chart")
  for (i = 0; i < chartsDiv.length; i++) {
	  var div = chartsDiv[i];
	  var data = eval("(" + div.getAttribute("data") + ")");
	  var labels = eval("(" + div.getAttribute("labels") + ")");
	  var name = div.getAttribute("name");
	  var id = div.getAttribute("id");
	  displayChart(id, name, labels, data);
  }
}


displayChart = function(id, name, l, data) {
	
	  $.jqplot(id, data,{
	    title: name,
	    highlighter:{show: true},
	    axes:{xaxis:{renderer:$.jqplot.DateAxisRenderer,tickOptions:{formatString:'%H:%M:%S'}}},
	    legend: {
            show: true,
            location: 'e',
            labels: l
        }
	  });
}
