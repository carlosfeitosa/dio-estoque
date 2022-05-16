package com.dio.estoque.business.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class PedidoCompletoRequestDTO extends PedidoRequestDTO {

    private UUID idProcessoEstoque;

    public PedidoCompletoRequestDTO(PedidoRequestDTO pedido, UUID idProcessoEstoque) {

        this.setDataPedido(pedido.getDataPedido());
        this.setId(pedido.getId());
        this.setIdPagamento(pedido.getIdPagamento());
        this.setIdProcessoEstoque(idProcessoEstoque);
        this.setItens(pedido.getItens());
        this.setNomeCliente(pedido.getNomeCliente());
        this.setNumeroCartao(pedido.getNumeroCartao());
    }
}
