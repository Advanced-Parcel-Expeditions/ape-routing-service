package si.ape.routing.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


import si.ape.routing.lib.Parcel;
import si.ape.routing.models.entities.ParcelEntity;
import si.ape.routing.models.converters.ParcelConverter;


@RequestScoped
public class ParcelBean {

    private Logger log = Logger.getLogger(ParcelBean.class.getName());

    @Inject
    private EntityManager em;

    public List<Parcel> getParcels() {

        TypedQuery<ParcelEntity> query = em.createNamedQuery(
                "ParcelEntity.getAll", ParcelEntity.class);

        List<ParcelEntity> resultList = query.getResultList();

        return resultList.stream().map(ParcelConverter::toDto).collect(Collectors.toList());

    }

    public List<Parcel> getParcelsByParameters(String id, Integer senderId, Integer recipientId, Integer parcelStatusId) {

        TypedQuery<ParcelEntity> query = em.createNamedQuery(
                "ParcelEntity.getAllByParameters", ParcelEntity.class);

        query.setParameter("id", id);
        query.setParameter("senderId", senderId);
        query.setParameter("recipientId", recipientId);
        query.setParameter("parcelStatusId", parcelStatusId);

        List<ParcelEntity> resultList = query.getResultList();

        return resultList.stream().map(ParcelConverter::toDto).collect(Collectors.toList());
    }

    public Parcel createParcel(Parcel parcel) {

        ParcelEntity parcelEntity = ParcelConverter.toEntity(parcel);

        try {
            beginTx();
            em.persist(parcelEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (parcelEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ParcelConverter.toDto(parcelEntity);
    }

    public Parcel putParcel(String id, Parcel parcel) {

        ParcelEntity c = em.find(ParcelEntity.class, id);

        if (c == null) {
            return null;
        }

        ParcelEntity updatedParcelEntity = ParcelConverter.toEntity(parcel);

        try {
            beginTx();
            updatedParcelEntity.setId(c.getId());
            updatedParcelEntity = em.merge(updatedParcelEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return ParcelConverter.toDto(updatedParcelEntity);
    }

    public boolean deleteParcel(Integer id) {

        ParcelEntity parcel = em.find(ParcelEntity.class, id);

        if (parcel != null) {
            try {
                beginTx();
                em.remove(parcel);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

}
