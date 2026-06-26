package com.Nexus_Fashion.cliente_service.dto;

import com.Nexus_Fashion.cliente_service.model.Rol;
import com.Nexus_Fashion.cliente_service.model.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long idCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String contrasena;

    @NotNull(message = "El ID del rol es obligatorio")
    private Long idRol;

    public Cliente toModel() {
        Rol rol = new Rol();
        rol.setIdRol(this.idRol);
        return new Cliente(idCliente, nombre, correo, contrasena, rol);
    }

    public static ClienteDTO fromModel(Cliente c) {
        if (c == null) return null;
        Long rolId = (c.getRol() != null) ? c.getRol().getIdRol() : null;
        return new ClienteDTO(c.getIdCliente(), c.getNombre(), c.getCorreo(), null, rolId);
    }
}