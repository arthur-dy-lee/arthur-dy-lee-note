# Association-Aggregation-Composition区别

--------

## 简介

**Association** 关联就是两个对象有关系，这个比较泛化，可以是一对一，一对我，多对一，多对多的关系 。

**Aggregation**聚合是一种特殊的关联，是一个'has-a'的关系，是'整体-部分'的关系。聚合的类包括另一个类的引用，可以说聚合类包含另一个类，被包含的类是聚合类的一部分。并且他们之间不存在循环引用关系。

A类包含B类，B类又包含A类的引用，那么他们之类不是'ownership'关系，只他们之间只是关联关系。

**Composition**是一种特殊的聚合，A对象包括B对象，如果B对象离开了A对象就无法存在，那么A和B就是一种聚合关系。

--------



## What is Association?

Association is a relationship between two objects. In other words, association defines the multiplicity between objects. You may be aware of one-to-one, one-to-many, many-to-one, many-to-many all these words define an association between objects. Aggregation is a special form of association. Composition is a special form of aggregation.

**Example:** A Student and a Faculty are having an association.

## What is Aggregation?

Aggregation is a special case of association. A directional association between objects. When an object ‘has-a’ another object, then you have got an aggregation between them. Direction between them specified which object contains the other object. 

Aggregation is also called a “Has-a” relationship.

Aggregation is a relationship between two classes that is best described as a “has-a” and “whole/part” relationship. It is a more specialized version of the association relationship. 

The aggregate class contains a reference to another class and is said to have ownership of that class. Each class referenced is considered to be part-of the aggregate class.Ownership occurs because there can be no cyclic references in an aggregation relationship. 

If Class A contains a reference to Class B and Class B contains a reference to Class A then no clear ownership can be determined and the relationship is simply one of association.

For example, imagine a Student class that stores information about individual students at a school. Now let’s say there is a Subject class that holds the details about a particular subject (e.g., history, geography). If the Student class is defined to contain a Subject object then it can be said that the Student object has-a Subject object. The Subject object also makes up part-of the Student object, after all there is no student without a subject to study. The Student object is therefore the owner of the Subject object.

**Example:**

There is an aggregation relationship between Student class and the Subject class:

```java
public class Subject {
    private String name;
    public void setName(String name){
    	this.name = name;
	}
    public String getName(){
    	return name;
    }
}
public class Student {
	private Subject[] studyAreas = new Subject[10];
	//the rest of the Student class
}
```

## What is Composition?

Composition is a special case of aggregation. In a more specific manner, a restricted aggregation is called composition. When an object contains the other object, if the contained object cannot exist without the existence of container object, then it is called composition.

**Example:** A class contains students. A student cannot exist without a class. There exists composition between class and students.

## What is Abstraction?

Abstraction is specifying the framework and hiding the implementation level information. Concreteness will be built on top of the abstraction. It gives you a blueprint to follow to while implementing the details. Abstraction reduces the complexity by hiding low level details.

**Example:** A wire frame model of a car.

## What is Generalization?

Generalization uses a “is-a” relationship from a specialization to the generalization class. Common structure and behaviour are used from the specialization to the generalized class. At a very broader level you can understand this as inheritance. Why I take the term inheritance is, you can relate this term very well. Generalization is also called a “Is-a” relationship.

**Example:** Consider there exists a class named Person. A student is a person. A faculty is a person. Therefore here the relationship between student and person, similarly faculty and person is generalization.



## Reference

[https://rakeshnarayan.wordpress.com/2012/06/01/what-is-association-aggregationcomposition-abstraction-generalization/](https://rakeshnarayan.wordpress.com/2012/06/01/what-is-association-aggregationcomposition-abstraction-generalization/)