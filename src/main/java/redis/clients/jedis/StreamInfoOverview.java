package redis.clients.jedis;

public class StreamInfoOverview {

  private long length;

  private long radixTreeKeys;

  private long radixTreeNodes;

  private long groups;

  private String lastGeneratedId;

  private StreamEntry firstEntry;

  private StreamEntry lastEntry;

  public long getLength() {
    return length;
  }

  public void setLength(long length) {
    this.length = length;
  }

  public long getRadixTreeKeys() {
    return radixTreeKeys;
  }

  public void setRadixTreeKeys(long radixTreeKeys) {
    this.radixTreeKeys = radixTreeKeys;
  }

  public long getRadixTreeNodes() {
    return radixTreeNodes;
  }

  public void setRadixTreeNodes(long radixTreeNodes) {
    this.radixTreeNodes = radixTreeNodes;
  }

  public long getGroups() {
    return groups;
  }

  public void setGroups(long groups) {
    this.groups = groups;
  }

  public String getLastGeneratedId() {
    return lastGeneratedId;
  }

  public void setLastGeneratedId(String lastGeneratedId) {
    this.lastGeneratedId = lastGeneratedId;
  }

  public StreamEntry getFirstEntry() {
    return firstEntry;
  }

  public void setFirstEntry(StreamEntry firstEntry) {
    this.firstEntry = firstEntry;
  }

  public StreamEntry getLastEntry() {
    return lastEntry;
  }

  public void setLastEntry(StreamEntry lastEntry) {
    this.lastEntry = lastEntry;
  }
}