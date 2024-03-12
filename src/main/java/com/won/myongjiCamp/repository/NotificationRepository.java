package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
