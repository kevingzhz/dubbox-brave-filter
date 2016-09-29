package com.csair.csmbp.brave.dubbo;

import static com.github.kristofa.brave.internal.Util.checkNotNull;

import java.util.Arrays;
import java.util.Collection;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Result;
import com.github.kristofa.brave.ClientResponseAdapter;
import com.github.kristofa.brave.KeyValueAnnotation;

public class DubboClientResponseAdapter implements ClientResponseAdapter {
	private static final String CLIENT_RESULT = " Client Result";
	private static final String CLIENT_EXCEPTION = " Client Exception";
	
	private Invocation invocation;
	private Result result;
	private RuntimeException e;

	public DubboClientResponseAdapter(Invocation invocation, RuntimeException e) {
		this.invocation = checkNotNull(invocation, "Null invocation");
		this.e = e;
	}

	public DubboClientResponseAdapter(Invocation invocation, Result rpcResult) {
		this.invocation = checkNotNull(invocation, "Null invocation");
		this.result = rpcResult;
	}

	@Override
	public Collection<KeyValueAnnotation> responseAnnotations() {
		KeyValueAnnotation keyValueAnnotation;
		if (e != null) {
			keyValueAnnotation = KeyValueAnnotation.create(
					invocation.getMethodName() + CLIENT_EXCEPTION, e.getMessage());
		} else {
			if (result.hasException()) {
				keyValueAnnotation = KeyValueAnnotation
						.create(invocation.getMethodName() + CLIENT_EXCEPTION, result.getException()
								.getMessage());
			} else {
				keyValueAnnotation = KeyValueAnnotation
						.create(invocation.getMethodName() + CLIENT_RESULT, "success");
			}
		}
		return Arrays.asList(keyValueAnnotation);
	}

}
