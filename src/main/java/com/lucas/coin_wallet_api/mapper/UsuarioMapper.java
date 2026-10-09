package com.lucas.coin_wallet_api.mapper;

import com.lucas.coin_wallet_api.dto.UsuarioRequest;
import com.lucas.coin_wallet_api.dto.UsuarioResponse;
import com.lucas.coin_wallet_api.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", source = "senha")
    @Mapping(target = "carteiras", ignore = true)
    Usuario toEntity(UsuarioRequest request);

    UsuarioResponse toResponse(Usuario usuario);
}