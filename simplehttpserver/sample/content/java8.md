
class: center, middle

### EPAM JavaCore course
# Java 8 Lambdas, Streams, Optional overview
<br/>
<br/>
<br/>
<br/>
<br/>
.right[Radmir Kashaev, EPAM Systems] 
.right[**email**: Radmir_Kashaev@epam.com] 
.right[**skype**: rkashaev] 

.footer[&copy; 2018]

???

Notes for the first slide

---

# Agenda
<br/>

1. [Before Java 8        ](#before)
1. [Lambdas              ](#lambda)
1. [Functional interfaces](#func-interfaces)
1. [Streams              ](#streams)
1. [Optional             ](#optional)

---

name: before

# Before Java 8
<br/>

### Hierarchy of nested classes

|           | Static              | Non-static        | Anonymous            
|-----------|---------------------|-------------------|----------------------
| Member    | Static nested class | Inner class       | -
| Local     | -                   | Local inner class | Anonymous inner class


---

# Before Java 8

Before
```java
    Button btn = new Button();
    final PrintStream pStream = ...;
    
    btn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            pStream.println("Button Clicked!");
        }
    });
```

After
```java
    Button btn = new Button();
    final PrintStream pStream = ...;
    
    btn.setOnAction(e -> pStream.println("Button Clicked!"));
```

---

class: center, middle
name: lambda

# Lambda project
## () -> {}

---

name: l-main-idea

# Project Lambda history
<br/>

* Project Lambda (JSR #335)
* Initiated in December 2009 as Straw-Man proposal
* ...
* Loooong awaited
* ...
* Full class support
* Not a syntactic sugar for an anonymous inner class
* Even though it can appear so.

---

class: center, middle
name: l-main-idea

# Lambdas. Main idea
<br/>

## (arguments) -> {body}

---

name: samples

# Lambdas. Examples
<br/>

```java
	(int x, int y) -> {return x + y;}

	(int x, int y) -> x + y
	
	(x, y) -> x + y

	s -> System.out.println(s);
	
	() -> 42
```

---

name: l-vs-aic

# Lambda vs Anonymous Inner class
<br/>

* Inner classes can have state (member variables), lambdas can't
* Inner classes can have multiple methods, lambda is a single method
* `this` point to the _object instance_ of AIC, but points to the _enclosing object_ in lambda
* Lambda != Anonymous Inner Class

---

class: center, middle

# What type a lambda is?

---

name: func-interfaces

# Functional interfaces
<br/>

Conceptually, a **functional interface has exactly one abstract method**. As such, it may be implicitly converted to a lambda expression.

Note that instances of functional interfaces can be created with lambda expressions or method/constructor references.

> But: To avoid breakage by accidently adding further methods , these interfaces should be explicitly tagged with `@FunctionalInterface`.

---

name: std-func-interfaces

# Standard functional interfaces
<br/>

`java.util.function` package

* `Predicate<T>` - a boolean-valued property of an object
* `Consumer<T>` - an action to be performed on an object
* `Supplier<T>` - provide an instance of a T (such as a factory)
* `UnaryOperator<T>` - a function from T to T
* `Function<T,R>` - a function transforming a T to a R
* `BinaryOperator<T>` - a function from (T, T) to T
* ...

---

# Standard functional interfaces
<br/>

.center.img-fit[![](./img/func-interfaces.png)]

---

name: method-references

# Method references
<br/>

What if we consider a method as an instance of a `Functional Interface`?

Treat method references as shortcuts for simple lambdas:

Type of method reference       | Method reference  | Equivalent lambda expression
-------------------------------|-------------------|------------------------------
Static method                  | `String::valueOf` | `x -> String.valueOf(x)`
Instance method of a class     | `Object::toString`| `x -> x.toString()`
Instance method of a reference | `x::toString`     | `() -> x.toString()`
Object constructor             | `ArrayList::new`  | `() -> new ArrayList<>()`

---

name: method-references-ex

# Method references examples

```java
    class Person {
        private String name;
        private int age;
    
        public int getAge() {return this.age;}
        public String getName() {return this.name;}
    }
    
    Person[] people = ...;
    Comparator<Person> byName = Comparator.comparing(Person::getName);
    Arrays.sort(people, byName);
```

```java
    Consumer<Integer> b1 = System::exit;
    Consumer<String[]> b2 = Arrays::sort;
    Consumer<String> b3 = MyProgram::main;
    Runnable r = MyProgram::main;
```

---

class: center, middle
name: streams

# Java 8 Stream API

---

# Java 8 Stream API. Main idea

<br/>

### `Stream<T>` is a sequence of elements supporting sequential and parallel operations
--

```
    source ->
    
        .op
        
        .op
        
        .op
    
    .finish -> result

```

---

# Stream API concept

<br/>

.center.img-fit[![](./img/streams-concept.png)]

---

# Stream properties
<br/>

Is `Stream` a collection?

* Lazy operations
* Ordered/Unordered
* Finite/Infinite
* Parallel/Sequential
* Streams are not reusable!

---

# Stream sources
<br/>

### Collections
```java
    Set<Student> set = new LinkedHashSet<>();
    Stream<Student> stream = set.stream();
```

### Generators
```java
    Random random = new Random();
    Stream<Integer> randomNumbers = Stream.generate(random::nextInt);
```

### From other streams
```java
    Stream newStream = Stream.concat(stream, randomNumbers);    
```

---

# Stream operations
<br/>

### are divided into

* Intermediate operations (returning another `Stream<T>`)
* Terminal operations (returning the final result)

### Parallel processing
Out-of-the box support of parallel processing (Multi-threading!)
```java
    stream.parallel()
```

---

# Stream intermediate operations
<br/>

* `filter`
* `map`
* `flatMap`
* `peek`
* `sorted`
* `distinct`
* `skip`
* `limit`
* `unordered`
* `parallel `(!!!)
* `sequential `(?!)

---

# Stream terminal operations
<br/>

* iteration (`forEach`, `iterator`)
* searching (`findFirst`, `findAny`)
* matching (`allMatch`, `anymatch`, `noneMatch`)
* aggreagation
    * reduction (`count()`, `sum()`)
    * collectors (`toList()`, `toSet()`)

---

# Stream example
<br/>

```java
    Collection<Task> tasks = getTasks();
    long totalPointsOfOpenTasks = tasks
        .stream()
        .filter( task -> task.getStatus() == Status.OPEN )
        .mapToInt( Task::getPoints )
        .sum();
    
    System.out.println( "Total points: " + totalPointsOfOpenTasks );
```

---

# Complex stream example
<br/>

```java
    // Calculate the weight of each tasks (as percent of total points)
    final Collection<String> result = tasks
            .stream()                                        // Stream<Task>
            .mapToInt( Task::getPoints )                     // IntStream
            .asLongStream()                                  // LongStream
            .mapToDouble( points -> points / totalPoints )   // DoubleStream
            .boxed()                                         // Stream<Double>
            .mapToLong( weigth -> ( long )( weigth * 100 ) ) // LongStream
            .mapToObj( percentage -> percentage + "%" )      // Stream<String>
            .collect( Collectors.toList() );                 // List<String>
    
    System.out.println( result );
```

---

# Another example
<br/>

```java
    // before java 8
    String joinWithPlainJava(String... words) {
        StringBuilder builder = new StringBuilder();
        for (String s : words) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(s);
        }
        return builder.toString();
    }

    // with java 8 Stream API
    String joinWithStreamAPI(String... words) {
        return Arrays.stream(words).collect(Collectors.joining(", "));
    }
```

---

class: center, middle
name: optional

# java.util.Optional

---

name: optional

# Optional
<br/>

What is Optional?

* A container with zero or one elements
* Intended to reduce `NullPointerExceptions`
* A signal to other developers that a value may not be present

What isn't Optional?

* Not capable of storing null
* Not serializable!
* Not widely used in the JDK

---

# Optional states
<br/>
<br/>

| value    | isEmpty() | isPresent() | .get()                 |
|----------|-----------|-------------|------------------------|
| not null | false     | true        | value                  |
| null     | true      | false       | NoSuchElementException |

---

# Optional methods
<br/>

* Create Optional: `Optional.empty()`, `Optional.of(val)`, `Optional.ofNullable(val)`
* `isPresent`, `ifPresent(Consumer)`
* `orElse`, `orElseGet(Supplier)`, `orElseThrow`
* Treat like collection: `map`, `flatMap`, `filter`

---

# Creating Optional examples
<br/>

Use one of the two built-in Factory methods
```java
    Optional<String> text = Optional.ofNullable("Hello, world!");
```

OR...

```java
    Optional<String> text = Optional.of(somethingThatIsntNull);
    Optional<String> text = Optional.of(null); // -> NullPointerException
```

---

# When should we use it?  

Only use it for return types where there is no natural way to represent "no result" instead of `null`:
```java
    // Yes
    public Optional<Customer> getCustomerById(long id);

    // No
    public Optional<List<Customer>> getCustomersForMonth(final YearMonth when);
    
    // Better:
    public List<Customer> getCustomersForMonth(final YearMonth when);
    // -> Collections.emptyList();
```

---

# When should we use it?  

Please, never do this:
```java
    Optional<String> text = service.getText();
    if(text.isPresent()) {
        System.out.println(text.get());
    } else {
        System.out.println("Hello, world!");
    }
```
--

It'd be better off using safe methods:
```java
    Optional<String> text = service.getText();
    System.out.println(text.orElse("Hello, world!"));
    
    // call a lambda/method reference if value is present
    text.ifPresent(this::doSomethingWithText);
    
    // throw an exception if there is no value
    System.out.println(text.orElseThrow( () -> new IllegalArgumentException("Nothing here") ));
    System.out.println(text.orElseThrow( IllegalArgumentException::new ));
```

---

# Better use cases for optional  

Before:
```java
    public String getUserName(User user) {
        return (user != null && user.getName() != null) ? user.getName() : "default";
    }
```

After:
```java
    public String getUserName(User user) {
        return ofNullable(user)
                .map(User::getName)
                .orElse("default");
    }
```

---

# Better use cases for optional

Assume we have a POJO:
```java
    class Person {
        Optional<Car> getCar() {
            return empty(); //stub
        }
    }
    
    class Car {
        Optional<Insurance> getInsurance() {
            return empty(); //stub
        }
    }
    
    class Insurance {
        String getName() {
            return ""; //stub
        }
    }
```

---

# Better use cases for optional 

Misuse of Optional:
```java
    public String getPersonCarInsuranceName(Person person) {
        String name = "Unknown";
        if (ofNullable(person).isPresent()) {
            if (person.getCar().isPresent()) {
                if (person.getCar().get().getInsurance().isPresent()) {
                    name = person.getCar().get().getInsurance().get().getName();
                }
            }
        }
        return name;
    }
```

---

# Better use cases for optional 

Good use of Optional:
```java
    public String getCarInsuranceNameFromPersonUsingFlatMap(Person person) {
        return ofNullable(person)
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }
```
