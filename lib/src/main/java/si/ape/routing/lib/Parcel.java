package si.ape.routing.lib;

public class Parcel {

    private String id;

    private Double weight;

    private Integer width;

    private Integer height;

    private Integer depth;

    private ParcelStatus parcelStatus;

    private Customer sender;

    private Street senderStreet;

    private Customer recipient;

    private Street recipientStreet;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public ParcelStatus getParcelStatus() {
        return parcelStatus;
    }

    public void setParcelStatus(ParcelStatus parcelStatus) {
        this.parcelStatus = parcelStatus;
    }

    public Customer getSender() {
        return sender;
    }

    public void setSender(Customer sender) {
        this.sender = sender;
    }

    public Street getSenderStreet() {
        return senderStreet;
    }

    public void setSenderStreet(Street senderStreet) {
        this.senderStreet = senderStreet;
    }

    public Customer getRecipient() {
        return recipient;
    }

    public void setRecipient(Customer recipient) {
        this.recipient = recipient;
    }

    public Street getRecipientStreet() {
        return recipientStreet;
    }

    public void setRecipientStreet(Street recipientStreet) {
        this.recipientStreet = recipientStreet;
    }
}