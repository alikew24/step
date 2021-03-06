// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.*;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final String ENTITY_COMMENT = "Comment";
  private static final String PROPERTY_COMMENT = "comment";
  private static final String PROPERTY_EMAIL = "email";
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(ENTITY_COMMENT);
    PreparedQuery results = datastore.prepare(query);

    int numComments = getNumComments(request);
    if (numComments < 0) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer greater than or equal to 0");
      return;
    }

    List<String> comments = new ArrayList<>();
    int numIteratedComments = 0;
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String comment = (String) entity.getProperty(PROPERTY_COMMENT);
      String email = (String) entity.getProperty(PROPERTY_EMAIL);
      String fullComment = email + ": " + comment;
      comments.add(fullComment);
      numIteratedComments++;
      if (numIteratedComments >= numComments){
          break;
      }
    }
   
    // convert the arraylist to json
    String json = convertToJsonUsingGson(comments);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Get the input from the form
      String comment = request.getParameter(PROPERTY_COMMENT);
      if (comment != null && !comment.isEmpty()){
        UserService userService = UserServiceFactory.getUserService();
        String userEmail = userService.getCurrentUser().getEmail();
        Entity commentEntity = new Entity(ENTITY_COMMENT);
        commentEntity.setProperty(PROPERTY_COMMENT, comment);
        commentEntity.setProperty(PROPERTY_EMAIL, userEmail);
        datastore.put(commentEntity);
      }
      
      // Redirect back to index page
      response.sendRedirect("/index.html");
  }


  private String convertToJsonUsingGson(List<String> comments) {
    return new Gson().toJson(comments);
  }

  private int getNumComments(HttpServletRequest request) {
    String numCommentsString = request.getParameter("numComments");
    try {
      return numCommentsString == null || numCommentsString.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(numCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numCommentsString);
      return Integer.MIN_VALUE;
    }
  }

}
