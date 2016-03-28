package server.test;

import server.app.Constants;
import server.app.models.MongoConnector;
import server.app.models.User;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserModelTest {
  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserByTokenExists() {
    MongoConnector conn = mock(MongoConnector.class);
    MongoCollection<Document> coll = (MongoCollection<Document>) mock(MongoCollection.class);
    when(conn.getCollectionByName(Constants.Mongo.USERS_COLLECTION)).thenReturn(coll);
    ObjectId userId = new ObjectId("000000000000000000000000");
    Document expectedUserDocument = new Document(Constants.Mongo.ID, userId);
    when(coll.find(isA(Bson.class))).thenAnswer(invocation -> {
      Bson query = (Bson) invocation.getArguments()[0];
      assertNotNull(query);
      assertEquals(
          Filters.eq(Constants.Mongo.ID, userId)
          .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()),
          query
          .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry())
      );
      FindIterable<Document> iter = (FindIterable<Document>) mock(FindIterable.class);
      when(iter.first()).thenReturn(expectedUserDocument);
      return iter;
    });
    User actualUser = User.getUserFromToken(conn, userId);
    User expectedUser = new User(expectedUserDocument);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserByTokenDoesntExist() {
    MongoConnector conn = mock(MongoConnector.class);
    MongoCollection<Document> coll = (MongoCollection<Document>) mock(MongoCollection.class);
    when(conn.getCollectionByName(Constants.Mongo.USERS_COLLECTION)).thenReturn(coll);
    ObjectId userId = new ObjectId("000000000000000000000000");
    when(coll.find(isA(Bson.class))).thenAnswer(invocation -> {
      Bson query = (Bson) invocation.getArguments()[0];
      assertNotNull(query);
      assertEquals(
          Filters.eq(Constants.Mongo.ID, userId)
          .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()),
          query
          .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry())
      );
      FindIterable<Document> iter = (FindIterable<Document>) mock(FindIterable.class);
      when(iter.first()).thenReturn(null);
      return iter;
    });
    User actualUser = User.getUserFromToken(conn, userId);
    assertNull(actualUser);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserFromRequestValidParam() {
    MongoConnector conn = mock(MongoConnector.class);
    MongoCollection<Document> coll = (MongoCollection<Document>) mock(MongoCollection.class);
    when(conn.getCollectionByName(Constants.Mongo.USERS_COLLECTION)).thenReturn(coll);
    ObjectId userId = new ObjectId("000000000000000000000000");
    Document expectedUserDocument = new Document(Constants.Mongo.ID, userId);
    when(coll.find(isA(Bson.class))).thenAnswer(invocation -> {
      Bson query = (Bson) invocation.getArguments()[0];
      assertNotNull(query);
      assertEquals(
          Filters.eq(Constants.Mongo.ID, userId)
          .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()),
          query
          .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry())
      );
      FindIterable<Document> iter = (FindIterable<Document>) mock(FindIterable.class);
      when(iter.first()).thenReturn(expectedUserDocument);
      return iter;
    });
    JsonNode param = new ObjectMapper().createObjectNode()
      .put(Constants.User.KEY_TOKEN, userId.toHexString());
    User actualUser = User.getUserFromRequest(conn, param);
    assertNotNull(actualUser);
    assertEquals(new User(expectedUserDocument), actualUser);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserFromRequestInvalidParam() {
    MongoConnector conn = mock(MongoConnector.class);
    when(conn.getCollectionByName(Constants.Mongo.USERS_COLLECTION)).thenAnswer(invocation -> {
      fail("This should not be called");
      return null;
    });
    JsonNode param = new ObjectMapper().createObjectNode()
      .put(Constants.User.KEY_TOKEN, "z");
    User actualUser = User.getUserFromRequest(conn, param);
    assertNull(actualUser);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserFromRequestNoParam() {
    MongoConnector conn = mock(MongoConnector.class);
    when(conn.getCollectionByName(Constants.Mongo.USERS_COLLECTION)).thenAnswer(invocation -> {
      fail("This should not be called");
      return null;
    });
    JsonNode param = new ObjectMapper().createObjectNode();
    User actualUser = User.getUserFromRequest(conn, param);
    assertNull(actualUser);
  }
}
