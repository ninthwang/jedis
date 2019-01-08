package redis.clients.jedis;

import redis.clients.jedis.Protocol.Command;
import redis.clients.jedis.Protocol.Keyword;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.SafeEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static redis.clients.jedis.Protocol.Command.*;
import static redis.clients.jedis.Protocol.Command.EXISTS;
import static redis.clients.jedis.Protocol.Command.PSUBSCRIBE;
import static redis.clients.jedis.Protocol.Command.PUNSUBSCRIBE;
import static redis.clients.jedis.Protocol.Command.SUBSCRIBE;
import static redis.clients.jedis.Protocol.Command.UNSUBSCRIBE;
import static redis.clients.jedis.Protocol.Keyword.*;
import static redis.clients.jedis.Protocol.toByteArray;

public class BinaryClient extends Connection {
  public enum LIST_POSITION {
    BEFORE, AFTER;
    public final byte[] raw;

    private LIST_POSITION() {
      raw = SafeEncoder.encode(name());
    }
  }

  private boolean isInMulti;

  private String password;

  private long db;

  private boolean isInWatch;

  public BinaryClient() {
    super();
  }

  public BinaryClient(final String host) {
    super(host);
  }

  public BinaryClient(final String host, final int port) {
    super(host, port);
  }

  public BinaryClient(final String host, final int port, final boolean ssl) {
    super(host, port, ssl);
  }

