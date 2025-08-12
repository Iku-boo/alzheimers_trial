package com.mihir.alzheimerscaregiver;


public class Contact {
    private String contactName;
    private String phoneNumber;

    /**
     * Constructor for Contact
     * @param contactName The name of the contact
     * @param phoneNumber The phone number of the contact
     */
    public Contact(String contactName, String phoneNumber) {
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get the contact's name
     * @return The contact name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Get the contact's phone number
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
