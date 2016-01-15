package sac.juke;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.sse.SseFeature;

public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(JukeboxREST.class);
        return s;
    }
    
    @Override
    public Set<Object> getSingletons() {
	    Set<Object> singletons=new HashSet<>();
	    singletons.add(new SseFeature());
	    return singletons;
	}
    
    
}
