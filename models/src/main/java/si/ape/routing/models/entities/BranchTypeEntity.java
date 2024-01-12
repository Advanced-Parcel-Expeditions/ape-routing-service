package si.ape.routing.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "branch_type")
@NamedQueries(value =
        {
            @NamedQuery(name = "BranchTypeEntity.getAll",
                query = "SELECT bt FROM BranchTypeEntity bt")
        })
public class BranchTypeEntity {

    @Id
    private Integer id;

    private String name;

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

}
