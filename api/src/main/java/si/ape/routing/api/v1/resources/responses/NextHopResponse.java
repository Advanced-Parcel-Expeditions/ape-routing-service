package si.ape.routing.api.v1.resources.responses;

import si.ape.routing.lib.Branch;
import si.ape.routing.lib.data.Reason;

/**
 * The NextHopResponse class represents a data-transfer object for the next hop response. It contains the
 * result of the next-hop request, which is the `branch` and additionally, the `reason` for the result. The `reason`
 * is used to determine the reason the hop was chosen or failed.
 */
public class NextHopResponse {

    /**
     * The branch.
     */
    private Branch branch;

    /**
     * The reason.
     */
    private Reason reason;

    /**
     * Instantiates a new NextHopResponse.
     *
     * @param branch the branch.
     * @param reason the reason.
     */
    public NextHopResponse(Branch branch, Reason reason) {
        this.branch = branch;
        this.reason = reason;
    }

    /**
     * Gets the branch.
     *
     * @return the branch.
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Sets the branch.
     *
     * @param branch the branch to set.
     */
    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * Gets the reason.
     *
     * @return the reason.
     */
    public Reason getReason() {
        return reason;
    }

    /**
     * Sets the reason.
     *
     * @param reason the reason to set.
     */
    public void setReason(Reason reason) {
        this.reason = reason;
    }

}
