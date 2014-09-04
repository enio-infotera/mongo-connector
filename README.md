# Mongo-Connector V1 (o V2, si contamos la 0)

Esta nueva versión del Mongo-Connector intenta resolver de manera muy simple todas las necesidades que tengamos a la hora de comunicarnos con una base de datos mongo y utilizar sus funcionalidades. La idea es que sea un componente muy liviano y flexible, de "manufactura" local, lo que nos permite agregar nuevas funcionalidades a medida que mongo vaya agregando funciones, o modificar existentes para hacerlas aun mas simples y accesibles, a diferencia de otros frameworks como Morphia o Jongo, donde nos encontramos limitados a lo que ya se haya implementado (que hay que decirlo, no estan todas las funcionalidades de mongo implementadas en ninguno de dichos frameworks). 

### Dependencia

    <dependency>
      <groupId>com.despegar.integration</groupId>
      <artifactId>mongo-connector</artifactId>
      <version>1.0.9</version>
    </dependency>

### Getting started...

Para utlizar el mongo-connector primero se debe instanciar un MongoDBConnection, que no es ni mas ni menos que la conexion con nuestra base de datos, indicandole el nombre de la misma, y el server (o servers) en los que se encuentra. A tener en cuenta, por ahora mongo-connector solo tiene soporte para replica set.
Una vez establecida la conexion, se puede utilizar un factory de MongoCollection para generar la conexion con las distintas colecciones que posea nuestra base. Como dato, pasa serializar y deserializar los objetos que luego se guardaran se utiliza FasterXML, por lo que se pueden utilizar las annotations del mismo para personalizar como se guardaran y obtendran los objetos.

#### Implementacion

    import com.despegar.integration.mongo.*;
    
    MongoDBConnection connection = new MongoDBConnection("dbTest", "localhost:27017");
    MongoCollectionFactory factory = new MongoCollectionFactory(connection);
                
    MongoCollection<Dog> collectionDogs = factory.buildMongoCollection("dogs", Dog.class);
    MongoCollection<Cat> collectionCats = factory.buildMongoCollection("cats", Cat.class);
    
    Dog dog = collectionDogs.findOne();
    Cat cat = collectionCats.findOne();

#### La clase "Cat" (o Dog)
	
    public class Cat implements IdentificableEntity  {
    
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

#### Add

Para insertar registros nuevos se pueden utilizar los metodos add o save, la diferencia entre uno y otro es que el add agrega siempre un nuevo registro, mientras que el save, verifica si en el objeto que le pasamos contamos ya con un id, en caso de que lo tenga, lo buscara en la coleccion de mongo, si encuentra uno, lo pisara, en caso contrario, agregara uno nuevo

       Cat firstCat = new Cat();
       firstCat.setName("Don Gato");       
        
       String catId = cats.add(firstCat);
        
       Cat secondCat = new Cat();
       secondCat.setAge(2);
       secondCat.setId(catId);
        
       cats.save(secondCat);
       // secondCat piso en la base a firstCat y se perdio el nombre

#### Queries

Para realizar queries, los metodos find del MongoCollection reciben un objeto Query, donde se debe armar la consulta que se realizara a la base de datos

       Query query = new Query();
       query.equals("type", "siames").greaterOrEqual("age", 2).addOrderCriteria("age", OrderDirection.DESC);
       query.limit(10);

       Collection<Cat> cats = collectionCats.find(query);

Estan implementados greater, greaterOrEqual, less, lessOrEqual, equal, not_equal, exists, mod, in, notIn, all, withIn, near, intersect. Se puede hacer ors, y obviamente limit y skip. Tambien sort, mediante el orderCriteria (que se puede agregar mas de uno, y se respeta el orden en que se los va agregando para el sort)

#### Updates

A la hora de hacer una actualizacion, se debe realizar mediante el objeto Update, indicando el tipo de actualizacion que se hara (UpdateOperation) y las properties a actualizar. A su vez, se puede utilizar el metodo save para guardar un objeto completo, actualizando el existente en caso de coincidir con alguno que ya exista en la base (por id) o ingresando uno nuevo.

        Update update = new Update();
        update.setUpdateOperation(UpdateOperation.INC);
        update.put("age", 1);
        
        Integer n = collectionCats.update("123", update);
        // update devuelve la cantidad de documentos actualizados
        
        Cat cat123 = collectionCats.findOne("123");
        cat123.setName("Bola de Nieve");
        
        collectionCats.save(cat123);

#### Aggregation Framework (BETA)

El aggregation framework es algo que estamos incluyendo en mongo connector de a poco, y a medida que se van necesitando distintas funcionalidades. Aun esta en un estado "beta", y no demasiado testeado, pero igualmente pueden utilizarlo y, de paso, ayudarnos a probarlo y sugerirnos cambios o mejoras. Por ahora solo soporta group, match y geoNear.

        GroupQuery gQuery = new GroupQuery();
        gQuery.idProperty("type", "$type").idProperty("age", "$age");
        gQuery.put("names", GroupOperation.PUSH, "$name");
        
        Query mQuery = new Query();
        mQuery.in("type", Arrays.asList("persa", "siames"));
        
        AggregateQuery query = new AggregateQuery();
        query.match(mQuery);
        query.group(gQuery);                
        
        Collection<CatNameList> catNames = collectionCats.aggregate(query, CatNameList.class);

#### Creando instancias en Spring xml

Tambien, utilizando los factories se pueden instanciar beans de MongoCollection por spring, para luego inyectarlos directamente en los diferentes servicios de nuestra aplicacion

	<import resource="classpath:com/despegar/integration/mongo/mongo-context.xml" />

	<bean id="myapp.mongoCollection.cats"
		factory-bean="com.despegar.integration.mongo.collection.factory" factory-method="buildMongoCollection">
		<constructor-arg value="cats"/>
		<constructor-arg value="com.myapp.entities.Cat"/>
	</bean>		

Se deben agregar las properties com.despegar.integration.mongo.dbName y com.despegar.integration.mongo.replicaSet para que el mongo-context.xml funcione

A medida que pueda voy a subir más documentación.
  [1]: https://github.com/typesafehub/config
