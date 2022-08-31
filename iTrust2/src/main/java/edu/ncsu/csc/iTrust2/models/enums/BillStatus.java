package edu.ncsu.csc.iTrust2.models.enums;

/**
 * Enumeration representing the bill status.
 *
 * @author timothygachigi, Tom Chen
 *
 */
public enum BillStatus {

    /** Bill has been fully paid */
    PAID ( "Paid" ),

    /** Bill has not been paid at all */
    UNPAID ( "Unpaid" ),

    /** Bill has been partially paid */
    PARTIALLY_PAID ( "Partially Paid" ),

    /** Bill is late */
    DELINQUENT ( "Delinquent" );

    /**
     * Name of the Status
     */
    private String name;

    /**
     * Retrieve the Name of the Status
     *
     * @return Name of the Status
     */
    public String getName () {
        return this.name;
    }

    /**
     * Constructor for Bill Status.
     *
     * @param name
     *            Name of the Bill to create
     */
    private BillStatus ( final String name ) {
        this.name = name;
    }

}
