package me.shimi.metrics.dashboard.greaph;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.yammer.metrics.core.Metered;

public class MeterGraphData extends GraphData<Metered> {

	private TreeMap<Long, Data> queue = new TreeMap<Long, Data>();
	
	public MeterGraphData(int maxItems) {
		super(maxItems);
	}

	@Override
	public void addItem(Metered metric) {
		queue.put(System.currentTimeMillis(), new Data(metric.meanRate(), metric.oneMinuteRate(), metric.fiveMinuteRate(), metric.fifteenMinuteRate()));
		if (queue.size() > maxItems) {
			queue.remove(queue.firstKey());
		}	
	}

	@Override
	public String data() {
		StringBuffer meanBuffer = new StringBuffer();
		StringBuffer oneBuffer = new StringBuffer();
		StringBuffer fithBuffer = new StringBuffer();
		StringBuffer fithteenBuffer = new StringBuffer();
		
		for (Entry<Long, Data> entry : queue.entrySet()) {
			Data data = entry.getValue();
			meanBuffer.append("[").append(entry.getKey()).append(",").append(data.mean).append("],");
			oneBuffer.append("[").append(entry.getKey()).append(",").append(data.oneMinute).append("],");
			fithBuffer.append("[").append(entry.getKey()).append(",").append(data.fithMinutes).append("],");
			fithteenBuffer.append("[").append(entry.getKey()).append(",").append(data.fithteenMinutes).append("],");
		}
		meanBuffer.setLength(meanBuffer.length() -1);
		oneBuffer.setLength(oneBuffer.length() -1);
		fithBuffer.setLength(fithBuffer.length() -1);
		fithteenBuffer.setLength(fithteenBuffer.length() -1);

		return "[[" + meanBuffer.toString() + "],[" + oneBuffer.toString() + "],[" +
		fithBuffer.toString() + "],[" +fithteenBuffer.toString() + "]]";
	}
	
	public class Data {
		double mean, oneMinute, fithMinutes, fithteenMinutes;

		public Data(double mean, double oneMinute, double fithMinutes, double fithteenMinutes) {
			this.mean = mean;
			this.oneMinute = oneMinute;
			this.fithMinutes = fithMinutes;
			this.fithteenMinutes = fithteenMinutes;
		}
	}
}