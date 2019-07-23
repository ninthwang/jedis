package redis.clients.jedis;

import java.util.*;

import redis.clients.jedis.util.JedisByteHashMap;
import redis.clients.jedis.util.SafeEncoder;

public final class BuilderFactory {
  public static final Builder<Double> DOUBLE = new Builder<Double>() {
    @Override
    public Double build(Object data) {
      String string = STRING.build(data);
      if (string == null) return null;
      try {
        return Double.valueOf(string);
      } catch (NumberFormatException e) {
        if (string.equals("inf") || string.equals("+inf")) return Double.POSITIVE_INFINITY;
        if (string.equals("-inf")) return Double.NEGATIVE_INFINITY;
        throw e;
      }
    }

    @Override
    public String toString() {
      return "double";
    }
  };
  public static final Builder<Boolean> BOOLEAN = new Builder<Boolean>() {
    @Override
    public Boolean build(Object data) {
      return ((Long) data) == 1;
    }

    @Override
    public String toString() {
      return "boolean";
    }
  };
  public static final Builder<byte[]> BYTE_ARRAY = new Builder<byte[]>() {
    @Override
    public byte[] build(Object data) {
      return ((byte[]) data); // deleted == 1
    }

    @Override
    public String toString() {
      return "byte[]";
    }
  };

