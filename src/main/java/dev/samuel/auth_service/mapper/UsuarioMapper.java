package dev.samuel.auth_service.mapper;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.request.UsuarioRequest;
import dev.samuel.auth_service.response.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "scopes", ignore = true)
    Usuario toEntity(UsuarioRequest request);

    @Mapping(target = "scopes", source = "scopes", qualifiedByName = "mapScopeEntitiesToStringScopes")
    UsuarioResponse toUsuarioResponse(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "scopes", ignore = true)
    void atualizarUsuario(UsuarioRequest request, @MappingTarget Usuario usuario);

    @Named("mapScopeEntitiesToStringScopes")
    default List<String> mapScopeEntitiesToStringScopes(List<Scope> scopes) {
        if (scopes == null) return List.of();

        return scopes.stream()
                .map(Scope::getNome)
                .toList();
    }


}
