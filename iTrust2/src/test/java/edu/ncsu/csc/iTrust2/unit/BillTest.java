package edu.ncsu.csc.iTrust2.unit;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class BillTest {

    /**
     * List of CPT codes for office visits
     */
    private List<CPTCode> codes;

    /**
     * The bill status
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private BillStatus    status;

    @Test
    @Transactional
    public void testBillMaking () {
        /** test patient */
        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );
        /** test CPT code */
        final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );
        /** New Bill */
        final Bill newBill = new Bill( alice, codes, 100, status.UNPAID, null );
        assertNull( newBill.getCodes() );
        final List<CPTCode> newCodes = new ArrayList<CPTCode>();
        newCodes.add( newCpt );
        newBill.setCodes( newCodes );
        /** makes sure a code is added and list is not empty) */
        assertEquals( newBill.getCodes().size(), 1 );
        assertNotNull( newBill.getCodes() );
    }

    @SuppressWarnings ( "static-access" )
    @Test
    @Transactional
    /**
     * Tests the rest of the setters and getters.
     */
    public void testSettingGetting () {
        /** test patient */
        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );
        /** test CPT code */
        final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );
        /** New Bill */
        final Bill newBill = new Bill( null, codes, 100, status.UNPAID, null );
        final List<CPTCode> newCodes = new ArrayList<CPTCode>();
        newCodes.add( newCpt );
        newBill.setCodes( newCodes );

        newBill.setDate( ZonedDateTime.now() );

        /** Cost test */
        newBill.setCost( 150 );
        assertEquals( 150, newBill.getCost() );

        /** Status tests */
        assertEquals( newBill.getStatus(), status.UNPAID );
        newBill.setStatus( status.PAID );
        assertEquals( newBill.getStatus(), status.PAID );

        /** setting and getting the patient */
        newBill.setPatient( alice );
        assertEquals( alice, newBill.getPatient() );

        final String s = newBill.toString();

    }

    @SuppressWarnings ( "static-access" )
    @Test
    @Transactional
    /**
     * Tests the rest of the setters and getters.
     */
    public void testEquals () {
        /** test patient */
        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );
        /** test CPT code */
        final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );
        /** New Bill */
        final Bill newBill = new Bill( null, codes, 100, status.UNPAID, null );
        /** test bill */
        final Bill testBill = new Bill( null, codes, 150, status.UNPAID, null );

        final List<CPTCode> newCodes = new ArrayList<CPTCode>();
        newCodes.add( newCpt );
        newBill.setCodes( newCodes );
        newBill.setDate( ZonedDateTime.now() );

        /** tests equals method */
        assertFalse( newBill.equals( testBill ) );
        testBill.setCost( 100 );
        assertTrue( newBill.equals( newBill ) );

        final int test1 = newBill.hashCode();
        final int test2 = testBill.hashCode();

        assertNotEquals( test1, test2 );

    }

}
