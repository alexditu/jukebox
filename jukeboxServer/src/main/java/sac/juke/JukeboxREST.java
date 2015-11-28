package sac.juke;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("rest")
public class JukeboxREST {
    public JukeboxREST() {
        super();
    }
    
    @GET
    @Path("test1")
    @Produces("text/plain")
    public String test(@QueryParam("p") String param) {
        System.out.println("Got param: " + param);
        return param;
    }
    
    @POST
    @Path("test2")
    @Produces("text/plain")
    public String test2(@FormParam("p") String param) {
        System.out.println("Got param: " + param);
        return param;
    }
}









