package com.omd.ws.forms;

@EntityForm(panels = {
        @Panel(name = "personalDetails", caption = "Personal Details")
})
public class EmployeeEntity {

    @FormField(columns = 6, panel = "personalDetails")
    private String firstName;
    @FormField(columns = 6, panel = "personalDetails")
    private String lastName;
    @FormField(panel = "personalDetails")
    private String address;
    @FormField(columns = 6, panel = "personalDetails")
    @Text(validationRegex = "/0[0-9]{10}/", validationMessage = "Must be 11 digits and start with a 0")
    private String phoneNumber;
    @FormField(columns = 6, panel = "personalDetails")
    @Text(masked = true, validationRegex = "^.*(?=.{8,})(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!#$%&? \"]).*$", validationMessage = "Must be at least 8 characters with at least 1 number, capital and special char")
    private String password;
    @FormField(columns = 6, panel = "personalDetails")
    @Select(valueProvider = CountryValueProvider.class)
    private Country country;


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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
