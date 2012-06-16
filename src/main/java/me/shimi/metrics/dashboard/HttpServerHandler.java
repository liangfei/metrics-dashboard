package me.shimi.metrics.dashboard;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HttpServerHandler extends SimpleChannelUpstreamHandler {

	private ResourceProvider resourceProvider;
	private DashBoardProvider dashboardProvider;
	
	public HttpServerHandler(ResourceProvider resourceProvider, DashBoardProvider dashboardProvider) {
		this.resourceProvider = resourceProvider;
		this.dashboardProvider = dashboardProvider;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent msg)
			throws Exception {
		
		HttpRequest request = (HttpRequest) msg.getMessage();
		String uri = request.getUri();
		
		if(uri.startsWith("/style") || uri.startsWith("/js")) {
			resourceProvider.process(msg);
		} else {
			dashboardProvider.process(msg);
		}
	}
}