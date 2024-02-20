# Commenter API Service

## Overview

This repository is an API service for Commenter app, a lightweight social media where users can create posts and comments on posts.

## Dependencies

- **Ratpack:** A set of Java libraries for building scalable HTTP applications.
- **PostgreSQL:** An open-source relational database management system.
- **Lombok:** A Java library that helps reduce boilerplate code by automatically generating common code for classes.
- **Guice:** A lightweight dependency injection framework for Java.

## Local Setup

Follow the steps below to set up the API service locally:

1. **Clone the Repository**
    ```bash
    git clone https://github.com/joshuagatizz/commenter-app.git
    cd commenter-app
    ```

2. **Database Setup**
   - Make sure you have PostgreSQL installed and running.
   - Create a new database for Commenter (e.g., commenter_db).
   - Update `application.properties` with your database connection details.


3. **Run SQL Scripts**

   Execute the following SQL scripts to create two tables: `posts` and `comments`. 
   Each post can have zero or more comments.

    #### `posts` Table:
    
    ```postgresql
    CREATE TABLE posts (
      id SERIAL PRIMARY KEY,
      title TEXT NOT NULL,
      content TEXT NOT NULL,
      "user" TEXT NOT NULL,
      comments INT DEFAULT 0
    );
    ```
   Explanation:
     - `id`: Auto-incremented unique identifier for each post.
     - `title`: Title of the post.
     - `content`: Content of the post.
     - `"user"`: Username of the post author.
     - `comments`: The count of comments associated with the post (default is 0).
   
    #### `comments` Table:
    ```postgresql
     CREATE TABLE comments (
       id SERIAL PRIMARY KEY,
       content TEXT NOT NULL,
       "user" TEXT NOT NULL,
       post_id INT,
       FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
    );
    ```
   Explanation:
    - `id`: Auto-incremented unique identifier for each comment.
    -  `content`: Content of the comment.
    -  `"user"`: Username of the comment author.
    -  `post_id`: Foreign key referencing the id column of the `posts` table.
    

4. **Build and Run the App**

    Execute the following commands to build and run the app.
    ```bash
    ./gradlew build
    ./gradlew run
    ```
    You can also use your IDE and run the `Main.java` file.    


5. **Access the API**

   Open your browser or use a tool like [Postman](https://postman.com) to interact with the API by sending requests to http://localhost:8080.


## API Docs
### Posts
* **Get All Posts**
    - Path: `api/posts`
    - Method: `GET`
    - Description: Retrieve all posts.
    - Response:
        ```json
        {
          "code": 200,
          "status": "OK",
          "data": [
            {
              "id": 1,
              "title": "Sample Post",
              "content": "This is a sample post.",
              "user": "john",
              "comments": 2
            },
            {
              "id": 2,
              "title": "Another Sample Post",
              "content": "This is another sample post.",
              "user": "doe",
              "comments": 0
            }
          ],
          "errors": null
      }
        ```
* **Create a New Post**
    - Path: `api/posts`
    - Method: `POST`
    - Description: Create a new post.
    - Request Body:
        ```json
        {
          "title": "This is a title",
          "content": "This is a post content.",
          "user": "john"
      }
        ```
    - Response:
        ```json
        {
          "code": 200,
          "status": "OK",
          "data": {
              "id": 1,
              "title": "This is a title",
              "content": "This is a post content.",
              "user": "john",
              "comments": 0
          },
          "errors": null
      }
        ```
* **Edit a Post**
    - Path: `api/posts/{postId}`
    - Method: `PUT`
    - Description: Update a post with id `{postId}`. 
    Note that the `user` field in the request body must match the `user` on the saved post.
    - Request Body:
        ```json
        {
          "title": "This is an updated title",
          "content": "This is an updated post content.",
          "user": "john"
      }
        ```
    - Response:
        ```json
        {
          "code": 200,
          "status": "OK",
          "data": true,
          "errors": null
      }
        ```
* **Delete a Post**
    - Path: `api/posts/{postId}`
    - Method: `DELETE`
    - Description: Delete a post with id `{postId}`.
      Note that the `user` field in the request body must match the `user` on the saved post.
      Deleting a post also means that every comment associated with that post will be deleted.
    - Request Body:
        ```json
        {
          "user": "john"
      }
        ```
    - Response:
        ```json
        {
          "code": 200,
          "status": "OK",
          "data": true,
          "errors": null
      }
        ```

### Comments
* **Get All Comments of a Post**
    - Path: `api/posts/{postId}/comments`
    - Method: `GET`
    - Description: Retrieve all comments of a post with id `{postId}`.
    - Response:
        ```json
        {
          "code": 200,
          "status": "OK",
          "data": [
            {
              "id": 1,
              "user": "john",
              "content": "This is a sample comment.",
              "postId": 2
            },
            {
              "id": 2,
              "user": "doe",
              "content": "This is another sample comment.",
              "postId": 2
            }
          ],
          "errors": null
      }
        ```
* **Create A New Comment on a Post**
    - Path: `api/posts/{postId}/comments`
    - Method: `POST`
    - Description: Create a new comment on a post with id `{postId}`. 
      Adding a comment will increment the value of `comments` field for the post in which the comment is created.
    - Request Body:
        ```json
        {
          "user": "john",
          "content": "This is a comment content."
      }
        ```
    - Response:
        ```json
        {
          "code": 200,
          "status": "OK",
          "data": {
              "id": 1,
              "user": "john",
              "content": "This is a comment content.",
              "postId": 1
          },
          "errors": null
      }
        ```
* **Edit A Comment**
    - Path: `api/posts/{postId}/comments/{commentId}`
    - Method: `PUT`
    - Description: Update a comment with id `{commentId}` in a post with id `{postId}`.
      Note that the `user` field in the request body must match the `user` on the saved comment.
    - Request Body:
        ```json
        {
          "user": "john",
          "content": "This is an updated comment content."
      }
        ```
    - Response:
        ```json
        {
          "code": 200,
          "status": "OK",
          "data": true,
          "errors": null
      }
        ```
* **Delete A Comment**
    - Path: `api/posts/{postId}/comments/{commentId}`
    - Method: `DELETE`
    - Description: Delete a comment with id `{commentId}` in a post with id `{postId}`.
      Note that the `user` field in the request body must match the `user` on the saved comment.
      Deleting a comment will decrement the value of `comments` field for the post in which the comment is created.
    - Request Body:
        ```json
        {
          "user": "john"
      }
        ```
    - Response:
        ```json
        {
          "code": 200,
          "status": "OK",
          "data": true,
          "errors": null
      }
        ```