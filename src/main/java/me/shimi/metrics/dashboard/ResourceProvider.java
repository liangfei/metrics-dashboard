package me.shimi.metrics.dashboard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.yammer.metrics.core.MetricsRegistry;

public class ResourceProvider extends ContentProvider {
	
	private static final int BUF_SIZE = 0x1000; // 4K
	private static final Map<String, Resource> cache = new HashMap<String, Resource>();
	
	public ResourceProvider(MetricsRegistry metricsRegistry) {
		super(metricsRegistry);
	}

	public void process(MessageEvent msg) throws IOException {
		HttpRequest request = (HttpRequest) msg.getMessage();
		String uri = request.getUri();
		String[] split = uri.split("/");
		
		Resource resource = getResource(split, split[2]);
		
		if (resource != null) {
			sendResponse(msg, resource.content, resource.contentType);
		} else {
			sendResponse(msg, "Bad request".getBytes(), "");
		}
	}

	private Resource getResource(String[] split, String name) throws IOException {
		Resource resource = cache.get(name);
		if (resource == null) {
			byte[] content = loadResource(name);
			if (content.length > 0) {
				resource = new Resource(content, contentType(split[1]));
				cache.put(name, resource);
			}
		}
		return resource;
	}
	
	private String contentType(String type) {
		if (type.equals("js")) {
			return "application/javascript";
		}
		return "application/css";
	}

	private byte[] loadResource(String name) throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream(name);
		if (in != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    copy(in, out);
		    return out.toByteArray();
		}
		return new byte[]{};
	}
	
	public static long copy(InputStream from, OutputStream to) throws IOException {
		byte[] buf = new byte[BUF_SIZE];
		long total = 0;
		while (true) {
			int r = from.read(buf);
			if (r == -1) {
				break;
			}
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}
	
	public class Resource {
		private byte[] content;
		private String contentType;
		
		public Resource(byte[] content, String contentType) {
			this.content = content;
			this.contentType = contentType;
		}
	}
}
