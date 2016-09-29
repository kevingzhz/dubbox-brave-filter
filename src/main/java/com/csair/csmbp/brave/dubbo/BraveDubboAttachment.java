package com.csair.csmbp.brave.dubbo;

public enum BraveDubboAttachment {
	Sampled("Dubbo-Sampled"), ParentSpanId("Dubbo-ParentSpanId"), TraceId(
			"Dubbo-TraceId"), SpanId("Dubbo-SpanId");
	private final String key;

	BraveDubboAttachment(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
