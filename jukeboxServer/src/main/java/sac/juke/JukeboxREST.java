package sac.juke;

import java.io.IOException;

import javax.json.Json;
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
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import sac.juke.exceptions.UserExistsException;
import sac.juke.model.GlobalData;
import sac.juke.model.Song;
import sac.juke.model.Songs;
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
    
    @GET
    @Path("closeSseConn")
    public String closeSseConn(@QueryParam("username") String username) {
    	User user = Utils.getUser(servletContext, username);
    	try {
			user.getEventOutput().close();
	    	log.debug("Closed SSE conn for user: " + username);
    	} catch (IOException e) {
			log.debug("Exception while closing SSE connection: " + e);
			log.debug(e.getStackTrace());
		}
    	
    	return "OK\n";
    }
    
    @GET
    @Path("openSseConn")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput openSseConn(@QueryParam("username") String username) {
    	log.debug("Registering " + username + " for SSE");
    	EventOutput e = JukeboxLogic.openSseConn(servletContext, username);
    	return e;
    }
    
    @POST
    @Path("getSongs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public JsonObject getSongs(@FormParam("username") String username) {
    	return JukeboxLogic.getSongsForUser(servletContext, username);
    }
    
    @POST
    @Path("checkSong")
    @Produces("text/plain")
    @Consumes(MediaType.WILDCARD)
    public String checkSong(@FormParam("username") String username,
    							@FormParam("songID") String id,
    							@FormParam("checked") boolean checked) {
    	return JukeboxLogic.markChecked(servletContext, username, id, checked);
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
    	
    	JsonObject jSong = json.build();
    	log.debug("getSong: " + jSong);
    	return jSong;
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
    	User user = null;
    	JsonObjectBuilder b = Json.createObjectBuilder();

    	try {
    		user = JukeboxLogic.createUser(servletContext, username);
    		b.add("status", "OK");
    		
    		/* send notification to other users */
        	JukeboxLogic.sendUserNotification(servletContext, user);
    	} catch (UserExistsException e) {
    		log.debug("Users exists");
    		b.add("status", "EXISTS");
    	}
    	
    	return b.build(); 
    }
    
    @POST
    @Path("testBcast")
    @Produces("text/plain")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String testBroadcast(@FormParam("msg") String msg,
    							@FormParam("id") String id) {
    	/* Generate event */
		OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.name(id);
        eventBuilder.data(String.class, msg);
        OutboundEvent event = eventBuilder.build();
        
    	GlobalData data = Utils.getGlobalData(servletContext);
    	data.broadcast(event);
    	log.debug("Broadcast: " + id + " - " + msg);
    	return "OK\n";
    }
    
    @POST
    @Path("removeUser")
    @Produces("text/plain")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeUser(@FormParam("username") String username) {
    	log.debug("removing username: " + username);
    	Users users = Utils.getUsers(servletContext);
    	Songs songs = Utils.getSongs(servletContext);
    	User user = users.get(username);
    	
    	songs.removeUser(user);
    	users.remove(username);
    	
    	/* send notification to other users */
    	JukeboxLogic.removeUserNotification(servletContext, user);
    	
    	return "OK"; 
    }
    
    @POST
    @Path("followUser")
    @Produces("text/plain")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String followUser(@FormParam("username") String username,
    						 @FormParam("followedUser") String followed) {
    	if (username.equals(followed)) {
    		return "unfollow";
    	}
    	
    	log.debug("Following username: " + followed);
    	Users users = Utils.getUsers(servletContext);
    	Songs songs = Utils.getSongs(servletContext);
    	
    	users.follow(username, followed);
    	users.updateState();
    	songs.update(users);
    	
    	/* send notification to other users */
    	JukeboxLogic.updatePowerNotification(servletContext);
    	
    	if (users.get(username).follows(followed)) {
    		return "follow";
    	} else {
    		return "unfollow";
    	}
    }
    
    @POST
    @Path("getPower")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public JsonObject getPower(@FormParam("username") String username) {
    	log.debug("Getting power for username: " + username);
    	Users users = Utils.getUsers(servletContext);
    	User u = users.get(username);
    	return u.toJson();
    }
    
    /**
     * 
     * @return {"users":[{"username":"Bob","votedSongs":[]},
     * 					 {"username":"Alice","votedSongs":[]}, ...}]}
     */
    @POST
    @Path("getUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public JsonObject getUsers(@FormParam("username") String username) { 
    	return JukeboxLogic.getUsersForUser(servletContext, username);
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









