package com.Nexus_Fashion.resena_service.repository;

import com.Nexus_Fashion.resena_service.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long>{

    List<Resena> findByIdCompra(Long idCompra);
}
