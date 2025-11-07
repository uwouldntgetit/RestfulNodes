package com.example.RESTMongo.Repository;

import com.example.RESTMongo.Model.Node;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/* @Repository marks this interface as a Spring data repository
* MongoRepository provides CRUD methods to interact with DB without writing custom queries
*/
@Repository
public interface NodeRepository extends MongoRepository<Node, String> {
    // this method finds all the nodes that have id in their parents list
    public List<Node> findByParentsContaining(String id);
}
