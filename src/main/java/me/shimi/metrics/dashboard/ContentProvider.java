package me.shimi.metrics.dashboard;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.yammer.metrics.core.MetricsRegistry;

public abstract class ContentProvider {

	protected MetricsRegistry metricsRegistry;
	
	public ContentProvider(MetricsRegistry metricsRegistry) {
		this.metricsRegistry = metricsRegistry;
	}

	public MetricsRegistry getMetricsRegistry() {
		return metricsRegistry;
	}
	
	protected String uri(MessageEvent msg) {
		HttpRequest request = (HttpRequest) msg.getMessage();
		return request.getUri();
	}
	
	protected void sendResponse(MessageEvent msg, byte[] content, String contentType) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setContent(ChannelBuffers.copiedBuffer(content));
		response.setHeader(CONTENT_TYPE, contentType);
		
		ChannelFuture future = msg.getChannel().write(response);
		future.addListener(ChannelFutureListener.CLOSE);
	}
	
	public abstract void process(MessageEvent msg) throws Exception;
}
