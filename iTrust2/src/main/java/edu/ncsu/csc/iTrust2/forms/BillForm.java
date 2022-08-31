package edu.ncsu.csc.iTrust2.forms;

import java.util.List;

import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;

public class BillForm {
    /** serializing id */
    private static final long serialVersionUID = 1L;

    private Long              id;
    private String            paymentMethod;
    private int               balance;
    private String            paymentDate;
    private String            status;
    private String            patient;
    private List<CPTCode>     codes;

    public BillForm () {

    }

    /**
     * Constructs a new form with information from the given bill.
     *
     * @param drug
     *            the bill object
     */
    public BillForm ( final Bill bill ) {
        setId( bill.getId() );
        setBalance( bill.getCost() );
        setPaymentDate( bill.getDate().toString() );
        setStatus( bill.getStatus().toString() );
        setPatient( bill.getPatient().getUsername() );
        setPaymentMethod( "" );
        setCodes( bill.getCodes() );
    }

    public String getPaymentMethod () {
        return paymentMethod;
    }

    public void setPaymentMethod ( final String paymentMethod ) {
        this.paymentMethod = paymentMethod;
    }

    public int getBalance () {
        return balance;
    }

    public String getStatus () {
        return status;
    }

    public void setBalance ( final int balance ) {
        this.balance = balance;
    }

    public String getPaymentDate () {
        return paymentDate;
    }

    public void setPaymentDate ( final String paymentDate ) {
        this.paymentDate = paymentDate;
    }

    public Long getId () {
        return id;
    }

    public void setId ( final long id ) {
        this.id = id;
    }

    public void setStatus ( final String status ) {
        this.status = status;
    }

    public String getPatient () {
        return patient;
    }

    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    public List<CPTCode> getCodes () {
        return codes;
    }

    public void setCodes ( final List<CPTCode> codes ) {
        this.codes = codes;
    }

}
