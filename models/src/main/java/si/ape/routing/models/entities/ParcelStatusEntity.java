package si.ape.routing.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "parcel_status")
@NamedQueries(value =
        {
                @NamedQuery(name = "ParcelStatusEntity.getAll",
                        query = "SELECT p FROM ParcelStatusEntity p")
        })
public class ParcelStatusEntity {
        @Id
        private Integer id;

        @Column(name = "name")
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
