package com.newcare.im.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by wangxuhaoon 2017/4/20.
 */

public class ProtocolMessageFactory implements ProtocolCodecFactory {
	
	
	private final ProtocalMessageEncoder encoder;
	private final ProtocolMessageDecoder decoder;

	// ππ‘Ï
	public ProtocolMessageFactory() {
		encoder = new ProtocalMessageEncoder();
		decoder = new ProtocolMessageDecoder();
	}

	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return encoder;
	}
}
