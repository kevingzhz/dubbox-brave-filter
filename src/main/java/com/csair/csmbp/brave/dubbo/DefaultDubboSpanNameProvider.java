package com.csair.csmbp.brave.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;

public class DefaultDubboSpanNameProvider implements DubboSpanNameProvider {

	@Override
	public String spanName(RpcContext context) {
		String interfaceName = context.getUrl().getPath();
		String simpleName = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);
		return simpleName + "." + context.getMethodName();
	}

}
