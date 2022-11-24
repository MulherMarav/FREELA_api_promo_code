package br.com.api.repository;

import br.com.api.dto.PromoCodeDTO;
import br.com.api.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface EventoPadrinhoRepository extends JpaRepository<EventoPadrinho, EventoPadrinhoID> {

    @Query(value = "SELECT COALESCE(SUM(ep.moeda), 0) + " +
        "  COALESCE((SELECT SUM(ea.moeda) FROM EventoAfiliado ea WHERE ea.eventoAfiliadoId.clienteAfiliado = :clientePadrinho), 0) + " +
        "  COALESCE((SELECT SUM(em.moeda) FROM EventoManual em WHERE em.eventoManualId.cliente = :clientePadrinho), 0)" +
        "  FROM PromoCode pc INNER JOIN EventoAfiliado ea ON pc = ea.eventoAfiliadoId.promoCode" +
        "  INNER JOIN EventoPadrinho ep ON ea = ep.eventoPadrinhoId.eventoAfiliado" +
        "  WHERE pc.promoCodeId.clientePadrinho = :clientePadrinho")
    Long consultaSaldoPadrinho(Cliente clientePadrinho);

    @Query(value = "SELECT NEW " +
        "  br.com.api.dto.PromoCodeDTO(pc.promoCode, pc.promoCodeId.produto.nome, COUNT(ep), COUNT(ea), SUM(ep.moeda))" +
        "  FROM PromoCode pc INNER JOIN EventoAfiliado ea ON pc = ea.eventoAfiliadoId.promoCode" +
        "  INNER JOIN EventoPadrinho ep ON ea = ep.eventoPadrinhoId.eventoAfiliado" +
        "  AND ea.dtCriacao BETWEEN :dataInicio AND :dataFim AND ep.dtCriacao BETWEEN :dataInicio AND :dataFim" +
        "  WHERE pc = :promoCode GROUP BY pc.promoCode, pc.promoCodeId.produto.nome")
    Optional<PromoCodeDTO> consultaEventosPromoCode(PromoCode promoCode, LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query(value = "SELECT NEW " +
        "  br.com.api.dto.PromoCodeDTO(ea.eventoAfiliadoId.promoCode.promoCode, ea.eventoAfiliadoId.promoCode.promoCodeId.produto.nome, " +
        "  COUNT(ep), COUNT(ea), SUM(ep.moeda)) FROM EventoAfiliado ea INNER JOIN EventoPadrinho ep " +
        "  ON ea.eventoAfiliadoId = ep.eventoPadrinhoId.eventoAfiliado" +
        "  WHERE ea.eventoAfiliadoId.promoCode = :promoCode" +
        "  GROUP BY ea.eventoAfiliadoId.promoCode.promoCode, ea.eventoAfiliadoId.promoCode.promoCodeId.produto.nome")
    Optional<PromoCodeDTO> consultaEventosPromoCode(PromoCode promoCode);

    Optional<EventoPadrinho> findByEventoPadrinhoIdUid(String uid);

    Optional<EventoPadrinho> findByEventoPadrinhoIdEventoAfiliadoAndIdReferencia(EventoAfiliado eventoAfiliado, String idReferencia);

    @Query(value = "SELECT COUNT(ep)" +
            "  FROM EventoPadrinho ep INNER JOIN PromoCode pc " +
            "  ON ep.eventoPadrinhoId.eventoAfiliado.eventoAfiliadoId.promoCode = pc" +
            "  WHERE pc = :promoCode")
    Long consultaQtdAplicacoesPromoCode(PromoCode promoCode);

}
