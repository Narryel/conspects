# [Андрей Беляев — DTO: живи быстро, гори ярко](https://www.youtube.com/watch?v=gD8xUkZW1GU)

## Adam Bein про DTO

1. Отвязать ентити jpa от ui
2. Типобезопасные
3. Большинство дто остается такими как entity
4. Когда растет DTO появляется слой мапперов

## Самые частые структуры DTO

* java class
* records (== data class kotlin)
* Maps (ассоциативные массивы)

### java class - entity

Почему плохо:

* изменяем модель данных - сразу меняем API
* проблемы с версионированием API
* проблемы с LAZY ассоциациями
* выдаем айдишку в паблик

### java class - pojo

Почему круто:

* просто
* понятно
* можно наследоваться
* можем делать мутабельно/немутабельно
* от бойлерплейта уводит lombok etc

### records

Почему круто:

* immutable
* мало кода
* не поддерживают наследование

### maps

почему хорошо

* Работают везде
* сложить можно все что угодно

почему плохо

* не несут описания нужных данных
* не типобезопасны

## Перенос маппинга на слой DAO

напрямую из ORM   
**Hibernate:**

* вбить напрямую

``` 
session.createQuery("select new com.example.pet.dto.PetFlatDto(p.id, p.name, p.petType.id, p.petType.name) from Pet p", PetFlatDto.class).list
```

* ResultTransformer ( hibernate 5) | TupleTransformer (hibernate 6)

**BlazeDS:**  
Можно описать @EntityView

```java

@EntityView(Pet.class)
public interface PetView {
    @IdMapping
    Long getId();

    @Mapping("CONCAT(name, ' ', age)")
    String getNameAndAge();
}
```

**Spring Data Jpa:**  
Проекции

```java
public interface PetInfo {
    Long getId();

    String getName();

    PetTypeInfo getPetType();

    Set<VisitInfo> getVisits();
}
```

Может быть проблема, что с одного запроса нужно уметь возвращать разные проекции:  
в спринг дате можно разрулить разными запросами, отличая их по префиксу между find и by

``` 
List<PetInfo> findPetsInfoById(Long Id);
List<PetsFullAgeInfo> findPetsFullInfoInfoById(Long Id);
``` 

## Сериализация

При сериализации чаще всего нужно только скрывать поля, можно это делать следующими аннотациями:

* @XmlTransient
* @JsonIgnoreProperties
* @JsonIgnore

### JsonView

```java

public class Controller{
    
    @GetMapping("/jsonview")
    @JsonView(Views.Public.class)
    public List<Pet> findAllJsonView() {
        return petService.findAll();
    }
    
    @GetMapping("/jsonview/admin")
    @JsonView(Views.Admin.class)
    public List<Pet> findAllJsonView() {
        return petService.findAll();
    }
}


public class Views {
    public static class Public {}
    public static class Admin extends Public
    {}
}


@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @Column(name = "id", nullable = false)
    @JsonView(Views.Public.class)
    private Long id;
    
    @Column(name = "name")
    @JsonView(Views.Public.class)
    private String name;
    
    @Column(name = "age")
    @JsonView(Views.Admin.class)
    private Integer age;
    
    @ManyToOne
    @JoinColumn(name = "pet_type_id")
    @JsonView(Views.Public.class)
    private PetType petType;
    
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "pet_id")
    @JsonView(Views.Public.class)
    private Set<Visit> visits = new LinkedHashSet<>();
}
```


Проблемы: 
* Jpa
  * LazyInit
  * N+1
* Вытаскиваем все данные
* Версионированние API
* Комбинаторный взрыв (логика оч сильно размазана)

### GraphQl
Недостатки:
* для постраничной выборки нужен отдельный тип данных
* выбираются все поля
* N+1
* Сложнее скрывать поля

### JSON Relational Duality View
Сериализатор утащили в бд, делаем вьюшку на основе таблицы где уже лежат JSON

```roomsql
create json relational duality view pets_dv as
select json {'petId' : p.id,
'petName' : p.name,
'petAge' : p.age,
'visits' :
[ select json {'visitId' : v.id,
'visitDate' : v.visitDate,
'diagnosis' : v.diagnosis}
FROM visits v with insert update delete
WHERE v.petId = p.id ]}
from pets p with insert update delete;
```

```roomsql 
select json_serialize(p.data pretty) from pets_dv p;
```

result:
```json
{
  "_metadata": {
    "etag": "E546E2220E8F9620E36C2A7F8858D6F7",
    "asof": "00000000001FA9FA"
  },
  "petId": 1,
  "petName": "Tom",
  "petAge": 2,
  "visits": [
    {
      "visitId": 1,
      "visitDate": "2023-04-09T23:00:29",
      "diagnosis": "Illness"
    }
  ]
}
```