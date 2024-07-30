package br.com.server.controller;

import br.com.server.model.Cliente;
import br.com.server.model.Produto;
import br.com.server.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;


    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }


    @GetMapping
    public List<Cliente> getAllClientes() {
        return clienteService.getAllClientes();
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Cliente> getClienteByCodigo(@PathVariable Long codigo) {
        Cliente cliente = clienteService.getClienteById(codigo);
        if (cliente != null) {
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        System.out.println("Recebido: " + cliente);
        Cliente savedCliente = clienteService.createCliente(cliente);
        return new ResponseEntity<>(savedCliente, HttpStatus.CREATED);
    }


}
