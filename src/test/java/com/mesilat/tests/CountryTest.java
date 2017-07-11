package com.mesilat.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import junit.framework.TestCase;

public class CountryTest extends TestCase {
    
    public CountryTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFlags() throws IOException {
        System.out.println("There are no flags for the following country codes:");
        try (
            FileInputStream in = new FileInputStream("src/main/resources/data/aa.dat");
            InputStreamReader r = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(r)
        ){
            do {
                String s = br.readLine();
                if (s == null){
                    break;
                } else if (s.isEmpty()) {
                    continue;
                }
                String[] ss = s.split(":");
                File f = new File("src/main/resources/images/" + ss[0] + ".png");
                if (!f.exists())
                    System.out.println(ss[0]);
            } while(true);
        }
    }
}
