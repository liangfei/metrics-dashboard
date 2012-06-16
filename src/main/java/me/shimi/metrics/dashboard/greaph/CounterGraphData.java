package me.shimi.metrics.dashboard.greaph;

import java.util.TreeMap;
import java.util.Map.Entry;


import com.yammer.metrics.core.Counter;

public class CounterGraphData extends GraphData<Counter> {

	private TreeMap<Long, Long> queue = new TreeMap<Long, Long>(); 
	
	public CounterGraphData(int maxItems) {
		super(maxItems);
	}

	public String data() {
		StringBuffer buffer = new StringBuffer("[[");
		for (Entry<Long, Long> entry : queue.entrySet()) {
			buffer.append("[").append(entry.getKey()).append(",").append(entry.getValue()).append("],");
		}
		buffer.setLength(buffer.length());
		buffer.append("]]"); 
		return buffer.toString();
	}

	@Override
	public void addItem(Counter metric) {
		queue.put(System.currentTimeMillis(), metric.count());
		if (queue.size() > maxItems) {
			queue.remove(queue.firstKey());
		}
	}
}