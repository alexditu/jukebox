package sac.juke;

import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Scheduler;

import sac.juke.model.Manager;
import sac.juke.model.User;
import sac.juke.timer.SongScheduler;
import sac.juke.util.Constants;

@Path("rest")
public class JukeboxREST {
	
	private static final Logger log = LogManager.getLogger(JukeboxREST.class);
	
	@Context
	ServletContext servletContext;
	
    public JukeboxREST() {
        super();
    }
    
    @POST
    @Path("getTime")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public JsonObject getTime() {
    	Scheduler sched = (Scheduler) servletContext.getAttribute(Constants.SCHEDULER);
    	int remainingTime = SongScheduler.getElapsedTime(sched);
    	int duration = Manager.songs.getSong(Manager.currentSong).getDuration();
    	int seekTime = (duration - remainingTime);
    	
    	JsonObjectBuilder ret = Json.createObjectBuilder();
    	ret.add("seekTime", seekTime);
    	log.debug("Seek time is: " + seekTime);
    	
    	return ret.build();
    }
    
    @POST
    @Path("addUser")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addUser(@FormParam("username") String username) {
    	log.debug("username: " + username);
    	ArrayList<User> users = (ArrayList<User>) servletContext.getAttribute(Constants.USERS);
    	
    	users.add(new User(username));
    	return "added user: " + username;
    }
    
    @GET
    @Path("getUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public JsonArray getUsers(@Context HttpServletResponse response) { 
    	ArrayList<User> users = (ArrayList<User>) servletContext.getAttribute(Constants.USERS);
    	log.debug("users: " + users.toString());
    	
    	ArrayList<String> ret = new ArrayList<>();
    	JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
    	
    	for (User i : users) {
    		jsonBuilder.add(i.toString());
    	}
    	
    	response.addHeader("Access-Control-Allow-Origin", "*");
    	return jsonBuilder.build();
    }
    
    @GET
    @Path("test1")
    @Produces("text/plain")
    public String test(@QueryParam("p") String param) {
    	log.debug("Got param: " + param);
    	log.info("Got param: " + param);
    	log.warn("Got param: " + param);
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









