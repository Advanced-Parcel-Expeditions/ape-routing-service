package si.ape.routing.api.v1.resources.requests;

import si.ape.routing.lib.Street;

public class NextHopRequest {

    private Street source;

    private Street destination;

    public Street getSource() {
        return source;
    }

    public void setSource(Street source) {
        this.source = source;
    }

    public Street getDestination() {
        return destination;
    }

    public void setDestination(Street destination) {
        this.destination = destination;
    }

}
