package si.ape.routing.models.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The StreetEntity class is a JPA entity class that represents the street table in the database. The street table
 * contains the basic information about the street, namely the street name, the street number, the city code, the city
 * name and the country code.
 */
@Entity
@Table(name = "street")
@NamedQueries(value =
        {
                @NamedQuery(name = "StreetEntity.getAll",
                        query = "SELECT s FROM StreetEntity s")
        })
@IdClass(StreetEntity.StreetId.class)
public class StreetEntity {

    /**
     * The street name.
     */
    @Id
    @Column(name = "street_name")
    private String streetName;

    /**
     * The street number.
     */
    @Id
    @Column(name = "street_number")
    private Integer streetNumber;

    /**
     * The CityEntity, referencing the `code`, `name` and `country_code` columns of the `city` table as a
     * primary foreign key.
     */
    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "code", referencedColumnName = "code"),
            @JoinColumn(name = "name", referencedColumnName = "name"),
            @JoinColumn(name = "country", referencedColumnName = "country_code")
    })
    private CityEntity city;

    /**
     * The StreetId class is a class that represents the composite primary key of the street table.
     */
    public static class StreetId implements Serializable {

        /**
         * The street name.
         */
        private String streetName;

        /**
         * The street number.
         */
        private Integer streetNumber;

        /**
         * The CityId class, representing the composite primary key of the city table.
         */
        private CityEntity.CityId city;

        /**
         * Instantiates a new Street composite primary key.
         *
         * @param streetName   The street name.
         * @param streetNumber The street number.
         * @param cityCode     The city code.
         * @param cityName     The city name.
         * @param countryCode  The country code.
         */
        public StreetId(String streetName, Integer streetNumber, String cityCode, String cityName, String countryCode) {
            this.streetName = streetName;
            this.streetNumber = streetNumber;
            this.city = new CityEntity.CityId(cityCode, cityName, countryCode);
        }

        /**
         * Gets the street name.
         *
         * @return the street name.
         */
        public String getStreetName() {
            return streetName;
        }

        /**
         * Sets the street name.
         *
         * @param streetName the street name.
         */
        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }

        /**
         * Gets the street number.
         *
         * @return the street number.
         */
        public Integer getStreetNumber() {
            return streetNumber;
        }

        /**
         * Sets the street number.
         *
         * @param streetNumber the street number.
         */
        public void setStreetNumber(Integer streetNumber) {
            this.streetNumber = streetNumber;
        }

        /**
         * Gets the city primary composite key.
         *
         * @return the city composite key.
         */
        public CityEntity.CityId getCity() {
            return city;
        }

        /**
         * Sets the city primary composite key.
         *
         * @param city the city composite key.
         */
        public void setCity(CityEntity.CityId city) {
            this.city = city;
        }

        /**
         * Gets the country code.
         *
         * @return the country code.
         */
        public String getCountryCode() {
            return city.getCountry();
        }

        /**
         * Sets the country code.
         *
         * @param countryCode the country code.
         */
        public void setCountryCode(String countryCode) {
            this.city.setCountry(countryCode);
        }

    }

    /**
     * Gets the street name.
     *
     * @return the street name.
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Sets the street name.
     *
     * @param streetName the street name.
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * Gets the street number.
     *
     * @return the street number.
     */
    public Integer getStreetNumber() {
        return streetNumber;
    }

    /**
     * Sets the street number.
     *
     * @param streetNumber the street number.
     */
    public void setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Gets the CityEntity.
     *
     * @return the CityEntity.
     */
    public CityEntity getCity() {
        return city;
    }

    /**
     * Sets the CityEntity.
     *
     * @param city the CityEntity.
     */
    public void setCity(CityEntity city) {
        this.city = city;
    }

}