  public BinaryClient(final String host, final int port, final boolean ssl,
      final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
      final HostnameVerifier hostnameVerifier) {
    super(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
  }

  public boolean isInMulti() {
    return isInMulti;
  }

  public boolean isInWatch() {
    return isInWatch;
  }

  protected byte[][] joinParameters(byte[] first, byte[][] rest) {
    byte[][] result = new byte[rest.length + 1][];
    result[0] = first;
    System.arraycopy(rest, 0, result, 1, rest.length);
    return result;
  }

  protected byte[][] joinParameters(byte[] first, byte[] second, byte[][] rest) {
    byte[][] result = new byte[rest.length + 2][];
    result[0] = first;
    result[1] = second;
    System.arraycopy(rest, 0, result, 2, rest.length);
    return result;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setDb(long db) {
    this.db = db;
  }

  @Override
  public void connect() {
    if (!isConnected()) {
      super.connect();
      if (password != null) {
        auth(password);
        getStatusCodeReply();
      }
      if (db > 0) {
        select((int)db);
        getStatusCodeReply();
      }
    }
  }

  public void ping() {
    sendCommand(Command.PING);
  }

  public void set(final byte[] key, final byte[] value) {
    sendCommand(Command.SET, key, value);
  }

  public void set(final byte[] key, final byte[] value, final byte[] nxxx, final byte[] expx,
      final long time) {
    sendCommand(Command.SET, key, value, nxxx, expx, toByteArray(time));
  }

  public void get(final byte[] key) {
    sendCommand(Command.GET, key);
  }

  public void quit() {
    db = 0;
    sendCommand(QUIT);
  }

  public void exists(final byte[]... key) {
    sendCommand(EXISTS, key);
  }

  public void del(final byte[]... keys) {
    sendCommand(DEL, keys);
  }

  public void type(final byte[] key) {
    sendCommand(TYPE, key);
  }

  public void flushDB() {
    sendCommand(FLUSHDB);
  }

  public void keys(final byte[] pattern) {
    sendCommand(KEYS, pattern);
  }

  public void randomKey() {
    sendCommand(RANDOMKEY);
  }

  public void rename(final byte[] oldkey, final byte[] newkey) {
    sendCommand(RENAME, oldkey, newkey);
  }

  public void renamenx(final byte[] oldkey, final byte[] newkey) {
    sendCommand(RENAMENX, oldkey, newkey);
  }

  public void dbSize() {
    sendCommand(DBSIZE);
  }

  public void expire(final byte[] key, final int seconds) {
    sendCommand(EXPIRE, key, toByteArray(seconds));
  }

  public void expireAt(final byte[] key, final long unixTime) {
    sendCommand(EXPIREAT, key, toByteArray(unixTime));
  }

  public void ttl(final byte[] key) {
    sendCommand(TTL, key);
  }

  public void select(final int index) {
    sendCommand(SELECT, toByteArray(index));
  }

  public void move(final byte[] key, final int dbIndex) {
    sendCommand(MOVE, key, toByteArray(dbIndex));
  }

  public void flushAll() {
    sendCommand(FLUSHALL);
  }

  public void getSet(final byte[] key, final byte[] value) {
    sendCommand(GETSET, key, value);
  }

  public void mget(final byte[]... keys) {
    sendCommand(MGET, keys);
  }

  public void setnx(final byte[] key, final byte[] value) {
    sendCommand(SETNX, key, value);
  }

  public void setex(final byte[] key, final int seconds, final byte[] value) {
    sendCommand(SETEX, key, toByteArray(seconds), value);
  }

  public void mset(final byte[]... keysvalues) {
    sendCommand(MSET, keysvalues);
  }

  public void msetnx(final byte[]... keysvalues) {
    sendCommand(MSETNX, keysvalues);
  }

  public void decrBy(final byte[] key, final long decrement) {
    sendCommand(DECRBY, key, toByteArray(decrement));
  }

  public void decr(final byte[] key) {
    sendCommand(DECR, key);
  }

  public void incrBy(final byte[] key, final long increment) {
    sendCommand(INCRBY, key, toByteArray(increment));
  }

  public void incrByFloat(final byte[] key, final double increment) {
    sendCommand(INCRBYFLOAT, key, toByteArray(increment));
  }

  public void incr(final byte[] key) {
    sendCommand(INCR, key);
  }

  public void append(final byte[] key, final byte[] value) {
    sendCommand(APPEND, key, value);
  }

  public void substr(final byte[] key, final int start, final int end) {
    sendCommand(SUBSTR, key, toByteArray(start), toByteArray(end));
  }

  public void hset(final byte[] key, final byte[] field, final byte[] value) {
    sendCommand(HSET, key, field, value);
  }

  public void hget(final byte[] key, final byte[] field) {
    sendCommand(HGET, key, field);
  }

  public void hsetnx(final byte[] key, final byte[] field, final byte[] value) {
    sendCommand(HSETNX, key, field, value);
  }

  public void hmset(final byte[] key, final Map<byte[], byte[]> hash) {
    final List<byte[]> params = new ArrayList<byte[]>();
    params.add(key);

    for (final Entry<byte[], byte[]> entry : hash.entrySet()) {
      params.add(entry.getKey());
      params.add(entry.getValue());
    }
    sendCommand(HMSET, params.toArray(new byte[params.size()][]));
  }

  public void hmget(final byte[] key, final byte[]... fields) {
    sendCommand(HMGET, joinParameters(key, fields));
  }

  public void hincrBy(final byte[] key, final byte[] field, final long value) {
    sendCommand(HINCRBY, key, field, toByteArray(value));
  }

  public void hexists(final byte[] key, final byte[] field) {
    sendCommand(HEXISTS, key, field);
  }

  public void hdel(final byte[] key, final byte[]... fields) {
    sendCommand(HDEL, joinParameters(key, fields));
  }

  public void hlen(final byte[] key) {
    sendCommand(HLEN, key);
  }

  public void hkeys(final byte[] key) {
    sendCommand(HKEYS, key);
  }

  public void hvals(final byte[] key) {
    sendCommand(HVALS, key);
  }

  public void hgetAll(final byte[] key) {
    sendCommand(HGETALL, key);
  }

  public void rpush(final byte[] key, final byte[]... strings) {
    sendCommand(RPUSH, joinParameters(key, strings));
  }

  public void lpush(final byte[] key, final byte[]... strings) {
    sendCommand(LPUSH, joinParameters(key, strings));
  }

  public void llen(final byte[] key) {
    sendCommand(LLEN, key);
  }

  public void lrange(final byte[] key, final long start, final long stop) {
    sendCommand(LRANGE, key, toByteArray(start), toByteArray(stop));
  }

  public void ltrim(final byte[] key, final long start, final long stop) {
    sendCommand(LTRIM, key, toByteArray(start), toByteArray(stop));
  }

  public void lindex(final byte[] key, final long index) {
    sendCommand(LINDEX, key, toByteArray(index));
  }

  public void lset(final byte[] key, final long index, final byte[] value) {
    sendCommand(LSET, key, toByteArray(index), value);
  }

  public void lrem(final byte[] key, final long count, final byte[] value) {
    sendCommand(LREM, key, toByteArray(count), value);
  }

  public void lpop(final byte[] key) {
    sendCommand(LPOP, key);
  }

  public void rpop(final byte[] key) {
    sendCommand(RPOP, key);
  }

  public void rpoplpush(final byte[] srckey, final byte[] dstkey) {
    sendCommand(RPOPLPUSH, srckey, dstkey);
  }

  public void sadd(final byte[] key, final byte[]... members) {
    sendCommand(SADD, joinParameters(key, members));
  }

  public void smembers(final byte[] key) {
    sendCommand(SMEMBERS, key);
  }

  public void srem(final byte[] key, final byte[]... members) {
    sendCommand(SREM, joinParameters(key, members));
  }

  public void spop(final byte[] key) {
    sendCommand(SPOP, key);
  }

  public void spop(final byte[] key, final long count) {
    sendCommand(SPOP, key, toByteArray(count));
  }

  public void smove(final byte[] srckey, final byte[] dstkey, final byte[] member) {
    sendCommand(SMOVE, srckey, dstkey, member);
  }

  public void scard(final byte[] key) {
    sendCommand(SCARD, key);
  }

  public void sismember(final byte[] key, final byte[] member) {
    sendCommand(SISMEMBER, key, member);
  }

  public void sinter(final byte[]... keys) {
    sendCommand(SINTER, keys);
  }

  public void sinterstore(final byte[] dstkey, final byte[]... keys) {
    sendCommand(SINTERSTORE, joinParameters(dstkey, keys));
  }

  public void sunion(final byte[]... keys) {
    sendCommand(SUNION, keys);
  }

  public void sunionstore(final byte[] dstkey, final byte[]... keys) {
    sendCommand(SUNIONSTORE, joinParameters(dstkey, keys));
  }

  public void sdiff(final byte[]... keys) {
    sendCommand(SDIFF, keys);
  }

  public void sdiffstore(final byte[] dstkey, final byte[]... keys) {
    sendCommand(SDIFFSTORE, joinParameters(dstkey, keys));
  }

  public void srandmember(final byte[] key) {
    sendCommand(SRANDMEMBER, key);
  }

  public void zadd(final byte[] key, final double score, final byte[] member) {
    sendCommand(ZADD, key, toByteArray(score), member);
  }

  public void zadd(final byte[] key, final double score, final byte[] member,
      final ZAddParams params) {
    sendCommand(ZADD, params.getByteParams(key, toByteArray(score), member));
  }

  public void zaddBinary(final byte[] key, final Map<byte[], Double> scoreMembers) {
    ArrayList<byte[]> args = new ArrayList<byte[]>(scoreMembers.size() * 2 + 1);
    args.add(key);
    args.addAll(convertScoreMembersToByteArrays(scoreMembers));

    byte[][] argsArray = new byte[args.size()][];
    args.toArray(argsArray);

    sendCommand(ZADD, argsArray);
  }

  public void zaddBinary(final byte[] key, final Map<byte[], Double> scoreMembers,
      final ZAddParams params) {
    ArrayList<byte[]> args = convertScoreMembersToByteArrays(scoreMembers);
    byte[][] argsArray = new byte[args.size()][];
    args.toArray(argsArray);

    sendCommand(ZADD, params.getByteParams(key, argsArray));
  }

  public void zrange(final byte[] key, final long start, final long stop) {
    sendCommand(ZRANGE, key, toByteArray(start), toByteArray(stop));
  }

  public void zrem(final byte[] key, final byte[]... members) {
    sendCommand(ZREM, joinParameters(key, members));
  }

  public void zincrby(final byte[] key, final double increment, final byte[] member) {
    sendCommand(ZINCRBY, key, toByteArray(increment), member);
  }

  public void zincrby(final byte[] key, final double increment, final byte[] member,
      final ZIncrByParams params) {
    // Note that it actually calls ZADD with INCR option, so it requires Redis 3.0.2 or upper.
    sendCommand(ZADD, params.getByteParams(key, toByteArray(increment), member));
  }

  public void zrank(final byte[] key, final byte[] member) {
    sendCommand(ZRANK, key, member);
  }

  public void zrevrank(final byte[] key, final byte[] member) {
    sendCommand(ZREVRANK, key, member);
  }

  public void zrevrange(final byte[] key, final long start, final long stop) {
    sendCommand(ZREVRANGE, key, toByteArray(start), toByteArray(stop));
  }

  public void zrangeWithScores(final byte[] key, final long start, final long stop) {
    sendCommand(ZRANGE, key, toByteArray(start), toByteArray(stop), WITHSCORES.raw);
  }

  public void zrevrangeWithScores(final byte[] key, final long start, final long stop) {
    sendCommand(ZREVRANGE, key, toByteArray(start), toByteArray(stop), WITHSCORES.raw);
  }

  public void zcard(final byte[] key) {
    sendCommand(ZCARD, key);
  }

  public void zscore(final byte[] key, final byte[] member) {
    sendCommand(ZSCORE, key, member);
  }

  public void multi() {
    sendCommand(MULTI);
    isInMulti = true;
  }

  public void discard() {
    sendCommand(DISCARD);
    isInMulti = false;
    isInWatch = false;
  }

  public void exec() {
    sendCommand(EXEC);
    isInMulti = false;
    isInWatch = false;
  }

  public void watch(final byte[]... keys) {
    sendCommand(WATCH, keys);
    isInWatch = true;
  }

  public void unwatch() {
    sendCommand(UNWATCH);
    isInWatch = false;
  }

  public void sort(final byte[] key) {
    sendCommand(SORT, key);
  }

  public void sort(final byte[] key, final SortingParams sortingParameters) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.addAll(sortingParameters.getParams());
    sendCommand(SORT, args.toArray(new byte[args.size()][]));
  }

  public void blpop(final byte[][] args) {
    sendCommand(BLPOP, args);
  }

  public void blpop(final int timeout, final byte[]... keys) {
    final List<byte[]> args = new ArrayList<byte[]>();
    for (final byte[] arg : keys) {
      args.add(arg);
    }
    args.add(Protocol.toByteArray(timeout));
    blpop(args.toArray(new byte[args.size()][]));
  }

  public void sort(final byte[] key, final SortingParams sortingParameters, final byte[] dstkey) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.addAll(sortingParameters.getParams());
    args.add(STORE.raw);
    args.add(dstkey);
    sendCommand(SORT, args.toArray(new byte[args.size()][]));
  }

