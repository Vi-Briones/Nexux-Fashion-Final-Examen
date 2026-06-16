package com.Nexus_Fashion.cliente_service.dto;


import com.Nexus_Fashion.cliente_service.model.Rol;
import com.Nexus_Fashion.cliente_service.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long idCliente;
    private String nombre;
    private String correo;
    private String contrasena; 
    
    // Solo pedimos el ID del Rol en Postman
    private Long idRol; 

    public Cliente toModel() {
        // Creamos el Rol "falso" solo con el ID para que JPA haga la relación mágica
        Rol rol = new Rol();
        rol.setIdRol(this.idRol);

        return new Cliente(idCliente, nombre, correo, contrasena, rol);
    }

    public static ClienteDTO fromModel(Cliente c) {
        if (c == null) return null;
        
        // Extraemos el ID del Rol para devolverlo y ocultamos la contraseña (null)
        Long rolId = (c.getRol() != null) ? c.getRol().getIdRol() : null;

        return new ClienteDTO(c.getIdCliente(), c.getNombre(), c.getCorreo(), null, rolId);
    }
}