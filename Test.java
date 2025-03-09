package minigrad;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Test implements Runnable {

  private static final List<Value> ListIterator = null;

  void testAdd() {
    Value a = new Value(10, "a");
    Value b = new Value(5, "b");
    Value result = a.add(b);
    assert (15 == result.data) : "Failed Adding two Values";
    assert ("(a+b)".equals(result.label)) : "Label mismatch in addition";
  }

  void testMultiply() {
    Value a = new Value(10, "a");
    Value b = new Value(5, "b");
    Value result = a.multiply(b);
    assert (50 == result.data) : "Failed Multiplying two Values";
    assert ("(a*b)".equals(result.label)) : "Label mismatch in multiplication";
  }

  void testTanh() {
    Value a = new Value(0, "a");
    Value result = a.tanh();
    assert (Math.tanh(0) == result.data) : "Failed applying tanh function";
  }

  void testAddWithDouble() {
    Value a = new Value(10, "a");
    Value result = a.add(5);
    assert (15 == result.data) : "Failed Adding Value and double";
  }

  void testMultiplyWithDouble() {
    Value a = new Value(10, "a");
    Value result = a.multiply(5);
    assert (50 == result.data) : "Failed Multiplying Value and double";
  }

  void testBackward() {
    Value a = new Value(10, "a");
    Value b = new Value(5, "b");
    Value result = a.add(b);
    result.gradient = 1.0;
    result.backward();
    assert (1.0 == b.gradient) : "Gradient mismatch after backward on addition";
  }

  void testDrawGraph() {
    Value a = new Value(10, "a");
    Value b = new Value(5, "b");
    Value result = a.add(b);
    Value.drawGraph(result);
    // This test is for visual inspection of the graph output
  }

  void backwardTest() {
    Value a = new Value(3.0, "a");
    Value b = new Value(32, "34");
    Value c = a.multiply(b).tanh();
    Value d = c.multiply(83);
    d.backward();
  }

  public static void main(String[] args)
      throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException {
    Thread d = new Thread(new Test());
    d.start();
    d.join();
  }

  @Override
  public void run() {
    testAdd();
    testAddWithDouble();
    testMultiply();
    testMultiplyWithDouble();
    testBackward();
    backwardTest();
    System.out.println("\033[92mAllTestPassed\033[0m");
  }
}
