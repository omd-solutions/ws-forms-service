package com.omd.ws.forms;

import java.util.List;

public class CountryValueProvider implements SelectValueProvider<Country> {

    @Override
    public List<Country> getValues() {
        return List.of(new Country("uk", "United Kingdom"),
                new Country("us", "United States"),
                new Country("france", "France"));
    }
}
