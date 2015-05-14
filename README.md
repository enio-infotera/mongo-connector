Mongo-Connector
===============

Lightweight connector to [MongoDB](http://www.mongodb.org/).

##Dependency

```xml
<dependency>
	<groupId>com.despegar.integration</groupId>
	<artifactId>mongo-connector</artifactId>
	<version>1.1.1</version>
</dependency>
```


###Getting started

```java
import com.despegar.integration.mongo.*;

MongoDBConnection connection = new MongoDBConnection("dbTest", "localhost:27017");
MongoCollectionFactory factory = new MongoCollectionFactory(connection);
            
MongoCollection<Dog> dogs = factory.buildMongoCollection("dogs", Dog.class);
MongoCollection<Cat> cats = factory.buildMongoCollection("cats", Cat.class);

Dog dog = collectionDogs.findOne();
Cat cat = collectionCats.findOne();
```

#### Example mapping class

```java
public class Cat implements IdentifiableEntity  {

    private String id;
	private String type;
	private String name;
	private Integer age;
	@JsonIgnore
	private String nonSaveProperty;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;	
    }

    public String getType() {
	    return this.type;
	}

	public void setType(String type) {
	    this.type = type;
	}
	...   
}
```

### Add

```java
Cat firstCat = new Cat();
firstCat.setName("Garfield");       

String catId = cats.add(firstCat);

Cat secondCat = new Cat();
secondCat.setAge(2);
secondCat.setId(catId);

cats.save(secondCat); //Overrides first cat
```


#### Queries

To query, the find methods in MongoCollection receive a [Query](https://github.com/despegar/mongo-connector/blob/master/src/main/java/com/despegar/integration/mongo/query/Query.java) object.

```java
Query query = new Query();
query.equals("type", "striped").greaterOrEqual("age", 2).addOrderCriteria("age", OrderDirection.DESC);
query.limit(10);

Collection<Cat> cats = cats.find(query);
```

#### Updates

Updates are done with the [Update](https://github.com/despegar/mongo-connector/blob/master/src/main/java/com/despegar/integration/mongo/query/Update.java) object. You must specify the UpdateOperation

```java
Update update = new Update();
update.setUpdateOperation(UpdateOperation.INC);
update.put("age", 1);

Integer n = cats.update("123", update);
// returns the amount of elements updated

Cat cat123 = cats.findOne("123");
cat123.setName("Snowball");

collectionCats.save(cat123);
```

### Aggregation Framework (BETA) (Supports Mongo 2.6 or greater)

#### Match

```java
Query mQuery = new Query();
mQuery.in("type", Arrays.asList("persa", "siames"));

AggregateQuery query = new AggregateQuery();
query.match(mQuery);

Collection<CatNameList> catNames = collectionCats.aggregate(query);
```

#### Group

Group works with the expressions that mongo supports.

```java
GroupQuery gQuery = new GroupQuery();
gQuery.addToId("type", "$type").addToId("age", "$age").put("names", Expression.push("$name"));

AggregateQuery query = new AggregateQuery();
query.group(gQuery);

Collection<CatNameList> catNames = cats.aggregate(query, CatNames.class);
```

#### Project

```java
ProjectQuery pQuery = new ProjectQuery();
pQuery.show("name").hideId();

AggregateQuery query = new AggregateQuery();
query.group(pQuery);

Collection<CatNameList> catNames = collectionCats.aggregate(query, CatNames.class);
```

### Bulk Operations (BETA) (Supports Mongo 2.6 or greater)

### Creation and execution
```java
//Here you instantiate a bulk object to load everything you want to run
Bulk<T> yourBulkOperation = new Bulk<T>();
//Here you execute your already loaded bulk
yourMongoCollection.bulk(yourBulkOperation);
```

### Insert
You can add inserts to your bulk.

```java
T entity = new T();
yourBulkOperation.insert(entity);
```

### Remove
You can remove objects given a query.

```java
Query yourQuery = new Query();
//Here you query the objects you would like to remove
yourBulkOperation.find(yourQuery).remove();
```

### Remove One
You can remove the first result of a query.

```java
Query yourQuery = new Query();
//Here you query the object you would like to remove
yourBulkOperation.find(yourQuery).removeOne();

### Replace One
You can replace the first result of a query.

```java
Query yourQuery = new Query();
//Here you query the objects you would like to replace
T yourEntityToReplace = new T();
yourBulkOperation.find(yourQuery).replaceOne(yourEntityToReplace);
```

### Update
You can update results of a query.

```java
Query yourQuery = new Query();
//Here you query the objects you would like to update
Update update = new Update();
//Here you load your updates
yourBulkOperation.find(yourQuery).update(update);
```

### Update One
You can update the first result of a query.

```java
Query yourQuery = new Query();
//Here you query the objects you would like to update
Update update = new Update();
//Here you load your updates
yourBulkOperation.find(yourQuery).updateOne(update);
```