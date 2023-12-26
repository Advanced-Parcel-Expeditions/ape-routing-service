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
