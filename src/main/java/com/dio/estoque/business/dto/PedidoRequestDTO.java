package com.dio.estoque.business.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoRequestDTO {

    private UUID id;

    private UUID idPagamento;

    private String nomeCliente;

    private String numeroCartao;

    private Date dataPedido;

    private List<String> itens;
}
