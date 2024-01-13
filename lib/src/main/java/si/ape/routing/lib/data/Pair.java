package si.ape.routing.lib.data;

/**
 * The Pair class represents a data-transfer object for a pair of objects.
 *
 * @param <T> the type of the first object
 * @param <V> the type of the second object
 */
public class Pair<T, V> {

    /**
     * The first object.
     */
    public final T first;

    /**
     * The second object.
     */
    public final V second;

    /**
     * Instantiates a new Pair.
     *
     * @param first  the first object.
     * @param second the second object.
     */
    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

}
