import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuizSystem {
    private static final String USERS_FILE = "src/main/resources/users.json";
    private static final String QUIZ_FILE = "src/main/resources/quiz.json";
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger LOGGER = Logger.getLogger(QuizSystem.class.getName());

    public static void main(String[] args) {
        login();
    }

    // Login system
    private static void login() {
        try {
            // Load users from users.json
            JSONArray users = (JSONArray) new JSONParser().parse(new FileReader(USERS_FILE));

            while (true) {  // Loop to allow retry if invalid credentials are entered
                System.out.print("System:> Enter your username: ");
                String username = scanner.nextLine();

                System.out.print("System:> Enter your password: ");
                String password = scanner.nextLine();

                boolean validCredentials = false;

                // Loop through users to check credentials
                for (Object obj : users) {
                    JSONObject user = (JSONObject) obj;
                    if (user.get("username").equals(username) && user.get("password").equals(password)) {
                        String role = (String) user.get("role");
                        if (role.equals("admin")) {
                            System.out.println("System:> Welcome admin! Please create new questions in the question bank.");
                            adminFunctionality();
                        } else if (role.equals("student")) {
                            System.out.println("System:> Welcome " + username + " to the quiz! We will throw you 10 questions. Are you ready? Press 's' to start.");
                            if (scanner.nextLine().equals("s")) {
                                studentFunctionality();
                            }
                        }
                        validCredentials = true;
                        break;  // Exit the loop if valid credentials are found
                    }
                }

                // If credentials are invalid, display an error message and retry
                if (!validCredentials) {
                    System.out.println("Invalid credentials. Please try again.");
                } else {
                    break;  // Exit the outer loop when valid credentials are entered
                }
            }

        } catch (IOException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred", e);
        }
    }


    // Admin functionality to add questions
    @SuppressWarnings("unchecked")
    private static void adminFunctionality() {
        try {
            JSONArray quizData = (JSONArray) new JSONParser().parse(new FileReader(QUIZ_FILE));

            while (true) {
                JSONObject newQuestion = new JSONObject();
                System.out.print("Admin:> Input your question: ");
                newQuestion.put("question", scanner.nextLine());

                System.out.print("System: Input option 1: ");
                newQuestion.put("option1", scanner.nextLine());

                System.out.print("System: Input option 2: ");
                newQuestion.put("option2", scanner.nextLine());

                System.out.print("System: Input option 3: ");
                newQuestion.put("option3", scanner.nextLine());

                System.out.print("System: Input option 4: ");
                newQuestion.put("option4", scanner.nextLine());

                System.out.print("System: What is the answer key? (1-4): ");
                newQuestion.put("answerKey", scanner.nextInt());
                scanner.nextLine();  // Consume newline

                quizData.add(newQuestion);

                // Save the question to quiz.json
                FileWriter file = new FileWriter(QUIZ_FILE);
                file.write(quizData.toJSONString());
                file.flush();
                file.close();

                System.out.println("System:> Saved successfully!");

                // Input validation for 's' or 'q'
                String choice;
                do {
                    System.out.print("Do you want to add more questions? (press 's' for start and 'q' for quit): ");
                    choice = scanner.nextLine().trim().toLowerCase();

                    if (!choice.equals("s") && !choice.equals("q")) {
                        System.out.println("Invalid input. Please press 's' for start and 'q' for quit.");
                    }
                } while (!choice.equals("s") && !choice.equals("q"));

                // Exit the loop if 'q' is pressed
                if (choice.equals("q")) {
                    break;
                }
            }
        } catch (IOException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred", e);
        }
    }

    // Student functionality to take quiz
    private static void studentFunctionality() {
        try {
            while (true) {  // Loop to allow retry
                JSONArray quizData = (JSONArray) new JSONParser().parse(new FileReader(QUIZ_FILE));
                if (quizData.size() < 10) {
                    System.out.println("Not enough questions in the quiz bank to start the quiz.");
                    return;
                }

                int score = 0;
                Collections.shuffle(quizData);  // Shuffle the questions
                for (int i = 0; i < 10; i++) {
                    JSONObject question = (JSONObject) quizData.get(i);
                    System.out.println("\n[Question " + (i + 1) + "] " + question.get("question"));
                    System.out.println("1. " + question.get("option1"));
                    System.out.println("2. " + question.get("option2"));
                    System.out.println("3. " + question.get("option3"));
                    System.out.println("4. " + question.get("option4"));

                    System.out.print("Your answer (1-4): ");
                    int answer = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    if (answer == ((Long) question.get("answerKey")).intValue()) {
                        score++;
                    }
                }

                // Display result
                System.out.println("\nYou have completed the quiz. Your score is " + score + "/10.");

                if (score >= 8) {
                    System.out.println("Excellent! You have got " + score + " out of 10.");
                } else if (score >= 5) {
                    System.out.println("Good! You have got " + score + " out of 10.");
                } else if (score >= 3) {
                    System.out.println("Very poor! You have got " + score + " out of 10.");
                } else {
                    System.out.println("Very sorry! You have failed. You got " + score + " out of 10.");
                }

                // Ask the student if they want to retry the quiz
                String choice;
                do {
                    System.out.print("Would you like to start again? Press 's' for start or 'q' for quit: ");
                    choice = scanner.nextLine().trim().toLowerCase();

                    if (!choice.equals("s") && !choice.equals("q")) {
                        System.out.println("Invalid input. Please press 's' for start or 'q' for quit.");
                    }
                } while (!choice.equals("s") && !choice.equals("q"));

                // Exit the loop if the student presses 'q'
                if (choice.equals("q")) {
                    System.out.println("You have quit the quiz. Thank you for participating!");
                    break;
                }
            }
        } catch (IOException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred", e);
        }
    }

}
