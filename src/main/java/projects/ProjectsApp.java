package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Material;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

//import projects.dao.DbConnection;

public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);
	private Project curProject;
	
	//@formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
			);
	//@formatter:on
	public static void main(String[] args) {
		//DbConnection.getConnection();
		new ProjectsApp().processUserSelections();
		
	}
	private void processUserSelections() {
		boolean done = false;
		while (!done) {
			try {
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
					break;
				case 5:
					deleteProject();
					break;
				default:
					System.out.println("\nNot a valid selection. Try again");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e);
			}
		}
		
	}
	private void deleteProject() {
		listProjects();
		Integer projectId = getIntInput("Enter the project ID you want to delete.");
		if (Objects.isNull(projectId)) {
			System.out.println("\nPlease select a project to delete.");
			return;
		}
		ProjectService.deleteProject(projectId);
		System.out.println("Project ID=" + projectId + " has been deleted.");
		if (Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
		
		
	}
	private void updateProjectDetails() {
		if (Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project to update.");
			return;
		}
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		String projectNotes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		Integer projectDifficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
		BigDecimal projectEstimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal projectActualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Project project = new Project();
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setNotes(Objects.isNull(projectNotes) ? curProject.getNotes() : projectNotes);
		project.setDifficulty(Objects.isNull(projectDifficulty) ? curProject.getDifficulty() : projectDifficulty);
		project.setEstimatedHours(Objects.isNull(projectEstimatedHours) ? curProject.getEstimatedHours() : projectEstimatedHours);
		project.setActualHours(Objects.isNull(projectActualHours) ? curProject.getActualHours() : projectActualHours);
		project.setProjectId(curProject.getProjectId());
		ProjectService.modifyProjectDetails(project); 
		curProject = ProjectService.fetchProjectById(curProject.getProjectId());
		
	}
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a Project ID to select a project");
		curProject = null;
		curProject = ProjectService.fetchProjectById(projectId);
		if (Objects.isNull(curProject)) {
			System.out.println("\nInvalid project ID");
		}
		
	}
	private void listProjects() {
		List<Project> projects = ProjectService.fetchAllProjects();
		System.out.println("\nProjects: ");
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
		
	}
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project DbProject = ProjectService.addProject(project);
		System.out.println("You have successfully added project " + DbProject);
	}
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}
	private int getUserSelection() {
		PrintOperations();
		Integer input = getIntInput("Enter a menu selection");
			
		return Objects.isNull(input) ? -1 : input;
	}
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		return input.isBlank() ? null : input.trim();
	}
	private void PrintOperations() {
		System.out.println("\nThese are your menu choices. Press the Enter key to quit.");
		operations.forEach(line -> System.out.println("   " + line));
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("You are working with project: " + curProject);
		}
		
	}
	private boolean exitMenu() {
		System.out.println("\nExiting menu.");
		return true;
	}	
		

}
