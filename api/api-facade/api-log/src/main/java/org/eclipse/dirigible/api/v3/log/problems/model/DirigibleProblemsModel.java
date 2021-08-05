package org.eclipse.dirigible.api.v3.log.problems.model;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "DIRIGIBLE_PROBLEMS")
public class DirigibleProblemsModel {

    @Id
    @GeneratedValue
    @Column(name = "PROBLEM_ID", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    @Column(name = "PROBLEM_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String location;

    @Column(name = "PROBLEM_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 120)
    private String type;

    @Column(name = "PROBLEM_LINE", columnDefinition = "VARCHAR", nullable = false, length = 120)
    private String line;

    @Column(name = "PROBLEM_COLUMN", columnDefinition = "VARCHAR", nullable = false, length = 120)
    private String column;

    @Column(name = "PROBLEM_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private Timestamp createdAt;

    @Column(name = "PROBLEM_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String createdBy;

    @Column(name = "PROBLEM_CATEGORY", columnDefinition = "VARCHAR", nullable = false, length = 120)
    private String category;

    @Column(name = "PROBLEM_MODULE", columnDefinition = "VARCHAR", nullable = false, length = 120)
    private String module;

    @Column(name = "PROBLEM_SOURCE", columnDefinition = "VARCHAR", nullable = false, length = 120)
    private String source;

    @Column(name = "PROBLEM_PROGRAM", columnDefinition = "VARCHAR", nullable = false, length = 120)
    private String program;

    public DirigibleProblemsModel() {
    }

    public DirigibleProblemsModel(String location,
                                  String type,
                                  String line,
                                  String column,
                                  String category,
                                  String module,
                                  String source,
                                  String program) {
        this.location = location;
        this.type = type;
        this.line = line;
        this.column = column;
        this.category = category;
        this.module = module;
        this.source = source;
        this.program = program;
    }

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    /**
     * To json.
     *
     * @return the string
     */
    public String toJson() {
        return GsonHelper.GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirigibleProblemsModel that = (DirigibleProblemsModel) o;
        return location.equals(that.location) && type.equals(that.type) && line.equals(that.line) && column.equals(that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, type, line, column);
    }
}
