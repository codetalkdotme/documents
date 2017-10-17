package com.newcare.im.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newcare.constant.Constants;
import com.newcare.im.protocal.ProtocalPackage;

/**
 * Created by wangxuhaoon 2017/4/20.
 */

public class ProtocalMessageEncoder extends ProtocolEncoderAdapter {
	// 用于打印日志信息
	private final static Logger log = LoggerFactory.getLogger(ProtocalMessageEncoder.class);
	private final String header = "|^|^hca/api/im/cs/getmsg^|^|";

	// 编码 将数据包转成字节数组
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		ProtocalPackage protocalPackage = (ProtocalPackage) message;
		// 根据报文长度开辟空间
		IoBuffer buff = IoBuffer.allocate(protocalPackage.getLength());
		// 设置为可自动扩展空间
		buff.setAutoExpand(true);
		// 将报文中的信息添加到buff中
		buff.putInt(protocalPackage.getHeadUrl().getBytes(Constants.ENCODING_UTF8).length);
		buff.putInt(protocalPackage.getContent().getBytes(Constants.ENCODING_UTF8).length);
		// CharsetEncoder charsetEncoder =
		// (CharsetEncoder)session.getAttribute("charset");
		// if(charsetEncoder == null){
		// charsetEncoder = Charset.defaultCharset().newEncoder();
		// session.setAttribute("charset",charsetEncoder);
		// }
		// buff.putString(protocalPackage.getHeadUrl(), charsetEncoder);
		// buff.putString(protocalPackage.getContent(), charsetEncoder);
		if (protocalPackage.getHeadUrl() != null) {
			buff.put(protocalPackage.getHeadUrl().getBytes(Constants.ENCODING_UTF8));
		}
		if (protocalPackage.getContent() != null) {
			buff.put(protocalPackage.getContent().getBytes(Constants.ENCODING_UTF8));
		}
		buff.flip();
		// 将报文发送出去
		out.write(buff);
	}
}
