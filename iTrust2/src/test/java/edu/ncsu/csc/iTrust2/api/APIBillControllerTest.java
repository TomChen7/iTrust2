package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for the API functionality for interacting with bills
 *
 * @author timothygachigi
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIBillControllerTest {
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BillService           arService;

    @Autowired
    private UserService<User>     service;

    @Autowired
    private CPTCodeService        codeservice;

    /**
     * Sets up tests
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        service.deleteAll();
        arService.deleteAll();

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User billingstaff = new Personnel( new UserForm( "billingstaff", "123456", Role.ROLE_BILLINGSTAFF, 1 ) );

        service.saveAll( List.of( patient, billingstaff ) );

    }

    /**
     * Tests that getting a bill that doesn't exist returns the proper status
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "billingstaff", roles = { "BILLINGSTAFF" } )
    public void testGetNonExistentBill () throws Exception {
        mvc.perform( get( "/api/v1/bills/2" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests that getting a bill that doesn't exist returns the proper status
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "billingstaff", roles = { "BILLINGSTAFF", "HCP", "ADMIN" } )
    public void testBillAPI () throws Exception {
        final ArrayList<CPTCode> codes = new ArrayList<CPTCode>();
        codes.add( new CPTCode( 10000, "Description", 250, true ) );
        codes.add( new CPTCode( 10001, "Another Descriptions", 100, true ) );
        codeservice.save( codes.get( 0 ) );
        codeservice.save( codes.get( 1 ) );

        // Create two bill forms for testing
        final BillForm form1 = new BillForm();
        form1.setPatient( "patient" );
        form1.setPaymentDate( "2030-11-19T04:50:00.000-05:00" );
        form1.setStatus( "Unpaid" );
        form1.setBalance( 78 );
        form1.setPaymentMethod( "Cash" );

        final BillForm form2 = new BillForm();
        form2.setPatient( "patient" );
        form2.setPaymentDate( "2031-12-19T04:50:00.000-05:00" );
        form2.setStatus( "Unpaid" );
        form2.setBalance( 58 );
        form2.setPaymentMethod( "Check" );
        form2.setCodes( codeservice.getCodes() );
        final List<CPTCode> servercodes = codeservice.getCodes();

        assertEquals( 2, servercodes.size() );
        form1.setCodes( codeservice.getCodes() );
        System.out.println( "JSON: \n" + TestUtils.asJsonString( form1 ) );
        System.out.println( TestUtils.asJsonString( form2 ) );
        // Add first bill to system
        final String content1 = mvc
                .perform( post( "/api/v1/bills" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        final Bill b1 = TestUtils.gson().fromJson( content1, Bill.class );
        final BillForm b1Form = new BillForm( b1 );
        assertEquals( form1.getPatient(), b1Form.getPatient() );
        // Weird format switch, only compare date and time, don't worry about
        // seconds
        assertEquals( form1.getPaymentDate().substring( 0, 13 ), b1Form.getPaymentDate().substring( 0, 13 ) );

        assertEquals( form1.getCodes().size(), b1Form.getCodes().size() );
        assertEquals( form1.getStatus().toLowerCase(), b1Form.getStatus().toLowerCase() );
        assertEquals( form1.getBalance(), b1Form.getBalance() );

        // Add second bill to system
        final String content2 = mvc
                .perform( post( "/api/v1/bills" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        final Bill b2 = TestUtils.gson().fromJson( content2, Bill.class );
        final BillForm b2Form = new BillForm( b2 );
        assertEquals( form2.getPatient(), b2Form.getPatient() );
        assertEquals( form2.getPaymentDate().substring( 0, 13 ), b2Form.getPaymentDate().substring( 0, 13 ) );
        assertEquals( form2.getCodes().size(), b2Form.getCodes().size() );
        assertEquals( form2.getStatus().toLowerCase(), b2Form.getStatus().toLowerCase() );
        assertEquals( form2.getBalance(), b2Form.getBalance() );

        // Verify bills have been added
        final String allBillContent = mvc.perform( get( "/api/v1/bills" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        final List<Bill> allBills = TestUtils.gson().fromJson( allBillContent, new TypeToken<List<Bill>>() {
        }.getType() );
        assertTrue( allBills.size() >= 2 );

        // Edit first bill
        b1.setStatus( BillStatus.PARTIALLY_PAID );
        final String editContent = mvc
                .perform( put( "/api/v1/bills" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new BillForm( b1 ) ) ) )
                .andReturn().getResponse().getContentAsString();

    }

}
