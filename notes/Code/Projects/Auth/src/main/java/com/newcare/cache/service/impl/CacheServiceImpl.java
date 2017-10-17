package com.newcare.cache.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import com.newcare.cache.service.ICacheService;

/**
 * Created by guobxu on 2017/4/6.
 */
@Service
public class CacheServiceImpl implements ICacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean exists(String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
    }

    @Override
    public boolean set(byte[] key, byte[] value, long activeTime) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                boolean rs = true;
                connection.set(key, value);
                if (activeTime > 0) {
                    rs = connection.expire(key, activeTime);
                }
                return rs;
            }
        });
    }

    @Override
    public boolean set(String key, Object obj, long activeTime) {
        Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
        byte[] valBytes = serializer.serialize(obj);

        return set(key.getBytes(), valBytes, activeTime);
    }

    @Override
    public boolean set(String key, Object obj) {
        return this.set(key, obj, 0L);
    }

    @Override
    public long delete(String key) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                long result = connection.del(key.getBytes());

                return result;
            }
        });
    }

    public Object get(final String key) {
        byte[] bytes = redisTemplate.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.get(key.getBytes());
            }
        });

        if(bytes == null) return null;
        
        Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
        return serializer.deserialize(bytes);
    }

	@Override
	public Object hget(String hash, String key) {
		byte[] data = redisTemplate.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	return connection.hGet(hash.getBytes(), key.getBytes());
            }
        });
		
		if(data == null) return null;
		
		Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
        return serializer.deserialize(data);
	}

	@Override
	public boolean hset(String hash, String key, Object obj) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
                byte[] valBytes = serializer.serialize(obj);
            	
            	return connection.hSet(hash.getBytes(), key.getBytes(), valBytes);
            }
        });
	}

	@Override
	public List<Object> hMGet(String hash, List<String> keys) {
		String[] keysArr = new String[keys.size()];
		keys.toArray(keysArr);
		
		return hMGet(hash, keysArr);
	}
	
	@Override
	public List<Object> hMGet(String hash, String[] keys) {
		List<byte[]> bytesList = redisTemplate.execute(new RedisCallback<List<byte[]>>() {
            public List<byte[]> doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	byte[][] bytesArr = new byte[keys.length][];
            	for(int i = 0; i < keys.length; i++) {
            		bytesArr[i] = keys[i].getBytes();
            	}
            	List<byte[]> bytesList = connection.hMGet(hash.getBytes(), bytesArr);
            	
            	return bytesList;
            }
        });
		
		if(bytesList == null) return null;
		
		Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
		List<Object> objList = new ArrayList<Object>();
		for(byte[] bytes : bytesList) {
			objList.add(serializer.deserialize(bytes));
		}
		
        return objList;
	}
	
	@Override
	public Object doCallback(RedisCallback callback) {
		return redisTemplate.execute(callback);
	}

	@Override
	public Set<String> hKeys(String hash) {
		Set<String> keys = new HashSet<String>();
		Set<byte[]> keyBytes = redisTemplate.execute(new RedisCallback<Set<byte[]>>() {
            public Set<byte[]> doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	Set<byte[]> tmpBytes = connection.hKeys(hash.getBytes());
            	
            	return tmpBytes;
            }
        });
		
		if(keyBytes != null && keyBytes.size() > 0) {
			for(byte[] bytes : keyBytes) {
				keys.add(new String(bytes));
			}
		}
		
		return keys;
	}

	@Override
	public void hDel(String hash, String key) {
		redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	return connection.hDel(hash.getBytes(), key.getBytes());
            }
        });
	}

}
