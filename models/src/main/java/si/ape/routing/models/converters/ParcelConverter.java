package si.ape.routing.models.converters;

import si.ape.routing.lib.Parcel;
import si.ape.routing.models.entities.ParcelEntity;

public class ParcelConverter {

    public static Parcel toDto(ParcelEntity entity) {

        Parcel dto = new Parcel();
        dto.setId(entity.getId());
        dto.setWeight(entity.getWeight());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setDepth(entity.getDepth());
        dto.setParcelStatus(ParcelStatusConverter.toDto(entity.getParcelStatus()));
        dto.setSender(CustomerConverter.toDto(entity.getSender()));
        dto.setSenderStreet(StreetConverter.toDto(entity.getSenderStreet()));
        dto.setRecipient(CustomerConverter.toDto(entity.getRecipient()));
        dto.setRecipientStreet(StreetConverter.toDto(entity.getRecipientStreet()));

        return dto;

    }

    public static ParcelEntity toEntity(Parcel dto) {

        ParcelEntity entity = new ParcelEntity();
        entity.setId(dto.getId());
        entity.setWeight(dto.getWeight());
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setDepth(dto.getDepth());
        entity.setParcelStatus(ParcelStatusConverter.toEntity(dto.getParcelStatus()));
        entity.setSender(CustomerConverter.toEntity(dto.getSender()));
        entity.setSenderStreet(StreetConverter.toEntity(dto.getSenderStreet()));
        entity.setRecipient(CustomerConverter.toEntity(dto.getRecipient()));
        entity.setRecipientStreet(StreetConverter.toEntity(dto.getRecipientStreet()));

        return entity;

    }

}
