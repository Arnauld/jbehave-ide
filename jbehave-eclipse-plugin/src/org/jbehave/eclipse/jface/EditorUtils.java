package org.jbehave.eclipse.jface;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.jbehave.eclipse.Activator;

public class EditorUtils {

    public static IProject findProject(ITextViewer viewer) {
        // TODO: huge hack but I don't know how to get the
        // project the other way
        IDocument document = viewer.getDocument();
        return findProject(document);
    }
    
    public static IProject findProject(IDocument document) {
        // TODO: huge hack but I don't know how to get the
        // project the other way
        ProjectAwareFastPartitioner partitioner = (ProjectAwareFastPartitioner) document.getDocumentPartitioner();
        return partitioner.getProject();
    }

    public static Integer getCharEnd(IDocument document, int lineNumber, int columnNumber) {
        try {
            return new Integer(document.getLineOffset(lineNumber - 1) + columnNumber);
        } catch (BadLocationException e) {
            Activator.logError("Unable to calculate charEnd at " + lineNumber +", " + columnNumber, e);
            return null;
        }
    }

    public static Integer getCharStart(IDocument document, int lineNumber, int columnNumber) {
        try {
            int lineStartChar = document.getLineOffset(lineNumber - 1);
            Integer charEnd = getCharEnd(document, lineNumber, columnNumber);
            if (charEnd != null) {
                ITypedRegion typedRegion = document.getPartition(charEnd.intValue() - 2);
                int partitionStartChar = typedRegion.getOffset();
                return new Integer(partitionStartChar);
            } else
                return new Integer(lineStartChar);
        } catch (BadLocationException e) {
            Activator.logError("Unable to calculate charStart at " + lineNumber +", " + columnNumber, e);
            return null;
        }
    }
}
