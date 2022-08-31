package edu.ncsu.csc.iTrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Controller to manage basic abilities for Billing Staff roles
 *
 * @author Auma Asiyo
 *
 */
@Controller
public class BillingStaffController {
    /**
     * Returns the Landing screen for the BillingStaff
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "/billingstaff/index" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILLINGSTAFF')" )
    public String index ( final Model model ) {
        return Role.ROLE_BILLINGSTAFF.getLanding();
    }

    /**
     * Manage CPT Codes
     *
     * @param model
     *            data for front end
     * @return mapping
     */
    @RequestMapping ( value = "/billingstaff/manageCPTCodes" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILLINGSTAFF')" )
    public String addCode ( final Model model ) {
        return "/billingstaff/manageCPTCodes";
    }

    /**
     * Create a page for the billing staff to view bills
     *
     * @param model
     *            data for front end
     * @return The page for the patient to view bills
     */
    @RequestMapping ( value = "billingstaff/bills/viewBills" )
    @PreAuthorize ( "hasRole('ROLE_BILLINGSTAFF')" )
    public String viewBills ( final Model model ) {
        return "/billingstaff/bills/viewBills";
    }

    /**
     * Create a page for the billing staff to mark bill status
     *
     * @param model
     *            data for front end
     * @return The page for the patient to view bills
     */
    @RequestMapping ( value = "billingstaff/bills/payBills" )
    @PreAuthorize ( "hasRole('ROLE_BILLINGSTAFF')" )
    public String payBills ( final Model model ) {
        return "/billingstaff/bills/payBills";
    }

}
