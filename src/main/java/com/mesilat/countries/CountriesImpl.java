package com.mesilat.countries;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.extras.decoder.v2.Version2LicenseDecoder;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@ExportAsService({Countries.class})
@Named("com.mesilat:countries:service")
public class CountriesImpl implements InitializingBean, DisposableBean, Countries {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountriesImpl.class);
    private static final List<String> EXCEPTIONS = Arrays.asList(new String[]{ /* No flags for those */
"BQ",
"BV",
"GF",
"GP",
"HM",
"IO",
"PM",
"RE",
"SJ",
"SX",
"UM" });

    private static CountriesImpl instance;

    private final LocaleManager localeManager;
    private final BootstrapManager bootstrapManager;
    private final Map<Locale,Map<String,String>> countries = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
        Object lic = bootstrapManager.getProperty("atlassian.license.message");
        Version2LicenseDecoder decoder = new Version2LicenseDecoder();
        if (decoder.canDecode(lic.toString())){
            Properties props = decoder.doDecode(lic.toString());
            props.store(System.out, "License details");
        }    }
    @Override
    public void destroy() throws Exception {
        instance = null;
    }
    @Override
    public String getName(String code) {
        User user = AuthenticatedUserThreadLocal.get();
        Locale locale = user == null? localeManager.getSiteDefaultLocale(): localeManager.getLocale(user);
        return getCountries(locale).get(code);
    }
    @Override
    public Map<String,String> find(String text) {
        User user = AuthenticatedUserThreadLocal.get();
        Locale locale = user == null? localeManager.getSiteDefaultLocale(): localeManager.getLocale(user);
        if (text == null){
            return getCountries(locale);
        } else {
            Map<String,String> map = new HashMap<>();
            for (Entry<String,String> e : getCountries(locale).entrySet()){
                if (e.getValue().toUpperCase().contains(text.toUpperCase())){
                    map.put(e.getKey(), e.getValue());
                }
            }
            return map;
        }
    }

    public static Countries getInstance(){
        return instance;
    }
    private Map<String,String> getCountries(Locale locale){
        if (!countries.containsKey(locale)){
            InputStream in = null;
            try {
                in = this.getClass().getClassLoader().getResourceAsStream("/data/" + locale.toString() + ".dat");
                if (in == null){
                    in = this.getClass().getClassLoader().getResourceAsStream("/data/" + locale.toString().substring(0,2) + ".dat");
                }
                if (in == null){
                    in = this.getClass().getClassLoader().getResourceAsStream("/data/aa.dat");
                }
                Map<String,String> map = new HashMap<>();
                try (InputStreamReader r = new InputStreamReader(in); BufferedReader br = new BufferedReader(r)) {
                    do {
                        String s = br.readLine();
                        if (s == null){
                            break;
                        } else if (s.isEmpty()) {
                            continue;
                        }
                        String[] ss = s.split(":");
                        if (!EXCEPTIONS.contains(ss[0])){
                            map.put(ss[0], ss[3]);
                        }
                    } while(true);
                }
                synchronized(countries) {
                    if (!countries.containsKey(locale)){
                        countries.put(locale, map);
                    }
                }
            } catch(IOException ex){ 
                LOGGER.warn("Failed to read countries for locale " + locale.toString(), ex);
            } finally {
                if (in != null){
                    try {
                        in.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
        return countries.get(locale);
    }

    @Inject
    public CountriesImpl(
        final @ComponentImport LocaleManager localeManager,
        final @ComponentImport BootstrapManager bootstrapManager
    ){
        this.localeManager = localeManager;
        this.bootstrapManager = bootstrapManager;
    }
}