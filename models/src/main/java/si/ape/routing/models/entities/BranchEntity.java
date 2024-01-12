package si.ape.routing.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "branch")
@NamedQueries(value =
        {
            @NamedQuery(name = "BranchEntity.getAll",
                query = "SELECT b FROM BranchEntity b")
        })
public class BranchEntity {

    @Id
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "branch_type_id", referencedColumnName = "id")
    private BranchTypeEntity branchType;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "street_name", referencedColumnName = "street_name"),
            @JoinColumn(name = "street_number", referencedColumnName = "street_number"),
            @JoinColumn(name = "city_code", referencedColumnName = "city_code"),
            @JoinColumn(name = "city_name", referencedColumnName = "city_name"),
            @JoinColumn(name = "country_code", referencedColumnName = "country_code")
    })
    private StreetEntity street;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BranchTypeEntity getBranchType() {
        return branchType;
    }

    public void setBranchType(BranchTypeEntity branchType) {
        this.branchType = branchType;
    }

    public StreetEntity getStreet() {
        return street;
    }

    public void setStreet(StreetEntity street) {
        this.street = street;
    }

}
