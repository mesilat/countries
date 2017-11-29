package com.mesilat.countries;

import java.util.Map;

public interface CountriesService {
    String getName(String code);
    Map<String,String> find(String text);
    void registerReferenceData(Object seedDataService);
}