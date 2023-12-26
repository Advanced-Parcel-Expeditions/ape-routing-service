package si.ape.routing.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "parcel")
@NamedQueries(value =
        {
                @NamedQuery(name = "ParcelEntity.getAll",
                        query = "SELECT p FROM ParcelEntity p"),
                @NamedQuery(name = "ParcelEntity.getAllByParameters",
                        query = "SELECT p FROM ParcelEntity p WHERE " +
                                "(:id IS NULL OR p.id = :id) AND " +
                                "(:senderId IS NULL OR p.sender.id = :senderId) AND " +
                                "(:recipientId IS NULL OR p.recipient.id = :recipientId) AND " +
                                "(:parcelStatusId IS NULL OR p.parcelStatus.id = :parcelStatusId)"),
        })
public class ParcelEntity {
    @Id
    private String id;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "depth")
    private Integer depth;

    @ManyToOne
    @JoinColumn(name = "parcel_status_id", referencedColumnName = "id")
    private ParcelStatusEntity parcelStatus;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private CustomerEntity sender;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "sender_street_name", referencedColumnName = "street_name"),
            @JoinColumn(name = "sender_street_number", referencedColumnName = "street_number"),
            @JoinColumn(name = "sender_city_code", referencedColumnName = "city_code"),
            @JoinColumn(name = "sender_city_name", referencedColumnName = "city_name"),
            @JoinColumn(name = "sender_country_code", referencedColumnName = "country_code")
    })
    private StreetEntity senderStreet;

    @OneToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private CustomerEntity recipient;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "recipient_street_name", referencedColumnName = "street_name"),
            @JoinColumn(name = "recipient_street_number", referencedColumnName = "street_number"),
            @JoinColumn(name = "recipient_city_code", referencedColumnName = "city_code"),
            @JoinColumn(name = "recipient_city_name", referencedColumnName = "city_name"),
            @JoinColumn(name = "recipient_country_code", referencedColumnName = "country_code")
    })
    private StreetEntity recipientStreet;

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

    public ParcelStatusEntity getParcelStatus() {
        return parcelStatus;
    }

    public void setParcelStatus(ParcelStatusEntity parcelStatus) {
        this.parcelStatus = parcelStatus;
    }

    public CustomerEntity getSender() {
        return sender;
    }

    public void setSender(CustomerEntity sender) {
        this.sender = sender;
    }

    public StreetEntity getSenderStreet() {
        return senderStreet;
    }

    public void setSenderStreet(StreetEntity senderStreet) {
        this.senderStreet = senderStreet;
    }

    public CustomerEntity getRecipient() {
        return recipient;
    }

    public void setRecipient(CustomerEntity recipient) {
        this.recipient = recipient;
    }

    public StreetEntity getRecipientStreet() {
        return recipientStreet;
    }

    public void setRecipientStreet(StreetEntity recipient_street) {
        this.recipientStreet = recipientStreet;
    }
}