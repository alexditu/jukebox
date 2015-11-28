package sac.juke;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    
    @POST
    @Path("test4")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response test4(JsonObject param) {
    	System.out.println("Got json: " + param);
    	return Response.ok().entity(param).build();
    }
    
    @POST
    @Path("test3")
    @Produces({MediaType.APPLICATION_JSON})
    public Response test3(@FormParam("p") String param) {
    	JsonObject value = Json.createObjectBuilder()
    		     .add("firstName", "John")
    		     .add("lastName", "Smith")
    		     .add("age", 25)
    		     .add("address", Json.createObjectBuilder()
    		         .add("streetAddress", "21 2nd Street")
    		         .add("city", "New York")
    		         .add("state", "NY")
    		         .add("postalCode", "10021"))
    		     .add("phoneNumber", Json.createArrayBuilder()
    		         .add(Json.createObjectBuilder()
    		             .add("type", "home")
    		             .add("number", "212 555-1234"))
    		         .add(Json.createObjectBuilder()
    		             .add("type", "fax")
    		             .add("number", "646 555-4567")))
    		     .build();
        
        return Response.ok().entity(value).build();
    }
}









