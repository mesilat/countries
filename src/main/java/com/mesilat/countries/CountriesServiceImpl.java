package com.mesilat.countries;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@ExportAsService({CountriesService.class})
@Named("com.mesilat:countries:service")
public class CountriesServiceImpl implements InitializingBean, DisposableBean, CountriesService {
    private static final String SEED_DATA_SERVICE_NAME = "com.mesilat:lov-placeholder:seedDataService";
    private static final Logger LOGGER = LoggerFactory.getLogger(CountriesServiceImpl.class);
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

    private final LocaleManager localeManager;
    private final Map<Locale,Map<String,String>> countries = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Object seedDataService = null;
        try {
            seedDataService = ContainerManager.getComponent(SEED_DATA_SERVICE_NAME);
        } catch(Throwable ex){
        }

        if (seedDataService == null){
            LOGGER.debug(String.format("No %s -- can't register with lov-placeholder", SEED_DATA_SERVICE_NAME));
        } else {
            registerReferenceData(seedDataService);
        }
    }
    @Override
    public void destroy() throws Exception {
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
    @Override
    public void registerReferenceData(Object seedDataService) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("/data/countries.txt")){
            String data = IOUtils.toString(in, StandardCharsets.UTF_8.name());
            registerReferenceData(seedDataService, "CTRY", "Countries", 1, data);
            LOGGER.debug("Registered data source 'countries'");
        } catch (Throwable ex) {
            LOGGER.warn("Failed to register data source 'countries'", ex);
        }
    }
    private void registerReferenceData(Object seedDataService, String code, String name, int type, String data) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = seedDataService.getClass().getMethod("putReferenceData", String.class, String.class, int.class, String.class);
        m.invoke(seedDataService, code, name, type, data);
    }

    @Inject
    public CountriesServiceImpl(
        final @ComponentImport LocaleManager localeManager
    ){
        this.localeManager = localeManager;
    }
}