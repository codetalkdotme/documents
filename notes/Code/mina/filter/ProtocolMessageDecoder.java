package com.newcare.im.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newcare.constant.Constants;
import com.newcare.im.protocal.ProtocalPackage;

/**
 * Created by wangxuhaoon 2017/4/20.
 */

public class ProtocolMessageDecoder extends CumulativeProtocolDecoder {
	// 打印日志信息
	private final static Logger log = LoggerFactory.getLogger(ProtocolMessageDecoder.class);

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// ProtocalPackage protocalPackage = (ProtocalPackage)
		// session.getAttribute("protocalPackage"); //
		// 从session对象中获取“xhs-upload”属性值
		// if(null==protocalPackage){
		//
		// }
		ProtocalPackage protocalPackage = null;
		int packHeadLenth = 8; // 包头长度(int 的长度) 根据自定义协议的包头的长度
		if (in.remaining() > packHeadLenth) { // 说明缓冲区中有数据
			in.mark();// 标记当前position，以便后继的reset操作能恢复position位置

			// 获取数据包长度
			int len = in.getInt();
			log.info("len = " + len);
			int contentLen = in.getInt();

			// 上面的get会改变remaining()的值

			if (in.remaining() < len + contentLen) {
				// 内容不够， 重置position到操作前，进行下一轮接受新数据
				in.reset();
				return false;
			} else {
				byte[] headUrl = new byte[len];
				in.get(headUrl);
				String headUrlStr = new String(headUrl, Constants.ENCODING_UTF8);
				log.info("wang", "headurlStr==" + headUrlStr);

				byte[] content = new byte[contentLen];
				in.get(content);
				String contentStr = new String(content, Constants.ENCODING_UTF8);
				log.info("wang", "contentStr==" + contentStr);

				protocalPackage = new ProtocalPackage(headUrlStr, contentStr);
				out.write(protocalPackage);

				// //内容足够
				// in.reset(); //重置回复position位置到操作前
				// byte[] packArray = new byte[8 +len +contentLen];
				// in.get(packArray, 0, 8 +len +contentLen); //获取整条报文
				//
				// //根据自己需要解析接收到的东西 我的例子 把收到的报文转成String
				// String str = new String(packArray);
				// out.write(str); //发送出去 就算完成了

				if (in.remaining() > 0) {// 如果读取一个完整包内容后还粘了包，就让父类再调用一次，进行下一次解析
					return true;
				}
			}
		}
		return false; // 处理成功
	}
}
