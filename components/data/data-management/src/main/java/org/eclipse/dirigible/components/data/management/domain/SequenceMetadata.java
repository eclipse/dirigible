package org.eclipse.dirigible.components.data.management.domain;

public class SequenceMetadata {
    /** The name. */
    private String name;

    /** The kind. */
    private String kind = "sequence";

    public SequenceMetadata(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
