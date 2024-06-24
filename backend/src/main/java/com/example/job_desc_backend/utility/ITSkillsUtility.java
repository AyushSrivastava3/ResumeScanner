package com.example.job_desc_backend.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ITSkillsUtility {
    private static final Map<String, List<String>> SKILLS_MAP = new HashMap<>();
    private static final Map<String, String> REVERSE_SKILLS_MAP = new HashMap<>();

    static {
        SKILLS_MAP.put("Java", List.of("Java", "java", "JAVA"));
        SKILLS_MAP.put("Python", List.of("Python", "python", "PYTHON"));
        SKILLS_MAP.put("JavaScript", List.of("JavaScript", "Javascript", "javascript", "JS", "js"));
        SKILLS_MAP.put("C#", List.of("C#", "Csharp", "c#", "csharp"));
        SKILLS_MAP.put("C++", List.of("C++", "CPP", "cpp", "C Plus Plus"));
        SKILLS_MAP.put("PHP", List.of("PHP", "php", "Php"));
        SKILLS_MAP.put("Swift", List.of("Swift", "swift", "SWIFT"));
        SKILLS_MAP.put("Kotlin", List.of("Kotlin", "kotlin", "KOTLIN"));
        SKILLS_MAP.put("SQL", List.of("SQL", "sql", "Sql"));
        SKILLS_MAP.put("HTML", List.of("HTML", "html", "Html"));
        SKILLS_MAP.put("CSS", List.of("CSS", "css", "Css"));
        SKILLS_MAP.put("React", List.of("React", "react", "React.js", "ReactJS"));
        SKILLS_MAP.put("Angular", List.of("Angular", "angular", "AngularJS", "Angular.js"));
        SKILLS_MAP.put("Node.js", List.of("Node.js", "Nodejs", "Node JS", "nodejs", "node.js"));
        SKILLS_MAP.put("Spring", List.of("Spring", "spring", "Spring Framework", "Spring Boot", "SpringBoot", "spring boot", "springboot"));
        SKILLS_MAP.put("Django", List.of("Django", "django", "DJANGO"));
        SKILLS_MAP.put("Flask", List.of("Flask", "flask", "FLASK"));
        SKILLS_MAP.put("Ruby", List.of("Ruby", "ruby", "RUBY"));
        SKILLS_MAP.put("Rails", List.of("Rails", "rails", "Ruby on Rails", "ruby on rails"));
        SKILLS_MAP.put("Machine Learning", List.of("Machine Learning", "machine learning", "ML", "ml"));
        SKILLS_MAP.put("Data Science", List.of("Data Science", "data science", "Data Scientist", "data scientist"));
        SKILLS_MAP.put("AWS", List.of("AWS", "aws", "Amazon Web Services", "amazon web services"));
        SKILLS_MAP.put("Azure", List.of("Azure", "azure", "Microsoft Azure", "microsoft azure"));
        SKILLS_MAP.put("Docker", List.of("Docker", "docker", "DOCKER"));
        SKILLS_MAP.put("Kubernetes", List.of("Kubernetes", "kubernetes", "K8s", "k8s"));
        SKILLS_MAP.put("Git", List.of("Git", "git", "GIT"));
        SKILLS_MAP.put("CI/CD", List.of("CI/CD", "ci/cd", "Continuous Integration", "continuous integration", "Continuous Delivery", "continuous delivery"));
        SKILLS_MAP.put("Agile", List.of("Agile", "agile", "AGILE"));
        SKILLS_MAP.put("Scrum", List.of("Scrum", "scrum", "SCRUM"));
        SKILLS_MAP.put("Jira", List.of("Jira", "jira", "JIRA"));
        SKILLS_MAP.put("DevOps", List.of("DevOps", "devops", "Dev Ops", "dev ops"));
        SKILLS_MAP.put("Hadoop", List.of("Hadoop", "hadoop", "HADOOP"));
        SKILLS_MAP.put("Spark", List.of("Spark", "spark", "SPARK"));
        SKILLS_MAP.put("Tableau", List.of("Tableau", "tableau", "TABLEAU"));
        SKILLS_MAP.put("Power BI", List.of("Power BI", "power bi", "PowerBI", "powerbi"));
        SKILLS_MAP.put("TensorFlow", List.of("TensorFlow", "tensorflow", "TENSORFLOW"));
        SKILLS_MAP.put("Keras", List.of("Keras", "keras", "KERAS"));
        SKILLS_MAP.put("Pandas", List.of("Pandas", "pandas", "PANDAS"));
        SKILLS_MAP.put("NumPy", List.of("NumPy", "numpy", "NUMPY"));
        SKILLS_MAP.put("Matplotlib", List.of("Matplotlib", "matplotlib", "MATPLOTLIB"));
        // Add more skills and their variations as necessary



        for (Map.Entry<String, List<String>> entry : SKILLS_MAP.entrySet()) {
            for (String variation : entry.getValue()) {
                REVERSE_SKILLS_MAP.put(variation.toLowerCase(), entry.getKey());
            }
        }
    }

    public static List<String> getAllSkills() {
        return SKILLS_MAP.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public static Map<String, List<String>> getSkillsMap() {
        return SKILLS_MAP;
    }

    public static String getCanonicalSkillName(String skill) {
        return REVERSE_SKILLS_MAP.getOrDefault(skill.toLowerCase(), skill);
    }
}
