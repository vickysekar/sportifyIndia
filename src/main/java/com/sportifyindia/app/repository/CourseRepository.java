package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Course entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("select course from Course course where course.facility.id = :facilityId")
    List<Course> findByFacilityId(@Param("facilityId") Long facilityId);

    @Query(
        value = "select course from Course course where course.facility.id = :facilityId",
        countQuery = "select count(course) from Course course where course.facility.id = :facilityId"
    )
    Page<Course> findByFacilityId(@Param("facilityId") Long facilityId, Pageable pageable);

    @Query(
        value = "select distinct course from Course course left join fetch course.subscriptionPlans",
        countQuery = "select count(distinct course) from Course course"
    )
    Page<Course> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct course from Course course left join fetch course.subscriptionPlans")
    List<Course> findAllWithEagerRelationships();

    @Query("select course from Course course left join fetch course.subscriptionPlans where course.id =:id")
    Optional<Course> findOneWithEagerRelationships(@Param("id") Long id);
}
