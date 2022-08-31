package edu.ncsu.csc.iTrust2.models;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAdapter;
import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAttributeConverter;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;

/**
 * Creates a bill object
 *
 * @author timothygachigi, Tom Chen
 *
 */
@Entity
public class Bill extends DomainObject {
    /**
     * Id of a given bill
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long          id;

    /**
     * List of CPT codes for office visits
     */
    @ManyToMany ( cascade = CascadeType.MERGE, fetch = FetchType.EAGER )
    private List<CPTCode> codes;

    /**
     * Date of bill creation
     */
    @NotNull
    @Basic
    @Convert ( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter ( ZonedDateTimeAdapter.class )
    private ZonedDateTime date;

    /**
     * Total Bill cost
     */
    private int           cost;

    /**
     * The bill status
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private BillStatus    status;

    @ManyToOne
    @JoinColumn ( name = "hcp_id", columnDefinition = "varchar(100)" )
    private User          patient;

    /**
     * Empty Constructor for hibernate
     */
    public Bill () {

    }

    /**
     * Constructor for Bills
     *
     * @author Tom Chen
     * @param codes
     * @param cost
     * @param status
     */
    public Bill ( final User patient, final List<CPTCode> codes, final int cost, final BillStatus status,
            final ZonedDateTime date ) {
        super();
        this.codes = codes;
        this.cost = cost;
        this.status = status;
        this.date = date;
        this.patient = patient;
    }

    @Override
    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public List<CPTCode> getCodes () {
        return codes;
    }

    public void setCodes ( final List<CPTCode> codes ) {
        this.codes = codes;
    }

    public ZonedDateTime getDate () {
        return date;
    }

    public void setDate ( final ZonedDateTime date ) {
        this.date = date;
    }

    public int getCost () {
        return cost;
    }

    public void setCost ( final int cost ) {
        this.cost = cost;
    }

    public BillStatus getStatus () {
        return status;
    }

    public void setStatus ( final BillStatus status ) {
        this.status = status;
    }

    public User getPatient () {
        return patient;
    }

    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    // /**
    // * Updates the bill information during payment
    // *
    // * @param billForm
    // */
    // public void updateBill ( final BillForm billForm ) {
    // cost = billForm.getBalance();
    // if ( cost > 0 ) {
    // status = BillStatus.PARTIALLY_PAID;
    // }
    // else if ( cost == 0 ) {
    // status = BillStatus.PAID;
    // }
    // }

    @Override
    public String toString () {
        return "Bill [id=" + id + ", codes=" + codes + ", date=" + date + ", cost=" + cost + ", status=" + status + "]";
    }

    @Override
    public int hashCode () {
        return Objects.hash( codes, cost, date, id, status );
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
        final Bill other = (Bill) obj;
        return Objects.equals( codes, other.codes ) && cost == other.cost && Objects.equals( date, other.date )
                && Objects.equals( id, other.id ) && status == other.status;
    }

}
