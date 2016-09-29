package com.csair.csmbp.brave.dubbo;

import static com.github.kristofa.brave.IdConversion.convertToLong;

import java.util.Arrays;
import java.util.Collection;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.RpcContext;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.ServerRequestAdapter;
import com.github.kristofa.brave.SpanId;
import com.github.kristofa.brave.TraceData;

public class DubboServerRequestAdapter implements ServerRequestAdapter {
	private Invocation invocation;
	private DubboSpanNameProvider spanNameProvider;

	public DubboServerRequestAdapter(Invocation invocation,
			DubboSpanNameProvider spanNameProvider) {
		this.invocation = invocation;
		this.spanNameProvider = spanNameProvider;
	}

	private SpanId getSpanId(String traceId, String spanId, String parentSpanId) {
		return SpanId
				.builder()
				.traceId(convertToLong(traceId))
				.spanId(convertToLong(spanId))
				.parentId(
						parentSpanId == null ? null
								: convertToLong(parentSpanId)).build();
	}

	@Override
	public TraceData getTraceData() {
		final String sampled = invocation
				.getAttachment(BraveDubboAttachment.Sampled.getKey());
		if (sampled != null) {
			if (sampled.equals("0") || sampled.toLowerCase().equals("false")) {
				return TraceData.builder().sample(false).build();
			} else {
				final String parentSpanId = invocation
						.getAttachment(BraveDubboAttachment.ParentSpanId
								.getKey());
				final String traceId = invocation
						.getAttachment(BraveDubboAttachment.TraceId.getKey());
				final String spanId = invocation
						.getAttachment(BraveDubboAttachment.SpanId.getKey());
				if (traceId != null && spanId != null) {
					SpanId span = getSpanId(traceId, spanId, parentSpanId);
					return TraceData.builder().sample(true).spanId(span)
							.build();
				}
			}
		}
		return TraceData.builder().build();
	}

	@Override
	public String getSpanName() {
		return spanNameProvider.spanName(RpcContext.getContext());
	}

	@Override
	public Collection<KeyValueAnnotation> requestAnnotations() {
		KeyValueAnnotation remoteAddrAnnotation = KeyValueAnnotation.create(
				"Client Address", RpcContext.getContext().getRemoteAddressString());
		return Arrays.asList(remoteAddrAnnotation);
	}

}
