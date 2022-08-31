package edu.ncsu.csc.iTrust2.services;

import java.time.ZonedDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.repositories.BillRepository;

/**
 * Service class for interacting with Bill model, performing CRUD tasks with
 * database and building a persistence object from a Form.
 *
 * @author Auma A
 *
 */
@Component
@Transactional
public class BillService extends Service<Bill, Long> {
    /** Repository for CRUD operations */
    @Autowired
    private BillRepository    repository;

    /** User service */
    @Autowired
    private UserService<User> userService;

    @Override
    protected JpaRepository<Bill, Long> getRepository () {
        return repository;
    }

    /**
     * Find all Bills for a given Patient
     *
     * @param patient
     *            User to find bills for
     * @return Matching Bills
     */
    public List<Bill> findByPatient ( final User patient ) {
        return repository.findByPatient( patient );
    }

    /**
     * Builds a Bill from the deserialised billForm
     *
     * @param form
     *            Form to build a Bill from
     * @return Build bill
     */
    public Bill build ( final BillForm form ) {
        final Bill bill = new Bill();
        bill.setCodes( form.getCodes() );
        final ZonedDateTime billDate = ZonedDateTime.parse( form.getPaymentDate() );
        bill.setDate( billDate );
        bill.setPatient( userService.findByName( form.getPatient() ) );
        if ( form.getId() != null ) {
            bill.setId( form.getId() );
        }
        bill.setCost( form.getBalance() );
        final BillStatus status = BillStatus.valueOf( form.getStatus().toUpperCase() );
        bill.setStatus( status );

        return bill;
    }

}
