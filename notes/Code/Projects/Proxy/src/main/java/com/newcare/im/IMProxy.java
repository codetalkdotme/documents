package com.newcare.im;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.newcare.im.exception.IMServiceException;
import com.newcare.im.filter.ProtocolMessageFactory;
import com.newcare.im.service.IMService;
import com.newcare.mesg.MessageService;
import com.newcare.proxy.AbstractServiceProxy;

@Component("imProxy")
@ConfigurationProperties("im")
public class IMProxy extends AbstractServiceProxy {

	private Logger LOGGER = LoggerFactory.getLogger(IMProxy.class);
	
	@Autowired
	private MessageService mesgService;
	
	@Autowired
	private IMService imService;
	
	@Autowired
	private IMProxyHandler proxyHandler;
	
	private String host;
	private int port;
	private int bufferSize;
	private int idleTime;
	
	@PostConstruct
    public void postConstruct() throws IMServiceException {
//		this.imService = (IMService)applicationContext.getBean("imService");
		
        start();
    }
	
	public void start() throws IMServiceException {
		LOGGER.info("IM Proxy listener starting...");
		
		IoAcceptor acceptor = new NioSocketAcceptor();

		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolMessageFactory()));

		proxyHandler.setImService(imService);
		acceptor.setHandler(proxyHandler);
		acceptor.getSessionConfig().setReadBufferSize(bufferSize);

		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
		try {
			acceptor.bind(new InetSocketAddress(host, port));
		} catch (IOException ex) {
			String errmsg = mesgService.get("im_startup_exception");
			LOGGER.error(errmsg, ex);
			
			throw new IMServiceException(errmsg, ex);
		}
		
		LOGGER.info("IM Proxy listener successfully started...listen on port {}...", port);
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}

}