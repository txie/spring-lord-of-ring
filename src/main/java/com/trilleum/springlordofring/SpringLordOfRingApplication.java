package com.trilleum.springlordofring;

import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.*;
import java.util.List;

@SpringBootApplication
public class SpringLordOfRingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringLordOfRingApplication.class, args);
	}
	@Bean
	CommandLineRunner initData(EmployeeRepository employeeRepository,
							   ManagerRepository managerRepository) {
		return args -> {
			Manager m = managerRepository.save(new Manager("Gandalf"));
			Manager m2 = managerRepository.save(new Manager("Sauron"));
			employeeRepository.save(new Employee("Frodo", "Baggins", "ring bearer", m));
			employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar", m));
			employeeRepository.save(new Employee("Samwise", "Gambee", "gardener", m2));
		};
	}
}

@Data
@Entity
class Employee {
	@Id
	@GeneratedValue
	Long id;
	String firstName;
	String lastName;
	String role;

	@ManyToOne
	Manager manager;

	private Employee() {}

	public Employee(String firstName, String lastName, String role, Manager manager) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
		this.manager = manager;
	}
}

@RepositoryRestResource
interface EmployeeRepository extends CrudRepository<Employee, Long> {
	List<Employee> findByLastName(@Param("q") String lastName);
	List<Employee> findEmployeesByLastName(@Param("q") String lastName);
	List<Employee> findByRole(@Param("q") String role);
}

@Data
@Entity
class Manager {
	@Id
	@GeneratedValue
	Long id;
	String name;

	@OneToMany(mappedBy = "manager")
	List<Employee> employees;

	private Manager() {}

	public Manager(String name) {
		this.name = name;
	}
}

@RepositoryRestResource
interface ManagerRepository extends CrudRepository<Manager, Long> {
	List<Manager> findByEmployeesRoleContains(@Param("q") String role);
	List<Manager> findDistinctByEmployeesLastName(@Param("q") String lastName);
}