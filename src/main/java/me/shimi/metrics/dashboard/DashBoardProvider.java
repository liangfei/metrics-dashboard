package me.shimi.metrics.dashboard;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.SortedMap;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;

public class DashBoardProvider extends ContentProvider {

	private static final Pattern REFRSH_PATTERN = Pattern.compile(".*refresh=(\\d+)");

	private static final String HEADER = "<html><head>" +
			"<script type=\"text/javascript\" src=\"js/jquery-1.7.2.min.js\"></script>" +
			"<script type=\"text/javascript\" src=\"js/bootstrap.min.js\"></script>" +
			"<script type=\"text/javascript\" src=\"js/jquery.jqplot.min.js\"></script>" +
			"<script type=\"text/javascript\" src=\"js/jqplot.json2.min.js\"></script>" +
			"<script type=\"text/javascript\" src=\"js/jqplot.dateAxisRenderer.min.js\"></script>" +
			"<script type=\"text/javascript\" src=\"js/jqplot.highlighter.min.js\"></script>" +
			"<script type=\"text/javascript\" src=\"js/console.js\"></script>" +
			"<link rel=\"stylesheet\" href=\"style/bootstrap.min.css\">" +
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"style/jquery.jqplot.css\">" +
			"<meta http-equiv=\"refresh\" content=\"%s\">" +
			"</head><body><div ><h1>Metrics</h1>";
	
	private static final String FOOTER = "<div id=\"footer\">generated at <span id=\"date\"></span></div>" +
			"</div><script type=\"text/javascript\">" +
			"$(document).ready(function() {pageloaded();});" +
			"</script>" +
			"</body></html>";
	
	private HtmlReporter reporter;
	
	public DashBoardProvider(MetricsRegistry defaultRegistry, HtmlReporter reporter) {
		super(defaultRegistry);
		this.reporter = reporter;
	}
	
	@Override
	public void process(MessageEvent msg) throws Exception {
		HttpRequest request = (HttpRequest) msg.getMessage();
		
		Matcher matcher = REFRSH_PATTERN.matcher(request.getUri());
		if (matcher.matches()) {
			processBody(msg, matcher.group(1));
		} else {
			processBody(msg, "3600");
		}
	}
	
	private void processBody(MessageEvent msg, String refresh) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
		PrintStream printStream = new PrintStream(baos);
		
		printStream.printf(HEADER, refresh);
		
		printStream.append("<div id=\"metrics\" class=\"accordion\">");
		
		SortedMap<String,SortedMap<MetricName,Metric>> groupedMetrics = getMetricsRegistry().groupedMetrics();
		Set<String> groupSet = groupedMetrics.keySet();
		for (String group : groupSet) {
			int groupId = group.hashCode();
			
			printStream.append("<div class=\"accordion-group\">");
			printStream.append("<div class=\"accordion-heading\">");
			printStream.printf("<a class=\"accordion-toggle\" data-toggle=\"collapse\" href=\"#collapse-%s\">", groupId);
			printStream.append("<h2>").append(group).append("</h2>").append("</a>");
			printStream.append("</div>");
	        
			printStream.printf("<div id=\"collapse-%s\" class=\"accordion-body collapse\">", groupId);
			printStream.append("<div class=\"accordion-inner\">");
			
			SortedMap<MetricName, Metric> groupMetrics = groupedMetrics.get(group);
			Set<Entry<MetricName, Metric>> metrics = groupMetrics.entrySet();
			for (Entry<MetricName, Metric> entry : metrics) {
				printStream.append("<div class=\"row\">");
				entry.getValue().processWith(reporter, entry.getKey(), printStream);
				printStream.append("</div>");
			}
			printStream.append("</div></div></div>");
		}
		printStream.append("</div>").append(FOOTER);
		
		sendResponse(msg, baos.toByteArray(), "text/html; charset=UTF-8");
	}
}
