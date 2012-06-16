package me.shimi.metrics.dashboard.greaph;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.stats.Snapshot;

public class HistogramGraphData extends GraphData<Histogram> {

	private TreeMap<Long, Data> queue = new TreeMap<Long, Data>();
	
	public HistogramGraphData(int maxItems) {
		super(maxItems);
	}

	@Override
	public void addItem(Histogram metric) {
		queue.put(System.currentTimeMillis(), new Data(metric));
		if (queue.size() > maxItems) {
			queue.remove(queue.firstKey());
		}	
	}

	@Override
	public String data() {
		StringBuilder meanbuilder = new StringBuilder();
		StringBuilder maxbuilder = new StringBuilder();
		StringBuilder minbuilder = new StringBuilder();
		
		for (Entry<Long, Data> entry : queue.entrySet()) {
			Data data = entry.getValue();
			meanbuilder.append("[").append(entry.getKey()).append(",").append(data.median).append("],");
			maxbuilder.append("[").append(entry.getKey()).append(",").append(data.max).append("],");
			minbuilder.append("[").append(entry.getKey()).append(",").append(data.min).append("],");
		}
		meanbuilder.setLength(meanbuilder.length() -1);
		maxbuilder.setLength(maxbuilder.length() -1);
		minbuilder.setLength(minbuilder.length() -1);

		return "[[" + meanbuilder.toString() + "],[" + maxbuilder.toString() + "],[" +
		minbuilder.toString() + "]]";
	}
	
	public class Data {
		double median, min, max;

		public Data(Histogram metric) {
			Snapshot snapshot = metric.getSnapshot();
			this.median = snapshot.getMedian();
			this.min = metric.min();
			this.max = metric.max();
		}
	}
}