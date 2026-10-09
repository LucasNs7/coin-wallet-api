package com.lucas.coin_wallet_api.mapper;

import com.lucas.coin_wallet_api.dto.CarteiraRequest;
import com.lucas.coin_wallet_api.dto.CarteiraResponse;
import com.lucas.coin_wallet_api.model.Carteira;
import com.lucas.coin_wallet_api.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarteiraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "moeda", source = "request.moeda")
    @Mapping(target = "quantidade", source = "request.quantidade")
    Carteira toEntity(CarteiraRequest request, Usuario usuario);

    CarteiraResponse toResponse(Carteira carteira);
}