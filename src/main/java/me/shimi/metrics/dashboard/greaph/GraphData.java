package me.shimi.metrics.dashboard.greaph;

import java.util.HashMap;
import java.util.Map;

import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Metered;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.Timer;

public abstract class GraphData<T extends Metric> {

	protected static Map<MetricName, GraphData<Counter>> CounterQueues = new HashMap<MetricName, GraphData<Counter>>();
	protected static Map<MetricName, GraphData<Timer>> TimerQueues = new HashMap<MetricName, GraphData<Timer>>();
	protected static Map<MetricName, GraphData<Metered>> MeteredQueues = new HashMap<MetricName, GraphData<Metered>>();
	protected static Map<MetricName, GraphData<Histogram>> HistogramQueues = new HashMap<MetricName, GraphData<Histogram>>();
	protected static Map<MetricName, GraphData<Gauge<?>>> GaugeQueues = new HashMap<MetricName, GraphData<Gauge<?>>>();

	protected int maxItems;
	
	public GraphData(int maxItems) {
		this.maxItems = maxItems;
	}
	
	public abstract void addItem(T metric);
	
	public abstract String data();
	
	public static GraphData<Counter> counterGraphData(MetricName name, int maxItems) {
		GraphData<Counter> data = CounterQueues.get(name);
		if (data == null) {
			data = new CounterGraphData(maxItems);
			CounterQueues.put(name, data);
		}
		return data;
	}
	
	public static GraphData<Timer> timerGraphData(MetricName name, int maxItems) {
		GraphData<Timer> data = TimerQueues.get(name);
		if (data == null) {
			data = new TimerGraphData(maxItems);
			TimerQueues.put(name, data);
		}
		return data;
	}
	
	public static GraphData<Metered> meteredGraphData(MetricName name, int maxItems) {
		GraphData<Metered> data = MeteredQueues.get(name);
		if (data == null) {
			data = new MeterGraphData(maxItems);
			MeteredQueues.put(name, data);
		}
		return data;
	}
	
	public static GraphData<Histogram> histogramGraphData(MetricName name, int maxItems) {
		GraphData<Histogram> data = HistogramQueues.get(name);
		if (data == null) {
			data = new HistogramGraphData(maxItems);
			HistogramQueues.put(name, data);
		}
		return data;
	}
	
	public static GraphData<Gauge<?>> gaugeGraphData(MetricName name, int maxItems) {
		GraphData<Gauge<?>> data = GaugeQueues.get(name);
		if (data == null) {
			data = new GaugeGraphData(maxItems);
			GaugeQueues.put(name, data);
		}
		return data;
	}
}
