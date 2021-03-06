package org.jbehave.eclipse.rule;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.jbehave.eclipse.util.BidirectionalStream;

public class CharacterStreamAdapter implements BidirectionalStream {
    
    private ICharacterScanner scanner;
    public CharacterStreamAdapter(ICharacterScanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int read() {
        return scanner.read();
    }
    @Override
    public void unread() {
        scanner.unread();
    }
}
