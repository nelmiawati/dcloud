/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.math;


import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author mia
 */
public class GFMathTest {

    private final static Logger LOG = Logger.getLogger(GFMathTest.class);
    private final static GFMath GFMATH = GFMath.getInstance();

    @Before
    public void before() {
    }

    @Test
    public void testGFMathInstantiate() {

        assertEquals(GFMath.DEFAULT_FIELD_SIZE, GFMATH.getFieldSize());
        assertEquals(GFMath.DEFAULT_PRIMITIVE_POLYNOMIAL, GFMATH.getPrimitivePolynomial());


    }

    @Test
    public void testPseudoRandom() {
        Random random1 = new Random(10);
        for (int i = 0; i < 10; i++) {
            LOG.debug(random1.nextInt(256));
        }

        Random random2 = new Random(10);
        for (int i = 0; i < 10; i++) {
            LOG.debug(random2.nextInt(256));
        }
    }

}
