package server.test;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.Test;

import server.app.Constants;
import server.app.models.MongoConnector;
import server.app.models.Recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecipeModelTest {
  @Test
  @SuppressWarnings("unchecked") // for mocking generic class
  public void testGetRecipesByTag() {
    // setup global mongo connection mock
    MongoConnector conn = mock(MongoConnector.class);
    MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
    when(conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION)).thenReturn(mongoCollection);
    // build expected recipe documents
    List<Document> expectedRecipeDocuments = Arrays.asList(
      new Document(
        new HashMap<String, Object>() {{
          put(Constants.Mongo.ID, new ObjectId("000000000000000000000000"));
          put(Constants.Recipe.KEY_TITLE, "Title");
          put(Constants.Recipe.KEY_DESC, "Desc");
          put(Constants.Recipe.KEY_PIC_URL, "photo");
        }}
     )
    );
    // mock find method
    when(mongoCollection.find(isA(Bson.class))).thenAnswer(invocation -> {
      Bson filters = (Bson) invocation.getArguments()[0];
      // make sure filters are as expected
      assertNotNull(filters);
      assertEquals(
        Filters.and(Filters.eq("tags", "tag0"), Filters.eq("tags", "tag1"))
        .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()),
        filters
        .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry())
      );
      // return expected recipe documents
      FindIterable<Document> iter = (FindIterable<Document>) mock(FindIterable.class);
      when(iter.skip(anyInt())).thenReturn(iter);
      when(iter.limit(anyInt())).thenReturn(iter);
      when(iter.iterator()).thenReturn(
        new MongoCursorWrapper(expectedRecipeDocuments.iterator())
      );
      return iter;
    });
    // insure expected recipe documents are actually returned
    List<Recipe> actualRecipes = Recipe.getRecipesByTag(conn, new ArrayList<String>(Arrays.asList("tag0", "tag1")), 0, 1);
    List<Recipe> expectedRecipes = new ArrayList<>(expectedRecipeDocuments.stream().map(Recipe::new).collect(Collectors.toList()));
    assertEquals(expectedRecipes, actualRecipes);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testSetupSearchIndexAlreadyExists() {
    MongoConnector conn = mock(MongoConnector.class);
    MongoCollection<Document> coll = (MongoCollection<Document>) mock(MongoCollection.class);
    when(conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION)).thenReturn(coll);
    // add fake indexes to document
    List<Document> indexDocuments = Arrays.asList(
      new Document(
        new HashMap<String, Object>() {{
          put("name", Constants.Recipe.INDEX_TITLE_TEXT);
        }}
      )
    );
    ListIndexesIterable<Document> indexes = (ListIndexesIterable<Document>) mock(ListIndexesIterable.class);
    when(indexes.iterator()).thenReturn(new MongoCursorWrapper(indexDocuments.iterator()));
    when(coll.listIndexes()).thenReturn(indexes);
    when(coll.createIndex(isA(Document.class))).thenAnswer(invocation -> {
      fail("Shouldn't try to create a search index since it already exists!");
      return null;
    });
    Recipe.setupSearchIndex(conn);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testSetupSearchIndexDoesntExist() {
    MongoConnector conn = mock(MongoConnector.class);
    MongoCollection<Document> coll = (MongoCollection<Document>) mock(MongoCollection.class);
    when(conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION)).thenReturn(coll);
    ListIndexesIterable<Document> indexes = (ListIndexesIterable<Document>) mock(ListIndexesIterable.class);
    // empty indexes
    when(indexes.iterator()).thenReturn(new MongoCursorWrapper(new ArrayList<Document>().iterator()));
    when(coll.listIndexes()).thenReturn(indexes);
    when(coll.createIndex(isA(Document.class))).thenAnswer(invocation -> {
      // verify we are creating the right index
      Document index = (Document) invocation.getArguments()[0];
      assertNotNull(index);
      assertEquals(new Document(Constants.Recipe.KEY_TITLE, "text"), index);
      IndexOptions options = (IndexOptions) invocation.getArguments()[1];
      assertNotNull(options);
      assertEquals(new IndexOptions().name(Constants.Recipe.INDEX_TITLE_TEXT), options);
      return null;
    });
    Recipe.setupSearchIndex(conn);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testSearchRecipesByTitle() {
    MongoConnector conn = mock(MongoConnector.class);
    MongoCollection<Document> coll = (MongoCollection<Document>) mock(MongoCollection.class);
    when(conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION)).thenReturn(coll);
    List<Document> expectedDocuments = Arrays.asList(
      new Document(
        new HashMap<String, Object>() {{
          put(Constants.Mongo.ID, new ObjectId("000000000000000000000000"));
          put(Constants.Recipe.KEY_TITLE, "Title");
          put(Constants.Recipe.KEY_DESC, "Desc");
          put(Constants.Recipe.KEY_PIC_URL, "photo");
        }}
      )
    );
    when(coll.find(isA(Bson.class))).thenAnswer(invocation -> {
      // verify we are searching for text
      Bson query = (Bson) invocation.getArguments()[0];
      assertNotNull(query);
      assertEquals(
        Filters.text("search term")
        .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()),
        query
        .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry())
      );
      FindIterable<Document> iter = mock(FindIterable.class);
      when(iter.skip(anyInt())).thenReturn(iter);
      when(iter.limit(anyInt())).thenReturn(iter);
      when(iter.iterator()).thenReturn(new MongoCursorWrapper(expectedDocuments.iterator()));
      return iter;
    });
    List<Recipe> actualRecipes = Recipe.searchRecipesByTitle(conn, "search term", 0, 1);
    List<Recipe> expectedRecipes = new ArrayList<>(expectedDocuments.stream().map(Recipe::new).collect(Collectors.toList()));
    assertEquals(expectedRecipes, actualRecipes);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetRecipeByIdExists() {
    MongoConnector conn = mock(MongoConnector.class);
    MongoCollection<Document> coll = (MongoCollection<Document>) mock(MongoCollection.class);
    when(conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION)).thenReturn(coll);
    ObjectId id = new ObjectId("000000000000000000000000");
    List<Document> expectedDocuments = Arrays.asList(
        new Document(
          new HashMap<String, Object>() {{
            put(Constants.Mongo.ID, id);
            put(Constants.Recipe.KEY_TITLE, "Title");
            put(Constants.Recipe.KEY_DESC, "Desc");
            put(Constants.Recipe.KEY_PIC_URL, "photo");
          }}
        )
    );
    when(coll.find(isA(Bson.class))).thenAnswer(invocation -> {
      Bson query = (Bson) invocation.getArguments()[0];
      assertNotNull(query);
      assertEquals(
          Filters.eq(Constants.Mongo.ID, id)
          .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()),
          query
          .toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry())
      );
      FindIterable<Document> iter = (FindIterable<Document>) mock(FindIterable.class);
      when(iter.first()).thenReturn(expectedDocuments.get(0));
      return iter;
    });
    Recipe actualRecipe = Recipe.getRecipeById(conn, id);
    Recipe expectedRecipe = new Recipe(expectedDocuments.get(0));
    assertEquals(expectedRecipe, actualRecipe);
  }
}
