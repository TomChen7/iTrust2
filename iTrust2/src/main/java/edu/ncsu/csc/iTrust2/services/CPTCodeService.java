package edu.ncsu.csc.iTrust2.services;

import java.util.List; 

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.repositories.CPTCodeRepository;

/**
 * CPTCodeService that extends from Service to allow for easy access to a
 * repsotiory of CPTCodes
 *
 * @author Simon Griggs, Josh Mason, Tom Chen
 */
@Component
@Transactional
public class CPTCodeService extends Service<CPTCode, Long> {
    /**
     * A repository of CPTCodes
     */
    @Autowired
    private CPTCodeRepository repository;

    /**
     * Gets the repository
     */
    @Override
    protected JpaRepository<CPTCode, Long> getRepository () {
        return repository;
    }

    /**
     * Finds the code if it is current in the repository
     *
     * @param code
     *            code to search for
     * @return the code if found
     */
    public CPTCode findByCode ( final int code ) {
        return repository.findByCode( code );
    }

    public List<CPTCode> getCodes () {
        return repository.findAll();
    }
}
