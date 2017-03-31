/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.model;

import java.time.LocalDateTime;

/**
 * This object declared all datamembers for job. Store in the database
 * and use this by calculating the {@code Hourly_pay}, {@code averageHourlyPay} 
 * and {@code totalPay}.
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @see timeclock.interfaces.Repository
 * @see timeclock.interfaces.JobRepository
 * @see timeclock.dao.JobRepositoryJDBCImpl
 * @see java.time.LocalDateTime
 */
public class Job {
    
    /**
     * @see #getBranch
     * @see #setBranch
     */
    private String branch;
    
    /**
     * @see #getProject
     * @see #setProject
     */
    private String project;
    
    /**
     * @see #getPackage
     * @see #setPackage
     */
    private String packag;
    
    /**
     * @see #getClazz
     * @see #setClazz 
     */
    private String clazz;
    
    /**
     * @see #getJobNumber
     * @see #setJobNumber
     */
    private int jobNumber;
    
    /**
     * @see #getStartAt
     * @see #setStartAt
     */
    private LocalDateTime startAt;
    
    /**
     * @see #getEndAt
     * @see #setEndAt
     */
    private LocalDateTime endAt;
    
    /**
     * @see #getStatus
     * @see #setStatus
     */
    private String status;
    
    /**
     * @see #getComment
     * @see #setComment
     */
    private String comment;
    
    /**
     * @see #getDeveloperId
     * @see #setDeveloperId
     */
    private int developerId;
    
    /**
     * Set the new {@code Job} object by start.
     * @param branch on the Git
     * @param project name of project
     * @param packag package of job
     * @param clazz class name of job
     * @param jobNumber number of job
     * @param startAt the start time
     * @param comment comment
     * @param developerId the developer's id
     */
    public Job(String branch, String project, String packag, String clazz, int jobNumber, LocalDateTime startAt, String comment, int developerId) {
        this.branch = branch;
        this.project = project;
        this.packag = packag;
        this.clazz = clazz;
        this.jobNumber = jobNumber;
        this.startAt = startAt;
        this.comment = comment;
        this.developerId = developerId;
    }
    
    /**
     * Set the new {@code Job} object by new start.
     * @param project name of project
     * @param packag package of job
     * @param clazz class name of job
     * @param jobNumber number of job
     * @param startAt the start time 
     */
    public Job(String project, String packag, String clazz, int jobNumber, LocalDateTime startAt) {
        this.project = project;
        this.packag = packag;
        this.clazz = clazz;
        this.jobNumber = jobNumber;
        this.startAt = startAt;
    }

    /**
     * Set the new {@code Job} object by end.
     * @param project name of project
     * @param packag package of job
     * @param clazz class name of job
     * @param jobNumber number of job
     * @param endAt the end time
     * @param status job's status
     */
    public Job(String project, String packag, String clazz, int jobNumber, LocalDateTime endAt, String status) {
        this.project = project;
        this.packag = packag;
        this.clazz = clazz;
        this.jobNumber = jobNumber;
        this.endAt = endAt;
        this.status = status;
    }
    
    /**
     * Set the new {@code Job} object for identification the correct job.
     * @param project name of project
     * @param packag package of job
     * @param clazz class name of job
     * @param jobNumber number of job 
     */
    public Job(String project, String packag, String clazz, int jobNumber) {
        this.project = project;
        this.packag = packag;
        this.clazz = clazz;
        this.jobNumber = jobNumber;
    }

    /**
     * Get the branch.
     * @return the branch as a {@code String}
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Set the branch.
     * @param branch for setting
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * Get the project.
     * @return the project name as a {@code String}
     */
    public String getProject() {
        return project;
    }

    /**
     * Set the project.
     * @param project for setting
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * Get the job's package.
     * @return package as a {@code String}
     */
    public String getPackage() {
        return packag;
    }

    /**
     * Set the job's package.
     * @param packag for setting
     */
    public void setPackage(String packag) {
        this.packag = packag;
    }

    /**
     * Get the job's class.
     * @return the job's class as a {@code String}
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Set the job's class.
     * @param clazz for setting
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    /**
     * Get the job's number.
     * @return the job's number
     */
    public int getJobNumber() {
        return jobNumber;
    }

    /**
     * Set the job's number.
     * @param jobNumber for setting
     */
    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    /**
     * Get the job's start time.
     * @return the start time
     */
    public LocalDateTime getStartAt() {
        return startAt;
    }

    /**
     * Set the job's start time
     * @param startAt for setting
     */
    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    /**
     * Get the job's end time.
     * @return the end time
     */
    public LocalDateTime getEndAt() {
        return endAt;
    }

    /**
     * Set the job's end time.
     * @param endAt for setting
     */
    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    /**
     * Get the job's status.
     * @return the status as a {@code String}
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the job's status.
     * @param status for setting
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the comment.
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment.
     * @param comment for setting
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Get developer's id.
     * @return id
     */
    public int getDeveloperId() {
        return developerId;
    }

    /**
     * Set the developer's id.
     * @param developerId for setting
     */
    public void setDeveloperId(int developerId) {
        this.developerId = developerId;
    }
    
}
