package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.CourseSubscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseSubscription entity.
 */
@Repository
public interface CourseSubscriptionRepository extends JpaRepository<CourseSubscription, Long> {
    @Query(
        "select courseSubscription from CourseSubscription courseSubscription where courseSubscription.user.login = ?#{principal.username}"
    )
    List<CourseSubscription> findByUserIsCurrentUser();

    @Query(
        "select courseSubscription from CourseSubscription courseSubscription where courseSubscription.user.login = ?#{principal.username}"
    )
    Page<CourseSubscription> findByUserIsCurrentUser(Pageable pageable);

    @Query(
        "select courseSubscription from CourseSubscription courseSubscription where courseSubscription.course.id = ?1 and courseSubscription.user.login = ?#{principal.username}"
    )
    Optional<CourseSubscription> findOneByCourseAndUserIsCurrentUser(Long courseId);
}
