package com.expevaluator.data;

import lombok.Data;

@Data
public class BinaryTreeNode<T> {

  private T value;
  private BinaryTreeNode<T> left;
  private BinaryTreeNode<T> right;
  
  public boolean findX(String val) {
    boolean ans = false;
    if (this.getValue().equals("x")) {
      return true;
    }
    ans = findX(val) || findX(val);
    return ans;

  }

}
