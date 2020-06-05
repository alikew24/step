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

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");
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
      String username;
      if (entity.getProperty("username") == null) {
          username = "anonymous";
      }
      else {
          username = (String) entity.getProperty("username");
      }
      String comment = username + " says: " + (String) entity.getProperty("comment");
      comments.add(comment);
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
      if (!request.getParameter("comment").isEmpty()) {
        String comment = request.getParameter("comment");
        String name;
        if (!request.getParameter("username").isEmpty()) {
          name = request.getParameter("username");
        }
        else {
          name = "anonymous";
        }
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("comment", comment);
        commentEntity.setProperty("username", name);
        datastore.put(commentEntity);
      }
      else {
        // Send empty response
        response.setContentType("");
        response.getWriter().println();
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
