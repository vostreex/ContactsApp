// IDuplicateContactService.aidl
package com.example.contactsapp;

// Declare any non-default types here with import statements

interface IDuplicateContactService {
    /**
     * 0 Success
     * 1 Not Found
     * 2 Error
     */
    int removeDuplicateContacts();
}