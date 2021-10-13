package redis.clients.jedis;

import java.util.ArrayList;
import java.util.Iterator;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.args.RawableFactory;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.params.IParams;

public class CommandArguments implements Iterable<Rawable> {

  private final ArrayList<Rawable> args;

  private boolean blocking;

  private CommandArguments() {
    throw new InstantiationError();
  }

  public CommandArguments(ProtocolCommand command) {
    args = new ArrayList<>();
    args.add(command);
  }

  public ProtocolCommand getCommand() {
    return (ProtocolCommand) args.get(0);
  }

  public CommandArguments add(Object arg) {
    if (arg instanceof Rawable) {
      args.add((Rawable) arg);
    } else if (arg instanceof byte[]) {
      args.add(RawableFactory.from((byte[]) arg));
    } else if (arg instanceof String) {
      args.add(RawableFactory.from((String) arg));
    } else {
      args.add(RawableFactory.from(String.valueOf(arg)));
    }
    return this;
  }

  public CommandArguments addObjects(Object... args) {
    for (Object arg : args) {
      add(arg);
    }
    return this;
  }

  public CommandArguments addObjects(int[] ints) {
    for (int i : ints) {
      add(i);
    }
    return this;
  }

  public CommandArguments key(Object key) {
    if (key instanceof Rawable) {
      Rawable raw = (Rawable) key;
      processKey(raw.getRaw());
      args.add(raw);
    } else if (key instanceof byte[]) {
      byte[] raw = (byte[]) key;
      processKey(raw);
      args.add(RawableFactory.from(raw));
    } else if (key instanceof String) {
      String raw = (String) key;
      processKey(raw);
      args.add(RawableFactory.from(raw));
    } else {
      throw new IllegalArgumentException("\"" + key.toString() + "\" is not a valid argument.");
    }
    return this;
  }

  public CommandArguments keys(Object... args) {
    for (Object arg : args) {
      key(arg);
    }
    return this;
  }

  public CommandArguments addParams(IParams params) {
    params.addParams(this);
    return this;
  }

  protected void processKey(byte[] key) {
    // do nothing
  }

  protected void processKey(String key) {
    // do nothing
  }

  public int size() {
    return args.size();
  }

  @Override
  public Iterator<Rawable> iterator() {
    return args.iterator();
  }

  public boolean isBlocking() {
    return blocking;
  }

  public CommandArguments blocking() {
    this.blocking = true;
    return this;
  }
}