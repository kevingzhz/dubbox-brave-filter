package com.csair.csmbp.brave.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;

public interface DubboSpanNameProvider {
	String spanName(RpcContext context);
}
