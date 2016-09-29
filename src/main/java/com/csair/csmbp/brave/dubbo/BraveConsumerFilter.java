package com.csair.csmbp.brave.dubbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ClientRequestInterceptor;
import com.github.kristofa.brave.ClientResponseInterceptor;

@Activate(group = Constants.CONSUMER)
public class BraveConsumerFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BraveConsumerFilter.class);
	private ClientRequestInterceptor clientRequestInterceptor;
	private ClientResponseInterceptor clientResponseInterceptor;
	private DubboSpanNameProvider spanNameProvider;

	public BraveConsumerFilter() {
		SpringExtensionFactory factory = new SpringExtensionFactory();
		Brave brave = factory.getExtension(Brave.class, "brave");
		if (brave == null) {
			throw new NullPointerException("null brave bean in spring context");
		}
		clientRequestInterceptor = brave.clientRequestInterceptor();
		clientResponseInterceptor = brave.clientResponseInterceptor();

		DubboSpanNameProvider spanNameProvider = factory.getExtension(
				DubboSpanNameProvider.class, "spanNameProvider");
		if (spanNameProvider == null) {
			throw new NullPointerException(
					"null spanNameProvider bean in spring context");
		}
		this.spanNameProvider = spanNameProvider;
	}

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation)
			throws RpcException {
		LOGGER.info("start brave consumer filter");
		clientRequestInterceptor.handle(new DubboClientRequestAdapter(
				spanNameProvider));
		DubboClientResponseAdapter adapter = null;
		try {
			Result result = invoker.invoke(invocation);
			adapter = new DubboClientResponseAdapter(invocation, result);
			return result;
		} catch (RuntimeException e) {
			adapter = new DubboClientResponseAdapter(invocation, e);
			throw e;
		} finally {
			clientResponseInterceptor.handle(adapter);
		}
	}

}
