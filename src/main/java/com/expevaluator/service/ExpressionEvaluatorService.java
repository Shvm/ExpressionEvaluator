package com.expevaluator.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expevaluator.data.BinaryTreeNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Class ExpressionEvaluatorService.
 */
@Service
public class ExpressionEvaluatorService {

  /** The expression parser service. */
  @Autowired
  JsonExpressionParserService expressionParserService;

  /** The negate operator. */
  private static Map<String, String> negateOperator;

  /**
   * The Enum Operator.
   */
  private enum Operator {

    /** The add. */
    ADD(1),
    /** The subtract. */
    SUBTRACT(2),
    /** The multiply. */
    MULTIPLY(3),
    /** The divide. */
    DIVIDE(4);

    /** The precedence. */
    final int precedence;

    /**
     * Instantiates a new operator.
     *
     * @param p
     *          the p
     */
    Operator(int p) {
      precedence = p;
    }
  }

  /** The ops. */
  private static Map<String, Operator> ops = new HashMap<String, Operator>() {

    {
      put("+", Operator.ADD);
      put("-", Operator.SUBTRACT);
      put("*", Operator.MULTIPLY);
      put("/", Operator.DIVIDE);
    }
  };

  static {
    negateOperator = new HashMap<>();
    negateOperator.put("+", "-");
    negateOperator.put("-", "+");
    negateOperator.put("*", "/");
    negateOperator.put("/", "*");
    negateOperator = Collections.unmodifiableMap(negateOperator);
  }

  /**
   * Gets the expression.
   *
   * @param str
   *          the str
   * @return the expression
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public String getExpression(String str) throws JsonProcessingException, IOException {
    str = putOperatorsInString(str);
    JsonNode json = expressionParserService.getJsonFromString(str);
    StringBuilder sbr = new StringBuilder();
    getExpression(expressionParserService.getTreeFromJson(json), sbr);
    String res = prettyFormatExpression(sbr);
    return res;
  }

  /**
   * Gets the expression in terms of x.
   *
   * @param str
   *          the str
   * @return the expression in terms of x
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public String getExpressionInTermsOfX(String str) throws JsonProcessingException, IOException {
    str = putOperatorsInString(str);
    JsonNode json = expressionParserService.getJsonFromString(str);
    BinaryTreeNode<String> root = expressionParserService.getTreeFromJson(json);
    root = manipulateTreeToSolveX(root);
    StringBuilder sbr = new StringBuilder();
    getExpression(root, sbr);
    return String.valueOf(prettyFormatExpression(sbr));
  }

  /**
   * Solve equation.
   *
   * @param str
   *          the str
   * @return the string
   * @throws JsonProcessingException
   *           the json processing exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public String solveEquation(String str) throws JsonProcessingException, IOException {
    str = putOperatorsInString(str);
    JsonNode json = expressionParserService.getJsonFromString(str);
    BinaryTreeNode<String> root = expressionParserService.getTreeFromJson(json);
    root = manipulateTreeToSolveX(root);
    StringBuilder sbr = new StringBuilder();
    getExpression(root, sbr);
    prettyFormatExpression(sbr);
    String expression = prettyFormatExpression(sbr);
    expression = expression.substring(3, expression.length());
    return solveInfixExpression(expression);
  }

  /**
   * Solve infix expression.
   *
   * @param infix
   *          the infix
   * @return the string
   */
  private String solveInfixExpression(String infix) {
    String postFix = getPostfixExpression(infix);
    Stack<String> st = new Stack<>();
    for (String token : postFix.split("\\s")) {
      if (!ops.containsKey(token)) {
        st.push(token);
      } else {
        String op2 = st.pop();
        String op1 = st.pop();
        String val = applyOperation(op1, op2, token);
        st.push(val);
      }
    }
    return st.peek();
  }

  /**
   * Gets the postfix expression.
   *
   * @param infix
   *          the infix
   * @return the postfix expression
   */
  private static String getPostfixExpression(String infix) {
    StringBuilder output = new StringBuilder();
    Deque<String> stack = new LinkedList<>();

    for (String token : infix.split("\\s")) {
      if (ops.containsKey(token)) {
        while (!stack.isEmpty() && isHigerPrec(token, stack.peek()))
          output.append(stack.pop()).append(' ');
        stack.push(token);
      } else if (token.equals("(")) {
        stack.push(token);
      } else if (token.equals(")")) {
        while (!stack.peek().equals("("))
          output.append(stack.pop()).append(' ');
        stack.pop();
      } else {
        output.append(token).append(' ');
      }
    }

    while (!stack.isEmpty())
      output.append(stack.pop()).append(' ');

    return output.toString();
  }

