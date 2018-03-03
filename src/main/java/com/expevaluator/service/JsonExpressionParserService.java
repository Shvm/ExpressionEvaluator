package com.expevaluator.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expevaluator.data.BinaryTreeNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * The Class JsonExpressionParserService.
 */
@Service
public class JsonExpressionParserService {

  /** The mapper. */
  @Autowired
  ObjectMapper mapper;

  /**
   * Gets the tree from json.
   *
   * @param jsonNode
   *          the json node
   * @return the tree from json
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public BinaryTreeNode<String> getTreeFromJson(JsonNode jsonNode)
      throws JsonProcessingException, IOException {
    BinaryTreeNode<String> root = null;

    if (jsonNode.getNodeType().equals(JsonNodeType.NUMBER)
        || jsonNode.getNodeType().equals(JsonNodeType.STRING)) {
      root = new BinaryTreeNode<>();
      root.setValue(jsonNode.asText());
      root.setLeft(null);
      root.setRight(null);
      return root;
    }

    if (jsonNode.has("op")) {
      root = new BinaryTreeNode<>();
      root.setValue(jsonNode.get("op").asText());
      if (jsonNode.has("lhs")) {
        root.setLeft(getTreeFromJson(jsonNode.get("lhs")));
      }
      if (jsonNode.has("lhs")) {
        root.setRight(getTreeFromJson(jsonNode.get("rhs")));
      }

    }
    return root;
  }

  /**
   * Gets the tree from json.
   *
   * @param json
   *          the json
   * @return the tree from json
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public BinaryTreeNode<String> getTreeFromJson(String json)
      throws JsonProcessingException, IOException {
    JsonNode node = mapper.readTree(json);
    BinaryTreeNode<String> root = getTreeFromJson(node);
    return root;
  }

  /**
   * Gets the json from string.
   *
   * @param str
   *          the str
   * @return the json from string
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public JsonNode getJsonFromString(String str) throws JsonProcessingException, IOException {
    return mapper.readTree(str);
  }
}
