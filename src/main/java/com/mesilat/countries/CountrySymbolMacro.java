package com.mesilat.countries;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

@Scanned
public class CountrySymbolMacro implements Macro {
    private final TemplateRenderer renderer;

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }
    @Override
    public OutputType getOutputType() {
        return OutputType.INLINE;
    }
    @Override
    public String execute(Map params, String body, ConversionContext conversionContext) throws MacroExecutionException {
        if (!params.containsKey("code")){
            return "";
        } else {
            Map<String,Object> map = new HashMap<>();
            map.put("code", params.get("code"));
            map.put("name", CountriesImpl.getInstance().getName(params.get("code").toString()));
            return renderFromSoy("Mesilat.Countries.Templates.countrySymbol.soy", map);
        }
    }

    @Inject
    public CountrySymbolMacro(final @ComponentImport TemplateRenderer renderer){
        this.renderer = renderer;
    }

    public String renderFromSoy(String soyTemplate, Map soyContext) {
        StringBuilder output = new StringBuilder();
        renderer.renderTo(output, "com.mesilat.countries:resources", soyTemplate, soyContext);
        return output.toString();
    }
}