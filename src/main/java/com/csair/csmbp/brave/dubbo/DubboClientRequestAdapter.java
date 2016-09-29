package com.csair.csmbp.brave.dubbo;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;

import com.alibaba.dubbo.rpc.RpcContext;
import com.csair.csmbp.brave.Util;
import com.github.kristofa.brave.ClientRequestAdapter;
import com.github.kristofa.brave.IdConversion;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.SpanId;
import com.twitter.zipkin.gen.Endpoint;

public class DubboClientRequestAdapter implements ClientRequestAdapter {
	private DubboSpanNameProvider spanNameProvider;

	public DubboClientRequestAdapter(DubboSpanNameProvider spanNameProvider) {
		this.spanNameProvider = spanNameProvider;
	}

	@Override
	public String getSpanName() {
		return spanNameProvider.spanName(RpcContext.getContext());
	}

	@Override
	public void addSpanIdToRequest(SpanId spanId) {
		RpcContext context = RpcContext.getContext();
		if (spanId == null) {
			context.setAttachment(BraveDubboAttachment.Sampled.getKey(), "0");
		} else {
			context.setAttachment(BraveDubboAttachment.Sampled.getKey(), "1");
			context.setAttachment(BraveDubboAttachment.TraceId.getKey(),
					IdConversion.convertToString(spanId.traceId));
			context.setAttachment(BraveDubboAttachment.SpanId.getKey(),
					IdConversion.convertToString(spanId.spanId));
			if (spanId.nullableParentId() != null) {
				context.setAttachment(
						BraveDubboAttachment.ParentSpanId.getKey(),
						IdConversion.convertToString(spanId.parentId));
			}
		}
	}

	@Override
	public Collection<KeyValueAnnotation> requestAnnotations() {
		return Arrays.asList(KeyValueAnnotation.create("dubbo.url", RpcContext
				.getContext().getUrl().toString()));
	}

	@Override
	public Endpoint serverAddress() {
		RpcContext context = RpcContext.getContext();
		if (context == null)
			return null;
		InetSocketAddress inetSocketAddress = context.getRemoteAddress();
		String ip = context.getUrl().getIp();
		// String serverName = serverNameProvider.serverName(RpcContext
		// .getContext());
		return Endpoint.create(null, Util.ip2Int(ip),
				inetSocketAddress.getPort());
	}

}