  public void sort(final byte[] key, final byte[] dstkey) {
    sendCommand(SORT, key, STORE.raw, dstkey);
  }

  public void brpop(final byte[][] args) {
    sendCommand(BRPOP, args);
  }

  public void brpop(final int timeout, final byte[]... keys) {
    final List<byte[]> args = new ArrayList<byte[]>();
    for (final byte[] arg : keys) {
      args.add(arg);
    }
    args.add(Protocol.toByteArray(timeout));
    brpop(args.toArray(new byte[args.size()][]));
  }

  public void auth(final String password) {
    setPassword(password);
    sendCommand(AUTH, password);
  }

  public void subscribe(final byte[]... channels) {
    sendCommand(SUBSCRIBE, channels);
  }

  public void publish(final byte[] channel, final byte[] message) {
    sendCommand(PUBLISH, channel, message);
  }

  public void unsubscribe() {
    sendCommand(UNSUBSCRIBE);
  }

  public void unsubscribe(final byte[]... channels) {
    sendCommand(UNSUBSCRIBE, channels);
  }

  public void psubscribe(final byte[]... patterns) {
    sendCommand(PSUBSCRIBE, patterns);
  }

  public void punsubscribe() {
    sendCommand(PUNSUBSCRIBE);
  }

  public void punsubscribe(final byte[]... patterns) {
    sendCommand(PUNSUBSCRIBE, patterns);
  }

  public void pubsub(final byte[]... args) {
    sendCommand(PUBSUB, args);
  }

  public void zcount(final byte[] key, final double min, final double max) {

    sendCommand(ZCOUNT, key, toByteArray(min), toByteArray(max));
  }

  public void zcount(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(ZCOUNT, key, min, max);
  }

  public void zcount(final byte[] key, final String min, final String max) {
    sendCommand(ZCOUNT, key, min.getBytes(), max.getBytes());
  }

  public void zrangeByScore(final byte[] key, final double min, final double max) {

    sendCommand(ZRANGEBYSCORE, key, toByteArray(min), toByteArray(max));
  }

  public void zrangeByScore(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(ZRANGEBYSCORE, key, min, max);
  }

  public void zrangeByScore(final byte[] key, final String min, final String max) {
    sendCommand(ZRANGEBYSCORE, key, min.getBytes(), max.getBytes());
  }

  public void zrevrangeByScore(final byte[] key, final double max, final double min) {

    sendCommand(ZREVRANGEBYSCORE, key, toByteArray(max), toByteArray(min));
  }

  public void zrevrangeByScore(final byte[] key, final byte[] max, final byte[] min) {
    sendCommand(ZREVRANGEBYSCORE, key, max, min);
  }

