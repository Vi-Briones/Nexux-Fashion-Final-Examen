package Nexus_Fashion.envio_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Nexus_Fashion.envio_service.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long>{

}
