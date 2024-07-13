package com.example.job_desc_backend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document
public class Skill_Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String resumeId;
    private LocalDate date;
    private Integer java;
    private Integer python;
    private Integer javascript;
    private Integer cSharp;
    private Integer cPlusPlus;
    private Integer php;
    private Integer swift;
    private Integer kotlin;
    private Integer sql;
    private Integer html;
    private Integer css;
    private Integer react;
    private Integer angular;
    private Integer nodeJs;
    private Integer spring;
    private Integer django;
    private Integer flask;
    private Integer ruby;
    private Integer rails;
    private Integer machineLearning;
    private Integer dataScience;
    private Integer aws;
    private Integer azure;
    private Integer docker;
    private Integer kubernetes;
    private Integer git;
    private Integer ciCd;
    private Integer agile;
    private Integer scrum;
    private Integer jira;
    private Integer devOps;
    private Integer hadoop;
    private Integer spark;
    private Integer tableau;
    private Integer powerBI;
    private Integer tensorflow;
    private Integer keras;
    private Integer pandas;
    private Integer numpy;
    private Integer matplotlib;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getJava() {
        return java;
    }

    public void setJava(Integer java) {
        this.java = java;
    }

    public Integer getPython() {
        return python;
    }

    public void setPython(Integer python) {
        this.python = python;
    }

    public Integer getJavascript() {
        return javascript;
    }

    public void setJavascript(Integer javascript) {
        this.javascript = javascript;
    }

    public Integer getcSharp() {
        return cSharp;
    }

    public void setcSharp(Integer cSharp) {
        this.cSharp = cSharp;
    }

    public Integer getcPlusPlus() {
        return cPlusPlus;
    }

    public void setcPlusPlus(Integer cPlusPlus) {
        this.cPlusPlus = cPlusPlus;
    }

    public Integer getPhp() {
        return php;
    }

    public void setPhp(Integer php) {
        this.php = php;
    }

    public Integer getSwift() {
        return swift;
    }

    public void setSwift(Integer swift) {
        this.swift = swift;
    }

    public Integer getKotlin() {
        return kotlin;
    }

    public void setKotlin(Integer kotlin) {
        this.kotlin = kotlin;
    }

    public Integer getSql() {
        return sql;
    }

    public void setSql(Integer sql) {
        this.sql = sql;
    }

    public Integer getHtml() {
        return html;
    }

    public void setHtml(Integer html) {
        this.html = html;
    }

    public Integer getCss() {
        return css;
    }

    public void setCss(Integer css) {
        this.css = css;
    }

    public Integer getReact() {
        return react;
    }

    public void setReact(Integer react) {
        this.react = react;
    }

    public Integer getAngular() {
        return angular;
    }

    public void setAngular(Integer angular) {
        this.angular = angular;
    }

    public Integer getNodeJs() {
        return nodeJs;
    }

    public void setNodeJs(Integer nodeJs) {
        this.nodeJs = nodeJs;
    }

    public Integer getSpring() {
        return spring;
    }

    public void setSpring(Integer spring) {
        this.spring = spring;
    }

    public Integer getDjango() {
        return django;
    }

    public void setDjango(Integer django) {
        this.django = django;
    }

    public Integer getFlask() {
        return flask;
    }

    public void setFlask(Integer flask) {
        this.flask = flask;
    }

    public Integer getRuby() {
        return ruby;
    }

    public void setRuby(Integer ruby) {
        this.ruby = ruby;
    }

    public Integer getRails() {
        return rails;
    }

    public void setRails(Integer rails) {
        this.rails = rails;
    }

    public Integer getMachineLearning() {
        return machineLearning;
    }

    public void setMachineLearning(Integer machineLearning) {
        this.machineLearning = machineLearning;
    }

    public Integer getDataScience() {
        return dataScience;
    }

    public void setDataScience(Integer dataScience) {
        this.dataScience = dataScience;
    }

    public Integer getAws() {
        return aws;
    }

    public void setAws(Integer aws) {
        this.aws = aws;
    }

    public Integer getAzure() {
        return azure;
    }

    public void setAzure(Integer azure) {
        this.azure = azure;
    }

    public Integer getDocker() {
        return docker;
    }

    public void setDocker(Integer docker) {
        this.docker = docker;
    }

    public Integer getKubernetes() {
        return kubernetes;
    }

    public void setKubernetes(Integer kubernetes) {
        this.kubernetes = kubernetes;
    }

    public Integer getGit() {
        return git;
    }

    public void setGit(Integer git) {
        this.git = git;
    }

    public Integer getCiCd() {
        return ciCd;
    }

    public void setCiCd(Integer ciCd) {
        this.ciCd = ciCd;
    }

    public Integer getAgile() {
        return agile;
    }

    public void setAgile(Integer agile) {
        this.agile = agile;
    }

    public Integer getScrum() {
        return scrum;
    }

    public void setScrum(Integer scrum) {
        this.scrum = scrum;
    }

    public Integer getJira() {
        return jira;
    }

    public void setJira(Integer jira) {
        this.jira = jira;
    }

    public Integer getDevOps() {
        return devOps;
    }

    public void setDevOps(Integer devOps) {
        this.devOps = devOps;
    }

    public Integer getHadoop() {
        return hadoop;
    }

    public void setHadoop(Integer hadoop) {
        this.hadoop = hadoop;
    }

    public Integer getSpark() {
        return spark;
    }

    public void setSpark(Integer spark) {
        this.spark = spark;
    }

    public Integer getTableau() {
        return tableau;
    }

    public void setTableau(Integer tableau) {
        this.tableau = tableau;
    }

    public Integer getPowerBI() {
        return powerBI;
    }

    public void setPowerBI(Integer powerBI) {
        this.powerBI = powerBI;
    }

    public Integer getTensorflow() {
        return tensorflow;
    }

    public void setTensorflow(Integer tensorflow) {
        this.tensorflow = tensorflow;
    }

    public Integer getKeras() {
        return keras;
    }

    public void setKeras(Integer keras) {
        this.keras = keras;
    }

    public Integer getPandas() {
        return pandas;
    }

    public void setPandas(Integer pandas) {
        this.pandas = pandas;
    }

    public Integer getNumpy() {
        return numpy;
    }

    public void setNumpy(Integer numpy) {
        this.numpy = numpy;
    }

    public Integer getMatplotlib() {
        return matplotlib;
    }

    public void setMatplotlib(Integer matplotlib) {
        this.matplotlib = matplotlib;
    }

}