  /**
   * Manipulate tree to solve x.
   *
   * @param root
   *          the root
   * @return the binary tree node
   */
  private BinaryTreeNode<String> manipulateTreeToSolveX(BinaryTreeNode<String> root) {
    while (!root.getLeft().getValue().equals("x") && !root.getRight().getValue().equals("x")) {
      if (findX(root.getLeft())) {
        String currOperator = root.getLeft().getValue();
        if (currOperator.equals("/") || currOperator.equals("-")
            || findX(root.getLeft().getLeft())) {
          BinaryTreeNode<String> temp = new BinaryTreeNode<>();
          temp.setValue(negateOperator.get(currOperator));
          temp.setLeft(root.getRight());
          if (root.getLeft() != null) {
            temp.setRight(root.getLeft().getRight());
            root.setLeft(root.getLeft().getLeft());
          } else {
            temp.setRight(null);
            root.setLeft(null);
          }
          root.setRight(temp);
        } else {
          BinaryTreeNode<String> temp = new BinaryTreeNode<>();
          temp.setValue(negateOperator.get(currOperator));
          temp.setLeft(root.getRight());
          if (root.getLeft() != null) {
            temp.setRight(root.getLeft().getLeft());
            root.setLeft(root.getLeft().getRight());
          } else {
            temp.setRight(null);
            root.setLeft(null);
          }
          root.setRight(temp);
        }
      } else {
        String currOperator = root.getRight().getValue();
        if (currOperator.equals("/") || currOperator.equals("-")
            || findX(root.getRight().getLeft())) {
          BinaryTreeNode<String> temp = new BinaryTreeNode<>();
          temp.setValue(negateOperator.get(currOperator));
          temp.setLeft(root.getLeft());
          if (root.getRight() != null) {
            temp.setRight(root.getRight().getRight());
            root.setRight(root.getRight().getLeft());
          } else {
            temp.setRight(null);
            root.setLeft(null);
          }
          root.setLeft(temp);
        } else {
          BinaryTreeNode<String> temp = new BinaryTreeNode<>();
          temp.setValue(negateOperator.get(currOperator));
          temp.setLeft(root.getLeft());
          if (root.getRight() != null) {
            temp.setRight(root.getRight().getLeft());
            root.setRight(root.getRight().getRight());
          } else {
            temp.setRight(null);
            root.setLeft(null);
          }
          root.setLeft(temp);
        }
      }
    }
    return root;

  }

  /**
   * Pretty format expression.
   *
   * @param sbr
   *          the sbr
   * @return the string
   */
  private String prettyFormatExpression(StringBuilder sbr) {
    String res = null;
    if (sbr.charAt(0) == '(' && sbr.charAt(sbr.length() - 2) == ')') {
      res = sbr.substring(2, sbr.length() - 3);
      int idx = res.indexOf('=');
      if (res.charAt(res.length() - 1) == 'x' && res.charAt(idx - 2) == ')'
          && res.charAt(0) == '(') {
        res = res.substring(2, idx - 2) + res.substring(idx, res.length());
        StringBuilder sb = new StringBuilder();
        res = sb.append(res.substring(res.length() - 4, res.length())).reverse()
            .append(res.substring(0, res.length() - 4)).toString();
      } else if (res.charAt(4) == '(' && res.charAt(res.length() - 1) == ')') {
        res = res.substring(0, 4) + res.substring(5, res.length() - 2);
      }
    }
    return res;
  }

  /**
   * Find x.
   *
   * @param root
   *          the root
   * @return true, if successful
   */
  private boolean findX(BinaryTreeNode<String> root) {
    boolean ans = false;
    if (root == null) {
      return false;
    }
    if (root.getValue().equals("x")) {
      return true;
    }
    ans = findX(root.getLeft()) || findX(root.getRight());
    return ans;

  }

  /**
   * Gets the expression.
   *
   * @param node
   *          the node
   * @param sbr
   *          the sbr
   * @return the expression
   */
  private static void getExpression(BinaryTreeNode<String> node, StringBuilder sbr) {
    if (node == null) {
      return;
    }
    if (!(node.getLeft() == null && node.getRight() == null)) {
      sbr.append("( ");
    }
    getExpression(node.getLeft(), sbr);
    sbr.append(node.getValue() + " ");
    getExpression(node.getRight(), sbr);
    if (!(node.getLeft() == null && node.getRight() == null)) {
      sbr.append(") ");
    }

  }

  /**
   * Put operators in string.
   *
   * @param str
   *          the str
   * @return the string
   */
  private static String putOperatorsInString(String str) {
    str = str.replaceAll("add", "+");
    str = str.replaceAll("multiply", "*");
    str = str.replaceAll("divide", "/");
    str = str.replaceAll("subtract", "-");
    str = str.replaceAll("equal", "=");
    return str;
  }

  /**
   * Apply operation.
   *
   * @param op1
   *          the op1
   * @param op2
   *          the op2
   * @param op
   *          the op
   * @return the string
   */
  private String applyOperation(String op1, String op2, String op) {
    switch (op) {
      case "+":
        return String.valueOf(Double.parseDouble(op1) + Double.parseDouble(op2));
      case "-":
        return String.valueOf(Double.parseDouble(op1) - Double.parseDouble(op2));
      case "*":
        return String.valueOf(Double.parseDouble(op1) * Double.parseDouble(op2));
      case "/":
        if (op2 == "0") {
          throw new RuntimeException("Can't divide by 0");
        }
        return String.valueOf(Double.parseDouble(op1) / Double.parseDouble(op2));
    }
    return null;
  }

  /**
   * Checks if is higer prec.
   *
   * @param op
   *          the op
   * @param sub
   *          the sub
   * @return true, if is higer prec
   */
  private static boolean isHigerPrec(String op, String sub) {
    return (ops.containsKey(sub) && ops.get(sub).precedence >= ops.get(op).precedence);
  }

}