  public void zrevrangeByScore(final byte[] key, final String max, final String min) {
    sendCommand(ZREVRANGEBYSCORE, key, max.getBytes(), min.getBytes());
  }

  public void zrangeByScore(final byte[] key, final double min, final double max, final int offset,
      final int count) {

    sendCommand(ZRANGEBYSCORE, key, toByteArray(min), toByteArray(max), LIMIT.raw, toByteArray(offset),
      toByteArray(count));
  }

  public void zrangeByScore(final byte[] key, final String min, final String max, final int offset,
      final int count) {

    sendCommand(ZRANGEBYSCORE, key, min.getBytes(), max.getBytes(), LIMIT.raw, toByteArray(offset),
      toByteArray(count));
  }

  public void zrevrangeByScore(final byte[] key, final double max, final double min,
      final int offset, final int count) {

    sendCommand(ZREVRANGEBYSCORE, key, toByteArray(max), toByteArray(min), LIMIT.raw, toByteArray(offset),
      toByteArray(count));
  }

  public void zrevrangeByScore(final byte[] key, final String max, final String min,
      final int offset, final int count) {

    sendCommand(ZREVRANGEBYSCORE, key, max.getBytes(), min.getBytes(), LIMIT.raw,
      toByteArray(offset), toByteArray(count));
  }

  public void zrangeByScoreWithScores(final byte[] key, final double min, final double max) {

    sendCommand(ZRANGEBYSCORE, key, toByteArray(min), toByteArray(max), WITHSCORES.raw);
  }

  public void zrangeByScoreWithScores(final byte[] key, final String min, final String max) {

    sendCommand(ZRANGEBYSCORE, key, min.getBytes(), max.getBytes(), WITHSCORES.raw);
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final double max, final double min) {

    sendCommand(ZREVRANGEBYSCORE, key, toByteArray(max), toByteArray(min), WITHSCORES.raw);
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final String max, final String min) {
    sendCommand(ZREVRANGEBYSCORE, key, max.getBytes(), min.getBytes(), WITHSCORES.raw);
  }

  public void zrangeByScoreWithScores(final byte[] key, final double min, final double max,
      final int offset, final int count) {

    sendCommand(ZRANGEBYSCORE, key, toByteArray(min), toByteArray(max), LIMIT.raw, toByteArray(offset),
      toByteArray(count), WITHSCORES.raw);
  }

  public void zrangeByScoreWithScores(final byte[] key, final String min, final String max,
      final int offset, final int count) {
    sendCommand(ZRANGEBYSCORE, key, min.getBytes(), max.getBytes(), LIMIT.raw, toByteArray(offset),
      toByteArray(count), WITHSCORES.raw);
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final double max, final double min,
      final int offset, final int count) {

    sendCommand(ZREVRANGEBYSCORE, key, toByteArray(max), toByteArray(min), LIMIT.raw, toByteArray(offset),
      toByteArray(count), WITHSCORES.raw);
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final String max, final String min,
      final int offset, final int count) {

    sendCommand(ZREVRANGEBYSCORE, key, max.getBytes(), min.getBytes(), LIMIT.raw,
      toByteArray(offset), toByteArray(count), WITHSCORES.raw);
  }

  public void zrangeByScore(final byte[] key, final byte[] min, final byte[] max, final int offset,
      final int count) {
    sendCommand(ZRANGEBYSCORE, key, min, max, LIMIT.raw, toByteArray(offset), toByteArray(count));
  }

  public void zrevrangeByScore(final byte[] key, final byte[] max, final byte[] min,
      final int offset, final int count) {
    sendCommand(ZREVRANGEBYSCORE, key, max, min, LIMIT.raw, toByteArray(offset), toByteArray(count));
  }

  public void zrangeByScoreWithScores(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(ZRANGEBYSCORE, key, min, max, WITHSCORES.raw);
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final byte[] max, final byte[] min) {
    sendCommand(ZREVRANGEBYSCORE, key, max, min, WITHSCORES.raw);
  }

  public void zrangeByScoreWithScores(final byte[] key, final byte[] min, final byte[] max,
      final int offset, final int count) {
    sendCommand(ZRANGEBYSCORE, key, min, max, LIMIT.raw, toByteArray(offset), toByteArray(count),
      WITHSCORES.raw);
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final byte[] max, final byte[] min,
      final int offset, final int count) {
    sendCommand(ZREVRANGEBYSCORE, key, max, min, LIMIT.raw, toByteArray(offset),
      toByteArray(count), WITHSCORES.raw);
  }

  public void zremrangeByRank(final byte[] key, final long start, final long stop) {
    sendCommand(ZREMRANGEBYRANK, key, toByteArray(start), toByteArray(stop));
  }

  public void zremrangeByScore(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(ZREMRANGEBYSCORE, key, min, max);
  }

  public void zremrangeByScore(final byte[] key, final String min, final String max) {
    sendCommand(ZREMRANGEBYSCORE, key, min.getBytes(), max.getBytes());
  }

  public void zunionstore(final byte[] dstkey, final byte[]... sets) {
    sendCommand(ZUNIONSTORE, joinParameters(dstkey, toByteArray(sets.length), sets));
  }

