package com.dio.estoque.controller;

import java.util.UUID;

import com.dio.estoque.business.dto.ItensProcessadosResponseDTO;
import com.dio.estoque.business.dto.PedidoCompletoRequestDTO;
import com.dio.estoque.business.dto.PedidoRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@AllArgsConstructor
public final class EstoqueController {

    private static final int TEMPO_PROCESSAMENTO = 1000;

    private static final String DESPACHO_BASE_URL = "http://localhost:8184";
    private static final String DESPACHAR_PEDIDO_API = "/api/dio/v1/despacharPedido";

    @PostMapping("v1/processarEstoque")
    public ResponseEntity<ItensProcessadosResponseDTO> processarEstoque(
            final @RequestBody PedidoRequestDTO pedido) {

        UUID pagamentoId = processar(pedido);

        ItensProcessadosResponseDTO response = new ItensProcessadosResponseDTO(pagamentoId.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("v1/processarEstoqueCompleto")
    public ResponseEntity<ItensProcessadosResponseDTO> processarEstoqueCompleto(
            final @RequestBody PedidoRequestDTO pedido) {

        ResponseEntity<ItensProcessadosResponseDTO> response = processarEstoque(pedido);

        UUID idProcessoEstoque = UUID.fromString(response.getBody().getMessage());

        PedidoCompletoRequestDTO pedidoCompleto = new PedidoCompletoRequestDTO(pedido, idProcessoEstoque);

        ResponseEntity<ItensProcessadosResponseDTO> estoqueResponse = despacharPedido(pedido);

        if (estoqueResponse.getStatusCode() == HttpStatus.OK) {

            String mensagemEstoque = response.getBody().getMessage();
            String mensagemDespacho = estoqueResponse.getBody().getMessage();

            response.getBody().setMessage(mensagemEstoque.concat("|").concat(mensagemDespacho));
        }

        return response;
    }

    private ResponseEntity<ItensProcessadosResponseDTO> despacharPedido(PedidoRequestDTO pedido) {

        WebClient client = WebClient.create(DESPACHO_BASE_URL);

        Mono<ItensProcessadosResponseDTO> responsePagamento = client.post().uri(DESPACHAR_PEDIDO_API)
                .body(Mono.just(pedido), PedidoRequestDTO.class).retrieve()
                .bodyToMono(ItensProcessadosResponseDTO.class);

        ItensProcessadosResponseDTO response = responsePagamento.block();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private UUID processar(PedidoRequestDTO pedido) {

        UUID idProcessoEstoque = UUID.randomUUID();

        try {

            log.info("Processando estoque...");
            log.debug("Processo itens de estoque: {}", idProcessoEstoque);
            log.debug("Pedido completo: {}", pedido.toString());

            log.info("Estado 6: Aguardando verificação de estoque");

            Thread.sleep(TEMPO_PROCESSAMENTO);

            log.info("Estado 8: Ítens separados");

        } catch (InterruptedException e) {

            log.warn("Interrupted!", e);

            Thread.currentThread().interrupt();
        }

        return idProcessoEstoque;
    }
}
