package pers.futuremac.controller;

/**
 * Created by 前程 on 2015/7/13.
 */

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.futuremac.domain.User;
import pers.futuremac.service.ChatService;
import pers.futuremac.service.UserService;
import pers.futuremac.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Enumeration;

@Component
@Path("/")
public class ServiceController {

    private final Logger logger = LoggerFactory.getLogger(ServiceController.class);
    private final String imgUploadDir = "/usr/share/nginx/html/17neitui/img/avatar/";

    @Context
    private HttpServletResponse response;
    @Context
    private HttpServletRequest request;

    @Autowired
    private UserService us;
    @Autowired
    private ChatService cs;

    //get method, no params
    @GET
    @Path("/rest")
    public Response getStatus() {
        return Response.status(200).entity("hello world").build();
    }

    @GET
    @Path("/rest/gethuman")
    public User getUser(){
        return us.getUser();
    }

    //get and return json
    @POST
    @Path("/rest/post")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User postUser(User u){
        logger.info("RequestUrl: {}, QueryString: {}", request.getRequestURI(), request.getQueryString());
        return u;
    }

    @GET
    @Path("/rest/get")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@QueryParam("id") String id){
        return us.getUser();
    }

    @POST
    @Path("/rest/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(@FormDataParam("file") InputStream fileInputStream,
                                @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
                                @FormDataParam("user") String user){
        try {
            logger.info("RequestUrl: {}, QueryString: {}", request.getRequestURI(), request.getQueryString());
            Enumeration<String> headers = request.getHeaderNames();
            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                logger.info("{}:{}", key, request.getHeader(key));
            }
            String suffix = ".jpg";
            int pos = contentDispositionHeader.getFileName().lastIndexOf(".");
            if (pos != -1)
                suffix = contentDispositionHeader.getFileName().substring(pos);
            String name = StringUtil.getBASE64String(user + "_" + contentDispositionHeader.getFileName()) + suffix;
            String targetFile = imgUploadDir + name;
            logger.info("target path:{}",targetFile);
            saveFile(fileInputStream, targetFile);

            return Response.status(200).entity(name).build();
        }catch(Exception e){
            logger.error(e.getMessage());
            return Response.status(200).entity("").build();
        }
    }

    @GET
    @Path("/rest/test")
    public Response generateQRCode(){
        return Response.ok(new Viewable("/test.jsp", null)).build();
    }

    private void saveFile(InputStream uploadInputStream,String dir){
        try{
            OutputStream output = new FileOutputStream(new File(dir));
            int read = 0;
            byte[] bytes = new byte[1024];
            while((read = uploadInputStream.read(bytes)) != -1){
                output.write(bytes, 0, read);
            }
            output.flush();
            output.close();
        }catch (IOException e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }


}