  public void zunionstore(final byte[] dstkey, final ZParams params, final byte[]... sets) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(dstkey);
    args.add(Protocol.toByteArray(sets.length));
    for (final byte[] set : sets) {
      args.add(set);
    }
    args.addAll(params.getParams());
    sendCommand(ZUNIONSTORE, args.toArray(new byte[args.size()][]));
  }

  public void zinterstore(final byte[] dstkey, final byte[]... sets) {
    sendCommand(ZINTERSTORE, joinParameters(dstkey, Protocol.toByteArray(sets.length), sets));
  }

  public void zinterstore(final byte[] dstkey, final ZParams params, final byte[]... sets) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(dstkey);
    args.add(Protocol.toByteArray(sets.length));
    for (final byte[] set : sets) {
      args.add(set);
    }
    args.addAll(params.getParams());
    sendCommand(ZINTERSTORE, args.toArray(new byte[args.size()][]));
  }

  public void zlexcount(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(ZLEXCOUNT, key, min, max);
  }

  public void zrangeByLex(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(ZRANGEBYLEX, key, min, max);
  }

  public void zrangeByLex(final byte[] key, final byte[] min, final byte[] max, final int offset,
      final int count) {
    sendCommand(ZRANGEBYLEX, key, min, max, LIMIT.raw, toByteArray(offset), toByteArray(count));
  }

  public void zrevrangeByLex(final byte[] key, final byte[] max, final byte[] min) {
    sendCommand(ZREVRANGEBYLEX, key, max, min);
  }

  public void zrevrangeByLex(final byte[] key, final byte[] max, final byte[] min,
      final int offset, final int count) {
    sendCommand(ZREVRANGEBYLEX, key, max, min, LIMIT.raw, toByteArray(offset), toByteArray(count));
  }

  public void zremrangeByLex(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(ZREMRANGEBYLEX, key, min, max);
  }

  public void save() {
    sendCommand(SAVE);
  }

  public void bgsave() {
    sendCommand(BGSAVE);
  }

  public void bgrewriteaof() {
    sendCommand(BGREWRITEAOF);
  }

  public void lastsave() {
    sendCommand(LASTSAVE);
  }

  public void shutdown() {
    sendCommand(SHUTDOWN);
  }

  public void info() {
    sendCommand(INFO);
  }

  public void info(final String section) {
    sendCommand(INFO, section);
  }

  public void monitor() {
    sendCommand(MONITOR);
  }

  public void slaveof(final String host, final int port) {
    sendCommand(SLAVEOF, host, String.valueOf(port));
  }

  public void slaveofNoOne() {
    sendCommand(SLAVEOF, NO.raw, ONE.raw);
  }

  public void configGet(final byte[] pattern) {
    sendCommand(CONFIG, Keyword.GET.raw, pattern);
  }

  public void configSet(final byte[] parameter, final byte[] value) {
    sendCommand(CONFIG, Keyword.SET.raw, parameter, value);
  }

  public void strlen(final byte[] key) {
    sendCommand(STRLEN, key);
  }

  public void sync() {
    sendCommand(SYNC);
  }

  public void lpushx(final byte[] key, final byte[]... string) {
    sendCommand(LPUSHX, joinParameters(key, string));
  }

  public void persist(final byte[] key) {
    sendCommand(PERSIST, key);
  }

  public void rpushx(final byte[] key, final byte[]... string) {
    sendCommand(RPUSHX, joinParameters(key, string));
  }

  public void echo(final byte[] string) {
    sendCommand(ECHO, string);
  }

  public void linsert(final byte[] key, final LIST_POSITION where, final byte[] pivot,
      final byte[] value) {
    sendCommand(LINSERT, key, where.raw, pivot, value);
  }

  public void debug(final DebugParams params) {
    sendCommand(DEBUG, params.getCommand());
  }

  public void brpoplpush(final byte[] source, final byte[] destination, final int timeout) {
    sendCommand(BRPOPLPUSH, source, destination, toByteArray(timeout));
  }

  public void configResetStat() {
    sendCommand(CONFIG, Keyword.RESETSTAT.name());
  }

  public void setbit(final byte[] key, final long offset, final byte[] value) {
    sendCommand(SETBIT, key, toByteArray(offset), value);
  }

  public void setbit(final byte[] key, final long offset, final boolean value) {
    sendCommand(SETBIT, key, toByteArray(offset), toByteArray(value));
  }

  public void getbit(final byte[] key, final long offset) {
    sendCommand(GETBIT, key, toByteArray(offset));
  }

  public void bitpos(final byte[] key, final boolean value, final BitPosParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.add(toByteArray(value));
    args.addAll(params.getParams());
    sendCommand(BITPOS, args.toArray(new byte[args.size()][]));
  }

  public void setrange(final byte[] key, final long offset, final byte[] value) {
    sendCommand(SETRANGE, key, toByteArray(offset), value);
  }

  public void getrange(final byte[] key, final long startOffset, final long endOffset) {
    sendCommand(GETRANGE, key, toByteArray(startOffset), toByteArray(endOffset));
  }

  public Long getDB() {
    return db;
  }

  @Override
  public void disconnect() {
    db = 0;
    super.disconnect();
  }

  @Override
  public void close() {
    db = 0;
    super.close();
  }

  public void resetState() {
    if (isInMulti()) discard();

    if (isInWatch()) {
      unwatch();
      getStatusCodeReply();
    }
  }

  public void eval(final byte[] script, final byte[] keyCount, final byte[][] params) {
    sendCommand(EVAL, joinParameters(script, keyCount, params));
  }

  public void eval(final byte[] script, final int keyCount, final byte[]... params) {
    sendCommand(EVAL, joinParameters(script, toByteArray(keyCount), params));
  }

  public void evalsha(final byte[] sha1, final byte[] keyCount, final byte[]... params) {
    sendCommand(EVALSHA, joinParameters(sha1, keyCount, params));
  }

  public void evalsha(final byte[] sha1, final int keyCount, final byte[]... params) {
    sendCommand(EVALSHA, joinParameters(sha1, toByteArray(keyCount), params));
  }

  public void scriptFlush() {
    sendCommand(SCRIPT, Keyword.FLUSH.raw);
  }

  public void scriptExists(final byte[]... sha1) {
    sendCommand(SCRIPT, joinParameters(Keyword.EXISTS.raw, sha1));
  }

  public void scriptLoad(final byte[] script) {
    sendCommand(SCRIPT, Keyword.LOAD.raw, script);
  }

  public void scriptKill() {
    sendCommand(SCRIPT, Keyword.KILL.raw);
  }

  public void slowlogGet() {
    sendCommand(SLOWLOG, Keyword.GET.raw);
  }

  public void slowlogGet(final long entries) {
    sendCommand(SLOWLOG, Keyword.GET.raw, toByteArray(entries));
  }

  public void slowlogReset() {
    sendCommand(SLOWLOG, RESET.raw);
  }

  public void slowlogLen() {
    sendCommand(SLOWLOG, LEN.raw);
  }

  public void objectRefcount(final byte[] key) {
    sendCommand(OBJECT, REFCOUNT.raw, key);
  }

  public void objectIdletime(final byte[] key) {
    sendCommand(OBJECT, IDLETIME.raw, key);
  }

  public void objectEncoding(final byte[] key) {
    sendCommand(OBJECT, ENCODING.raw, key);
  }

  public void bitcount(final byte[] key) {
    sendCommand(BITCOUNT, key);
  }

  public void bitcount(final byte[] key, final long start, final long end) {
    sendCommand(BITCOUNT, key, toByteArray(start), toByteArray(end));
  }

  public void bitop(final BitOP op, final byte[] destKey, final byte[]... srcKeys) {
    Keyword kw = Keyword.AND;
    switch (op) {
    case AND:
      kw = Keyword.AND;
      break;
    case OR:
      kw = Keyword.OR;
      break;
    case XOR:
      kw = Keyword.XOR;
      break;
    case NOT:
      kw = Keyword.NOT;
      break;
    }

    sendCommand(BITOP, joinParameters(kw.raw, destKey, srcKeys));
  }

  public void sentinel(final byte[]... args) {
    sendCommand(SENTINEL, args);
  }

  public void dump(final byte[] key) {
    sendCommand(DUMP, key);
  }

  public void restore(final byte[] key, final int ttl, final byte[] serializedValue) {
    sendCommand(RESTORE, key, toByteArray(ttl), serializedValue);
  }

  @Deprecated
  public void pexpire(final byte[] key, final int milliseconds) {
    pexpire(key, (long) milliseconds);
  }

  public void pexpire(final byte[] key, final long milliseconds) {
    sendCommand(PEXPIRE, key, toByteArray(milliseconds));
  }

  public void pexpireAt(final byte[] key, final long millisecondsTimestamp) {
    sendCommand(PEXPIREAT, key, toByteArray(millisecondsTimestamp));
  }

  public void pttl(final byte[] key) {
    sendCommand(PTTL, key);
  }

  @Deprecated
  public void psetex(final byte[] key, final int milliseconds, final byte[] value) {
    psetex(key, (long) milliseconds, value);
  }

  public void psetex(final byte[] key, final long milliseconds, final byte[] value) {
    sendCommand(PSETEX, key, toByteArray(milliseconds), value);
  }

  public void set(final byte[] key, final byte[] value, final byte[] nxxx) {
    sendCommand(Command.SET, key, value, nxxx);
  }

  public void set(final byte[] key, final byte[] value, final byte[] nxxx, final byte[] expx,
      final int time) {
    sendCommand(Command.SET, key, value, nxxx, expx, toByteArray(time));
  }

  public void srandmember(final byte[] key, final int count) {
    sendCommand(SRANDMEMBER, key, toByteArray(count));
  }

  public void clientKill(final byte[] client) {
    sendCommand(CLIENT, Keyword.KILL.raw, client);
  }

  public void clientGetname() {
    sendCommand(CLIENT, Keyword.GETNAME.raw);
  }

  public void clientList() {
    sendCommand(CLIENT, Keyword.LIST.raw);
  }

  public void clientSetname(final byte[] name) {
    sendCommand(CLIENT, Keyword.SETNAME.raw, name);
  }

  public void time() {
    sendCommand(TIME);
  }

  public void migrate(final byte[] host, final int port, final byte[] key, final int destinationDb,
      final int timeout) {
    sendCommand(MIGRATE, host, toByteArray(port), key, toByteArray(destinationDb),
      toByteArray(timeout));
  }

  public void hincrByFloat(final byte[] key, final byte[] field, final double increment) {
    sendCommand(HINCRBYFLOAT, key, field, toByteArray(increment));
  }

  @Deprecated
  /**
   * This method is deprecated due to bug (scan cursor should be unsigned long)
   * And will be removed on next major release
   * @see https://github.com/xetorthio/jedis/issues/531
   */
  public void scan(int cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(toByteArray(cursor));
    args.addAll(params.getParams());
    sendCommand(SCAN, args.toArray(new byte[args.size()][]));
  }

  @Deprecated
  /**
   * This method is deprecated due to bug (scan cursor should be unsigned long)
   * And will be removed on next major release
   * @see https://github.com/xetorthio/jedis/issues/531
   */
  public void hscan(final byte[] key, int cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.add(toByteArray(cursor));
    args.addAll(params.getParams());
    sendCommand(HSCAN, args.toArray(new byte[args.size()][]));
  }

  @Deprecated
  /**
   * This method is deprecated due to bug (scan cursor should be unsigned long)
   * And will be removed on next major release
   * @see https://github.com/xetorthio/jedis/issues/531
   */
  public void sscan(final byte[] key, int cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.add(toByteArray(cursor));
    args.addAll(params.getParams());
    sendCommand(SSCAN, args.toArray(new byte[args.size()][]));
  }

  @Deprecated
  /**
   * This method is deprecated due to bug (scan cursor should be unsigned long)
   * And will be removed on next major release
   * @see https://github.com/xetorthio/jedis/issues/531
   */
  public void zscan(final byte[] key, int cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.add(toByteArray(cursor));
    args.addAll(params.getParams());
    sendCommand(ZSCAN, args.toArray(new byte[args.size()][]));
  }

  public void scan(final byte[] cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(cursor);
    args.addAll(params.getParams());
    sendCommand(SCAN, args.toArray(new byte[args.size()][]));
  }

  public void hscan(final byte[] key, final byte[] cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.add(cursor);
    args.addAll(params.getParams());
    sendCommand(HSCAN, args.toArray(new byte[args.size()][]));
  }

  public void sscan(final byte[] key, final byte[] cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.add(cursor);
    args.addAll(params.getParams());
    sendCommand(SSCAN, args.toArray(new byte[args.size()][]));
  }

  public void zscan(final byte[] key, final byte[] cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<byte[]>();
    args.add(key);
    args.add(cursor);
    args.addAll(params.getParams());
    sendCommand(ZSCAN, args.toArray(new byte[args.size()][]));
  }

  public void waitReplicas(final int replicas, final long timeout) {
    sendCommand(WAIT, toByteArray(replicas), toByteArray(timeout));
  }

  public void cluster(final byte[]... args) {
    sendCommand(CLUSTER, args);
  }

  public void asking() {
    sendCommand(Command.ASKING);
  }

  public void pfadd(final byte[] key, final byte[]... elements) {
    sendCommand(PFADD, joinParameters(key, elements));
  }

  public void pfcount(final byte[] key) {
    sendCommand(PFCOUNT, key);
  }

  public void pfcount(final byte[]... keys) {
    sendCommand(PFCOUNT, keys);
  }

  public void pfmerge(final byte[] destkey, final byte[]... sourcekeys) {
    sendCommand(PFMERGE, joinParameters(destkey, sourcekeys));
  }

  public void readonly() {
    sendCommand(Command.READONLY);
  }

  public void geoadd(final byte[] key, final double longitude, final double latitude, final byte[] member) {
    sendCommand(GEOADD, key, toByteArray(longitude), toByteArray(latitude), member);
  }

  public void geoadd(final byte[] key, final Map<byte[], GeoCoordinate> memberCoordinateMap) {
    List<byte[]> args = new ArrayList<byte[]>(memberCoordinateMap.size() * 3 + 1);
    args.add(key);
    args.addAll(convertGeoCoordinateMapToByteArrays(memberCoordinateMap));

    byte[][] argsArray = new byte[args.size()][];
    args.toArray(argsArray);

    sendCommand(GEOADD, argsArray);
  }

  public void geodist(final byte[] key, final byte[] member1, final byte[] member2) {
    sendCommand(GEODIST, key, member1, member2);
  }

  public void geodist(final byte[] key, final byte[] member1, final byte[] member2, final GeoUnit unit) {
    sendCommand(GEODIST, key, member1, member2, unit.raw);
  }

  public void geohash(final byte[] key, final byte[]... members) {
    sendCommand(GEOHASH, joinParameters(key, members));
  }

  public void geopos(final byte[] key, final byte[][] members) {
    sendCommand(GEOPOS, joinParameters(key, members));
  }

  public void georadius(final byte[] key, final double longitude, final double latitude, final double radius, final GeoUnit unit) {
    sendCommand(GEORADIUS, key, toByteArray(longitude), toByteArray(latitude), toByteArray(radius),
      unit.raw);
  }

  public void georadius(final byte[] key, final double longitude, final double latitude, final double radius, final GeoUnit unit,
      final GeoRadiusParam param) {
    sendCommand(GEORADIUS, param.getByteParams(key, toByteArray(longitude), toByteArray(latitude),
      toByteArray(radius), unit.raw));
  }

  public void georadiusByMember(final byte[] key, final byte[] member, final double radius, final GeoUnit unit) {
    sendCommand(GEORADIUSBYMEMBER, key, member, toByteArray(radius), unit.raw);
  }

  public void georadiusByMember(final byte[] key, final byte[] member, final double radius, final GeoUnit unit,
      final GeoRadiusParam param) {
    sendCommand(GEORADIUSBYMEMBER, param.getByteParams(key, member, toByteArray(radius), unit.raw));
  }

  private ArrayList<byte[]> convertScoreMembersToByteArrays(final Map<byte[], Double> scoreMembers) {
    ArrayList<byte[]> args = new ArrayList<byte[]>(scoreMembers.size() * 2);

    for (Map.Entry<byte[], Double> entry : scoreMembers.entrySet()) {
      args.add(toByteArray(entry.getValue()));
      args.add(entry.getKey());
    }

    return args;
  }

  private List<byte[]> convertGeoCoordinateMapToByteArrays(
      final Map<byte[], GeoCoordinate> memberCoordinateMap) {
    List<byte[]> args = new ArrayList<byte[]>(memberCoordinateMap.size() * 3);

    for (Entry<byte[], GeoCoordinate> entry : memberCoordinateMap.entrySet()) {
      GeoCoordinate coordinate = entry.getValue();
      args.add(toByteArray(coordinate.getLongitude()));
      args.add(toByteArray(coordinate.getLatitude()));
      args.add(entry.getKey());
    }

    return args;
  }

  public void bitfield(final byte[] key, final byte[]... value) {
    sendCommand(BITFIELD, joinParameters(key, value));
  }

  public void xadd(byte[] key, byte[] entryId, byte[][] pairs) {
    sendCommand(XADD,joinParameters(key,entryId,pairs));
  }

  public void xadd(byte[] key,boolean approx, long maxLen, byte[] entryId, byte[][] pairs){
    if(maxLen<=0){
      xadd(key,entryId,pairs);
      return;
    }
    byte[][] params;
    if(approx){
      params=joinParameters(MAXLEN.raw,SafeEncoder.encode("~"), joinParameters(toByteArray(maxLen)
              ,entryId,pairs));
    }else{
      params=joinParameters(MAXLEN.raw,joinParameters(toByteArray(maxLen)
              ,entryId,pairs));
    }
    sendCommand(XADD,joinParameters(key,params));
  }

  public void xlen(byte[] key){
    sendCommand(XLEN,key);
  }

  public void xrange(byte[] key, byte[] startEntryId, byte[] endEntryId, long count){
    if (count < 0) {
      sendCommand(XRANGE, key, startEntryId, endEntryId);
    } else {
      sendCommand(XRANGE, key, startEntryId, endEntryId, COUNT.raw, toByteArray(count));
    }
  }

  public void xrevrange(byte[] key, byte[] startEntryId, byte[] endEntryId, long count){
    if (count < 0) {
      sendCommand(XREVRANGE, key, startEntryId, endEntryId);
    } else {
      sendCommand(XREVRANGE, key, startEntryId, endEntryId, COUNT.raw, toByteArray(count));
    }
  }

  public void xread(byte[][] pairs){
    sendCommand(XREAD,joinParameters(STREAMS.raw,pairs));
  }

  public void xread(long count, byte[][] pairs){
    sendCommand(XREAD,joinParameters(COUNT.raw,toByteArray(count),joinParameters(STREAMS.raw,pairs)));
  }

  public void xreadBlock(long block,byte[][] keys){
    int keyNum=keys.length;
    byte[][] params=new byte[2 + keyNum * 2][];
    params[0] = BLOCK.raw;
    params[1] = toByteArray(block);
    System.arraycopy(keys,0,params,2, keyNum);
    for(int i = keyNum;i < params.length;i++){
      params[i] = $.raw;
    }
    sendCommand(XREAD,params);
  }

  public void xdel(byte[] key, byte[] entryId){
    sendCommand(XDEL, key, entryId);
  }

  public void xtrimWithMaxlen(byte[] key, boolean approx, long maxlen) {
    byte[][] params;
    if(approx){
      sendCommand(XTRIM, key, MAXLEN.raw, SafeEncoder.encode("~"), toByteArray(maxlen));
    }else{
      sendCommand(XTRIM, key, MAXLEN.raw, toByteArray(maxlen));
    }
  }

  public void xgroupcreate(byte[] key, byte[] group, byte[] entryId, boolean mkstream){
    if(mkstream) {
      sendCommand(XGROUP, CREATE.raw, key, group, entryId, MKSTREAM.raw);
    }else{
      sendCommand(XGROUP, CREATE.raw, key, group, entryId);
    }
  }

  public void xgroupsetid(byte[] key, byte[] group, byte[] entryId){
    sendCommand(XGROUP, SETID.raw, key, group, entryId);
  }

  public void xgroupdestroy(byte[] key, byte[] group){
    sendCommand(XGROUP, DESTROY.raw, key, group);
  }

  public void xgroupdelconsumer(byte[] key, byte[] group, byte[] consumer){
    sendCommand(XGROUP, DELCONSUMER.raw, key, group, consumer);
  }

  public void xinfostream(byte[] key){
    sendCommand(XINFO,STREAMS.raw, key);
  }

  public void xinfogroups(byte[] key){
    sendCommand(XINFO,GROUPS.raw, key);
  }

  public void xinfoconsumers(byte[] key, byte[] group){
    sendCommand(XINFO,CONSUMERS.raw, key, group);
  }

  public void xpending(byte[] key, byte[] group){
    sendCommand(XPENDING,key,group);
  }

  public void xpending(byte[] key, byte[] group, byte[] startEntryId, byte[] endEntryId, long count, byte[] consumer){
    if(consumer==null){
      sendCommand(XPENDING, key, group, startEntryId, endEntryId, toByteArray(count));
    }else{
      sendCommand(XPENDING, key, group, startEntryId, endEntryId, toByteArray(count), consumer);
    }
  }

  public void xreadgroup(byte[] group, byte[] consumer, byte[][] params){
    sendCommand(XREADGROUP, joinParameters(GROUP.raw, joinParameters(group, consumer,joinParameters(STREAMS.raw, params))));
  }

  public void xreadgroup(byte[] group, byte[] consumer, long count, byte[][] params){
    sendCommand(XREADGROUP, joinParameters(GROUP.raw
            , joinParameters(group, consumer
                    , joinParameters(COUNT.raw, toByteArray(count)
                            ,joinParameters(STREAMS.raw, params)))));
  }

  public void xreadgroupBlock(byte[] group, byte[] consumer, long block, byte[][] keys){
    int keyNum=keys.length;
    byte[][] params=new byte[5 + keyNum * 2][];
    params[0] = GROUP.raw;
    params[1] = group;
    params[2] = consumer;
    params[3] = BLOCK.raw;
    params[4] = toByteArray(block);
    System.arraycopy(keys,0,params,5, keyNum);
    for(int i = keyNum + 5;i < params.length;i++){
      params[i] = SafeEncoder.encode(">");
    }
    sendCommand(XREADGROUP, params);
  }

  public void xack(byte[] key, byte[] group, byte[][] entryIds){
    sendCommand(XACK, joinParameters(key, group, entryIds));
  }

  public void xclaim(boolean force, boolean justid, byte[] key, byte[] group, byte[] consumer, long minIdleTime, long idleTime, long retryCount, byte[][] entryIds){
    int len = 6;
    if(retryCount > 0){
      len +=2;
    }
    if(force){
      len++;
    }
    if(justid){
      len++;
    }
    byte[][] args = new byte[entryIds.length + len][];
    len = entryIds.length + 4;
    args[0] = key;
    args[1] = group;
    args[2] = consumer;
    args[3] = toByteArray(minIdleTime);
    System.arraycopy(entryIds, 0, args, 4, entryIds.length);
    args[len++] = IDLE.raw;
    args[len++] = toByteArray(idleTime);
    if(retryCount > 0){
      args[len++] = RETRYCOUNT.raw;
      args[len++] = toByteArray(retryCount);
    }
    if(force){
      args[len++] = FORCE.raw;
    }
    if(justid){
      args[len] = JUSTID.raw;
    }
    sendCommand(XCLAIM, args);
  }
}
