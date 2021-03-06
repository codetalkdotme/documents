package com.newcare.cache.service;

import java.util.List;

import org.springframework.data.redis.core.RedisCallback;

/**
 * Created by Administrator on 2017/4/6.
 */
public interface ICacheService {

    public boolean exists(String key);

    public boolean set(byte[] key, byte[] value, long activeTime);

    public boolean set(String key, Object obj, long activeTime);

    public boolean set(String key, Object obj);

    public long delete(String key);
    
    public long delete(String... keys);

    public Object get(String key);
    
    // hash
    public Object hget(String hash, String key);
    
    public Long hdel(String hash, String key);
    
    public List<Object> hMGet(String hash, List<String> keys);
    
    public List<Object> hMGet(String hash, String[] keys);
  
    // 返回值仅用于判断是新增加键值对, 或是更新了已有键值对
    public boolean hset(String hash, String key, Object obj);

    public Object doCallback(RedisCallback callback);
    
    boolean setNX(String key, Object obj);
    
    boolean setNX(byte[] key, byte[] value);

}
