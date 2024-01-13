package si.ape.routing.lib;

/**
 * The Street DTO class. This class serves as a counterpart to the StreetEntity class, and should be used to transfer
 * data between the service and the client.
 */
public class Street {

    /**
     * The street name.
     */
    private String streetName;

    /**
     * The street number.
     */
    private Integer streetNumber;

    /**
     * The city.
     */
    private City city;

    /**
     * Gets the street name.
     *
     * @return the street name
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Sets the street name.
     *
     * @param streetName the street name
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * Gets the street number.
     *
     * @return the street number
     */
    public Integer getStreetNumber() {
        return streetNumber;
    }

    /**
     * Sets the street number.
     *
     * @param streetNumber the street number
     */
    public void setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Gets the city.
     *
     * @return the city
     */
    public City getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city the city
     */
    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Street) {
            Street street = (Street) obj;
            return street.getStreetName().equals(this.getStreetName()) &&
                    street.getStreetNumber().equals(this.getStreetNumber()) &&
                    street.getCity().equals(this.getCity());
        }
        return false;
    }

}
