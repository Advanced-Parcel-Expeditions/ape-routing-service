package si.ape.routing.models.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The CityEntity class is a JPA entity class that represents the city table in the database. The city table
 * contains the basic information about the city, namely the city code, the city name, the country code and its
 * geographical coordinates.
 */
@Entity
@Table(name = "city")
@NamedQueries(value =
        {
                @NamedQuery(name = "CityEntity.getAll",
                        query = "SELECT c FROM CityEntity c")
        })
@IdClass(CityEntity.CityId.class)
public class CityEntity {

    /**
     * The city code.
     */
    @Id
    private String code;

    /**
     * The city name.
     */
    @Id
    private String name;

    /**
     * The CountryEntity, referencing the `code` column of the `country` table as a primary foreign key.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "country_code", referencedColumnName = "code")
    private CountryEntity country;

    /**
     * The latitude of the city.
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * The longitude of the city.
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * The CityId class is a class that represents the composite primary key of the city table.
     */
    public static class CityId implements Serializable {

        /**
         * The city code.
         */
        private String code;

        /**
         * The city name.
         */
        private String name;

        /**
         * The country code.
         */
        private String country;

        /**
         * Instantiates a new City composite primary key.
         *
         * @param code    The city code.
         * @param name    The city name.
         * @param country The country code.
         */
        public CityId(String code, String name, String country ) {
            this.code = code;
            this.name = name;
            this.country = country;
        }

        /**
         * Gets the city code.
         *
         * @return The city code.
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the city code.
         *
         * @param code The city code.
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * Gets the city name.
         *
         * @return The city name.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the city name.
         *
         * @param name The city name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the country code.
         *
         * @return The country code.
         */
        public String getCountry() {
            return country;
        }

        /**
         * Sets the country code.
         *
         * @param country The country code.
         */
        public void setCountry(String country) {
            this.country = country;
        }

        /**
         * Checks if two CityId objects are equal. Two CityId objects are equal if their city codes, city names and
         * country codes are equal.
         *
         * @param o The object to compare to.
         * @return true if the objects are equal, false otherwise.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CityId)) return false;
            CityId cityId = (CityId) o;
            return getCode().equals(cityId.getCode()) &&
                    getName().equals(cityId.getName()) &&
                    getCountry().equals(cityId.getCountry());
        }

        /**
         * Returns the hash code of the CityId object.
         *
         * @return The hash code of the CityId object.
         */
        @Override
        public int hashCode() {
            return code.hashCode();
        }

    }

    /**
     * Gets the city code.
     *
     * @return The city code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the city code.
     *
     * @param code The city code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the city name.
     *
     * @return The city name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the city name.
     *
     * @param name The city name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the CountryEntity.
     *
     * @return The CountryEntity.
     */
    public CountryEntity getCountry() {
        return country;
    }

    /**
     * Sets the CountryEntity.
     *
     * @param country The CountryEntity.
     */
    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    /**
     * Gets the latitude of the city.
     *
     * @return The latitude of the city.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the city.
     *
     * @param latitude The latitude of the city.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of the city.
     *
     * @return The longitude of the city.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the city.
     *
     * @param longitude The longitude of the city.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Checks if two CityEntity objects are equal. Two CityEntity objects are equal if their city codes, city names and
     * country codes are equal.
     *
     * @param obj The object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CityEntity &&
                ((CityEntity) obj).code.equals(this.code) &&
                ((CityEntity) obj).name.equals(this.name) &&
                ((CityEntity) obj).country.equals(this.country);
    }

    /**
     * Returns the hash code of the CityEntity object.
     *
     * @return The hash code of the CityEntity object.
     */
    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
