package com.example.RESTMongo.Service;


import com.example.RESTMongo.DTO.NodeDTO;
import com.example.RESTMongo.Model.Node;

import java.util.List;
import java.util.Optional;

public interface NodeService {
    List<NodeDTO> getAllNodes();
    Optional<NodeDTO> getNodeById(String id);
    NodeDTO saveNode(NodeDTO n);
    List<NodeDTO> updateNode(String id, NodeDTO n);
}
