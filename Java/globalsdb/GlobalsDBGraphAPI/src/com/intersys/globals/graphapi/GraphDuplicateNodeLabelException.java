package com.intersys.globals.graphapi;

public class GraphDuplicateNodeLabelException extends GraphException {
    public GraphDuplicateNodeLabelException(Graph graph, String string) {
        super(graph, string);
    }

    public GraphDuplicateNodeLabelException() {
        super();
    }

    public GraphDuplicateNodeLabelException(String string) {
        super(string);
    }

    public GraphDuplicateNodeLabelException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public GraphDuplicateNodeLabelException(Throwable throwable) {
        this("Duplicate node label", throwable);
    }
}
