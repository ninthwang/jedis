package redis.clients.jedis;

public class StreamInfoGroup {

  private String name;

  private long consumers;

  private long pending;

  private StreamEntryID lastDeliveredId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getConsumers() {
    return consumers;
  }

  public void setConsumers(long consumers) {
    this.consumers = consumers;
  }

  public long getPending() {
    return pending;
  }

  public void setPending(long pending) {
    this.pending = pending;
  }

  public StreamEntryID getLastDeliveredId() {
    return lastDeliveredId;
  }

  public void setLastDeliveredId(StreamEntryID lastDeliveredId) {
    this.lastDeliveredId = lastDeliveredId;
  }
}
