package com.omd.ws.forms;

@EntityForm(panels = {
        @Panel(name = "personalDetails", caption = "Personal Details")
})
public class EmployeeEntity {

    @FormField(columns = 6, section = "personalDetails")
    private String firstName;
    @FormField(columns = 6, section = "personalDetails")
    private String lastName;
    @FormField(section = "personalDetails")
    private String address;
    @FormField(columns = 6, section = "personalDetails")
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
