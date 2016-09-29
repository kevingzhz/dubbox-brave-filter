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
import com.github.kristofa.brave.ServerRequestInterceptor;
import com.github.kristofa.brave.ServerResponseInterceptor;

@Activate(group = Constants.PROVIDER)
public class BraveProviderFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BraveProviderFilter.class);
	private ServerRequestInterceptor serverRequestInterceptor;
	private ServerResponseInterceptor serverResponseInterceptor;
	private DubboSpanNameProvider spanNameProvider;

	public BraveProviderFilter() {
		SpringExtensionFactory factory = new SpringExtensionFactory();
		Brave brave = factory.getExtension(Brave.class, "brave");
		if (brave == null) {
			throw new NullPointerException("null brave bean in spring context");
		}
		serverRequestInterceptor = brave.serverRequestInterceptor();
		serverResponseInterceptor = brave.serverResponseInterceptor();
		
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
		LOGGER.info("start brave provider filter");
		// before filter ...
		serverRequestInterceptor.handle(new DubboServerRequestAdapter(
				invocation, spanNameProvider));
		DubboServerResponseAdapter adapter = null;
		try {
			Result result = invoker.invoke(invocation);
			adapter = new DubboServerResponseAdapter(invocation, result);
			return result;
		} catch (RuntimeException e) {
			adapter = new DubboServerResponseAdapter(invocation, e);
			throw e;
		} finally {
			// after filter ...
			serverResponseInterceptor.handle(adapter);
		}
	}

}
