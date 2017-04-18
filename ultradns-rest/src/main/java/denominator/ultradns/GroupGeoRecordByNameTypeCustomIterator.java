package denominator.ultradns;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import denominator.common.PeekingIterator;
import denominator.model.ResourceRecordSet;
import denominator.model.ResourceRecordSet.Builder;
import denominator.model.profile.Geo;
import denominator.ultradns.model.DirectionalRecord;
import denominator.ultradns.util.RRSetUtil;

import static denominator.common.Util.peekingIterator;
import static denominator.common.Util.toMap;

/**
 * Generally, this iterator will produce {@link ResourceRecordSet} for only a single record type.
 * However, there are special cases where this can produce multiple. For example, {@link
 * DirectionalPool.RecordType#IPV4} and {@link DirectionalPool.RecordType#IPV6} emit both address
 * ({@code A} or {@code AAAA}) and {@code CNAME} records.
 */
public final class GroupGeoRecordByNameTypeCustomIterator implements Iterator<ResourceRecordSet<?>> {

  private final Map<String, Geo> cache = new LinkedHashMap<String, Geo>();
  private final PeekingIterator<DirectionalRecord> peekingIterator;
  private final String zoneName;
  private final UltraDNSRestGeoSupport ultraDNSRestGeoSupport;

  private GroupGeoRecordByNameTypeCustomIterator(UltraDNSRestGeoSupport ultraDNSRestGeoSupport,
                                                 Iterator<DirectionalRecord> sortedIterator,
                                                 String zoneName) {
    this.ultraDNSRestGeoSupport = ultraDNSRestGeoSupport;
    this.peekingIterator = peekingIterator(sortedIterator);
    this.zoneName = zoneName;
  }

  static boolean typeTTLAndGeoGroupEquals(DirectionalRecord actual, DirectionalRecord expected) {
    return actual.getType().equals(expected.getType())
            && actual.getTtl() == expected.getTtl()
            && actual.getGeoGroupName().equals(expected.getGeoGroupName())
            && actual.getName().equals(expected.getName());
  }

  /**
   * skips no response records as they aren't portable.
   */
  @Override
  public boolean hasNext() {
    if (!peekingIterator.hasNext()) {
      return false;
    }
    DirectionalRecord record;
    while (true) {
      if (peekingIterator.hasNext()) {
        record = peekingIterator.peek();
        if (record.isNoResponseRecord()) {
          peekingIterator.next();
        } else {
          return true;
        }
      } else {
        return false;
      }
    }
  }

  @Override
  public ResourceRecordSet<?> next() {
    DirectionalRecord record = peekingIterator.next();

    Builder<Map<String, Object>>
        builder =
        ResourceRecordSet.builder().name(record.getName()).type(record.getType())
            .qualifier(record.getGeoGroupName()).ttl(record.getTtl());

    builder.add(toMap(record.getType(), record.getRdata()));

    final String key = record.getName() + "||" + record.getType() + "||" + record.getGeoGroupName();
    if (!cache.containsKey(key)) {
      Geo profile = Geo.create(ultraDNSRestGeoSupport.getDirectionalDNSGroupByName(zoneName, record.getName(),
              RRSetUtil.directionalRecordType(record.getType()), record.getGeoGroupName()).getRegionToTerritories());
      cache.put(key, profile);
    }

    builder.geo(cache.get(key));
    while (hasNext()) {
      DirectionalRecord next = peekingIterator.peek();
      if (typeTTLAndGeoGroupEquals(next, record)) {
        peekingIterator.next();
        builder.add(toMap(record.getType(), next.getRdata()));
      } else {
        break;
      }
    }
    return builder.build();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  static final class Factory {

    private final UltraDNSRest api;

    @Inject
    Factory(UltraDNSRest api) {
      this.api = api;
    }

    /**
     * Construct a custom iterator from directional record iterator & zone name.
     *
     * @param sortedIterator only contains records with the same {@link DirectionalRecord#name()},
     *                       sorted by {@link DirectionalRecord#type()}, {@link
     *                       DirectionalRecord#getGeolocationGroup()} or {@link
     *                       DirectionalRecord#group()}
     */
    Iterator<ResourceRecordSet<?>> create(Iterator<DirectionalRecord> sortedIterator, String name) {
      return new GroupGeoRecordByNameTypeCustomIterator(new UltraDNSRestGeoSupport(api), sortedIterator, name);
    }
  }
}
