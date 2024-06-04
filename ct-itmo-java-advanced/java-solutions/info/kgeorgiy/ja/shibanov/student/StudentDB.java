package info.kgeorgiy.ja.shibanov.student;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.GroupQuery;
import info.kgeorgiy.java.advanced.student.Student;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements GroupQuery {

    private static final Comparator<Student> NAME_COMPARATOR = Comparator
            .comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .thenComparing(Student::getId, Comparator.reverseOrder());

    private static final Function<String, Predicate<Student>> EQUALS_BY_FIRST_NAME = makeEqualsPredicate(Student::getFirstName);
    private static final Function<String, Predicate<Student>> EQUALS_BY_LAST_NAME = makeEqualsPredicate(Student::getLastName);
    private static final Function<GroupName, Predicate<Student>> EQUALS_BY_GROUP = makeEqualsPredicate(Student::getGroup);

    private final Map<List<Student>, Integer> distinctListSize = new HashMap<>();


    private static <T> Stream<Student> findStudentsStreamBy(
            final Collection<Student> students,
            final Function<T, Predicate<Student>> predicateByElement,
            final T element
    ) {
        return students.stream().filter(predicateByElement.apply(element)).sorted(NAME_COMPARATOR);
    }

    private static <T> Function<T, Predicate<Student>> makeEqualsPredicate(Function<Student, T> f) {
        return target -> student -> Objects.equals(f.apply(student), target);
    }

    private static <T, A, R> R applyMapToStudentList(
            final List<Student> list,
            final Function<Student, ? extends T> mapper,
            final Collector<T, A, R> collector
    ) {
        return list.stream().map(mapper).collect(collector);
    }

    private static <T> List<T> applyMapToStudentList(final List<Student> list, final Function<Student, ? extends T> mapper) {
        return applyMapToStudentList(list, mapper, Collectors.toList());
    }

    private static List<Student> sortStudentsBy(
            final Collection<Student> students,
            final Comparator<Student> comparator
    ) {
        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    private static <T> List<Student> findStudentsBy(final Collection<Student> students,
                                                    final Function<T, Predicate<Student>> predicateByElement,
                                                    final T element) {
        return findStudentsStreamBy(students, predicateByElement, element)
                .collect(Collectors.toList());
    }


    private Set<Map.Entry<GroupName, List<Student>>> toGroups(final Collection<Student> students) {
        return students
                .stream()
                .collect(Collectors.groupingBy(Student::getGroup))
                .entrySet();
    }

    private List<Group> getGroupsBy(final Collection<Student> students, final Function<Collection<Student>, List<Student>> mapper) {
        return toGroups(students)
                .stream()
                .map(entry -> new Group(entry.getKey(), mapper.apply(entry.getValue())))
                .sorted(Comparator.comparing(Group::getName))
                .collect(Collectors.toList());
    }

    public GroupName getLargestGroupBy(
            final Collection<Student> students,
            final Function<List<Student>, Integer> sizeOfList,
            final Comparator<GroupName> groupNameComparator) {
        return toGroups(students)
                .stream()
                .max(Map.Entry
                        .<GroupName, List<Student>>comparingByValue(Comparator.comparing(sizeOfList))
                        .thenComparing(Map.Entry.comparingByKey(groupNameComparator)))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public List<Group> getGroupsByName(final Collection<Student> students) {
        return getGroupsBy(students, this::sortStudentsByName);
    }

    @Override
    public List<Group> getGroupsById(final Collection<Student> students) {
        return getGroupsBy(students, this::sortStudentsById);
    }

    @Override
    public GroupName getLargestGroup(final Collection<Student> students) {
        return getLargestGroupBy(students, List::size, Comparator.naturalOrder());
    }


    private int sizeOfDistinctList(final List<Student> list) {
        return distinctListSize.containsKey(list)
                ? distinctListSize.get(list)
                : Optional.ofNullable(distinctListSize.put(list, getDistinctFirstNames(list).size()))
                    .orElse(distinctListSize.get(list));
    }

    @Override
    public GroupName getLargestGroupFirstName(final Collection<Student> students) {
        return getLargestGroupBy(students, this::sizeOfDistinctList, Comparator.reverseOrder());
    }

    @Override
    public List<String> getFirstNames(final List<Student> students) {
        return applyMapToStudentList(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(final List<Student> students) {
        return applyMapToStudentList(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(final List<Student> students) {
        return applyMapToStudentList(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(final List<Student> students) {
        return applyMapToStudentList(students,
                student -> student.getFirstName() + " " + student.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(final List<Student> students) {
        return applyMapToStudentList(students, Student::getFirstName, Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(final List<Student> students) {
        return students.stream()
                .max(Comparator.naturalOrder())
                .map(Student::getFirstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(final Collection<Student> students) {
        return sortStudentsBy(students, Comparator.naturalOrder());
    }

    @Override
    public List<Student> sortStudentsByName(final Collection<Student> students) {
        return sortStudentsBy(students, NAME_COMPARATOR);
    }

    @Override
    public List<Student> findStudentsByFirstName(final Collection<Student> students, final String name) {
        return findStudentsBy(students, EQUALS_BY_FIRST_NAME, name);
    }

    @Override
    public List<Student> findStudentsByLastName(final Collection<Student> students, final String name) {
        return findStudentsBy(students, EQUALS_BY_LAST_NAME, name);
    }

    @Override
    public List<Student> findStudentsByGroup(final Collection<Student> students, final GroupName group) {
        return findStudentsBy(students, EQUALS_BY_GROUP, group);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(final Collection<Student> students, final GroupName group) {
        return findStudentsStreamBy(students, EQUALS_BY_GROUP, group)
                .collect(
                        Collectors.toMap(
                                Student::getLastName,
                                Student::getFirstName,
                                BinaryOperator.minBy(Comparator.naturalOrder())
                        )
                );
    }
}
