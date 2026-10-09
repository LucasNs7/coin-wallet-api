package com.lucas.coin_wallet_api.repository;

import com.lucas.coin_wallet_api.model.Carteira;
import com.lucas.coin_wallet_api.model.Usuario;
import com.lucas.coin_wallet_api.model.enums.Moeda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, Long> {

    Optional<Carteira> findByUsuarioAndMoeda(Usuario usuario, Moeda moeda);

    List<Carteira> findAllByUsuario(Usuario usuario);
}
