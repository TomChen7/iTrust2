package edu.ncsu.csc.iTrust2.models;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.TestConfig;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class CPTCodeTest {

    // @Test
    // @Transactional
    // public void testGetID () {
    // final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );
    // final long test = 1;
    // newCpt.setId( test );
    // assertNotNull( newCpt.getId() );
    // }

    @Test
    @Transactional
    public void testGetCode () {
        final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );
        assertEquals( 12345, newCpt.getCode() );
        assertNotEquals( 54321, newCpt.getCode() );
    }

    @Test
    @Transactional
    public void testGetString () {
        final CPTCode newCpt = new CPTCode( 12345, "hello", 100, true );
        assertEquals( "hello", newCpt.getDescription() );
    }

    @Test
    @Transactional
    public void testGetCost () {
        final CPTCode newCpt = new CPTCode( 12345, "hello", 100, true );
        assertEquals( 100, newCpt.getValue() );
    }

    @Test
    @Transactional
    public void testGetBoolean () {
        final CPTCode newCpt = new CPTCode( 12345, "hello", 100, true );
        assertTrue( newCpt.isCurrent() );
        newCpt.makeObsolete();
        assertFalse( newCpt.isCurrent() );

    }

}
