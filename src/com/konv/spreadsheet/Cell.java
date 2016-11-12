package com.konv.spreadsheet;

import java.math.BigInteger;

public class Cell  {

    private int mRow;
    private int mColumn;
    private boolean mIsEvaluable;
    private String mFormula;
    private BigInteger mValue;

    public Cell(int row, int column) {
        mColumn = column;
        mRow = row;
        mIsEvaluable = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Cell cell = (Cell) obj;

        return mRow == cell.mRow && mColumn == cell.mColumn;
    }

    @Override
    public int hashCode() {
        int result = mRow;
        result = 31 * result + mColumn;
        return result;
    }

    @Override
    public String toString() {
        if (!isEvaluable()) return mFormula;
        if (mValue != null) return mValue.toString();
        if (mFormula != null) return mFormula;
        return "";
    }

    public int getRow() {
        return mRow;
    }

    public int getColumn() {
        return mColumn;
    }

    public void setFormula(String formula) {
        mFormula = formula;
    }

    public String getFormula() {
        return mFormula;
    }

    public boolean isEvaluable() {
        return mIsEvaluable;
    }

    public boolean containsFormula() {
        return mFormula != null;
    }

    public void setValue(BigInteger value) {
        mValue = value;
    }

    public String getStringCoordinates() {
        char column = (char)('A' + mColumn);
        int row = mRow + 1;
        return String.valueOf(column) + String.valueOf(row);
    }

    public BigInteger getValue() {
        return mValue;
    }

    public void setEvaluable(boolean evaluated) {
        mIsEvaluable = evaluated;
    }
}