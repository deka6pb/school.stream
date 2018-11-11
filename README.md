# Stream API

## Зачем нам Stream?

- Как быстро можно понять что тут происходит?

```
public void printGroups(List<People> people) {
	Set<Group> groups = new HashSet<>();
	for (People p: people) {
		if (p.getAge() >= 65) {
			groups.add(p.getGroup());
		}
	}
	List<Group> sorted = new ArrayList<>(groups);
	Collections.sort(sorted, new Comparator<Group>() {
		public int compare (Group a, Group b) {
			return Integer.compare(a.getSize(), b.getSize());
		}
	});
	for (Group g: sorted) {ss
		System.out.println(g.getName());
	}
}
```

- Было бы круто работать попозже/поменьше (laziness)

```
public void printGroups(List<People> people) {
	people.stream()
		.filter(p -> p.getAge() >= 65)
		.map(p -> p.getGroup())
		.distinct()
		.sorted(comparing(g -> g.getSize()))
		.map(g -> g.getName())
		.forEach(n -> System.out.println(n)) // <-- ACIONS!!
	;
}
```

- Параллелизм

Параллелизм индексного цикла реально сложен 
```
Collection<Item> data;
...
for(int i = 0; i < data.size(); i++) {
    processItem(data.get(i));
}
```

Если не связываться с индексами, то все равно остается проблемы с итераторами
```
Collection<Item> data;
...
for(Item item : data) {
    processItem(item);
}
```

С помощью Stream:

```
Collection<Item> data;
...
data.parallelStream()
    .forEach(item -> processItem(item));
```

Просто Волшебство!


## Дизайн

Большая часть кода укладывается в простой паттерн
```
source -> op -> op -> op -> sink
```

sources: collections, iterators, channels
operations: filter, map, reduce
sinks: collections, locals 

## Stream

- Множество данных
- Не структура данных (no storage). У стрима нет хранения элементов
- Выполнение операций отложено до последнего (lazy)
- Может быть бесконечным
- Не мутирует источник (это необязательно, но является хорошим тоном)
- Одноразовый
- Ordered/Unordered
- Parallel/Sequential
- Примитивные специализации: IntStream, LongStream, DoubleStream

## Stream pipeline

a source:               Stream -> Stream
intermidiate operation: Stream -> Stream
a terminal operation:   Stream -> PROFIT! 

```
public void printGroups(List<People> people) {
	people.stream()                           // <-- Source
		.filter(p -> p.getAge() >= 65)        // <-- Intermidiate operation
		.map(p -> p.getGroup())               // <-- Intermidiate operation
		.distinct()                           // <-- Intermidiate operation
		.sorted(comparing(g -> g.getSize()))  // <-- Intermidiate operation
		.map(g -> g.getName())                // <-- Intermidiate operation
		.forEach(n -> System.out.println(n))  // <-- Terminal Operation
	;
}
```

Все Exception будут кинуты на уровне терминальной операции.

По сути это аналогично:

```
public void printGroups(List<People> people) {
     Stream<People> s1 = people.stream();
     Stream<People> s2 = s1.filter(p -> p.getAge() >= 65);
     Stream<Group>  s3 = s2.map(p -> p.getGroup());
     Stream<Group>  s4 = s3.distinct();
     Stream<Group>  s5 = s4.sorted(comparing(g -> g.getSize()));
     Stream<String> s6 = s5.map(g -> g.getName());
     s6.forEach(n -> System.out.println(n));
}
```

## Stream Sources
### Stream Sources: collections

```
ArrayList<T> list;
Stream<T> s = list.stream();  // sized, ordered

HashSet<T> set;
Stream<T> s = set.stream();   // sized, distinct

TreeSet<T> set;
Stream<T> s = set.stream();   // sized, distinct, sortered, ordered
```
### Stream Sources: factories, builders  

```
T [] arr;
Stream<T> s = Arrays.stream(arr);

Stream<T> s = Stream.of(v0, v1, v2);

Stream<T> s = Stream.builder()
                    .add(v0).add(v1).add(v2)
                    .build();
                    
IntStream s = IntStream.range(0, 100);
```

### Stream Sources: generators

```
AtomicInteger init = new AtomicInteger(0);
Stream<Integer> s = Stream.generate(init::getAndIncrement);
```

```
Stream<Integer> s = Stream.iterate(0, i -> i + 1);
```

### Stream Sources: others

Получить stream строк из bufferedReader:
```
Stream<String> s = bufferedReader.lines();
```

Сделать разбивку через regex:
 ```
 Stream<String> s = Pattern.compile(myRegEx)
                            .splitAsStream(myStr);
 ```
 
Случайный поток Double чисел:
 ```
 Stream<String> s = new SplittableRandom().doubles();
 ```
 
 ## Intermediate Operations
 
 Практически все операции, которые есть в Stream API:
 
 ```
 Stream<S> s;
 Stream<S> s.filter(Predicate<S>);
 Stream<T> s.map(Function<S, T>);
 Stream<T> s.flatMap(Function<S, Stream<T>>);
 Stream<S> s.peek(Consumer<S>);
 Stream<S> s.sorted();
 Stream<S> s.distinct();
 Stream<S> s.limit(long);
 Stream<S> s.skip(long);
 
 Stream<S> s.unordered();  // если stream был упорядочен, то теперь можно считать что не упорядочен
 Stream<S> s.parallel();   // последующая обработка будет происходить в нескольких потоках
 Stream<S> s.sequential(); // в одном потоке
 ```
 
 ?? Как указать стриму определенные свойства вручную
 
 ## Terminal Operations
 
 - терминальный операции дают результат
 - параллельно или последовательно
 - можно выделить
    - итерация: forEach, iterator
    - поиск: findFirst, findAny
    - проверка: allMatch, anyMatch, noneMatch
    - агрегаторы: 
        - reducion   // из всего стрима получаем скаляр
        - collectors // хотим сложить в другую коллекцию
        
### Short-circuiting

- некоторые операции могут "бросить" поток
- получают смысл операции над бесконечными потоками
- find*, *Match, limit
e.g.:

Первое четное число в бесконечном потоке:
```
int v = Stream.iterate(1, i -> i + 1)
                .filter(i % 2 == 0)
                .findFirst().get();
```

### Iteration
- делают действие над каждым элементом поиска:
```
IntStream.range(0, 100)
            .forEach(System.out::println);
```
- для совместимости с iterator:
```
Iterator<Integer> = 
    Stream.iterate(0, i -> i + 1)
            .limit(100)
            .iterator();
```

Как