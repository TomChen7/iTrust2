package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;
// import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with CPT codes. Exposes functionality to
 * add, edit, and fetch CPT codes.
 *
 * @author jlmason6, sdgriggs, tchen25
 */
// TODO possibly a CPTCodeForm class similar to
// DrugForm, and transaction type
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APICPTCodeController extends APIController {

    /** CPT service */
    @Autowired
    private CPTCodeService service;

    /** LoggerUtil */
    @Autowired
    private LoggerUtil     loggerUtil;

    /**
     * Adds a new CPT code to the system. Requires Billing staff permissions.
     * Returns an error message if something goes wrong.
     *
     * @param code
     *            the cpt code
     * @return the created code
     */
    @PreAuthorize ( "hasRole('ROLE_BILLINGSTAFF')" )
    @PostMapping ( BASE_PATH + "/cptcodes" )
    public ResponseEntity addCPTCode ( @RequestBody final CPTCode code ) {
        try {
            final List<CPTCode> allCodes = service.findAll();
            // Make sure code does not conflict with existing codes
            for ( final CPTCode c : allCodes ) {
                if ( c.getCode() == code.getCode() && c.isCurrent() ) {
                    loggerUtil.log( TransactionType.CPTCODE_CREATE, LoggerUtil.currentUser(),
                            "Conflict: CPT code with code " + code.getCode() + "already exists" );
                    return new ResponseEntity( errorResponse( "CPT Code " + code.getCode() + " already exists" ),
                            HttpStatus.CONFLICT );
                }
            }
            service.save( code );
            loggerUtil.log( TransactionType.CPTCODE_CREATE, LoggerUtil.currentUser(),
                    "CPT Code " + code.getCode() + " created" );
            return new ResponseEntity( code, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.CPTCODE_CREATE, LoggerUtil.currentUser(), "Failed to create CPT code" );
            return new ResponseEntity( errorResponse( "Could not add CPT Code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits a CPT Code in the system. Code number cannot be edited. Requires
     * billing staff permissions.
     *
     * @param form
     *            the edited CPT code
     * @return the edited CPT code or an error message
     */
    @PreAuthorize ( "hasRole('ROLE_BILLINGSTAFF')" )
    @PutMapping ( BASE_PATH + "/cptcodes" )
    public ResponseEntity editCPTCode ( @RequestBody final CPTCode code ) {
        try {
            // Check for existing code in database
            CPTCode savedCode = null;
            final List<CPTCode> allCodes = service.findAll();
            // Make sure code does not conflict with existing codes
            for ( final CPTCode c : allCodes ) {
                if ( c.getCode() == code.getCode() && c.isCurrent() ) {
                    savedCode = c;
                    break;
                }
            }
            if ( savedCode == null ) {
                return new ResponseEntity( errorResponse( "No CPT code found with code " + code.getCode() ),
                        HttpStatus.NOT_FOUND );
            }

            System.out.println( "Comparing codes: " + savedCode.getId() + " " + code.getId() );
            // Make current code not current
            savedCode.makeObsolete();
            service.save( savedCode );

            // Make a new code
            final CPTCode newCode = new CPTCode( code.getCode(), code.getDescription(), code.getValue(), true );
            // Add updated code
            service.save( newCode );

            loggerUtil.log( TransactionType.CPTCODE_EDIT, LoggerUtil.currentUser(),
                    "Code with id " + code.getId() + " edited" );
            return new ResponseEntity( code, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.CPTCODE_EDIT, LoggerUtil.currentUser(), "Failed to edit CPT code" );
            return new ResponseEntity( errorResponse( "Could not update CPT Code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Makes the CPT code with the code number matching the given number
     * obsolete. Requires billing staff permissions.
     *
     * @param number
     *            the number of the CPT code to delete
     * @return the number of the obselete CPT Code
     */
    @PreAuthorize ( "hasRole('ROLE_BILLINGSTAFF')" )
    @DeleteMapping ( BASE_PATH + "/cptcodes/{number}" )
    public ResponseEntity deleteCPTCode ( @PathVariable ( "number" ) final int number ) {
        try {
            CPTCode savedCode = null;
            final List<CPTCode> allCodes = service.findAll();
            for ( final CPTCode c : allCodes ) {
                if ( c.getCode() == number && c.isCurrent() ) {
                    savedCode = c;
                    break;
                }
            }
            if ( savedCode == null ) {
                loggerUtil.log( TransactionType.CPTCODE_DELETE, LoggerUtil.currentUser(),
                        "Could not find CPT code with code number " + number );
                return new ResponseEntity( errorResponse( "No CPT code found with code " + number ),
                        HttpStatus.NOT_FOUND );
            }
            savedCode.makeObsolete();
            service.save( savedCode );
            loggerUtil.log( TransactionType.CPTCODE_DELETE, LoggerUtil.currentUser(),
                    "Deleted CPT code with number " + savedCode.getCode() );
            return new ResponseEntity( number, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.CPTCODE_DELETE, LoggerUtil.currentUser(), "Failed to delete code" );
            return new ResponseEntity( errorResponse( "Could not delete CPT code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    // Add default codes
    private final ArrayList<CPTCode> timeCodes = new ArrayList<CPTCode>();

    private void createDefaultCodes () {
        timeCodes.add( new CPTCode( 99202, "15-29 minutes", 75, true ) );
        timeCodes.add( new CPTCode( 99203, "30-44 minutes", 150, true ) );
        timeCodes.add( new CPTCode( 99204, "45-59 minutes", 200, true ) );
        timeCodes.add( new CPTCode( 99205, "60-74 minutes", 250, true ) );
        timeCodes.add( new CPTCode( 99212, "10-19 minutes", 75, true ) );
        timeCodes.add( new CPTCode( 99213, "20-29 minutes", 100, true ) );
        timeCodes.add( new CPTCode( 99214, "30-39 minutes", 125, true ) );
        timeCodes.add( new CPTCode( 99215, "40-54 minutes", 175, true ) );
    }

    /**
     * Gets a list of all current CPT Codes in the system.
     *
     * @return a list of current CPT Codes
     */
    @GetMapping ( BASE_PATH + "/cptcodes" )
    public List<CPTCode> getCodes () {
        // loggerUtil.log( TransactionType.CPTCODE_VIEW,
        // LoggerUtil.currentUser(), "Fetched list of CPT codes" );
        final ArrayList<CPTCode> current = new ArrayList<CPTCode>();
        createDefaultCodes();
        for ( final CPTCode c : timeCodes ) {
            addCPTCode( c );
        }
        final List<CPTCode> allCodes = service.findAll();
        for ( final CPTCode c : allCodes ) {
            if ( c.isCurrent() ) {
                current.add( c );
            }
        }
        return current;
    }

    /**
     * Gets a CPT Code if current in the system.
     *
     * @param number
     *            code number
     *
     * @return a current CPT Code
     */
    @GetMapping ( BASE_PATH + "/cptcodes/{number}" )
    public ResponseEntity getCode ( @PathVariable ( "number" ) final int number ) {
        CPTCode savedCode = null;
        final List<CPTCode> allCodes = service.findAll();
        for ( final CPTCode c : allCodes ) {
            if ( c.getCode() == number && c.isCurrent() ) {
                savedCode = c;
                break;
            }
        }
        if ( savedCode == null ) {
            loggerUtil.log( TransactionType.CPTCODE_GET, LoggerUtil.currentUser(),
                    "Could not find CPT code with code number " + number );
            return new ResponseEntity( errorResponse( "No current CPT code found with code " + number ),
                    HttpStatus.NOT_FOUND );
        }
        loggerUtil.log( TransactionType.CPTCODE_GET, LoggerUtil.currentUser(), "Fetched CPT code" );
        return new ResponseEntity( savedCode, HttpStatus.OK );
    }

}
