package edu.ncsu.csc.iTrust2.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Assert;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;

/**
 * Test Class for the API endpoints of CPT Code
 *
 * @author Tom Chen
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICPTCodeControllerTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CPTCodeService        service;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        service.deleteAll();
    }

    /**
     * Tests the adding of CPT codes into SQL
     *
     * @throws Exception
     *             if some exception is thrown
     */
    @Test
    @Transactional
    @WithMockUser ( username = "billing", roles = { "BILLINGSTAFF" } )
    public void testAddCode () throws Exception {
        service.deleteAll();

        final List<CPTCode> l = service.findAll();
        Assert.assertEquals( 0, l.size() );

        final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );

        mvc.perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newCpt ) ) ).andExpect( status().isOk() );

        final CPTCode dupCpt = new CPTCode( 12345, "test", 100, true );

        mvc.perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( dupCpt ) ) ).andExpect( status().isConflict() );

    }

    /**
     * Tests editing a existing CPT Code
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "billing", roles = { "BILLINGSTAFF" } )
    public void testEditCode () throws Exception {
        service.deleteAll();

        final List<CPTCode> l = service.findAll();
        Assert.assertEquals( 0, l.size() );

        final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );
        final CPTCode error = new CPTCode( 56789, "test", 100, true );
        final CPTCode editCode = new CPTCode( 12345, "updating cpt", 120, true );

        mvc.perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newCpt ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( editCode ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( error ) ) ).andExpect( status().isNotFound() );

        final ResultActions current = mvc.perform( get( "/api/v1/cptcodes" ) );

    }

    /**
     * Tests retrieving a existing CPT Code.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "billing", roles = { "BILLINGSTAFF" } )
    public void testGetCode () throws Exception {
        service.deleteAll();

        final List<CPTCode> l = service.findAll();
        Assert.assertEquals( 0, l.size() );

        final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );

        mvc.perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newCpt ) ) ).andExpect( status().isOk() );

        // Attempt to look for the CPT code 12345
        mvc.perform( get( "/api/v1/cptcodes/12345" ) ).andExpect( status().isOk() );
        // Asserts that the code 54321 does not exist
        mvc.perform( get( "/api/v1/cptcodes/54321" ) ).andExpect( status().isNotFound() );

    }

    /**
     * Tests deleting an existing CPT Code
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "billing", roles = { "BILLINGSTAFF" } )
    public void testRemoveCode () throws Exception {
        service.deleteAll();
        final List<CPTCode> l = service.findAll();
        Assert.assertEquals( 0, l.size() );

        final CPTCode newCpt = new CPTCode( 12345, "test", 100, true );
        final CPTCode error = new CPTCode( 56789, "test", 100, true );

        mvc.perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newCpt ) ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/cptcodes/12345" ) ).andExpect( status().isOk() );

        // error checking
        mvc.perform( put( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( error ) ) ).andExpect( status().isNotFound() );
        mvc.perform( delete( "/api/v1/cptcodes/54321" ) ).andExpect( status().isNotFound() );

        // Makes sure that the CPT code is obsolete
        assertFalse( service.findByCode( 12345 ).isCurrent() );

    }

}
