package com.example.RESTMongo.Service;

import com.example.RESTMongo.DTO.NodeDTO;
import com.example.RESTMongo.Model.Node;
import com.example.RESTMongo.Repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements NodeService {
    private final NodeRepository nodeRepository;

    public NodeServiceImpl(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }


    @Override
    public List<NodeDTO> getAllNodes() {
        return nodeRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<NodeDTO> getNodeById(String id) {
        return nodeRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public NodeDTO saveNode(NodeDTO n) {
        Node node = convertToEntity(n);
        Node saveNode = nodeRepository.save(node);
        return convertToDTO(saveNode);
    }

    // this function changes the value of the node selected node and proportionally prpagates that change to all related nodes
    @Override
    public List<NodeDTO> updateNode(String id, NodeDTO nodeDTO) {
        Node node = nodeRepository.findById(id).orElseThrow();

        if (null != nodeDTO.parents()) {
            node.setParents(nodeDTO.parents());
            nodeRepository.save(node);
        }
        // Only the parents field was changed so there's no need for value propagation
        if (null == nodeDTO.value()){
            nodeRepository.save(node);
            return getAllNodes();
        }

        // I get the value for which all related node.value must be multiplied
        // check for 1
        double change;
        // I can't multiply related nodes by infinity so I make it that if a node was 0 and its value is changed to a
        // number > 0 then the proportion of change is 1, and the only updated value is the one of the node to update
        if(0 == node.getValue())
            change = 1;
        else
            change = nodeDTO.value() / node.getValue();


        ArrayList<Node> nodesToUpdate = findRelatedNodes(node.getId());

        for (Node n : nodesToUpdate){
            n.setValue(n.getValue() * change);
        }
        nodeRepository.saveAll(nodesToUpdate);

        // if I don't do this, when node's value is 0 it can't be changed cause the above loop sets the value to 0 * 0
        // while in reality the node's value might be changed
        if(0 != nodeDTO.value() && 1 == change){
            node.setValue(nodeDTO.value());
            nodeRepository.save(node);
        }

        return getAllNodes();
    }

    private NodeDTO convertToDTO(Node n){
        return new NodeDTO(n.getId(), n.getValue(), n.getParents());
    }
    private Node convertToEntity(NodeDTO n){
        return new Node(n.id(), n.value(), n.parents());
    }

    /*
    - find related nodes
        - make sure they aren't in a cycle
    - change the values of these nodes
     */
    private ArrayList<Node> findRelatedNodes(String startingNodeId) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        ArrayList<Node> nodesToUpdate = new ArrayList<>();

        queue.add(startingNodeId);

        while(!queue.isEmpty()){
            String currentId = queue.poll();

            if(visited.contains(currentId))
                continue;

            visited.add(currentId);
            Node currentNode = nodeRepository.findById(currentId).orElse(null);
            if(null == currentNode) continue;

            // I add all children of the current node to the queue
            List<Node> children = nodeRepository.findByParentsContaining(currentId);
            children.forEach(n -> queue.add(n.getId()));

            // Add all parents of the currentNode to the queue
            queue.addAll(currentNode.getParents());
        }
        // I populate nodesToUpdate with all the node objects of the visited set
        visited.forEach(n -> nodesToUpdate.add(nodeRepository.findById(n).orElse(null)));
        return nodesToUpdate;
    }

}
