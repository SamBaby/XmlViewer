package main;

public class Employees {
	private String ID;
	private String firstname;
	private String lastname;
	private String age;
	private String salary;
	/*
	public Employees(String ID, String firstname, String lastname, String age, String salary){
		this.ID = ID;
		this.firstname = firstname;
		this.lastname = lastname;
		this.age = age;
		this.salary = salary;
	}
	*/
	public Employees() {
		ID = null;
		firstname = null;
		lastname = null;
		age = null;
		salary = null;
	}
	
	public void setID (String ID) {
		this.ID= ID;
	}
	
	public void setFirstname (String firstname) {
		this.firstname= firstname;
	}
	
	public void setLastname (String lastname) {
		this.lastname = lastname;
	}
	
	public void setAge(String age) {
		this.age = age;
	}
	
	public void setSalary(String salary) {
		this.salary = salary;
	}
	
	public String getID () {
		return ID;
	}
	
	public String getFirstname () {
		return firstname;
	}
	
	public String getLastname () {
		return lastname;
	}
	
	public String getAge() {
		return age;
	}
	
	public String getSalary() {
		return salary;
	}
}
