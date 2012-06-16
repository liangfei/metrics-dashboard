package me.shimi.metrics.dashboard;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import me.shimi.metrics.dashboard.greaph.GraphData;

import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Metered;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricProcessor;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.reporting.AbstractReporter;
import com.yammer.metrics.stats.Snapshot;

public class HtmlReporter extends AbstractReporter implements MetricProcessor<PrintStream> {

	public static final String TABLE_BEGIN = "<div class=\"span4\"><table class=\"table table-striped table-bordered table-condensed\">";
	public static final String TABLE_END = "</tbody></table></div>";

	private int maxItems;
	
	protected HtmlReporter(MetricsRegistry registry, int maxItems) {
		super(registry);
		this.maxItems = maxItems;
	}

    public void processGauge(MetricName name, Gauge<?> gauge, PrintStream stream) {
    	stream.append(TABLE_BEGIN);
    	header(name.getName(), stream);
    	
    	stream.printf("<tr><td>value</td><td>%s</td></tr>", gauge.value());
    	stream.append(TABLE_END);
    	
    	GraphData<Gauge<?>> graphData = GraphData.gaugeGraphData(name, maxItems);
    	graphData.addItem(gauge);
    	
    	stream.append("<div class=\"span8 chart\"");
    	stream.printf(" id=\"chart-%s\"", name.hashCode());
    	stream.append(" style=\"height:250px;width:500px;\"");
    	stream.printf(" data=\"%s\"", graphData.data());
    	stream.append(" name=\"gauge\" labels=\"['value']\"");
    	stream.append("></div>");
    }
    
    public void processCounter(MetricName name, Counter counter, PrintStream stream) {
    	GraphData<Counter> graphData = GraphData.counterGraphData(name, maxItems);
    	graphData.addItem(counter);

    	stream.append(TABLE_BEGIN);
    	header(name.getName(), stream);
    	stream.printf("<tr><td>value</td><td>%d</td></tr>", counter.count());
    	stream.append(TABLE_END);
    	
    	stream.append("<div class=\"span8 chart\"");
    	stream.printf(" id=\"chart-%s\"", name.hashCode());
    	stream.append(" style=\"height:250px;width:500px;\"");
    	stream.printf(" data=\"%s\"", graphData.data());
    	stream.append(" name=\"count\" labels=\"['count']\"");
    	stream.append("></div>");
    }
    
    public void processMeter(MetricName name, Metered meter, PrintStream stream) {
    	 
    	 stream.append(TABLE_BEGIN);
    	 header(name.getName(), stream);
    	 meterRows(meter, stream);
    	 stream.append(TABLE_END);
    	 meterGraph(name, meter, stream);
    }
    
    private void meterRows(Metered meter, PrintStream stream) {
    	String rate = abbrev(meter.rateUnit());
    	stream.printf("<tr><td>count</td><td>%d</td></tr>", meter.count());
    	stream.printf("<tr><td>mean rate</td><td>%2.2f/%s</td></tr>", meter.meanRate(), rate);
    	stream.printf("<tr><td>1-minute rate</td><td>%2.2f/%s</td></tr>", meter.oneMinuteRate(), rate);
    	stream.printf("<tr><td>5-minute rate</td><td>%2.2f/%s</td></tr>", meter.fiveMinuteRate(), rate);
    	stream.printf("<tr><td>15-minute rate</td><td>%2.2f/%s</td></tr>", meter.fifteenMinuteRate(), rate);
    }
    
    private void meterGraph(MetricName name, Metered meter, PrintStream stream) {
    	GraphData<Metered> graphData = GraphData.meteredGraphData(name, maxItems);
    	graphData.addItem(meter);
    	
    	stream.append("<div class=\"span8 chart\"");
    	stream.printf(" id=\"chart-%s-meter\"", name.hashCode());
    	stream.append(" style=\"height:250px;width:500px;\"");
    	stream.printf(" data=\"%s\"", graphData.data());
    	stream.append(" name=\"rate\" labels=\"['mean', '1-minute', '5-minute', '15-minute']\"" );
    	stream.append("></div>");
     	 
      }
    
