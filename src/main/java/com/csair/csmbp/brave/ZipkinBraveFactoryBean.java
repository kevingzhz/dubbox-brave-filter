package com.csair.csmbp.brave;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.http.HttpSpanCollector;

public class ZipkinBraveFactoryBean implements FactoryBean<Brave> {

	private String serviceName;
	@Value("${zipkin.url}")
	private String url;
	@Value("${zipkin.connectTimeout}")
	private int connectTimeout;
	@Value("${zipkin.readTimeout}")
	private int readTimeout;
	@Value("${zipkin.flushInterval}")
	private int flushInterval;
	@Value("${zipkin.compressionEnabled}")
	private boolean compressionEnabled;

	private Brave instance;

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getFlushInterval() {
		return flushInterval;
	}

	public void setFlushInterval(int flushInterval) {
		this.flushInterval = flushInterval;
	}

	public boolean isCompressionEnabled() {
		return compressionEnabled;
	}

	public void setCompressionEnabled(boolean compressionEnabled) {
		this.compressionEnabled = compressionEnabled;
	}

	private void createInstance() {
		if (serviceName == null) {
			throw new BeanInitializationException(
					"property serviceName must be set");
		}

		Brave.Builder builder = new Brave.Builder(serviceName);
		HttpSpanCollector.Config config = HttpSpanCollector.Config.builder()
				.connectTimeout(connectTimeout)
				.readTimeout(readTimeout)
				.compressionEnabled(compressionEnabled)
				.flushInterval(flushInterval).build();
		if (url != null && !"".equals(url)) {
			builder.spanCollector(HttpSpanCollector.create(url,
					config, new EmptySpanCollectorMetricsHandler()));
		}
		builder.traceSampler(Sampler.ALWAYS_SAMPLE);
		instance = builder.build();
	}

	@Override
	public Brave getObject() throws Exception {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return Brave.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
