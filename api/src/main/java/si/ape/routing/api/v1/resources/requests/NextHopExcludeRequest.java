package si.ape.routing.api.v1.resources.requests;

import si.ape.routing.lib.Branch;
import si.ape.routing.lib.Street;

import java.util.List;

public class NextHopExcludeRequest {

    /**
     * The source street.
     */
    private Street source;

    /**
     * The destination street.
     */
    private Street destination;

    /**
     * The exclude branches.
     */
    private List<Branch> exclude;

    /**
     * Gets the source street.
     *
     * @return the source street
     */
    public Street getSource() {
        return source;
    }

    /**
     * Sets the source street.
     *
     * @param source the source street
     */
    public void setSource(Street source) {
        this.source = source;
    }

    /**
     * Gets the destination street.
     *
     * @return the destination street
     */
    public Street getDestination() {
        return destination;
    }

    /**
     * Sets the destination street.
     *
     * @param destination the destination street
     */
    public void setDestination(Street destination) {
        this.destination = destination;
    }

    /**
     * Gets the exclude branches.
     *
     * @return the exclude branches
     */
    public List<Branch> getExclude() {
        return exclude;
    }

    /**
     * Sets the exclude branches.
     *
     * @param exclude the exclude branches
     */
    public void setExclude(List<Branch> exclude) {
        this.exclude = exclude;
    }

}
