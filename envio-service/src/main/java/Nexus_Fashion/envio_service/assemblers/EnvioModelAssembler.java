package Nexus_Fashion.envio_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import Nexus_Fashion.envio_service.controller.EnvioController;
import Nexus_Fashion.envio_service.dto.EnvioDTO;
import Nexus_Fashion.envio_service.model.Envio;

@Component
public class EnvioModelAssembler implements RepresentationModelAssembler<Envio, EntityModel<EnvioDTO>> {
    @Override
    public EntityModel<EnvioDTO> toModel(Envio envio) {
        EnvioDTO dto = EnvioDTO.fromModel(envio);
        EntityModel<EnvioDTO> model = EntityModel.of(dto);
        
        model.add(linkTo(methodOn(EnvioController.class).listarTodosLosEnvios()).withRel("todos-los-envios"));
        model.add(linkTo(methodOn(EnvioController.class).obtenerEnvioPorId(dto.getIdCompra())).withRel("verificar-existencia"));
        return model;
}
}
