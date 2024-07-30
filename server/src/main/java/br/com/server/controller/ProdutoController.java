package br.com.server.controller;

import br.com.server.model.Produto;
import br.com.server.repository.ProdutoRepository;
import br.com.server.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Produto> getProdutoByCodigo(@PathVariable Long codigo) {
        Produto produto = produtoService.getProdutoById(codigo);
        if (produto != null) {
            return new ResponseEntity<>(produto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {
        Produto savedProduto = produtoService.createProduto(produto);
        return new ResponseEntity<>(savedProduto, HttpStatus.CREATED);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Long codigo, @RequestBody Produto produto) {
        Produto updatedProduto = produtoService.updateProduto(codigo, produto);
        if (updatedProduto != null) {
            return new ResponseEntity<>(updatedProduto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long codigo) {
        boolean isDeleted = produtoService.deleteProduto(codigo);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            System.err.println("Produto com código " + codigo + " não encontrado.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
