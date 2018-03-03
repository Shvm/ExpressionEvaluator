package com.expevaluator.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.expevaluator.service.ExpressionEvaluatorService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The Class ExpressionEvaluatorController.
 */
@Controller
public class ExpressionEvaluatorController {

  /** The expression evaluator service. */
  @Autowired
  ExpressionEvaluatorService expressionEvaluatorService;

  /**
   * Evaluate expression.
   *
   * @param json
   *          the json
   * @return the response entity
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @GetMapping("/expression/evaluate")
  public ResponseEntity<String> evaluateExpression(@RequestParam(value = "json") String json)
      throws JsonProcessingException, IOException {
    return new ResponseEntity<String>(expressionEvaluatorService.getExpression(json),
        HttpStatus.OK);
  }

  /**
   * Evaluate expression for x.
   *
   * @param json
   *          the json
   * @return the response entity
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @GetMapping("/expression/evaluate-for-x")
  public ResponseEntity<String> evaluateExpressionForX(@RequestParam(value = "json") String json)
      throws JsonProcessingException, IOException {
    return new ResponseEntity<String>(expressionEvaluatorService.getExpressionInTermsOfX(json),
        HttpStatus.OK);
  }

  /**
   * Solve expression for x.
   *
   * @param json
   *          the json
   * @return the response entity
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @GetMapping("/expression/solve-for-x")
  public ResponseEntity<String> solveExpressionForX(@RequestParam(value = "json") String json)
      throws JsonProcessingException, IOException {
    return new ResponseEntity<String>(expressionEvaluatorService.solveEquation(json),
        HttpStatus.OK);
  }
}
