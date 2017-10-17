package com.newcare.storage.service.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.newcare.storage.exception.FileStorageException;
import com.newcare.storage.service.IStorageService;
import com.newcare.storage.service.StorageConstants;

/**
 * 
 * 文件存储服务实现，基于FASTDFS
 * 
 * @author guobxu
 *
 */

@Service("storageService")
@ConfigurationProperties("fdfs")
public class StorageServiceImpl implements IStorageService {
	
	// 文件不存在异常消息 TODO
	public static final String FILE_MISSING_EXPMSG = "getStoreStorage fail, errno code: 22";
	
	private int connectTimeout;
	private int networkTimeout;
	private String charset;
	private String trackerServers;
	
	@PostConstruct
    public void postConstruct() throws Exception {
		ClientGlobal.g_connect_timeout = getConnectTimeout() * 1000;
		ClientGlobal.g_network_timeout = getNetworkTimeout() * 1000;
		ClientGlobal.g_charset = charset;
		
		// track group
		String[] arr = trackerServers.split(",");
		InetSocketAddress[] addresses = new InetSocketAddress[arr.length];
  		for (int i=0; i< arr.length; i++) {
  			String tmp = arr[i];
  			int index = tmp.indexOf(":");
  			String host = tmp.substring(0, index), port = tmp.substring(index + 1);
  			
  			addresses[i] = new InetSocketAddress(host.trim(), Integer.parseInt(port));
  		}
  		
  		ClientGlobal.g_tracker_group = new TrackerGroup(addresses);
    }
	
	@Override
	public String store(byte[] data, Map<String, String> metas) throws FileStorageException {
		TrackerServer trackerServer = null;
		
		try {
			TrackerClient tracker = new TrackerClient();
			trackerServer = tracker.getConnection();
			StorageServer server = null;
	        StorageClient client = new StorageClient(trackerServer, server);  
	  
	        String[] rt = client.upload_file(data, null, convertAsNameValues(metas));
	        
	        return rt != null ? rt[0] + "/" + rt[1] : null;
		} catch(Exception ex) {
			throw new FileStorageException(ex);
		} finally {
			if(trackerServer != null) {
				try {
					trackerServer.close();
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
			
	}
	
	@Override
	public byte[] fetch(String uri) throws FileStorageException {
		TrackerServer trackerServer = null;
		
		try {
			TrackerClient tracker = new TrackerClient();
			trackerServer = tracker.getConnection();
	        StorageClient1 client = new StorageClient1(trackerServer, null);  
	        
	        // uri转换为FDFS的goup & path
	        int index = uri.indexOf("/");
			String group = uri.substring(0, index), path = uri.substring(index + 1);
	        byte[] data = client.download_file(group, path);
	        
	        return data;
		} catch(MyException ex) {
			ex.printStackTrace();
			
			if(FILE_MISSING_EXPMSG.equals(ex.getMessage())) {
				return null;
			} else {
				throw new FileStorageException(ex); 
			}
		} catch(Exception ex) {
			throw new FileStorageException(ex);
		} finally {
			if(trackerServer != null) {
				try {
					trackerServer.close();
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public Map<String, Object> fetchWithMeta(String uri) throws FileStorageException {
		TrackerServer trackerServer = null;

		try {
			TrackerClient tracker = new TrackerClient();
			trackerServer = tracker.getConnection();
	        StorageClient1 client = new StorageClient1(trackerServer, null);  
	        
	        // uri转换为FDFS的goup & path
	        int index = uri.indexOf("/");
			String group = uri.substring(0, index), path = uri.substring(index + 1);
	        byte[] data = client.download_file(group, path);
	        
	        // 元数据
	        NameValuePair[] pairs = client.get_metadata(group, path);
	        Map<String, String> metas = convertAsMap(pairs);
	        
	        Map<String, Object> rt = new HashMap<String, Object>();
	        rt.put(StorageConstants.MAP_FILE_DATA, data);
	        rt.put(StorageConstants.MAP_FILE_META, metas);
	        
	        return rt;
		} catch(MyException ex) {
			ex.printStackTrace();
			
			if(FILE_MISSING_EXPMSG.equals(ex.getMessage())) {
				return null;
			} else {
				throw new FileStorageException(ex); 
			}
		} catch(Exception ex) {
			throw new FileStorageException(ex);
		} finally {
			if(trackerServer != null) {
				try {
					trackerServer.close();
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private NameValuePair[] convertAsNameValues(Map<String, String> metas) {
		if(metas == null) return null;
		
		NameValuePair[] pairs = new NameValuePair[metas.size()];
		
		int i = 0;
		for(Map.Entry<String, String> kv: metas.entrySet()) {
			pairs[i++] = new NameValuePair(kv.getKey(), kv.getValue());
		}
		
		return pairs;
	}
	
	private Map<String, String> convertAsMap(NameValuePair[] nameValues) {
		if(nameValues == null) return null;
		
		Map<String, String> metas = new HashMap<String, String>();
		for(NameValuePair nameValue : nameValues) {
			metas.put(nameValue.getName(), nameValue.getValue());
		}
		
		return metas;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getNetworkTimeout() {
		return networkTimeout;
	}

	public void setNetworkTimeout(int networkTimeout) {
		this.networkTimeout = networkTimeout;
	}

	public String getTrackerServers() {
		return trackerServers;
	}

	public void setTrackerServers(String trackerServers) {
		this.trackerServers = trackerServers;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}













