package edu.ncsu.csc.iTrust2.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;

/**
 * CPTCode is a class that maintains a CPT Code, its value, a description, and
 * its current.
 *
 * @author Simon Griggs, Josh Mason, Tom Chen
 *
 */
@Entity
public class CPTCode extends DomainObject {
    /**
     * Id of the CPT Code
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long id;

    /**
     * Code, 5 digits long
     */
    int          code;

    /**
     * String Description of what the CPT Code is about
     */
    String       description;

    /**
     * Value of the Code, in dollars
     */
    int          value;

    /**
     * Flag to see if the code is current
     */
    boolean      isCurrent;

    /**
     * Empty constructor for Hibernate
     */
    public CPTCode () {

    }

    /**
     * Constructor for the CPT Object
     *
     * @author Tom Chen
     * @param id
     *            is the id of a CPT code in SQL
     * @param code
     *            is the code a HCP or billing member will see
     * @param description
     *            is what the Code is associated with
     * @param value
     *            is the dollar value of the CPT code
     * @param isCurrent
     *            is a boolean signifying is it is the most current code.
     */
    public CPTCode ( final int code, final String description, final int value, final boolean isCurrent ) {
        super();
        this.code = code;
        this.description = description;
        this.value = value;
        this.isCurrent = isCurrent;
    }

    /**
     * Construct from a form
     *
     * @param form
     *            The form that validates and sanitizes input
     */
    public CPTCode ( final CPTCodeForm form ) {
        setCode( form.getCode() );
        setDescription( form.getDescription() );
        setValue( form.getValue() );
        setId( form.getId() );
        setIsCurrent( form.getIsCurrent() );
    }

    public void setCode ( final int code ) {
        this.code = code;
    }

    public void setDescription ( final String description ) {
        this.description = description;
    }

    public void setValue ( final int value ) {
        this.value = value;
    }

    public void setIsCurrent ( final boolean isCurrent ) {
        this.isCurrent = isCurrent;
    }

    /**
     * Returns if the code is Current
     *
     * @return whether the code is current
     */
    public boolean isCurrent () {
        return isCurrent;
    }

    /**
     * Makes a code obsolete, changes the isCurrent to false
     */
    public void makeObsolete () {
        this.isCurrent = false;
    }

    /**
     * Gets the CPT Code Value
     *
     * @return CPT Code
     */
    public int getCode () {
        return code;
    }

    /**
     * Gets the description of the Code
     *
     * @return the description
     */
    public String getDescription () {
        return description;
    }

    /**
     * Gets the value of the CPT Code
     *
     * @return the Cost of a CPT Code
     */
    public int getValue () {
        return value;
    }

    // /**
    // * Gets the ID of the code
    // *
    // * @return ID of the code
    // */
    // @Override
    // public Serializable getId () {
    // return id;
    // }
    /**
     * Gets the ID of the code
     *
     * @return ID of the code
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the code
     *
     * @param ID
     *            of the code
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    @Override
    public int hashCode () {
        return Objects.hash( code, description, id, isCurrent, value );
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final CPTCode other = (CPTCode) obj;
        return code == other.code && Objects.equals( description, other.description ) && Objects.equals( id, other.id )
                && isCurrent == other.isCurrent && value == other.value;
    }

}
