package ru.popov.bodya.machineintelligence;

import ru.popov.bodya.pojo.GraphNode;
import ru.popov.bodya.pojo.XOField;

public class Main {
    public static void main(String[] args) {
        final GraphNode root  = new GraphBuilder().build(XOField.Figure.X, new XOField());
        System.out.println(root.getNode());
        System.out.println(GraphHelper.countNodes(root));
    }
}