  public static final Builder<Long> LONG = new Builder<Long>() {
    @Override
    public Long build(Object data) {
      return (Long) data;
    }

    @Override
    public String toString() {
      return "long";
    }

  };
  public static final Builder<String> STRING = new Builder<String>() {
    @Override
    public String build(Object data) {
      return data == null ? null : SafeEncoder.encode((byte[]) data);
    }

    @Override
    public String toString() {
      return "string";
    }

  };
  public static final Builder<List<String>> STRING_LIST = new Builder<List<String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<String> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final ArrayList<String> result = new ArrayList<String>(l.size());
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(SafeEncoder.encode(barray));
        }
      }
      return result;
    }

    @Override
    public String toString() {
      return "List<String>";
    }

  };
  public static final Builder<Map<String, String>> STRING_MAP = new Builder<Map<String, String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> build(Object data) {
      final List<byte[]> flatHash = (List<byte[]>) data;
      final Map<String, String> hash = new HashMap<String, String>(flatHash.size()/2, 1);
      final Iterator<byte[]> iterator = flatHash.iterator();
      while (iterator.hasNext()) {
        hash.put(SafeEncoder.encode(iterator.next()), SafeEncoder.encode(iterator.next()));
      }

      return hash;
    }

    @Override
    public String toString() {
      return "Map<String, String>";
    }

  };

  public static final Builder<Map<String, String>> PUBSUB_NUMSUB_MAP = new Builder<Map<String, String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> build(Object data) {
      final List<Object> flatHash = (List<Object>) data;
      final Map<String, String> hash = new HashMap<String, String>(flatHash.size()/2, 1);
      final Iterator<Object> iterator = flatHash.iterator();
      while (iterator.hasNext()) {
        hash.put(SafeEncoder.encode((byte[]) iterator.next()),
          String.valueOf((Long) iterator.next()));
      }

      return hash;
    }

    @Override
    public String toString() {
      return "PUBSUB_NUMSUB_MAP<String, String>";
    }

  };

  public static final Builder<Set<String>> STRING_SET = new Builder<Set<String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<String> result = new HashSet<String>(l.size(), 1);
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(SafeEncoder.encode(barray));
        }
      }
      return result;
    }

    @Override
    public String toString() {
      return "Set<String>";
    }

  };

  public static final Builder<List<byte[]>> BYTE_ARRAY_LIST = new Builder<List<byte[]>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<byte[]> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;

      return l;
    }

    @Override
    public String toString() {
      return "List<byte[]>";
    }
  };

  public static final Builder<Set<byte[]>> BYTE_ARRAY_ZSET = new Builder<Set<byte[]>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<byte[]> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<byte[]> result = new LinkedHashSet<byte[]>(l);
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(barray);
        }
      }
      return result;
    }

    @Override
    public String toString() {
      return "ZSet<byte[]>";
    }
  };
  public static final Builder<Map<byte[], byte[]>> BYTE_ARRAY_MAP = new Builder<Map<byte[], byte[]>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Map<byte[], byte[]> build(Object data) {
      final List<byte[]> flatHash = (List<byte[]>) data;
      final Map<byte[], byte[]> hash = new JedisByteHashMap();
      final Iterator<byte[]> iterator = flatHash.iterator();
      while (iterator.hasNext()) {
        hash.put(iterator.next(), iterator.next());
      }

      return hash;
    }

    @Override
    public String toString() {
      return "Map<byte[], byte[]>";
    }

  };

  public static final Builder<Set<String>> STRING_ZSET = new Builder<Set<String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<String> result = new LinkedHashSet<String>(l.size(), 1);
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(SafeEncoder.encode(barray));
        }
      }
      return result;
    }

    @Override
    public String toString() {
      return "ZSet<String>";
    }

  };

  public static final Builder<Set<Tuple>> TUPLE_ZSET = new Builder<Set<Tuple>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<Tuple> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<Tuple> result = new LinkedHashSet<Tuple>(l.size()/2, 1);
      Iterator<byte[]> iterator = l.iterator();
      while (iterator.hasNext()) {
        result.add(new Tuple(iterator.next(), DOUBLE.build(iterator.next())));
      }
      return result;
    }

    @Override
    public String toString() {
      return "ZSet<Tuple>";
    }

  };

  public static final Builder<Object> EVAL_RESULT = new Builder<Object>() {

    @Override
    public Object build(Object data) {
      return evalResult(data);
    }

    @Override
    public String toString() {
      return "Eval<Object>";
    }

    private Object evalResult(Object result) {
      if (result instanceof byte[]) return SafeEncoder.encode((byte[]) result);

      if (result instanceof List<?>) {
        List<?> list = (List<?>) result;
        List<Object> listResult = new ArrayList<Object>(list.size());
        for (Object bin : list) {
          listResult.add(evalResult(bin));
        }

        return listResult;
      }

      return result;
    }

  };

  public static final Builder<Object> EVAL_BINARY_RESULT = new Builder<Object>() {

    @Override
    public Object build(Object data) {
      return evalResult(data);
    }

    @Override
    public String toString() {
      return "Eval<Object>";
    }

    private Object evalResult(Object result) {
      if (result instanceof List<?>) {
        List<?> list = (List<?>) result;
        List<Object> listResult = new ArrayList<Object>(list.size());
        for (Object bin : list) {
          listResult.add(evalResult(bin));
        }

        return listResult;
      }

      return result;
    }

  };

  public static final Builder<List<GeoCoordinate>> GEO_COORDINATE_LIST = new Builder<List<GeoCoordinate>>() {
    @Override
    public List<GeoCoordinate> build(Object data) {
      if (null == data) {
        return null;
      }
      return interpretGeoposResult((List<Object>) data);
    }

    @Override
    public String toString() {
      return "List<GeoCoordinate>";
    }

    private List<GeoCoordinate> interpretGeoposResult(List<Object> responses) {
      List<GeoCoordinate> responseCoordinate = new ArrayList<GeoCoordinate>(responses.size());
      for (Object response : responses) {
        if (response == null) {
          responseCoordinate.add(null);
        } else {
          List<Object> respList = (List<Object>) response;
          GeoCoordinate coord = new GeoCoordinate(DOUBLE.build(respList.get(0)),
              DOUBLE.build(respList.get(1)));
          responseCoordinate.add(coord);
        }
      }
      return responseCoordinate;
    }
  };

  public static final Builder<List<GeoRadiusResponse>> GEORADIUS_WITH_PARAMS_RESULT = new Builder<List<GeoRadiusResponse>>() {
    @Override
    public List<GeoRadiusResponse> build(Object data) {
      if (data == null) {
        return null;
      }

      List<Object> objectList = (List<Object>) data;

      List<GeoRadiusResponse> responses = new ArrayList<GeoRadiusResponse>(objectList.size());
      if (objectList.isEmpty()) {
        return responses;
      }

      if (objectList.get(0) instanceof List<?>) {
        // list of members with additional informations
        GeoRadiusResponse resp;
        for (Object obj : objectList) {
          List<Object> informations = (List<Object>) obj;

          resp = new GeoRadiusResponse((byte[]) informations.get(0));

          int size = informations.size();
          for (int idx = 1; idx < size; idx++) {
            Object info = informations.get(idx);
            if (info instanceof List<?>) {
              // coordinate
              List<Object> coord = (List<Object>) info;

              resp.setCoordinate(new GeoCoordinate(DOUBLE.build(coord.get(0)),
                  DOUBLE.build(coord.get(1))));
            } else {
              // distance
              resp.setDistance(DOUBLE.build(info));
            }
          }

          responses.add(resp);
        }
      } else {
        // list of members
        for (Object obj : objectList) {
          responses.add(new GeoRadiusResponse((byte[]) obj));
        }
      }

      return responses;
    }

    @Override
    public String toString() {
      return "GeoRadiusWithParamsResult";
    }
  };


  public static final Builder<List<Module>> MODULE_LIST = new Builder<List<Module>>() {
    @Override
    public List<Module> build(Object data) {
      if (data == null) {
        return null;
      }

      List<List<Object>> objectList = (List<List<Object>>) data;

      List<Module> responses = new ArrayList<Module>(objectList.size());
      if (objectList.isEmpty()) {
        return responses;
      }

      for (List<Object> moduleResp: objectList) {
        Module m = new Module(SafeEncoder.encode((byte[]) moduleResp.get(1)), ((Long) moduleResp.get(3)).intValue());
        responses.add(m);
      }

      return responses;
    }

    @Override
    public String toString() {
      return "List<Module>";
    }
  };

  public static final Builder<List<Long>> LONG_LIST = new Builder<List<Long>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<Long> build(Object data) {
      if (null == data) {
        return null;
      }
      return (List<Long>) data;
    }

    @Override
    public String toString() {
      return "List<Long>";
    }

  };

  public static final Builder<StreamEntryID> STREAM_ENTRY_ID = new Builder<StreamEntryID>() {
    @Override
    @SuppressWarnings("unchecked")
    public  StreamEntryID build(Object data) {
      if (null == data) {
        return null;
      }
      String id = SafeEncoder.encode((byte[])data);
      return new StreamEntryID(id);
    }

    @Override
    public String toString() {
      return "StreamEntryID";
    }
  };
  

  public static final Builder<List<StreamEntry>> STREAM_ENTRY_LIST = new Builder<List<StreamEntry>>() {
    @Override
    @SuppressWarnings("unchecked")
    public  List<StreamEntry> build(Object data) {
      if (null == data) {
        return null;
      }
      List<ArrayList<Object>> objectList = (List<ArrayList<Object>>) data;

      List<StreamEntry> responses = new ArrayList<>(objectList.size()/2);
      if (objectList.isEmpty()) {
        return responses;
      }

      for(ArrayList<Object> res : objectList) {
        String entryIdString = SafeEncoder.encode((byte[])res.get(0));
        StreamEntryID entryID = new StreamEntryID(entryIdString);
        List<byte[]> hash = (List<byte[]>)res.get(1);
        
        Iterator<byte[]> hashIterator = hash.iterator();
        Map<String, String> map = new HashMap<>(hash.size()/2);
        while(hashIterator.hasNext()) {
          map.put(SafeEncoder.encode((byte[])hashIterator.next()), SafeEncoder.encode((byte[])hashIterator.next()));
        }
        responses.add(new StreamEntry(entryID, map));
      }

      return responses;
    }

    @Override
    public String toString() {
      return "List<StreamEntry>";
    }
  };
  
  public static final Builder<List<StreamPendingEntry>> STREAM_PENDING_ENTRY_LIST = new Builder<List<StreamPendingEntry>>() {
    @Override
    @SuppressWarnings("unchecked")
    public  List<StreamPendingEntry> build(Object data) {
      if (null == data) {
        return null;
      }
      
      List<Object> streamsEntries = (List<Object>)data;
      List<StreamPendingEntry> result = new ArrayList<>(streamsEntries.size());
      for(Object streamObj : streamsEntries) {
        List<Object> stream = (List<Object>)streamObj;
        String id = SafeEncoder.encode((byte[])stream.get(0));
        String consumerName = SafeEncoder.encode((byte[])stream.get(1));
        long idleTime = BuilderFactory.LONG.build(stream.get(2));      
        long deliveredTimes = BuilderFactory.LONG.build(stream.get(3));
        result.add(new StreamPendingEntry(new StreamEntryID(id), consumerName, idleTime, deliveredTimes));
      }
      return result;
    }

    @Override
    public String toString() {
      return "List<StreamPendingEntry>";
    }
  };

  public static final Builder<StreamEntry> STREAM_ENTRY = new Builder<StreamEntry>() {
    @Override
    @SuppressWarnings("unchecked")
    public StreamEntry build(Object data) {
      if (null == data) {
        return null;
      }
      List<Object> streamsEntry = (List<Object>)data;
      String entryIdString = SafeEncoder.encode((byte[])streamsEntry.get(0));
      StreamEntryID entryID = new StreamEntryID(entryIdString);
      List<byte[]> hash = (List<byte[]>)streamsEntry.get(1);

      Iterator<byte[]> hashIterator = hash.iterator();
      Map<String, String> map = new HashMap<>(hash.size()/2);
      while(hashIterator.hasNext()) {
        map.put(SafeEncoder.encode((byte[])hashIterator.next()), SafeEncoder.encode((byte[])hashIterator.next()));
      }
      return new StreamEntry(entryID, map);
    }

    @Override
    public String toString(){
      return "StreamEntry";
    }
  };

  public static final Builder<StreamInfoOverview> STREAM_INFO_OVERVIEW = new Builder<StreamInfoOverview>() {
    @Override
    @SuppressWarnings("unchecked")
    public StreamInfoOverview build(Object data) {
      if (null == data) {
        return null;
      }
      List<Object> info = (List<Object>) data;
      StreamInfoOverview infoParams = new StreamInfoOverview();
      infoParams.setLength(LONG.build(info.get(1)));
      infoParams.setRadixTreeKeys(LONG.build(info.get(3)));
      infoParams.setRadixTreeNodes(LONG.build(info.get(5)));
      infoParams.setGroups(LONG.build(info.get(7)));
      infoParams.setLastGeneratedId(STRING.build(info.get(9)));
      infoParams.setFirstEntry(STREAM_ENTRY.build(info.get(11)));
      infoParams.setLastEntry(STREAM_ENTRY.build(info.get(13)));
      return infoParams;
    }

    @Override
    public String toString(){
      return "StreamInfoOverview";
    }
  };

  public static final Builder<StreamInfoGroup> STREAM_INFO_GROUP = new Builder<StreamInfoGroup>() {
    @Override
    @SuppressWarnings("unchecked")
    public StreamInfoGroup build(Object data) {
      if (null == data) {
        return null;
      }
      List<Object> list = (List<Object>) data;
      StreamInfoGroup groupInfo = new StreamInfoGroup();
      groupInfo.setName(STRING.build(list.get(1)));
      groupInfo.setConsumers(LONG.build(list.get(3)));
      groupInfo.setPending(LONG.build(list.get(5)));
      groupInfo.setLastDeliveredId(STREAM_ENTRY_ID.build(list.get(7)));
      return groupInfo;
    }

    @Override
    public String toString(){
      return "StreamInfoGroup";
    }
  };

  public static final Builder<List<StreamInfoGroup>> STREAM_INFO_GROUP_LIST = new Builder<List<StreamInfoGroup>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<StreamInfoGroup> build(Object data) {
      if (null == data) {
        return new ArrayList<>();
      }
      List<Object> list = (List<Object>) data;
      List<StreamInfoGroup> result = new ArrayList<>(list.size());
      for(Object group : list){
        result.add(BuilderFactory.STREAM_INFO_GROUP.build(group));
      }
      return result;
    }

    @Override
    public String toString(){
      return "List<StreamInfoGroup>";
    }
  };

  public static final Builder<StreamInfoConsumer> STREAM_INFO_CONSUMER = new Builder<StreamInfoConsumer>() {
    @Override
    @SuppressWarnings("unchecked")
    public StreamInfoConsumer build(Object data) {
      if (null == data) {
        return null;
      }
      List<Object> list = (List<Object>) data;
      StreamInfoConsumer consumerInfo = new StreamInfoConsumer();
      consumerInfo.setName(STRING.build(list.get(1)));
      consumerInfo.setPending(LONG.build(list.get(3)));
      if(list.size() > 4){
        consumerInfo.setIdle(LONG.build(list.get(5)));
      }
      return consumerInfo;
    }

    @Override
    public String toString(){
      return "StreamInfoConsumer";
    }
  };

  public static final Builder<List<StreamInfoConsumer>> STREAM_INFO_CONSUMER_LIST = new Builder<List<StreamInfoConsumer>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<StreamInfoConsumer> build(Object data) {
      if (null == data) {
        return new ArrayList<>();
      }
      List<Object> list = (List<Object>) data;
      List<StreamInfoConsumer> result = new ArrayList<>(list.size());
      for(Object consumer : list){
        result.add(BuilderFactory.STREAM_INFO_CONSUMER.build(consumer));
      }
      return result;
    }

    @Override
    public String toString(){
      return "List<StreamInfoConsumer>";
    }
  };

  public static final Builder<Object> OBJECT = new Builder<Object>() {
    @Override
    public Object build(Object data) {
      return data;
    }
    @Override
    public String toString() {
      return "Object";
    }
  };



  private BuilderFactory() {
    throw new InstantiationError( "Must not instantiate this class" );
  }

}
