package redis.clients.jedis;


import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.util.Pool;

/**
 * PoolableObjectFactory custom impl.
 */
public class ConnectionFactory implements PooledObjectFactory<Connection> {

  private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

  private final JedisSocketFactory jedisSocketFactory;

  private final JedisClientConfig clientConfig;

  private volatile Pool<Connection> pool;

  public ConnectionFactory(final HostAndPort hostAndPort) {
    this.clientConfig = DefaultJedisClientConfig.builder().build();
    this.jedisSocketFactory = new DefaultJedisSocketFactory(hostAndPort);
  }

  public ConnectionFactory(final HostAndPort hostAndPort, final JedisClientConfig clientConfig) {
    this.clientConfig = DefaultJedisClientConfig.copyConfig(clientConfig);
    this.jedisSocketFactory = new DefaultJedisSocketFactory(hostAndPort, this.clientConfig);
  }

  public ConnectionFactory(final JedisSocketFactory jedisSocketFactory, final JedisClientConfig clientConfig) {
    this.clientConfig = DefaultJedisClientConfig.copyConfig(clientConfig);
    this.jedisSocketFactory = jedisSocketFactory;
  }

  public void setPool(Pool<Connection> pool) {
    this.pool = pool;
  }

  public void setPassword(final String password) {
    this.clientConfig.updatePassword(password);
  }

  @Override
  public void activateObject(PooledObject<Connection> pooledConnection) throws Exception {
//    final Connection jedis = pooledConnection.getObject();
//    if (jedis.getDB() != clientConfig.getDatabase()) {
//      jedis.select(clientConfig.getDatabase());
//    }
  }

  @Override
  public void destroyObject(PooledObject<Connection> pooledConnection) throws Exception {
    final Connection jedis = pooledConnection.getObject();
    if (jedis.isConnected()) {
      try {
        // need a proper test, probably with mock
        if (!jedis.isBroken()) {
          jedis.quit();
        }
      } catch (RuntimeException e) {
        logger.warn("Error while QUIT", e);
      }
      try {
        jedis.close();
      } catch (RuntimeException e) {
        logger.warn("Error while close", e);
      }
    }
  }

  @Override
  public PooledObject<Connection> makeObject() throws Exception {
    if (pool == null) {
      throw new IllegalStateException("Pool is not set.");
    }
    Connection jedis = null;
    try {
      jedis = new Connection(jedisSocketFactory, clientConfig, pool);
      jedis.connect();
      return new DefaultPooledObject<>(jedis);
    } catch (JedisException je) {
      if (jedis != null) {
        try {
          jedis.quit();
        } catch (RuntimeException e) {
          logger.warn("Error while QUIT", e);
        }
        try {
          jedis.close();
        } catch (RuntimeException e) {
          logger.warn("Error while close", e);
        }
      }
      throw je;
    }
  }

  @Override
  public void passivateObject(PooledObject<Connection> pooledConnection) throws Exception {
    // TODO maybe should select db 0? Not sure right now.
  }

  @Override
  public boolean validateObject(PooledObject<Connection> pooledConnection) {
    final Connection jedis = pooledConnection.getObject();
    try {
//      String host = jedisSocketFactory.getHost();
//      int port = jedisSocketFactory.getPort();
//      
//
//      String connectionHost = jedis.getClient().getHost();
//      int connectionPort = jedis.getClient().getPort();
//
//      return host.equals(connectionHost)
//          && port == connectionPort && jedis.isConnected()
//          && jedis.ping().equals("PONG");
      return jedis.isConnected() && jedis.ping();
    } catch (final Exception e) {
      logger.error("Error while validating pooled Connection object.", e);
      return false;
    }
  }
}
