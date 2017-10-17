package com.newcare.im.dubbo.loadbalance;

import java.util.List;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;
import com.newcare.im.protocal.ProtocalPackage;

public class DirectIPLoadBalance extends AbstractLoadBalance {

	public static final String METHOD_DOCALLBACK = "doCallback";
	
	@Override
	protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
		Object[] arguments = invocation.getArguments();
		ProtocalPackage pack = (ProtocalPackage)arguments[0];
		
		for(Invoker invoker : invokers) {
			URL proxyUrl = invoker.getUrl();
			if(proxyUrl.getIp().equals(pack.getProxyIp())) {
				return invoker;
			}
		}
		
		return null;
	}

}
