package com.dio.estoque.servicebus;

import java.util.UUID;

import com.dio.estoque.business.dto.ItensProcessadosResponseDTO;
import com.dio.estoque.business.dto.PedidoCompletoRequestDTO;
import com.dio.estoque.business.dto.PedidoRequestDTO;
import com.dio.estoque.controller.EstoqueController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class QueueConsumer {

    private EstoqueController controller;
    private QueueSender queueSender;

    @RabbitListener(queues = { "${queue.name.estoque}" })
    public void receive(@Payload String payload) {

        ObjectMapper objectMapper = new ObjectMapper();

        PedidoRequestDTO pedido = new PedidoRequestDTO();

        try {

            pedido = objectMapper.readValue(payload, PedidoRequestDTO.class);

        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }

        ResponseEntity<ItensProcessadosResponseDTO> processoEstoque = controller.processarEstoque(pedido);

        UUID idProcessoEstoque = UUID.fromString(processoEstoque.getBody().getMessage());

        PedidoCompletoRequestDTO pedidoCompleto = new PedidoCompletoRequestDTO(pedido, idProcessoEstoque);

        queueSender.send(pedidoToJson(pedidoCompleto));
    }

    private String pedidoToJson(PedidoCompletoRequestDTO pedido) {

        ObjectMapper mapper = new ObjectMapper();

        try {

            return mapper.writeValueAsString(pedido);

        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }

        return null;
    }
}
