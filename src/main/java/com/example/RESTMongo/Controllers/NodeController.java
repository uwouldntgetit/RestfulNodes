package com.example.RESTMongo.Controllers;

import com.example.RESTMongo.DTO.NodeDTO;
import com.example.RESTMongo.Model.Node;
import com.example.RESTMongo.Service.NodeService;
import com.example.RESTMongo.Validation.OnCreate;
import com.example.RESTMongo.Validation.OnUpdate;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/nodes")
public class NodeController {
    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public List<NodeDTO> getAllNodes(){
        return nodeService.getAllNodes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeDTO> getNodeById(@PathVariable String id){
        Optional<NodeDTO> nodeById = nodeService.getNodeById(id);
        return nodeById.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    // here I implemented the functionality where the response to a POSTS request is all the nodes
    @PostMapping
    public List<NodeDTO> createNode(@Validated(OnCreate.class) @RequestBody NodeDTO n){
        nodeService.saveNode(n);
        return nodeService.getAllNodes();
    }

    // if i use @PathVariable the variable must be mapped as /{varName}
    @PatchMapping("/{id}")
    public ResponseEntity<List<NodeDTO>> updateNode(@PathVariable String id, @Validated(OnUpdate.class) @RequestBody NodeDTO node){
        return ResponseEntity.ok(nodeService.updateNode(id, node));
    }

}
