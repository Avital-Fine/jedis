package redis.clients.jedis.commands;

import redis.clients.jedis.Response;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.List;
import java.util.Set;

public interface PipelineSetCommands {

  Response<Long> sadd(String key, String... member);

  Response<Set<String>> smembers(String key);

  Response<Long> srem(String key, String... member);

  Response<String> spop(String key);

  Response<Set<String>> spop(String key, long count);

  Response<Long> scard(String key);

  Response<Boolean> sismember(String key, String member);

  Response<List<Boolean>> smismember(String key, String... members);

  Response<String> srandmember(String key);

  Response<List<String>> srandmember(String key, int count);

  default Response<ScanResult<String>> sscan(String key, String cursor) {
    return sscan(key, cursor, new ScanParams());
  }

  Response<ScanResult<String>> sscan(String key, String cursor, ScanParams params);

  Response<Set<String>> sdiff(String... keys);

  Response<Long> sdiffstore(String dstKey, String... keys);

  Response<Set<String>> sinter(String... keys);

  Response<Long> sinterstore(String dstKey, String... keys);

  Response<Set<String>> sunion(String... keys);

  Response<Long> sunionstore(String dstKey, String... keys);

  Response<Long> smove(String srckey, String dstKey, String member);

}