/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

/**
 *
 * @author USER
 */
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class PatientManagementSystem {

    Connection conn;
    Scanner scanner = new Scanner(System.in);

    public void db_connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "easy@#123");
            if (conn != null) {
                System.out.println("Connected to the database.");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public void createPatientsTable() {
        String createPatientsTable = "CREATE TABLE IF NOT EXISTS patients "
                + "(id INT AUTO_INCREMENT PRIMARY KEY,"
                + " name VARCHAR(255), "
                + " age INT, "
                + " gender VARCHAR(10), "
                + " mobile_number VARCHAR(20))";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(createPatientsTable);
            System.out.println("Patients table created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating patients table: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

    public void createDoctorsTable() {
        String createDoctorsTable = "CREATE TABLE IF NOT EXISTS doctors "
                + "(id INT AUTO_INCREMENT PRIMARY KEY,"
                + " name VARCHAR(255), "
                + " specialization VARCHAR(255))";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(createDoctorsTable);
            System.out.println("Doctors table created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating doctors table: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

    public void createAppointmentsTable() {
        String createAppointmentsTable = "CREATE TABLE IF NOT EXISTS appointments "
                + "(id INT AUTO_INCREMENT PRIMARY KEY,"
                + " patient_id INT, "
                + " doctor_id INT, "
                + " appointment_date DATE, "
                + " appointment_time TIME, "
                + " reason VARCHAR(255), "
                + " FOREIGN KEY(patient_id) REFERENCES patients(id),"
                + " FOREIGN KEY(doctor_id) REFERENCES doctors(id))";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(createAppointmentsTable);
            System.out.println("Appointments table created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating appointments table: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

    public void addPatient() {
        System.out.println("Adding a new patient...");
        // Collect patient information from user
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.print("Enter patient age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter patient gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter patient mobile number: ");
        String mobileNumber = scanner.nextLine();

        // Insert patient into database
        String insertPatientQuery = "INSERT INTO patients (name, age, gender, mobile_number) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertPatientQuery)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setString(4, mobileNumber);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient added successfully.");
            } else {
                System.out.println("Failed to add patient.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding patient: " + e.getMessage());
        }
    }

    public void updatePatient() {
        System.out.println("Updating patient information...");
        // Ask for patient ID to update
        System.out.print("Enter patient ID to update: ");
        int patientId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Check if the patient exists
        if (!isPatientExists(patientId)) {
            System.out.println("Patient with ID " + patientId + " does not exist.");
            return;
        }

        // Prompt user to choose which field to update
        System.out.println("Which field do you want to update?");
        System.out.println("1. Name");
        System.out.println("2. Age");
        System.out.println("3. Gender");
        System.out.println("4. Mobile Number");
        System.out.print("Enter your choice: ");
        int fieldChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Collect updated information based on the chosen field
        String updateField = "";
        String newValuePrompt = "";
        switch (fieldChoice) {
            case 1:
                updateField = "name";
                newValuePrompt = "Enter new name: ";
                break;
            case 2:
                updateField = "age";
                newValuePrompt = "Enter new age: ";
                break;
            case 3:
                updateField = "gender";
                newValuePrompt = "Enter new gender: ";
                break;
            case 4:
                updateField = "mobile_number";
                newValuePrompt = "Enter new mobile number: ";
                break;
            default:
                System.out.println("Invalid choice. No field updated.");
                return;
        }

        // Collect the new value for the chosen field
        System.out.print(newValuePrompt);
        String newValue = scanner.nextLine();

        // Update the chosen field in the database
        String updatePatientQuery = "UPDATE patients SET " + updateField + " = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updatePatientQuery)) {
            pstmt.setString(1, newValue);
            pstmt.setInt(2, patientId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient " + updateField + " updated successfully.");
            } else {
                System.out.println("Failed to update patient " + updateField + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error updating patient: " + e.getMessage());
        }
    }

    public boolean isPatientExists(int patientId) {
        String checkPatientQuery = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkPatientQuery)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If a result is found, patient exists
        } catch (SQLException e) {
            System.out.println("Error checking patient existence: " + e.getMessage());
            return false; // Assume patient does not exist if an error occurs
        }
    }

    public void displayAllPatients() {
        System.out.println("Displaying all patients...");
        // Retrieve all patients from the database
        String selectAllPatientsQuery = "SELECT * FROM patients";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectAllPatientsQuery);
            // Display table header for patients
            System.out.println("+-----+--------------------+-----+----------+--------------------+");
            System.out.printf("| %-5s | %-20s | %-5s | %-10s | %-20s|\n", "ID", "Name", "Age", "Gender", "Mobile Number");
            System.out.println("+-----+--------------------+-----+----------+--------------------+");
            // Display each patient
            while (rs.next()) {
                System.out.printf("| %-5s | %-20s | %-5s | %-10s | %-20s|\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("mobile_number"));
            }
            System.out.println("+-----+--------------------+-----+----------+--------------------+");
        } catch (SQLException e) {
            System.out.println("Error displaying patients: " + e.getMessage());
        }
    }

    public void searchPatient() {
        System.out.println("Searching for a patient...");
        // Ask for the first letter of the patient's name to search
        System.out.print("Enter the first letter of the patient's name to search: ");
        String searchLetter = scanner.nextLine().trim().substring(0, 1).toLowerCase(); // Convert to lowercase

        // Search for patients in the database whose names start with the specified letter
        String searchPatientQuery = "SELECT * FROM patients WHERE LOWER(LEFT(name, 1)) = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(searchPatientQuery)) {
            pstmt.setString(1, searchLetter);
            ResultSet rs = pstmt.executeQuery();

            // Display search results as a table
            System.out.println("+-----+--------------------+-----+----------+--------------------+");
            System.out.printf("| %-5s | %-20s | %-5s | %-10s | %-20s|\n", "ID", "Name", "Age", "Gender", "Mobile Number");
            System.out.println("+-----+--------------------+-----+----------+--------------------+");
            boolean found = false; // Flag to check if any patient is found
            while (rs.next()) {
                found = true;
                System.out.printf("| %-5d | %-20s | %-5d | %-10s | %-20s|\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("mobile_number"));
            }
            if (!found) {
                System.out.println("| No patient found with a name starting with the letter '" + searchLetter.toUpperCase() + "'.");
            }
            System.out.println("+-----+--------------------+-----+----------+--------------------+");
        } catch (SQLException e) {
            System.out.println("Error searching for patient: " + e.getMessage());
        }
    }

    public void deletePatient() {
        System.out.println("Deleting a patient...");
        // Ask for patient ID to delete
        System.out.print("Enter patient ID to delete: ");
        int patientId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Check if the patient exists
        if (!isPatientExists(patientId)) {
            System.out.println("Patient with ID " + patientId + " does not exist.");
            return;
        }

        // Delete patient from the database
        String deletePatientQuery = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deletePatientQuery)) {
            pstmt.setInt(1, patientId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient deleted successfully.");
            } else {
                System.out.println("Failed to delete patient.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting patient: " + e.getMessage());
        }
    }

    public void displayDoctors() {
        System.out.println("Displaying all doctors...");
        // Retrieve all doctors from the database
        String selectAllDoctorsQuery = "SELECT * FROM doctors";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectAllDoctorsQuery);
            // Display table header for doctors
            System.out.println("+-----+--------------------+--------------------+");
            System.out.printf("| %-3s | %-20s | %-20s |\n", "ID", "Name", "Specialization");
            System.out.println("+-----+--------------------+--------------------+");
            // Display each doctor
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String specialization = rs.getString("specialization");
                System.out.printf("| %-3d | %-20s | %-20s |\n", id, name, specialization);
            }
            System.out.println("+-----+--------------------+--------------------+");
        } catch (SQLException e) {
            System.out.println("Error displaying doctors: " + e.getMessage());
        }
    }

    public void addAppointment() {
        System.out.println("Adding a new appointment...");
        // Collect appointment information from user
        System.out.print("Enter patient ID: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter doctor ID: ");
        int doctorId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDateStr = scanner.nextLine();
        LocalDate appointmentDate = LocalDate.parse(appointmentDateStr);
        LocalDate currentDate = LocalDate.now();

        // Check if appointment date is in the future
        if (appointmentDate.isBefore(currentDate)) {
            System.out.println("Error: Appointment date cannot be in the past.");
            return; // Exit the method
        }

        System.out.print("Enter appointment time (HH:MM): ");
        String appointmentTime = scanner.nextLine();
        System.out.print("Enter reason for appointment: ");
        String reason = scanner.nextLine();

        // Check if appointment slot is available
        if (isAppointmentSlotAvailable(patientId, doctorId, appointmentDate, appointmentTime)) {
            // Insert appointment into database
            String insertAppointmentQuery = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertAppointmentQuery)) {
                pstmt.setInt(1, patientId);
                pstmt.setInt(2, doctorId);
                pstmt.setString(3, appointmentDateStr);
                pstmt.setString(4, appointmentTime);
                pstmt.setString(5, reason);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Appointment added successfully.");
                } else {
                    System.out.println("Failed to add appointment.");
                }
            } catch (SQLException e) {
                System.out.println("Error adding appointment: " + e.getMessage());
            }
        } else {
            System.out.println("The appointment slot is not available....Please Book another slot");
        }
    }

    public boolean isAppointmentSlotAvailable(int patientId, int doctorId, LocalDate appointmentDate, String appointmentTime) {
        String checkAvailabilityQuery = "SELECT * FROM appointments WHERE (doctor_id = ? AND appointment_date = ? AND appointment_time = ?) OR (patient_id = ? AND appointment_date = ? AND appointment_time = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(checkAvailabilityQuery)) {
            pstmt.setInt(1, doctorId);
            pstmt.setObject(2, appointmentDate);
            pstmt.setString(3, appointmentTime);
            pstmt.setInt(4, patientId);
            pstmt.setObject(5, appointmentDate);
            pstmt.setString(6, appointmentTime);
            ResultSet rs = pstmt.executeQuery();
            return !rs.next(); // If there are no results, slot is available
        } catch (SQLException e) {
            System.out.println("Error checking appointment availability: " + e.getMessage());
            return false; // Assume slot is unavailable if an error occurs
        }
    }

    public void displayAppointments() {
        System.out.println("Displaying all appointments...");
        // Retrieve all appointments from the database
        String selectAllAppointmentsQuery = "SELECT * FROM appointments";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectAllAppointmentsQuery);
            // Display table header for appointments
            System.out.printf("+-----+--------------+------------+------------------+------------------+----------------------+\n");
            System.out.printf("| %-3s | %-12s | %-10s | %-16s | %-16s | %-20s |\n", "ID", "Patient ID", "Doctor ID", "Appointment Date", "Appointment Time", "Reason");
            System.out.printf("+-----+--------------+------------+------------------+------------------+----------------------+\n");
            // Display each appointment
            while (rs.next()) {
                System.out.printf("| %-3d | %-12d | %-10d | %-16s | %-15s | %-20s |\n",
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("reason"));
            }
            System.out.println("+-----+--------------+------------+------------------+------------------+----------------------+");
        } catch (SQLException e) {
            System.out.println("Error displaying appointments: " + e.getMessage());
        }
    }

    public void cancelAppointment(int appointmentId) {
        // Check if the appointment ID exists
        if (!isAppointmentExists(appointmentId)) {
            System.out.println("Appointment with ID " + appointmentId + " does not exist.");
            return;
        }

        // Delete appointment from the database
        String deleteAppointmentQuery = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteAppointmentQuery)) {
            pstmt.setInt(1, appointmentId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Appointment with ID " + appointmentId + " has been canceled successfully.");
            } else {
                System.out.println("Failed to cancel appointment with ID " + appointmentId + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error canceling appointment: " + e.getMessage());
        }
    }

    public boolean isAppointmentExists(int appointmentId) {
        String checkAppointmentQuery = "SELECT * FROM appointments WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkAppointmentQuery)) {
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If a result is found, appointment exists
        } catch (SQLException e) {
            System.out.println("Error checking appointment existence: " + e.getMessage());
            return false; // Assume appointment does not exist if an error occurs
        }
    }

    public static void main(String[] args) {
        PatientManagementSystem pms = new PatientManagementSystem();
        pms.db_connect();
        pms.createPatientsTable();
        pms.createDoctorsTable();
        pms.createAppointmentsTable();

        int choice;
        do {
            System.out.println("\n\n---- Patient Management System Menu ----");
            System.out.println("1. Add Patient");
            System.out.println("2. Update Patient");
            System.out.println("3. Delete Patient");
            System.out.println("4. Search Patient");
            System.out.println("5. Display All Patients");
            System.out.println("6. Display Doctors");
            System.out.println("7. Add Appointment");
            System.out.println("8. Display Appointments");
            System.out.println("9. Cancel Appointment"
            );
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            choice = pms.scanner.nextInt();
            pms.scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    pms.addPatient();
                    break;
                case 2:
                    pms.updatePatient();
                    break;
                case 3:
                    pms.deletePatient();
                    break;
                case 4:
                    pms.searchPatient();
                    break;
                case 5:
                    pms.displayAllPatients();
                    break;
                case 6:
                    pms.displayDoctors();
                    break;
                case 7:
                    pms.addAppointment();
                    break;
                case 8:
                    pms.displayAppointments();
                    break;
                case 9:
                    // Prompt for the ID of the appointment to cancel
                    System.out.print("Enter the ID of the appointment to cancel: ");
                    int appointmentIdToCancel = pms.scanner.nextInt();
                    pms.scanner.nextLine(); // Consume newline

                    // Call the cancelAppointment method with the provided ID
                    pms.cancelAppointment(appointmentIdToCancel);
                    break;
                case 10:
                    System.out.println("Exiting...");
                    System.out.println("Thank you for using the Patient Management System!!!5"
                            + "");

                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 10);
    }

}
