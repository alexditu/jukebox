package sac.juke;

import javax.json.stream.JsonGenerator;

import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {
/*	public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }*/
	
	public RestApplication() {
//        s.add(JukeboxREST.class);
        
        packages("sac.juke");
        packages("com.fasterxml.jackson.jaxrs.json");
//        register(MyObjectMapperProvider.class);
        /* enable Server Sent Events: also need to enable async requests in web.xml */
        register(SseFeature.class); 
        property(JsonGenerator.PRETTY_PRINTING, true);
    }
	
	/*
	 * @Override
   public Set<Object> getSingletons() {
   Set<Object> singletons=new HashSet<>();
   singletons.add(new SseFeature());
   return singletons;
}
	 */
}
