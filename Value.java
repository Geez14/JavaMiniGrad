package minigrad;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Consumer;

public class Value implements Comparable<Value>, java.io.Serializable {
  static List<Value> topologyListCache = null;
  double data;
  double gradient;
  String label;
  Set<Value> prevs;
  String operation;
  private Apply _backward;

  private Value(double data, Set<Value> childrenSet, String operation, String label) {
    this.data = data;
    this.prevs = childrenSet; // saving the prev child element to make a graph structure!
    this.operation = operation;
    this.label = label;
    this._backward = () -> {
    }; // no operation for leaf node
    this.gradient = 0.0;
  }

  public Value(double data, String label) {
    this(data, Collections.emptySet(), "nop", label);
  }

  public Value add(Value other) {
    Set<Value> prevs = new HashSet<>(2, 1.0f);
    prevs.add(this);
    prevs.add(other);
    // Value out = new Value(this.data + other.data, prevs, "+",
    // "(".concat(this.label.concat("+").concat(other.label)).concat(")"));

    Value out = new Value(this.data + other.data, prevs, "+", "");
    // in plus operation differentiation become 1
    out._backward = () -> {
      this.gradient += out.gradient * 1;
      other.gradient += out.gradient * 1;
    };

    return out;
  }

  public Value multiply(Value other) {
    Set<Value> prevs = new HashSet<>(2, 1.0f);
    prevs.add(other);
    prevs.add(this);

    // Value out = new Value(this.data * other.data, prevs, "*",
    // "(".concat(this.label.concat("*").concat(other.label)).concat(")"));

    Value out = new Value(data * other.data, prevs, "*", "");

    // my backpropagation logic!
    out._backward = () -> {
      this.gradient += other.data * out.gradient;
      other.gradient += Value.this.data * out.gradient;
    };

    return out;
  }

  public Value tanh() {
    // Value out = new Value(Math.tanh(this.data), Set.of(this), "tanh(x)",
    // "tanh(".concat(this.label).concat(")"));
    Value out = new Value(Math.tanh(this.data), Set.of(this), "tanh", "");
    out._backward = () -> {
      this.gradient = 1 + (this.data * this.data);
    };
    return out;
  }

  public Iterator<Value> getPrevIterator() {
    return prevs.iterator();
  }

  private static String trace(Value root) {
    StringBuilder sb = new StringBuilder();
    sb.append("Value: { label: ").append(root.label)
        .append(", data: ").append(root.data)
        .append(", gradient: ").append(String.format("%.2f", root.gradient))
        .append(", operation: ").append(root.operation)
        .append(", childrens: ").append(root.prevs).append(" }");
    return sb.toString();
  }

  public void backward() {
    // put the current node gradient to 1
    this.gradient = 1;
    LinkedList<Value> topology = new LinkedList<>();
    Set<Value> visited = new HashSet<>();
    // build node!
    final Consumer<Value> con = new Consumer<>() {
      public void accept(Value root) {
        if (!visited.contains(root)) {
          visited.add(root);
          for (Value child : root.prevs) {
            accept(child);
          }
          topology.push(root);
        }
      }
    };
    con.accept(this);
    // reverse List!
    // Collections.reverse(null);
    Value.topologyListCache = topology;
    for (Value node : topology) {
      node._backward.compute();
    }
  }

  @Override
  public String toString() {
    return "Value: {data:".concat(String.format("%.2f", data)).concat(", gradient:")
        .concat(String.format("%.2f", gradient)).concat("}");
  }

  public static void drawGraph(Value root) {
    // int i = 0;
    LinkedList<Value> l = new LinkedList<>();
    l.push(root);
    Value curr;
    while (!l.isEmpty()) {
      curr = l.pop();
      for (Value value : curr.prevs) {
        l.push(value);
      }
      System.out.println(
          trace(curr));
    }
  }

  @Override
  public int compareTo(Value o) {
    return Double.compare(this.data, o.data);
  }

  // fallback methods
  public Value add(double other) {
    return this.add(new Value(other, label = Double.toString(other)));
  }

  public Value multiply(double other) {
    return this.multiply(new Value(other, label = Double.toString(other)));
  }

  interface Apply extends java.io.Serializable {

    void compute();

  }

}
