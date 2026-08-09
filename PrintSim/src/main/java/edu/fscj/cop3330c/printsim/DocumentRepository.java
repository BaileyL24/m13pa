// DocumentRepository.java
// D. Singletary
// 11/17/24
// manage documents for printer simulation
// Bailey Lester
// 8/9/2026
// Added ReentrantLock to make DocRepository thread-safe

package edu.fscj.cop3330c.printsim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DocumentRepository {
    private final List<String> documentList;
    private int documentIndex;
    private final Lock lock;

    public DocumentRepository() {
        this.documentList = new ArrayList<>();
        this.documentIndex = 0;
        this.lock = new ReentrantLock();
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
        lock.lock();
        try {
            if (documentIndex < documentList.size()) {
                return documentList.get(documentIndex++);
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    // Check if there are more documents
    public boolean hasMoreDocuments() {
        lock.lock();
        try {
            return documentIndex < documentList.size();
        } finally {
            lock.unlock();
        }
    }
}
