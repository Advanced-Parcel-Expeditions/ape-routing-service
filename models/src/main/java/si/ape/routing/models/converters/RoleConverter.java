package si.ape.routing.models.converters;

import si.ape.routing.lib.Role;
import si.ape.routing.models.entities.RoleEntity;

public class RoleConverter {

    public static Role toDto(RoleEntity entity) {

        Role dto = new Role();
        dto.setId(entity.getId());
        dto.setRoleName(entity.getRoleName());
        return dto;

    }

    public static RoleEntity toEntity(Role dto) {

        RoleEntity entity = new RoleEntity();
        entity.setId(dto.getId());
        entity.setRoleName(dto.getRoleName());
        return entity;

    }

}
