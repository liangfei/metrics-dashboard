package me.shimi.metrics.dashboard.greaph;

import java.util.TreeMap;
import java.util.Map.Entry;


import com.yammer.metrics.core.Timer;

public class TimerGraphData extends GraphData<Timer> {

	private TreeMap<Long, Data> queue = new TreeMap<Long, Data>();
	
	public TimerGraphData(int maxItems) {
		super(maxItems);
	}

	@Override
	public void addItem(Timer metric) {
		queue.put(System.currentTimeMillis(), new Data(metric.getSnapshot().getMedian(), metric.max(), metric.min()));
		if (queue.size() > maxItems) {
			queue.remove(queue.firstKey());
		}
	}

	@Override
	public String data() {
		StringBuffer meanBuffer = new StringBuffer("");
		StringBuffer maxBuffer = new StringBuffer("");
		StringBuffer minBuffer = new StringBuffer("");
		
		for (Entry<Long, Data> entry : queue.entrySet()) {
			Data data = entry.getValue();
			meanBuffer.append("[").append(entry.getKey()).append(",").append(data.median).append("],");
			maxBuffer.append("[").append(entry.getKey()).append(",").append(data.max).append("],");
			minBuffer.append("[").append(entry.getKey()).append(",").append(data.min).append("],");
		}
		meanBuffer.setLength(meanBuffer.length() -1);
		maxBuffer.setLength(maxBuffer.length() -1);
		minBuffer.setLength(minBuffer.length() -1);

		return "[[" + meanBuffer.toString() + "],[" + maxBuffer.toString() + "],[" +
			minBuffer.toString() + "]]";
	}
	
	public class Data {
		double median, max, min;

		public Data(double median, double max, double min) {
			this.median = median;
			this.max = max;
			this.min = min;
		}
	}
}