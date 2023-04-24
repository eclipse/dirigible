package org.eclipse.dirigible.components.engine.camel.domain;

import org.eclipse.dirigible.components.base.artefact.Artefact;

import javax.persistence.*;

@Entity
@Table(name = "DIRIGIBLE_CAMEL")
public class Camel extends Artefact {
    public static final String ARTEFACT_TYPE = "camel";

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAMEL_ID", nullable = false)
    private Long id;

    /** The content. */
    @Transient
    private transient byte[] content;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }
}
