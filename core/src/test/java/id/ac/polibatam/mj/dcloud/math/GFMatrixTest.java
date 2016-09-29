/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.math;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mia
 */
public class GFMatrixTest {


    private final int[][] c = new int[][]{new int[]{1, 3, 5}, new int[]{1, 4, 3}, new int[]{1, 5, 2}, new int[]{1, 6, 7}, new int[]{1, 7, 6}};
    private final int[] cStar = new int[]{3, 6, 7};
    private final int[] cRowColumn = new int[]{1, 3, 5, 1, 4, 3, 1, 5, 2, 1, 6, 7, 1, 7, 6};
    private final int[] cColumnRow = new int[]{1, 1, 1, 1, 1, 3, 4, 5, 6, 7, 5, 3, 2, 7, 6};

    private final int[][] d = new int[][]{new int[]{1, 4, 7}, new int[]{2, 5, 8}, new int[]{3, 6, 9}};

    private final int[][] e = new int[][]{new int[]{8, 6, 7}, new int[]{12, 9, 9}, new int[]{13, 10, 8}, new int[]{4, 8, 8}, new int[]{5, 11, 9}};
    private final int[][] eStar = new int[][]{new int[]{8, 6, 7}, new int[]{4, 8, 8}, new int[]{5, 11, 9}};

    private final GFMatrix gfMatrix = new GFMatrix(16, 19);

    @Before
    public void before() {

    }

    @Test
    public void testIsValidDimension() {

        assertTrue(this.gfMatrix.isValidDimension(c));
        assertTrue(this.gfMatrix.isValidDimension(d));

        assertFalse(this.gfMatrix.isValidDimension(null));
        assertFalse(this.gfMatrix.isValidDimension(new int[][]{}));
        assertFalse(this.gfMatrix.isValidDimension(new int[][]{null, null}));
        assertFalse(this.gfMatrix.isValidDimension(new int[][]{new int[]{}, new int[]{}}));
        assertFalse(this.gfMatrix.isValidDimension(new int[][]{new int[]{1, 2, 3}, new int[]{4, 5}, new int[]{6, 7, 8}}));

    }

    @Test
    public void testGetRow() {

        assertTrue(Arrays.equals(this.gfMatrix.getRow(this.c, 0), new int[]{1, 3, 5}));
        assertTrue(Arrays.equals(this.gfMatrix.getRow(this.c, 1), new int[]{1, 4, 3}));
        assertTrue(Arrays.equals(this.gfMatrix.getRow(this.c, 2), new int[]{1, 5, 2}));
        assertTrue(Arrays.equals(this.gfMatrix.getRow(this.c, 3), new int[]{1, 6, 7}));
        assertTrue(Arrays.equals(this.gfMatrix.getRow(this.c, 4), new int[]{1, 7, 6}));

        assertTrue(Arrays.equals(this.gfMatrix.getRow(this.d, 0), new int[]{1, 4, 7}));
        assertTrue(Arrays.equals(this.gfMatrix.getRow(this.d, 1), new int[]{2, 5, 8}));
        assertTrue(Arrays.equals(this.gfMatrix.getRow(this.d, 2), new int[]{3, 6, 9}));

    }

    @Test
    public void testSetRow() {

        final int[] newRow = new int[]{0, 0, 0};
        for (int i = 0; i < this.d.length; i++) {
            this.gfMatrix.setRow(this.d, newRow, i);
            assertTrue(Arrays.equals(newRow, this.gfMatrix.getRow(this.d, i)));
        }

    }

    @Test
    public void testGetColumn() {

        assertTrue(Arrays.equals(this.gfMatrix.getColumn(this.c, 0), new int[]{1, 1, 1, 1, 1}));
        assertTrue(Arrays.equals(this.gfMatrix.getColumn(this.c, 1), new int[]{3, 4, 5, 6, 7}));
        assertTrue(Arrays.equals(this.gfMatrix.getColumn(this.c, 2), new int[]{5, 3, 2, 7, 6}));

        assertTrue(Arrays.equals(this.gfMatrix.getColumn(this.d, 0), new int[]{1, 2, 3}));
        assertTrue(Arrays.equals(this.gfMatrix.getColumn(this.d, 1), new int[]{4, 5, 6}));
        assertTrue(Arrays.equals(this.gfMatrix.getColumn(this.d, 2), new int[]{7, 8, 9}));

    }

    @Test
    public void testSetColumn() {

        final int[] newColumn = new int[]{0, 0, 0};
        for (int j = 0; j < this.d[0].length; j++) {
            this.gfMatrix.setColumn(this.d, newColumn, j);
            assertTrue(Arrays.equals(newColumn, this.gfMatrix.getColumn(this.d, j)));
        }

    }

    @Test
    public void testToArrayRowColumn() {
        assertTrue(Arrays.equals(this.cRowColumn, this.gfMatrix.toArrayRowColumn(this.c)));

    }

    @Test
    public void testToArrayColumnRow() {
        assertTrue(Arrays.equals(this.cColumnRow, this.gfMatrix.toArrayColumnRow(this.c)));

    }

    @Test
    public void testToMatrixRowColumn() {
        assertTrue(Arrays.deepEquals(this.c, this.gfMatrix.toMatrixRowColumn(this.cRowColumn, this.c.length, this.c[0].length)));

    }

    @Test
    public void testToMatrixColumnRow() {
        assertTrue(Arrays.deepEquals(this.c, this.gfMatrix.toMatrixColumnRow(this.cColumnRow, this.c.length, this.c[0].length)));

    }

    @Test
    public void testTranspose() {

        assertTrue(Arrays.deepEquals(this.gfMatrix.transpose(this.c), new int[][]{new int[]{1, 1, 1, 1, 1}, new int[]{3, 4, 5, 6, 7}, new int[]{5, 3, 2, 7, 6}}));

        assertTrue(Arrays.deepEquals(this.gfMatrix.transpose(this.d), new int[][]{new int[]{1, 2, 3}, new int[]{4, 5, 6}, new int[]{7, 8, 9}}));

    }

    @Test
    public void testAdd() {

    }

    @Test
    public void testSubstract() {

    }

    @Test
    public void testMultiply() {
        assertTrue(Arrays.deepEquals(this.e, this.gfMatrix.multiply(this.c, this.d)));
    }

    @Test
    public void testSolveVandermondeSystem() {

    }

    @Test
    public void testSolveVandermondeSystemTPerColumn() {

        for (int j = 0; j < this.eStar[0].length; j++) {

            final int[] solve = this.gfMatrix.solveVandermondeSystemT(this.cStar, this.gfMatrix.getColumn(eStar, j));
            assertTrue(Arrays.equals(this.gfMatrix.getColumn(d, j), solve));

        }

    }

    @Test
    public void testSolveVandermondeSystemT() {

        assertTrue(Arrays.deepEquals(this.d, this.gfMatrix.solveVandermondeSystemT(this.cStar, this.eStar)));

    }

}
