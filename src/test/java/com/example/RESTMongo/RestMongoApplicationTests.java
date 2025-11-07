package com.example.RESTMongo;

import com.example.RESTMongo.DTO.NodeDTO;
import com.example.RESTMongo.Model.Node;
import com.example.RESTMongo.Repository.NodeRepository;
import com.example.RESTMongo.Service.NodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Use Mockito's extension to manage mocks
@ExtendWith(MockitoExtension.class)
class NodeServiceImplTest {
	@Mock
	private NodeRepository nodeRepository;

	@InjectMocks
	private NodeServiceImpl nodeService;

	private Node nodeA;
	private Node nodeB;
	private NodeDTO nodeDTO;

	@BeforeEach
	void setUp() {
		nodeA = new Node("id-A", 10.0, new ArrayList<>());
		nodeB = new Node("id-B", 20.0, new ArrayList<>(Arrays.asList("id-A")));
		nodeDTO = new NodeDTO("id-A", 10.0, new ArrayList<>());
	}

	@Test
	void testGetAllNodes() {
		when(nodeRepository.findAll()).thenReturn(Arrays.asList(nodeA, nodeB));

		List<NodeDTO> results = nodeService.getAllNodes();

		assertEquals(2, results.size());
		assertEquals("id-A", results.get(0).id());
		assertEquals("id-B", results.get(1).id());
		verify(nodeRepository).findAll();
	}

	@Test
	void getNodeByIdShouldReturnNode() {
		when(nodeRepository.findById("id-A")).thenReturn(Optional.of(nodeA));

		Optional<NodeDTO> result = nodeService.getNodeById("id-A");

		assertTrue(result.isPresent());
		assertEquals("id-A", result.get().id());
		verify(nodeRepository, times(1)).findById("id-A");
	}

	@Test
	void getNodeByIdReturnsEmptyWhenNotFound() {
		when(nodeRepository.findById("id-C")).thenReturn(Optional.empty());

		Optional<NodeDTO> result = nodeService.getNodeById("id-C");

		assertFalse(result.isPresent());
	}

	@Test
	void testSaveNode() {
		NodeDTO dtoToSave = new NodeDTO(null, 30.0, new ArrayList<>());
		Node savedNode = new Node("new-id", 30.0, new ArrayList<>());
		when(nodeRepository.save(any(Node.class))).thenReturn(savedNode);

		NodeDTO result = nodeService.saveNode(dtoToSave);

		assertEquals("new-id", result.id());
		assertEquals(30.0, result.value());
		verify(nodeRepository).save(any(Node.class));
	}

	@Test
	void updateNodeShouldOnlyUpdateParentsWhenValueIsNull() {
		List<String> newParents = Arrays.asList("p1", "p2");
		NodeDTO updateDTO = new NodeDTO("id-A", null, new ArrayList<>(newParents));

		when(nodeRepository.findById("id-A")).thenReturn(Optional.of(nodeA));
		when(nodeRepository.findAll()).thenReturn(Collections.singletonList(nodeA));

		nodeService.updateNode("id-A", updateDTO);

		assertEquals(newParents, nodeA.getParents());
		verify(nodeRepository, times(2)).save(nodeA);
		verify(nodeRepository).findAll();
	}

	@Test
	void updateNodeShouldPropagateValueChanges() {
		// nodeA is parent of nodeB, change nodeA from 10 to 20 (ratio = 2.0)
		NodeDTO updateDTO = new NodeDTO("id-A", 20.0, null);

		when(nodeRepository.findById("id-A")).thenReturn(Optional.of(nodeA));
		when(nodeRepository.findByParentsContaining("id-A")).thenReturn(Collections.singletonList(nodeB));
		when(nodeRepository.findByParentsContaining("id-B")).thenReturn(Collections.emptyList());
		when(nodeRepository.findById("id-B")).thenReturn(Optional.of(nodeB));
		when(nodeRepository.findAll()).thenReturn(Arrays.asList(nodeA, nodeB));

		nodeService.updateNode("id-A", updateDTO);

		// values should be multiplied by 2
		assertEquals(20.0, nodeA.getValue());
		assertEquals(40.0, nodeB.getValue());
		verify(nodeRepository, times(1)).saveAll(anyList());
	}

	@Test
	void testUpdateWithZeroValue() {
		Node zeroNode = new Node("id-Z", 0.0, new ArrayList<>());
		NodeDTO updateDTO = new NodeDTO("id-Z", 50.0, null);

		when(nodeRepository.findById("id-Z")).thenReturn(Optional.of(zeroNode));
		when(nodeRepository.findByParentsContaining("id-Z")).thenReturn(Collections.emptyList());
		when(nodeRepository.findAll()).thenReturn(Collections.singletonList(zeroNode));

		ArgumentCaptor<Node> nodeCaptor = ArgumentCaptor.forClass(Node.class);

		nodeService.updateNode("id-Z", updateDTO);

		verify(nodeRepository, times(1)).save(nodeCaptor.capture());
		assertEquals(50.0, nodeCaptor.getValue().getValue());
	}
}