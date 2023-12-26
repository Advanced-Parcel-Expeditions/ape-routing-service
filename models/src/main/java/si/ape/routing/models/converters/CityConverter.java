package si.ape.routing.models.converters;

import si.ape.routing.lib.City;
import si.ape.routing.models.entities.CityEntity;

/**
 * The CityConverter class is used for conversion between the City DTO and the CityEntity JPA entity.
 */
public class CityConverter {

    /**
     * Converts a CityEntity to a City DTO.
     *
     * @param entity The CityEntity.
     * @return The City DTO.
     */
    public static City toDto(CityEntity entity) {

        City dto = new City();
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setCountry(CountryConverter.toDto(entity.getCountry()));

        return dto;

    }

    /**
     * Converts a City DTO to a CityEntity.
     *
     * @param dto The City DTO.
     * @return The CityEntity.
     */
    public static CityEntity toEntity(City dto) {

        CityEntity entity = new CityEntity();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setCountry(CountryConverter.toEntity(dto.getCountry()));

        return entity;

    }

}
