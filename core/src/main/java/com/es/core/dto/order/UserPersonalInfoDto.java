package com.es.core.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserPersonalInfoDto {
    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-]+$", message = "First name must contain only letters, spaces, or hyphens")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-]+$", message = "Last name must contain only letters, spaces, or hyphens")
    private String lastName;

    @NotBlank(message = "Address is required")
    private String deliveryAddress;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\+\\d+", message = "Phone number must start with '+' and contain only digits")
    @Size(min = 11, max = 15, message = "Phone number must be between 11 and 15 symbols")
    private String contactPhoneNo;

    @Size(max = 255, message = "So long additional information")
    private String additionalInformation;

    public UserPersonalInfoDto() {
    }

    public UserPersonalInfoDto(
            String firstName,
            String lastName,
            String deliveryAddress,
            String contactPhoneNo
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.deliveryAddress = deliveryAddress;
        this.contactPhoneNo = contactPhoneNo;
    }

    public UserPersonalInfoDto(
            String firstName,
            String lastName,
            String deliveryAddress,
            String contactPhoneNo,
            String additionalInformation
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.deliveryAddress = deliveryAddress;
        this.contactPhoneNo = contactPhoneNo;
        this.additionalInformation = additionalInformation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactPhoneNo() {
        return contactPhoneNo;
    }

    public void setContactPhoneNo(String contactPhoneNo) {
        this.contactPhoneNo = contactPhoneNo;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
