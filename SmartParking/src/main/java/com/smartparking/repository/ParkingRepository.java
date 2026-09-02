package com.smartparking.repository;

import com.smartparking.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {

    /**
     * Cerca parcheggi entro un raggio specifico (in metri) da un punto dato.
     * Usa la funzione PostGIS ST_DWithin con geometria geografica.
     *
     * @param lng   longitudine del punto centrale
     * @param lat   latitudine del punto centrale
     * @param radius raggio in metri
     */
    @Query(value = """
        SELECT p.* FROM parkings p
        WHERE ST_DWithin(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radius
        )
        ORDER BY ST_Distance(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """, nativeQuery = true)
    List<Parking> findNearby(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radius") double radius
    );

    /**
     * Cerca parcheggi entro un raggio con filtro per categoria.
     */
    @Query(value = """
        SELECT p.* FROM parkings p
        WHERE ST_DWithin(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radius
        )
        AND p.category = :category
        ORDER BY ST_Distance(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """, nativeQuery = true)
    List<Parking> findNearbyByCategory(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radius") double radius,
        @Param("category") String category
    );

    /**
     * Cerca parcheggi entro un raggio con filtro per disponibilita (posti > 0).
     */
    @Query(value = """
        SELECT p.* FROM parkings p
        WHERE ST_DWithin(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radius
        )
        AND p.available_spots > 0
        ORDER BY ST_Distance(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """, nativeQuery = true)
    List<Parking> findNearbyAvailable(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radius") double radius
    );

    /**
     * Cerca parcheggi con servizio EV entro un raggio.
     */
    @Query(value = """
        SELECT p.* FROM parkings p
        WHERE ST_DWithin(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radius
        )
        AND p.has_ev_charging = true
        ORDER BY ST_Distance(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """, nativeQuery = true)
    List<Parking> findNearbyEV(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radius") double radius
    );

    /**
     * Cerca parcheggi accessibili disabili entro un raggio.
     */
    @Query(value = """
        SELECT p.* FROM parkings p
        WHERE ST_DWithin(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radius
        )
        AND p.has_disabled_access = true
        ORDER BY ST_Distance(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """, nativeQuery = true)
    List<Parking> findNearbyDisabled(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radius") double radius
    );

    /**
     * Cerca parcheggi coperti/sotterranei entro un raggio.
     */
    @Query(value = """
        SELECT p.* FROM parkings p
        WHERE ST_DWithin(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radius
        )
        AND p.is_covered = true
        ORDER BY ST_Distance(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """, nativeQuery = true)
    List<Parking> findNearbyCovered(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radius") double radius
    );

    /**
     * Cerca parcheggi gratuiti entro un raggio.
     */
    @Query(value = """
        SELECT p.* FROM parkings p
        WHERE ST_DWithin(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radius
        )
        AND p.is_free = true
        ORDER BY ST_Distance(
            p.geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """, nativeQuery = true)
    List<Parking> findNearbyFree(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radius") double radius
    );

    /**
     * Cerca tutti i parcheggi (senza filtro geospaziale).
     */
    List<Parking> findAllByOrderByAvailableSpotsDesc();
}
