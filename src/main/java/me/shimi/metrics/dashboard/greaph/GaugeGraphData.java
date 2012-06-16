package me.shimi.metrics.dashboard.greaph;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.yammer.metrics.core.Gauge;

public class GaugeGraphData extends GraphData<Gauge<?>> {

	private TreeMap<Long, String> queue = new TreeMap<Long, String>();
	
	public GaugeGraphData(int maxItems) {
		super(maxItems);
	}
	
	@Override
	public void addItem(Gauge<?> metric) {
		queue.put(System.currentTimeMillis(), metric.value().toString());
		if (queue.size() > maxItems) {
			queue.remove(queue.firstKey());
		}
	}

	@Override
	public String data() {
		StringBuffer buffer = new StringBuffer("[[");
		for (Entry<Long, String> entry : queue.entrySet()) {
			buffer.append("[").append(entry.getKey()).append(",").append(entry.getValue()).append("],");
		}
		buffer.setLength(buffer.length());
		buffer.append("]]"); 
		return buffer.toString();
	}
}