package si.ape.routing.models.entities;

import javax.persistence.*;

/**
 * The CountryEntity class is a JPA entity class that represents the country table in the database. The country table
 * contains only the basic information about the country, namely the country code and the country name.
 */
@Entity
@Table(name = "country")
@NamedQueries(value =
        {
                @NamedQuery(name = "CountryEntity.getAll",
                        query = "SELECT c FROM CountryEntity c")
        })
public class CountryEntity {

    /**
     * The country code.
     */
    @Id
    private String code;

    /**
     * The country name.
     */
    @Column(name = "name")
    private String name;

    /**
     * Gets the country code.
     *
     * @return the country code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the country code.
     *
     * @param code the country code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the country name.
     *
     * @return the country name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the country name.
     *
     * @param name the country name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks if two country entities are equal. Two country entities are equal if their country codes are equal.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CountryEntity && ((CountryEntity) obj).code.equals(this.code);
    }

    /**
     * Gets the hash code of the country entity.
     *
     * @return the hash code of the country entity
     */
    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
