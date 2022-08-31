package edu.ncsu.csc.iTrust2.forms;

import java.util.Objects;

import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * Intermediate form for adding or editing CPTCodes. Used to create and
 * serialize CPTCodes.
 *
 * @author Auma Asiyo
 *
 */
public class CPTCodeForm {
    /** Id of the CPT Code */
    private Long id;

    /** Code, 5 digits long */
    int          code;

    /** String Description of what the CPT Code is about */
    String       description;

    /** Value of the Code, in dollars */
    int          value;

    /** Flag to see if the code is current */
    boolean      isCurrent;

    /**
     * Empty constructor for GSON
     */
    public CPTCodeForm () {

    }

    /**
     * Construct a form off an existing ICDCode object
     *
     * @param code
     *            The object to fill this form with
     */
    public CPTCodeForm ( final CPTCode code ) {
        setCode( code.getCode() );
        setDescription( code.getDescription() );
        setId( code.getId() );
        setValue( code.getValue() );
        setIsCurrent( code.isCurrent() );
    }

    public long getId () {
        return id;
    }

    public void setId ( final long l ) {
        id = l;
    }

    public int getCode () {
        return code;
    }

    public void setCode ( final int code ) {
        this.code = code;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription ( final String description ) {
        this.description = description;
    }

    public int getValue () {
        return value;
    }

    public void setValue ( final int value ) {
        this.value = value;
    }

    public boolean getIsCurrent () {
        return isCurrent;
    }

    public void setIsCurrent ( final boolean isCurrent ) {
        this.isCurrent = isCurrent;
    }

    /**
     * Makes a code obsolete, changes the isCurrent to false
     */
    public void makeObsolete () {
        this.isCurrent = false;
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
        final CPTCodeForm other = (CPTCodeForm) obj;
        return code == other.code && Objects.equals( description, other.description ) && Objects.equals( id, other.id )
                && isCurrent == other.isCurrent && value == other.value;
    }

}
