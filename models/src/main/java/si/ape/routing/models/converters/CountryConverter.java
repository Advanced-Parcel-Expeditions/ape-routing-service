package si.ape.routing.models.converters;

import si.ape.routing.lib.Country;
import si.ape.routing.models.entities.CountryEntity;

/**
 * The CountryConverter class is used for conversion between the Country DTO and the CountryEntity JPA entity.
 */
public class CountryConverter {

    /**
     * Converts a CountryEntity to a Country DTO.
     *
     * @param entity The CountryEntity.
     * @return The Country DTO.
     */
    public static Country toDto(CountryEntity entity) {

        Country dto = new Country();
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());

        return dto;

    }

    /**
     * Converts a Country DTO to a CountryEntity.
     *
     * @param dto The Country DTO.
     * @return The CountryEntity.
     */
    public static CountryEntity toEntity(Country dto) {

        CountryEntity entity = new CountryEntity();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());

        return entity;

    }

}
