package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.User;

/**
 * Repository for interacting with Bill model. Method implementations generated
 * by Spring
 *
 * @author Auma A
 *
 */
public interface BillRepository extends JpaRepository<Bill, Long> {
    /**
     * Find all Bills for a given Patient
     *
     * @param patient
     *            User to find bills for
     * @return Matching Bills
     */
    public List<Bill> findByPatient ( final User patient );
    
//    /**
//     * Find all Bills with a given status
//     *
//     * @param status
//     *            bill status to check for
//     * @return Matching bills
//     */
//    public List<Bill> findByStatus ( final String status );
}
