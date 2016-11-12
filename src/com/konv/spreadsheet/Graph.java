package com.konv.spreadsheet;

import javafx.scene.control.Alert;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.event.TraversalListenerAdapter;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Graph extends DirectedPseudograph<Cell, Edge> {

    private static Pattern mReferencePattern = Pattern.compile(Tokenizer.TokenType.REFERENCE.pattern);
    private static Tokenizer mTokenizer = new Tokenizer();

    private Cell[][] mCells;
    private DirectedSimpleCycles<Cell, Edge> mCycleDetector;
    private List<List<Cell>> mCycles;

    public Graph(int rows, int columns) {
        super(Edge::new);
        mCells = new Cell[rows][columns];
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                Cell cell = new Cell(row, column);
                mCells[row][column] = cell;
                addVertex(cell);
            }
        }
        mCycleDetector = new TarjanSimpleCycles<>(this);
        mCycles = new ArrayList<>();
    }

    public Cell getCell(String reference) {
        int column = reference.charAt(0) - 'A';
        int row = Integer.parseInt(reference.substring(1)) - 1;
        return mCells[row][column];
    }

    public Cell getCell(int row, int column) {
        return mCells[row][column];
    }

    public void resolveDependencies(Cell cell) {
        Edge[] outgoingEdges = outgoingEdgesOf(cell).toArray(new Edge[0]);
        for (Edge edge : outgoingEdges) removeEdge(edge);
        Matcher matcher = mReferencePattern.matcher(cell.getFormula());
        while (matcher.find()) {
            addEdge(cell, getCell(matcher.group()));
        }
    }

    private void resetEvaluabilityMarks() {
        for (int row = 0; row < mCells.length; ++row) {
            for (int column = 0; column < mCells[0].length; ++column) {
                mCells[row][column].setEvaluable(true);
            }
        }
    }

    private void markCycledVertices() {
        mCycles = mCycleDetector.findSimpleCycles();
        String massage = "";

        for (List<Cell> list : mCycles) {
            String cycle = "Cycle: ";
            for (Cell cell : list) {
                cycle += cell.getStringCoordinates() + " ";
                cell.setEvaluable(false);
            }
            massage += cycle + " ";
        }
        if (mCycles.size() > 0) {
            DialogHelper.showAlert(Alert.AlertType.INFORMATION, "Spreadsheet", "Cycled references found",
                    "Cells that contain undirect references to self value can`t be evaluated properly. \n"+ massage);
        }
    }

    private void markEmptyDependencies() {
        for (int row = 0; row < mCells.length; ++row) {
            for (int column = 0; column < mCells[0].length; ++column) {
                Cell cell = mCells[row][column];
                if (!cell.containsFormula()) {
                    markUnevaluable(cell);
                }
            }
        }
    }

    public void markUnevaluable(Cell cell) {
        for (Edge incoimingEdge : incomingEdgesOf(cell)) {
            Cell dependentCell = incoimingEdge.getSource();
            if (dependentCell.isEvaluable()) {
                markUnevaluable(dependentCell);
            }
        }
        cell.setEvaluable(false);
    }

    public void evaluate() {
        resetEvaluabilityMarks();
        markCycledVertices();
        markEmptyDependencies();
        DepthFirstIterator<Cell, Edge> dfsIterator = new DepthFirstIterator<>(this);
        dfsIterator.setCrossComponentTraversal(true);
        dfsIterator.addTraversalListener(new TraversalListenerAdapter<Cell, Edge>() {
            @Override
            public void vertexFinished(VertexTraversalEvent e) {
                Cell cell = (Cell) e.getVertex();
                if (cell.isEvaluable()) evaluateCell(cell);
            }
        });
        while (dfsIterator.hasNext()) dfsIterator.next();
    }

    private void evaluateCell(Cell cell) {
        cell.setValue(evaluate(mTokenizer.tokenize(cell.getFormula())));
    }

    private BigInteger evaluate(List<Tokenizer.Token> tokensStream) {
        tokensStream.add(0, new Tokenizer.Token(Tokenizer.TokenType.BRACEOPEN, "("));
        tokensStream.add(new Tokenizer.Token(Tokenizer.TokenType.BRACECLOSE, ")"));
        LinkedList<Tokenizer.Token> outputStack = new LinkedList<>();
        LinkedList<Tokenizer.Token> operatorStack = new LinkedList<>();
        for (Tokenizer.Token token : tokensStream) {
            switch (token.type) {
                case NUMBER:
                    outputStack.addLast(token);
                    break;
                case REFERENCE:
                    Cell cell = getCell(token.data);
                    outputStack.addLast(new Tokenizer.Token(Tokenizer.TokenType.NUMBER, cell.getValue().toString()));
                    break;
                case BINARYOP:
                    while (!isHigherPrecedence(token, operatorStack.peekLast())) {
                        String secondOperand = outputStack.removeLast().data;
                        String firstOperand = outputStack.removeLast().data;
                        String operator = operatorStack.removeLast().data;
                        String result = evaluate(firstOperand, secondOperand, operator).toString();
                        outputStack.addLast(new Tokenizer.Token(Tokenizer.TokenType.NUMBER, result));
                    }
                    operatorStack.addLast(token);
                    break;
                case BRACEOPEN:
                    outputStack.addLast(token);
                    break;
                case BRACECLOSE:
                    int openBraceIndex = outputStack.lastIndexOf(new Tokenizer.Token(Tokenizer.TokenType.BRACEOPEN, "("));
                    int operatorsNumber = outputStack.size() - openBraceIndex - 2;
                    for (int i = 0; i < operatorsNumber; ++i) {
                        String secondOperand = outputStack.removeLast().data;
                        String firstOperand = outputStack.removeLast().data;
                        String operator = operatorStack.removeLast().data;
                        String result = evaluate(firstOperand, secondOperand, operator).toString();
                        outputStack.addLast(new Tokenizer.Token(Tokenizer.TokenType.NUMBER, result));
                    }
                    outputStack.removeLastOccurrence(new Tokenizer.Token(Tokenizer.TokenType.BRACEOPEN, "("));
                    break;
            }
        }
        if (outputStack.isEmpty()) return null;
        else return new BigInteger(outputStack.getLast().data);
    }

    private BigInteger evaluate(String firstOperand, String secondOperand, String operator) {
        BigInteger a = new BigInteger(firstOperand);
        BigInteger b = new BigInteger(secondOperand);
        switch (operator) {
            case "+":
                return a.add(b);
            case "-":
                return a.subtract(b);
            case "*":
                return a.multiply(b);
            case "/":
                return a.divide(b);
            case "^":
                return a.pow(b.intValue());
            case "|":
                return a.or(b);
            case "&":
                return a.and(b);
            default:
                return null;
        }
    }

    private boolean isHigherPrecedence(Tokenizer.Token firstOperator, Tokenizer.Token secondOperator) {
        int firstPrecedence = getPrecedence(firstOperator);
        int secondPrecedence = getPrecedence(secondOperator);
        return firstPrecedence > secondPrecedence;
    }


    private int getPrecedence(Tokenizer.Token operator) {
        if (operator == null) return -1;
        switch (operator.data) {
            case "^":
                return 10;
            case "*":
            case "/":
                return 9;
            case "+":
            case "-":
                return 8;
            case "&":
                return 7;
            case "|":
                return 7;
            default:
                return -1;
        }
    }

    public String getJsonString() {
        JSONArray cells = new JSONArray();
        for (Cell cell : vertexSet()) {
            JSONObject cellJson = new JSONObject();
            cellJson.put("row", cell.getRow());
            cellJson.put("column", cell.getColumn());
            cellJson.put("formula", cell.getFormula());
            cells.put(cellJson);
        }
        return cells.toString();
    }

    public void restoreCellsFromJson(String jsonCells) {
        try {
            JSONArray cells = new JSONArray(jsonCells);
            for (Object jsonCell : cells) {
                JSONObject currentCell = (JSONObject) jsonCell;
                int row = currentCell.getInt("int");
                int column = currentCell.getInt("column");
                String formula = currentCell.getString("formula");
                Cell cell = getCell(row, column);
                cell.setFormula(formula);
                resolveDependencies(cell);
            }
            evaluate();
        } catch (Exception e) {

        }
    }
}
