package si.ape.routing.models.converters;

import si.ape.routing.lib.Street;
import si.ape.routing.models.entities.StreetEntity;

/**
 * The StreetConverter class is used for conversion between the Street DTO and the StreetEntity JPA entity.
 */
public class StreetConverter {

    /**
     * Converts a StreetEntity to a Street DTO.
     *
     * @param entity The StreetEntity.
     * @return The Street DTO.
     */
    public static Street toDto(StreetEntity entity) {

        Street dto = new Street();
        dto.setStreetName(entity.getStreetName());
        dto.setStreetNumber(entity.getStreetNumber());
        dto.setCity(CityConverter.toDto(entity.getCity()));

        return dto;

    }

    /**
     * Converts a Street DTO to a StreetEntity.
     *
     * @param dto The Street DTO.
     * @return The StreetEntity.
     */
    public static StreetEntity toEntity(Street dto) {

        StreetEntity entity = new StreetEntity();
        entity.setStreetName(dto.getStreetName());
        entity.setStreetNumber(dto.getStreetNumber());
        entity.setCity(CityConverter.toEntity(dto.getCity()));

        return entity;

    }

}
