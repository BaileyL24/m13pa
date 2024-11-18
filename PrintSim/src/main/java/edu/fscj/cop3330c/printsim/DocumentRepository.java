// DocumentRepository.java
// D. Singletary
// 11/17/24
// manage documents for printer simulation

package edu.fscj.cop3330c.printsim;

import java.util.ArrayList;
import java.util.List;

public class DocumentRepository {
    private List<String> documentList;
    private int documentIndex;

    public DocumentRepository() {
        this.documentList = new ArrayList<>();
        this.documentIndex = 0;
        initializeDocuments();
    }

    // Initialize the document list
    private void initializeDocuments() {
        for (int i = 1; i <= 50; i++) {
            documentList.add("Document_" +
                    String.format("%03d", i) + ".docx");
        }
    }

    // Get the next document
    public String getNextDocument() {
        String nextDocument = null;
        try {
            nextDocument = documentList.get(documentIndex++);
        } finally {
            return nextDocument;
        }
    }

    // Check if there are more documents
    public boolean hasMoreDocuments() {
         return documentIndex < documentList.size();
    }
}
