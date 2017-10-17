package com.newcare.im.pojo;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 
 * IM消息
 * 
 * @author guobxu
 *
 */
public class Message {

	private Long id;
	private Long sn;			// 消息序列号, 客户端生成
	private String content;		// 消息内容
	private int status;			// 1 - 未读 2 - 已读
	private Long fromUser;		// 发送者
	private Long toUser;		// 接收者
	private Timestamp receiveDate;	// 消息收到日期
	private Timestamp sendDate;		// 消息发送日期
//	private Date ackDate;		// ack消息

	private String fromSession;
	private String toSession;
	
	private Timestamp createDate;
	
	private String thread;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSn() {
		return sn;
	}

	public void setSn(Long sn) {
		this.sn = sn;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getFromUser() {
		return fromUser;
	}

	public void setFromUser(Long fromUser) {
		this.fromUser = fromUser;
	}

	public Long getToUser() {
		return toUser;
	}

	public void setToUser(Long toUser) {
		this.toUser = toUser;
	}

	public Timestamp getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Timestamp receiveDate) {
		this.receiveDate = receiveDate;
	}

	public Timestamp getSendDate() {
		return sendDate;
	}

	public void setSendDate(Timestamp sendDate) {
		this.sendDate = sendDate;
	}

	public String getFromSession() {
		return fromSession;
	}

	public void setFromSession(String fromSession) {
		this.fromSession = fromSession;
	}

	public String getToSession() {
		return toSession;
	}

	public void setToSession(String toSession) {
		this.toSession = toSession;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}
	
}
