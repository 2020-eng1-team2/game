package marlin.auber.common;

import java.util.Collection;

public class Helpers {

    /**
     * Pick a random element from a collection.
     *
     * From https://stackoverflow.com/a/21092353
     * @param coll a collection
     * @param <T> the type of elements
     * @return a randomly picked element
     */
    public static <T> T randomCollectionElement(Collection<T> coll) {
        int num = (int) (Math.random() * coll.size());
        for(T t: coll) if (--num < 0) return t;
        throw new AssertionError();
    }
}
