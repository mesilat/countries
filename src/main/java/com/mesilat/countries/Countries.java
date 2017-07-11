package com.mesilat.countries;

import java.util.Map;

public interface Countries {
    String getName(String code);
    Map<String,String> find(String text);
}