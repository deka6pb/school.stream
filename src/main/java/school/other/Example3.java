package school.other;

import java.util.*;
import java.util.stream.Collectors;

public class Example3 {
    enum Speciality {
        Biology, ComputerScience, Economics, Finance,
        History, Philosophy, Physics, Psychology
    }

    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
                new Student("Alex", Speciality.Physics, 1),
                new Student("Rika", Speciality.Biology, 4),
                new Student("Julia", Speciality.Biology, 2),
                new Student("Steve", Speciality.History, 4),
                new Student("Mike", Speciality.Finance, 1),
                new Student("Hinata", Speciality.Biology, 2),
                new Student("Richard", Speciality.History, 1),
                new Student("Kate", Speciality.Psychology, 2),
                new Student("Sergey", Speciality.ComputerScience, 4),
                new Student("Maximilian", Speciality.ComputerScience, 3),
                new Student("Tim", Speciality.ComputerScience, 5),
                new Student("Ann", Speciality.Psychology, 1)
        );

//        // Все специальности где есть учащиеся
//        students.stream()
//                .map(Student::getSpeciality)
//                .distinct()
//                .sorted(Comparator.comparing(Enum::name))
//                .forEach(System.out::println);



//        // Вывести количество учащихся на каждой из специальностей.
//        students.stream()
//                .collect(Collectors.groupingBy(
//                        Student::getSpeciality, Collectors.counting()))
//                .forEach((s, count) -> System.out.println(s + ": " + count));



        // Сгруппировать студентов по специальностям, сохраняя алфавитный порядок специальности, а затем сгруппировать по курсу.
        Map<Speciality, Map<Integer, List<Student>>> result = students.stream()
                .sorted(Comparator
                        .comparing(Student::getSpeciality, Comparator.comparing(Enum::name))
                        .thenComparing(Student::getCourse)
                )
                .collect(Collectors.groupingBy(
                        Student::getSpeciality,
                        LinkedHashMap::new,
                        Collectors.groupingBy(Student::getCourse)));

        result.forEach((s, map) -> {
            System.out.println("-= " + s + " =-");
            map.forEach((course, list) -> System.out.format("%d: %s%n", course, list.stream()
                    .map(Student::getFirstName)
                    .sorted()
                    .collect(Collectors.joining(", ")))
            );
            System.out.println();
        });

    }
}

class Student {
    private final String firstName;
    private final Example3.Speciality speciality;
    private final int course;

    public Student(String firstName, Example3.Speciality speciality, int course) {
        this.firstName = firstName;
        this.speciality = speciality;
        this.course = course;
    }

    public int getCourse() {
        return course;
    }

    public Example3.Speciality getSpeciality() {
        return speciality;
    }

    public String getFirstName() {
        return firstName;
    }
}
