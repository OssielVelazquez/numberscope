package com.numberscope.phone;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PhoneQueryRepository extends JpaRepository<PhoneQuery, Long> {
    List<PhoneQuery> findTop100ByOrderByCreatedAtDesc();
    Optional<PhoneQuery> findFirstByE164OrderByCreatedAtDesc(String e164);
}