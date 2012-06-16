package me.shimi.metrics.dashboard;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.MetricsRegistry;

public class MetricsDashboard {
	private final int port;
	private final int maxItems;

	public MetricsDashboard(int port, int maxItems) {
		this.port = port;
		this.maxItems = maxItems;
	}

	public void run() {

		MetricsRegistry registry = Metrics.defaultRegistry();

		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		HtmlReporter reporter = new HtmlReporter(registry, maxItems);
		ResourceProvider resourceProvider = new ResourceProvider(registry);
		DashBoardProvider dashboardProvider = new DashBoardProvider(registry, reporter);
		HttpServerHandler serverHandler = new HttpServerHandler(
				resourceProvider, dashboardProvider);
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpServerPipelineFactory(
				serverHandler));

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));
	}

	public static void main(String[] args) {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 9091;
		}
		new MetricsDashboard(port, 20).run();
	}
}