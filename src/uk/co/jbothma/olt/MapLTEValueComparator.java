package uk.co.jbothma.olt;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator that does <= comparison on values of HashMap<Object, Integer> elements.
 * 
 */
public class MapLTEValueComparator implements Comparator<Object> {
	Map<Object, Integer> base;

	public MapLTEValueComparator(Map<Object, Integer> base) {
		this.base = base;
	}

	public int compare(Object a, Object b) {
		if ((Integer)base.get(a) <= (Integer)base.get(b)) {
			return 1;
		} else {
			return -1;
		}
	}
}