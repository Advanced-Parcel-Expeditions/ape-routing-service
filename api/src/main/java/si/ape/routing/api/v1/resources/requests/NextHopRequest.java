package si.ape.routing.api.v1.resources.requests;

import si.ape.routing.lib.Street;

/**
 * The NextHopRequest class represents a data-transfer object for the next hop request. It acts as a wrapper for the
 * source and destination streets.
 */
public class NextHopRequest {

    /**
     * The source street.
     */
    private Street source;

    /**
     * The destination street.
     */
    private Street destination;

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

}
