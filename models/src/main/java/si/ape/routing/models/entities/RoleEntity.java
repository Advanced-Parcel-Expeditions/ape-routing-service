package si.ape.routing.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "role")
@NamedQueries(value =
        {
                @NamedQuery(name = "RoleEntity.getAll",
                        query = "SELECT r FROM RoleEntity r")
        })

public class RoleEntity {
    @Id
    private Integer id;

    @Column(name = "role_name")
    private String roleName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
