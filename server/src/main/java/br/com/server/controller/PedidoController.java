package br.com.server.controller;

import br.com.server.model.Cliente;
import br.com.server.model.Pedido;
import br.com.server.model.Produto;
import br.com.server.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Pedido pedido = pedidoService.getPedidoById(id);
        return pedido != null ? new ResponseEntity<>(pedido, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> getPedidosAgrupadosPorCliente() {
        List<Map<String, Object>> pedidosAgrupados = pedidoService.getPedidosAgrupadosPorCliente();
        return new ResponseEntity<>(pedidosAgrupados, HttpStatus.OK);
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<Map<String, Object>>> getPedidosAgrupadosPorProduto() {
        List<Map<String, Object>> pedidosAgrupados = pedidoService.getPedidosAgrupadosPorProduto();
        return new ResponseEntity<>(pedidosAgrupados, HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<String> createPedido(@RequestBody Pedido pedido) {
        try {
            pedidoService.createPedido(pedido);
            return new ResponseEntity<>("Pedido criado com sucesso.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id, @RequestBody Pedido pedidoDetails) {
        Pedido updatedPedido = pedidoService.updatePedido(id, pedidoDetails);
        return updatedPedido != null ? new ResponseEntity<>(updatedPedido, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        return pedidoService.deletePedido(id) ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
