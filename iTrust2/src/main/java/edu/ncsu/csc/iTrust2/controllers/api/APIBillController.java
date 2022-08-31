package edu.ncsu.csc.iTrust2.controllers.api;

import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with Bills. Exposes functionality to add,
 * edit, fetch, and delete drug.
 *
 * @author Auma A
 * @author Timothy G
 *
 */
@SuppressWarnings ( { "rawtypes", "unchecked" } )
@RestController
public class APIBillController extends APIController {
    /** Drug service */
    @Autowired
    private BillService service;

    /** LoggerUtil */
    @Autowired
    private LoggerUtil  loggerUtil;

    /** User service */
    @Autowired
    private UserService userService;

    /**
     * Returns a list of bills for the given patient.
     *
     * @param name
     *            the user name of the patient
     * @return the requested list of bills
     */

    // @GetMapping ( BASE_PATH + "/bills/patients/{patient}" )
    // @PreAuthorize ( "hasAnyRole('ROLE_BILLINGSTAFF', 'ROLE_PATIENT')" )
    // public List<Bill> getBillsByPatient ( @PathVariable ( "patient" ) final
    // String name ) {
    // final User patient = userService.findByName( name );
    // if ( patient == null ) {
    // return null;
    // }
    // final List<Bill> bills = service.findByPatient( patient );
    // loggerUtil.log( TransactionType.BILL_VIEW_PATIENT,
    // LoggerUtil.currentUser(),
    // "User viewed a specific patient's bills" );
    // return bills;
    // }

    /**
     * Retrieves the Bill specified by the ID provided
     *
     * @param id
     *            The (numeric) ID of the bill desired
     * @return The bill corresponding to the ID provided or HttpStatus.NOT_FOUND
     *         if no such bill could be found
     */
    @GetMapping ( BASE_PATH + "/bills/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILLINGSTAFF', 'ROLE_PATIENT')" )
    public ResponseEntity getBill ( @PathVariable ( "id" ) final Long id ) {
        final Bill bill = service.findById( id );
        if ( bill != null ) {
            loggerUtil.log( TransactionType.BILL_GET, LoggerUtil.currentUser(), "User viewed a specific bill" );

            final ZonedDateTime currentTime = ZonedDateTime.now();
            final ZonedDateTime apptTime = bill.getDate();
            final Period p = Period.between( currentTime.toLocalDate(), apptTime.toLocalDate() );
            if ( p.getDays() > 60 && bill.getStatus() != BillStatus.DELINQUENT && bill.getCost() != 0 ) {
                bill.setStatus( BillStatus.DELINQUENT );
                service.save( bill );
            }
        }
        return null == bill ? new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( bill, HttpStatus.OK );

    }

    /**
     * Adds a new bill to the system. Requires HCP permissions.
     *
     * @param form
     *            details of the new bill
     * @return the created bill
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    @PostMapping ( BASE_PATH + "/bills" )
    public ResponseEntity addBill ( @RequestBody final BillForm form ) {
        try {
            final Bill bill = service.build( form );
            service.save( bill );
            loggerUtil.log( TransactionType.BILL_CREATE, LoggerUtil.currentUser(),
                    "Created bill with id " + bill.getId() );
            return new ResponseEntity( bill, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.BILL_CREATE, LoggerUtil.currentUser(), "Failed to create bill" );
            return new ResponseEntity( errorResponse( "Could not save the bill: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    // /**
    // * Returns a list of bills with the given status.
    // *
    // *
    // * @return the requested list of bills
    // */
    // @PreAuthorize ( "hasRole('ROLE_BILLINGSTAFF')" )
    // @GetMapping ( BASE_PATH + "/bill/{}" )
    // public ResponseEntity getBillsByStatus ( @PathVariable final String
    // status ) {
    // final List<Bill> bill1 = service.findByStatus( status );
    // return null;
    //
    // }

    /**
     * Returns a list of bills in the system
     *
     *
     * @return all saved bills
     */
    @GetMapping ( BASE_PATH + "/bills" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILLINGSTAFF', 'ROLE_PATIENT')" )
    public List<Bill> getBills () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        List<Bill> toReturn = null;
        if ( self.getRoles().contains( Role.ROLE_BILLINGSTAFF ) ) {
            // Returns all bills in system
            loggerUtil.log( TransactionType.BILL_VIEW, LoggerUtil.currentUser(),
                    "Billing Staff member viewed a list of all bills" );
            toReturn = service.findAll();
        }
        else {
            // Returns patient's assigned bills
            loggerUtil.log( TransactionType.BILL_VIEW_PATIENT, LoggerUtil.currentUser(),
                    "Patient Viwed a list of their bills" );
            toReturn = service.findByPatient( self );
        }
        boolean edits = false;
        for ( final Bill bill : toReturn ) {
            final ZonedDateTime currentTime = ZonedDateTime.now();
            final ZonedDateTime apptTime = bill.getDate();
            final long p = -1 * ChronoUnit.DAYS.between( currentTime.toLocalDate(), apptTime.toLocalDate() );
            System.out.println( p );
            if ( p > 60 && bill.getStatus() != BillStatus.DELINQUENT && bill.getCost() != 0 ) {
                bill.setStatus( BillStatus.DELINQUENT );
                service.save( bill );
                edits = true;
            }
        }

        if ( !edits ) {
            return toReturn;
        }
        return getBills();

    }

    /**
     * Edits an existing bill in the system. Matches bills by ids
     *
     * @param form
     *            the form containing the details of the new bill
     * @return the edited bill
     */
    @PutMapping ( BASE_PATH + "/bills" )
    @PreAuthorize ( "hasRole('ROLE_BILLINGSTAFF')" )
    public ResponseEntity updateBill ( @RequestBody final Bill bill ) {
        try {
            System.out.println( "INSIDE UPDATE BILL" );
            final Bill found = service.findById( bill.getId() );
            if ( found == null ) {
                loggerUtil.log( TransactionType.BILL_EDIT, LoggerUtil.currentUser(),
                        "No Bill found with id " + bill.getId() );
                return new ResponseEntity( errorResponse( "No Bill found with id " + bill.getId() ),
                        HttpStatus.NOT_FOUND );
            }

            found.setStatus( bill.getStatus() );
            found.setCost( bill.getCost() );
            System.out.println( "Found Bills:" + found.toString() );
            service.save( found );
            loggerUtil.log( TransactionType.BILL_EDIT, LoggerUtil.currentUser(),
                    "Billing Staff member updated the bill" );

            return new ResponseEntity( bill, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Failed to update bill: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

}
