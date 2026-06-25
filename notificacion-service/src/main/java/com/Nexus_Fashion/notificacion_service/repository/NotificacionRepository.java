package com.Nexus_Fashion.notificacion_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Nexus_Fashion.notificacion_service.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long>{

}
