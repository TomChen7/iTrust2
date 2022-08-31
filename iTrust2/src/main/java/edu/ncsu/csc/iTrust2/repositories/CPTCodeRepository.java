package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * A repository for the CPTCode to allow for access
 *
 * @author Simon Griggs, Josh Mason, Tom Chen
 *
 */
public interface CPTCodeRepository extends JpaRepository<CPTCode, Long> {
    /**
     * Finds a code in the repository by the code number
     *
     * @param code
     *            code number to search for
     * @return the found cpt code, if it is current
     */
    public CPTCode findByCode ( int code );

}
