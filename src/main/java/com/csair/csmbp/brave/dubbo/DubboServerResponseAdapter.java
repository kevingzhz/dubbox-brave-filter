package com.csair.csmbp.brave.dubbo;

import static com.github.kristofa.brave.internal.Util.checkNotNull;

import java.util.Arrays;
import java.util.Collection;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Result;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.ServerResponseAdapter;

public class DubboServerResponseAdapter implements ServerResponseAdapter {
	private static final String SERVER_RESULT = " Server Result";
	private static final String SERVER_EXCEPTION = " Server Exception";
	private Invocation invocation;
	private Result result;
	private RuntimeException e;

	public DubboServerResponseAdapter(Invocation invocation, Result result) {
		this.invocation = checkNotNull(invocation, "Null invocation");
		this.result = result;
	}

	public DubboServerResponseAdapter(Invocation invocation, RuntimeException e) {
		this.invocation = checkNotNull(invocation, "Null invocation");
		this.e = e;
	}

	@Override
	public Collection<KeyValueAnnotation> responseAnnotations() {
		KeyValueAnnotation keyValueAnnotation;
		if (e != null) {
			keyValueAnnotation = KeyValueAnnotation.create(
					invocation.getMethodName() + SERVER_EXCEPTION,
					e.getMessage());
		} else {
			if (result.hasException()) {
				keyValueAnnotation = KeyValueAnnotation.create(
						invocation.getMethodName() + SERVER_EXCEPTION, result
								.getException().getMessage());
			} else {
				keyValueAnnotation = KeyValueAnnotation.create(
						invocation.getMethodName() + SERVER_RESULT, "success");
			}
		}
		return Arrays.asList(keyValueAnnotation);
	}

}
