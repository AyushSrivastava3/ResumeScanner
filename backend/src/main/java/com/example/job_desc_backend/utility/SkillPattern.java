package com.example.job_desc_backend.utility;

import java.util.Map;
import java.util.regex.Pattern;

public class SkillPattern {
    private static final Map<String, String> skillVariations = Map.ofEntries(
            // Frontend Technologies
            Map.entry("html", "html|HTML|Html"),
            Map.entry("css", "css|CSS"),
            Map.entry("javascript", "javascript|JavaScript|JS|js"),
            Map.entry("typescript", "typescript|TypeScript|TS|ts"),
            Map.entry("react", "react|React|ReactJS|reactjs"),
            Map.entry("angular", "angular|Angular|AngularJS|angularjs"),
            Map.entry("vue", "vue|Vue|VueJS|vuejs"),

            // Backend Technologies
            Map.entry("java", "java|Java"),
            Map.entry("spring", "spring|Spring|Spring Framework"),
            Map.entry("springboot", "springboot|Spring Boot|Spring boot|spring boot|Springboot"),
            //Map.entry("nodejs", "nodejs|NodeJS|Node Js|node js|Node.js|node.js"),
            Map.entry("nodeJs", "nodejs|NodeJS|Node Js|node js|Node.js|node.js"),
            Map.entry("express", "express|Express|ExpressJS|expressjs"),

            // Databases
            Map.entry("mysql", "mysql|MySQL|my sql|MySql|My sql"),
            Map.entry("postgresql", "postgresql|PostgreSQL|Postgres|postgres"),
            Map.entry("mongodb", "mongodb|MongoDB|mongo db|Mongo Db"),
            Map.entry("sqlite", "sqlite|SQLite|sql lite|Sql Lite"),

            // Cloud Technologies
            Map.entry("aws", "aws|AWS|Amazon Web Services|amazon web services|Aws"),
            Map.entry("azure", "azure|Azure|Microsoft Azure|microsoft azure"),
            Map.entry("gcp", "gcp|GCP|Google Cloud|google cloud|Google Cloud Platform|google cloud platform"),

            // DevOps
            Map.entry("docker", "docker|Docker"),
            Map.entry("kubernetes", "kubernetes|Kubernetes|K8s|k8s"),
            Map.entry("jenkins", "jenkins|Jenkins"),
            Map.entry("terraform", "terraform|Terraform"),

            // Programming Languages
            Map.entry("python", "python|Python"),
            Map.entry("ruby", "ruby|Ruby"),
            Map.entry("php", "php|PHP|Php"),
            Map.entry("csharp", "csharp|C#|c#|C Sharp|c sharp"),
            Map.entry("cplusplus", "cplusplus|C\\+\\+|c\\+\\+|C Plus Plus|c plus plus|C++|c++"),

            // Other Technologies
            Map.entry("git", "git|Git|GIT"),
            Map.entry("graphql", "graphql|GraphQL|Graph QL|graph ql"),
            Map.entry("rest", "rest|REST|Rest|RESTful|restful"),
            Map.entry("soap", "soap|SOAP|Soap"),
            Map.entry("kafka", "kafka|Kafka"),
            Map.entry("hadoop", "hadoop|Hadoop"),
            Map.entry("spark", "spark|Spark|Apache Spark|apache spark"),

            // Big Data Technologies
            Map.entry("hive", "hive|Hive|Apache Hive|apache hive"),
            Map.entry("pig", "pig|Pig|Apache Pig|apache pig"),

            // Frameworks and Libraries
            Map.entry("django", "django|Django"),
            Map.entry("flask", "flask|Flask"),
            Map.entry("laravel", "laravel|Laravel"),
            Map.entry("rails", "rails|Rails|Ruby on Rails|ruby on rails"),
            Map.entry("springmvc", "springmvc|Spring MVC|spring mvc"),

            // Data Science and Machine Learning
            Map.entry("tensorflow", "tensorflow|TensorFlow|tensor flow|Tensor Flow"),
            Map.entry("pytorch", "pytorch|PyTorch|py torch|Py Torch"),
            Map.entry("scikitlearn", "scikitlearn|scikit-learn|Scikit Learn|scikit learn|sklearn|Sklearn"),
            Map.entry("pandas", "pandas|Pandas"),
            Map.entry("numpy", "numpy|NumPy|num py|Num Py"),

            // Miscellaneous
            Map.entry("aem", "aem|AEM|Adobe Experience Manager|adobe experience manager")
            // Add more skills as needed
    );

    public static Pattern getSkillPattern(String skill) {
        String skillPattern = skillVariations.getOrDefault(skill.toLowerCase(), skill.toLowerCase());
        return Pattern.compile("\\b(" + skillPattern + ")\\b", Pattern.CASE_INSENSITIVE);
    }
}
