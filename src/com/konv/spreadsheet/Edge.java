package com.konv.spreadsheet;

public class Edge {
    private final Cell mSource;
    private final Cell mTarget;

    public Edge(Cell from, Cell to) {
        mSource = from;
        mTarget = to;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Edge edge = (Edge) obj;

        return mSource.equals(edge.mSource) && mTarget.equals(edge.mTarget);
    }

    @Override
    public int hashCode() {
        int result = mSource.hashCode();
        result = 31 * result + mTarget.hashCode();
        return result;
    }

    public Cell getSource() {
        return mSource;
    }

    public Cell getTarget() {
        return mTarget;
    }
}