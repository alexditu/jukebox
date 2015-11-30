package sac.juke;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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

import sac.juke.exceptions.UserExistsException;
import sac.juke.model.Song;
import sac.juke.model.User;
import sac.juke.model.Users;
import sac.juke.util.Utils;

@Path("rest")
public class JukeboxREST {
	
	private static final Logger log = LogManager.getLogger(JukeboxREST.class);
	
	@Context
	ServletContext servletContext;
	
	@Context
	HttpServletResponse httpServletResponse;
	
	@Context
	HttpServletRequest httpServletRequest;
	
    public JukeboxREST() {
        super();
    }
    
    @POST
    @Path("getSongs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public JsonArray getSongs(@FormParam("username") String username) {
    	return JukeboxLogic.getSongsForUser(servletContext, username);
    }
    
    @POST
    @Path("getSong")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public JsonObject getSong() {
    	Song song = Utils.getCurrentSong(servletContext);
    	int seekTime = JukeboxLogic.getSeekTime(servletContext);
    	JsonObjectBuilder json = song.toJsonBuilder();
    	json.add("seekTime", seekTime);
    	
    	log.debug("getSong: " + json);
    	return json.build();
    }
    
    @POST
    @Path("getTime")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public JsonObject getTime() {

    	int seekTime = JukeboxLogic.getSeekTime(servletContext);
    	
    	JsonObjectBuilder ret = Json.createObjectBuilder();
    	ret.add("seekTime", seekTime);
    	log.debug("getTime: " + seekTime);
    	
    	return ret.build();
    }
    
    @POST
    @Path("addUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public JsonObject addUser(@FormParam("username") String username) {
    	log.debug("username: " + username);
    	Users users = Utils.getUsers(servletContext);
    	
    	JsonObjectBuilder b = Json.createObjectBuilder();
    	try {
    		users.add(username, new User(username));
    		b.add("status", "OK");
    	} catch (UserExistsException e) {
    		log.debug("Users exists");
    		b.add("status", "EXISTS");
    	}
    	return b.build(); 
    }
    
    /**
     * 
     * @return {"users":[{"username":"Bob","votedSongs":[]},
     * 					 {"username":"Alice","votedSongs":[]}, ...}]}
     */
    @POST
    @Path("getUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public JsonObject getUsers() { 
    	Users users = Utils.getUsers(servletContext);
    	log.debug("users: " + users.toString());    	
//    	response.addHeader("Access-Control-Allow-Origin", "*");
    	return users.toJson();
    }
    
    
    
    /* example methods for various HTTP methods/params/return values */
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









