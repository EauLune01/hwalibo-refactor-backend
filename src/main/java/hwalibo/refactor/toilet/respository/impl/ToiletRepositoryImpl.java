package hwalibo.refactor.toilet.respository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hwalibo.refactor.toilet.dto.query.QStationSearchResult;
import hwalibo.refactor.toilet.dto.query.QStationSuggestResult;
import hwalibo.refactor.toilet.dto.query.StationSearchResult;
import hwalibo.refactor.toilet.dto.query.StationSuggestResult;
import hwalibo.refactor.toilet.dto.result.StationNearestResult;
import hwalibo.refactor.toilet.respository.custom.ToiletRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;


import java.util.List;

import static hwalibo.refactor.toilet.domain.QToilet.toilet;

@RequiredArgsConstructor
public class ToiletRepositoryImpl implements ToiletRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<StationSuggestResult> findSuggestStations(String keyword, Pageable pageable) {
        return queryFactory
                .select(new QStationSuggestResult(
                        toilet.id.min(),
                        toilet.name,
                        toilet.line))
                .from(toilet)
                .where(toilet.name.startsWith(keyword))
                .groupBy(toilet.name, toilet.line)
                .orderBy(toilet.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<StationSearchResult> findToiletsByExactStationName(String keyword) {
        return queryFactory
                .select(new QStationSearchResult(
                        toilet.id,
                        toilet.name,
                        toilet.line,
                        toilet.gender.stringValue(),
                        toilet.star,
                        toilet.numReview))
                .from(toilet)
                .where(toilet.name.eq(keyword))
                .orderBy(toilet.line.asc())
                .fetch();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StationNearestResult> findNearestStations(double lat, double lng) {
        String sql = """
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
        """;

        return em.createNativeQuery(sql)
                .setParameter("lat", lat)
                .setParameter("lng", lng)
                .unwrap(org.hibernate.query.NativeQuery.class)
                .setTupleTransformer((tuple, aliases) -> new StationNearestResult(
                        ((Number) tuple[0]).longValue(),
                        (String) tuple[1],
                        (String) tuple[2]
                ))
                .getResultList();
    }
}
