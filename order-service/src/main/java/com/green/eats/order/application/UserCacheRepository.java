package com.green.eats.order.application;

import com.green.eats.order.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCacheRepository extends JpaRepository<UserCache, Long> {

}
