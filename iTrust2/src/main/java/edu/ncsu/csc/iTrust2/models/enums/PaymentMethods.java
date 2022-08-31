package edu.ncsu.csc.iTrust2.models.enums;

/**
 * Enum for payment methods used to pay the bill by billing staff
 * @author timothygachigi
 *
 */
public enum PaymentMethods {
	/** paid */
	CASH ("Cash"),
	
	/** Bill has been paid by insurance*/
	INSURANCE ("Insurance"),
	
	/** Bill has been paid by check */
	CHECK ("Check"),
	
	/** Bill has been paid by credit card */
	CREDIT_CARD ("Credit Card");
	
    /**
     * Name of the method
     */
    private String name;
    
    /**
     * Retrieve the Name of the method
     *
     * @return Name of the method
     */
    public String getName () {
        return this.name;
    }

    /**
     * Constructor for Payment Methods.
     *
     * @param name
     *            Name of the Payment method to create
     */
    private PaymentMethods ( final String name ) {
        this.name = name;
    }
}
