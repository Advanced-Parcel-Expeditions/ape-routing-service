package si.ape.routing.lib;

/**
 * The Country DTO class. This class serves as a counterpart to the CountryEntity class, and should be used to transfer
 * data between the service and the client.
 */
public class Country {

    /**
     * The country code.
     */
    private String code;

    /**
     * The country name.
     */
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Country)) return false;
        Country other = (Country) obj;
        return this.code.equals(other.code);
    }

}
