package si.ape.routing.lib;

/**
 * The City DTO class. This class serves as a counterpart to the CityEntity class, and should be used to transfer data
 * between the service and the client.
 */
public class City {

    /**
     * The city code.
     */
    private String code;

    /**
     * The city name.
     */
    private String name;

    /**
     * The country.
     */
    private Country country;

    /**
     * The latitude of the city.
     */
    private Double latitude;

    /**
     * The longitude of the city.
     */
    private Double longitude;

    /**
     * Gets the city code.
     *
     * @return the city code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the city code.
     *
     * @param code the city code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the city name.
     *
     * @return the city name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the city name.
     *
     * @param name the city name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the latitude of the city.
     *
     * @return the latitude of the city
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the city.
     *
     * @param latitude the latitude of the city
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of the city.
     *
     * @return the longitude of the city
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the city.
     *
     * @param longitude the longitude of the city
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the country.
     *
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

}