    private void snapShotRows(Snapshot snapshot, PrintStream stream) {
    	stream.printf("<tr><td>median</td><td>%2.2f</td></tr>", snapshot.getMedian());
    	stream.printf("<tr><td>75%%</td><td>%2.2f</td></tr>", snapshot.get75thPercentile());
    	stream.printf("<tr><td>95%%</td><td>%2.2f</td></tr>", snapshot.get95thPercentile());
    	stream.printf("<tr><td>98%%</td><td>%2.2f</td></tr>", snapshot.get98thPercentile());
    	stream.printf("<tr><td>99%%</td><td>%2.2f</td></tr>", snapshot.get99thPercentile());
    	stream.printf("<tr><td>99.9%%</td><td>%2.2f</td></tr>", snapshot.get999thPercentile());
    }

    public void processHistogram(MetricName name, Histogram histogram, PrintStream stream) {
    	
    	stream.append(TABLE_BEGIN);
    	header(name.getName(), stream);
    	final Snapshot snapshot = histogram.getSnapshot();
    	stream.printf("<tr><td>min</td><td>%2.2f</td></tr>", histogram.min());
    	stream.printf("<tr><td>max</td><td>%2.2f</td></tr>", histogram.max());
    	stream.printf("<tr><td>mean</td><td>%2.2f</td></tr>", histogram.mean());
    	stream.printf("<tr><td>stddev</td><td>%2.2f</td></tr>", histogram.stdDev());
    	snapShotRows(snapshot, stream);
        
        stream.append(TABLE_END);
        
        GraphData<Histogram> graphData = GraphData.histogramGraphData(name, maxItems);
    	graphData.addItem(histogram);
    	
    	stream.append("<div class=\"span8 chart\"");
    	stream.printf(" id=\"chart-%s\"", name.hashCode());
    	stream.append(" style=\"height:250px;width:500px;\"");
    	stream.printf(" data=\"%s\"", graphData.data());
    	stream.append(" name=\"histogram\" labels=\"['median', 'max', 'min']\"" );
    	stream.append("></div>");
    }

    public void processTimer(MetricName name, Timer timer, PrintStream stream) {
    	
    	stream.append(TABLE_BEGIN);
    	header(name.getName(), stream);
    	
    	meterRows(timer, stream);
    	String durationUnit = abbrev(timer.durationUnit());
    	final Snapshot snapshot = timer.getSnapshot();
    	stream.printf("<tr><td>min</td><td>%2.2f %s</td></tr>", timer.min(), durationUnit);
    	stream.printf("<tr><td>max</td><td>%2.2f %s</td></tr>", timer.max(), durationUnit);
    	stream.printf("<tr><td>mean</td><td>%2.2f %s</td></tr>", timer.mean(), durationUnit);
    	stream.printf("<tr><td>stddev</td><td>%2.2f %s</td></tr>", timer.stdDev(), durationUnit);
    	snapShotRows(snapshot, stream);
    	
    	stream.append(TABLE_END);
    	
    	GraphData<Timer> graphData = GraphData.timerGraphData(name, maxItems);
    	graphData.addItem(timer);
    	
    	stream.append("<div class=\"span8 chart\"");
    	stream.printf(" id=\"chart-%s\"", name.hashCode());
    	stream.append(" style=\"height:250px;width:500px;\"");
    	stream.printf(" data=\"%s\"", graphData.data());
    	stream.append(" name=\"time\" labels=\"['median', 'max', 'min']\"" );
    	stream.append("></div>");
    	
    	meterGraph(name, timer, stream);
    }
    
    
    private void header(String name, PrintStream stream) {
    	stream.printf("<thead><th>%s</th></thead><tbody>", name);
    }

    private String abbrev(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "us";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "m";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new IllegalArgumentException("Unrecognized TimeUnit: " + unit);
        }
    }
}
