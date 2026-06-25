package com.Nexus_Fashion.recomendaciones_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;

@Repository
public interface RecomendacionRepository extends JpaRepository<Recomendacion,Long> {
    List<Recomendacion> findByIdCliente(Long idCliente);

}
