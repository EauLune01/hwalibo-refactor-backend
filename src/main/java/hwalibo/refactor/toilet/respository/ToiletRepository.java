package hwalibo.refactor.toilet.respository;

import hwalibo.refactor.toilet.domain.Toilet;
import hwalibo.refactor.toilet.dto.result.StationNearestResult;
import hwalibo.refactor.toilet.dto.result.StationSearchResult;
import hwalibo.refactor.toilet.dto.result.StationSuggestResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ToiletRepository extends JpaRepository<Toilet, Long> {

    @Query("SELECT MIN(t.id) as id, t.name as name, t.line as line " +
            "FROM Toilet t " +
            "WHERE t.name LIKE :keyword% " +
            "GROUP BY t.name, t.line " +
            "ORDER BY t.name ASC")
    List<StationSuggestResult> findSuggestStations(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t.id as id, t.name as name, t.line as line, " +
            "t.gender as gender, t.star as star, t.numReview as numReview " +
            "FROM Toilet t " +
            "WHERE t.name = :keyword " +
            "ORDER BY t.line ASC")
    List<StationSearchResult> findToiletsByExactStationName(@Param("keyword") String keyword);

    @Query(value = """
    SELECT r.id, r.name, r.line, r.distance
    FROM (
        SELECT 
            t.toilet_id AS id, 
            t.name AS name, 
            t.line AS line,
            (6371e3 * acos(
                cos(radians(:lat)) * cos(radians(t.latitude)) *
                cos(radians(t.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(t.latitude))
            )) AS distance,
            ROW_NUMBER() OVER (
                PARTITION BY t.name, t.line 
                ORDER BY (6371e3 * acos(
                    cos(radians(:lat)) * cos(radians(t.latitude)) *
                    cos(radians(t.longitude) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(t.latitude))
                ))
            ) AS rn
        FROM toilets t
    ) r
    WHERE r.rn = 1
    ORDER BY r.distance
    LIMIT 3
    """, nativeQuery = true)
    List<StationNearestResult> findNearestStations(@Param("lat") double lat, @Param("lng") double lng);
}
